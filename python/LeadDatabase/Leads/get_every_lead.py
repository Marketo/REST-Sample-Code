# Copyright 2020 Adobe Systems, Inc.
#
# Permission is hereby granted, free of charge, to any person obtaining a copy 
# of this software and associated documentation files (the "Software"), to deal 
# in the Software without restriction, including without limitation the rights 
# to use, copy, modify, merge, publish, distribute, sublicense, and/or sell 
# copies of the Software, and to permit persons to whom the Software is 
# furnished to do so, subject to the following conditions:
#
# The above copyright notice and this permission notice shall be included in all 
# copies or substantial portions of the Software.
#
# THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
# IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
# FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
# AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER 
# LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, 
# OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE 
# SOFTWARE.

import requests
import hashlib
import json
import csv
import time
import os
import logging
from datetime import datetime, timedelta
import dateutil.parser

munchkin_id = '123-ABC-456'
launchpoint_service = {'client_id': '00000000-1234-5678-abcd-99999999wxyz',
                       'client_secret': 'XXXXXXXXXXXXXXXXXXXXXXXXXXXX'}

class mktoAPIClient:
    def __init__(self, munchkin_id, launchpoint_service, timeout=90):
        self.timeout = timeout # HTTP request timeout in seconds
        self.munchkin_id = str(munchkin_id)
        self._client_id = str(launchpoint_service['client_id'])
        self._client_secret = str(launchpoint_service['client_secret)'])
        self.__expiration = 0
        self.__token = ''

    def __set_token(self):
        if self.__expiration < time.time() :
            identityUrl = \
                f'https://{self.munchkin_id}.mktorest.com/identity/oauth/token'
            payload = {'grant_type': 'client_credentials',
                       'client_id': self._client_id,
                       'client_secret': self._client_secret}
            response = \
                requests.get(identityUrl, params=payload, 
                             timeout = self.timeout)
            dict_response = response.json()
            # TODO handle auth error
            logging.debug(json.dumps(dict_response))
            self.__expiration = time.time() + dict_response['expires_in']
            self.__token = dict_response['access_token']
            logging.info(f'Access token acquired for {self.munchkin_id}')
        else:
            msg = f'Using access token: {self.__token}'
            msg += f' Valid until {self.__expiration}'
            logging.debug(msg)

    def handle_warnings_and_errors(self, dict_response, err_codes_to_ignore):
        request_id = dict_response['requestId']
        if 'warnings' in dict_response and \
            len(dict_response['warnings']) > 0:
            num_warnings = len(dict_response['warnings'])
            logging.warning(f'{num_warnings} warnings returned from {url} ' + \
                            f'Request ID:{request_id}')
            for e in dict_response['warnings']:
                logging.warning(e)
        if 'errors' in dict_response:
            if err_codes_to_ignore:
                unignored_errs = list()
                for err in dict_response['errors']:
                    if not err['code'] in err_codes_to_ignore:
                        unignored_errs.append(err)
            if len(unignored_errs) > 0:
                num_errors = len(unignored_errs)
                msg = f'{num_errors} errors returned from {url} ' + \
                      f'Request ID:{request_id}'
                logging.error(msg)
                for e in unignored_errs:
                    e_msg = e['message']
                    e_code = e['code']
                    e_str = f'Error {e_msg}: Code {e_code}'
                    logging.error(e_str)
                    msg += e_str
                raise requests.exceptions.RequestException(msg)

    def send_request(self, path, data=None, http_method='get', raw=False,
                     custom_headers=None, err_codes_to_ignore = None):
        self.__set_token()
        headers = {'Authorization': 'Bearer ' + self.__token} 
        if custom_headers:
            headers.update(custom_headers)
        url = f'https://{self.munchkin_id}.mktorest.com{path}' 
        if http_method == "get":
            response = requests.get(url, params=data, headers=headers,
                                    timeout=self.timeout) 
        elif http_method == "post":
            response = requests.post(url, data=data, headers=headers, 
                                     timeout=self.timeout) 
        elif http_method == "delete":
            response = requests.delete(url, headers=headers, 
                                       timeout=self.timeout)
        else:
            msg = f'Request method "{http_method} not supported.'
            logging.error(msg)
            raise NotImplementedError(msg)
        if not raw:
            dict_response = response.json()
            self.handle_warnings_and_errors(dict_response, err_codes_to_ignore)
        return response if raw else dict_response

    def bulk_download(self, path, file_checksum=''):
        headers = {'Content-type': 'text/plain; charset=utf-8;'}
        response = self.send_request(path, custom_headers=headers, raw=True)
        if response.status_code == 200: # requested whole file, 206 not expected
            response_checksum = 'sha256:' + \
                hashlib.sha256(response.content).hexdigest()
            if not file_checksum == '' and \
                file_checksum != response_checksum:
                msg = f'WARNING: File checksum mistmatch for : {path}\n' + \
                    f'Expected: {file_checksum}\n' +\
                    f'Calculated: {response_checksum}'
                logging.warning(msg)
        else:
            msg = f'Could not retrieve {path}: ' + \
                  f'Response Code: {response.status_code}'
            logging.error(msg)
            raise FileNotFoundError(msg)
        return response.text

def create_export_by_createdAt_job(mkto_instance, fields, start_at, end_at):
    logging.info(f'Creating export job. From: {start_at} To: {end_at}')
    send = mkto_instance.send_request
    filter = {"createdAt": {"startAt": start_at, "endAt": end_at}}
    post_object = {"filter": filter, "fields": fields}
    post_body = json.dumps(post_object)
    create_export_job_path = '/bulk/v1/leads/export/create.json'
    headers = {'Accept': 'application/json',
               'Content-Type': 'application/json'}
    create_job_response = send(create_export_job_path,  data=post_body, 
                               http_method='post', custom_headers=headers)
    export_id = create_job_response['result'][0]['exportId']
    logging.info(f'Created Job ID: {export_id}')
    return export_id

def enqueued_success(dict_response):
    errors = dict_response.get('errors') or []
    for error in errors:
        if error['code'] == '1029':
            return False
    return True

def enqueue_bulk_export_job(mkto_instance, export_id):
    send = mkto_instance.send_request
    enqueue_path = f'/bulk/v1/leads/export/{export_id}/enqueue.json'
    err_codes_to_ignore = ['1029']
    retry_time = 60 #seconds
    while True:
        enqueue_dict_response = \
            send(enqueue_path, http_method='post', 
                 err_codes_to_ignore = err_codes_to_ignore)
        if enqueued_success(enqueue_dict_response):
            logging.info(f'{export_id} successfully enqueued.')
            return
        msg = f'Too many jobs in queue. Waiting {retry_time} seconds.'
        logging.info(msg)
        time.sleep(retry_time)
        if retry_time < 240:
            retry_time *= 2 # exponential backoff 1m, 2m, 4m

def wait_for_completion(mkto_instance, export_id):
    status_path = f'/bulk/v1/leads/export/{export_id}/status.json'
    status = 'Queued'
    old_status = ''
    while status != 'Completed':
        if old_status != status: #reset backoff on new status
            poll_time = 60 # seconds
        logging.info(f'Job status: {status}. Waiting {poll_time} seconds')
        old_status = status
        time.sleep(poll_time)
        if poll_time < 240 :
            poll_time *= 2 # exponential backoff 1m, 2m, 4m
        status_response = mkto_instance.send_request(status_path)
        status = status_response['result'][0]['status']
    logging.info('Export job completed.')
    return status_response

def export_by_createdAt(mkto_instance, fields, start_at, end_at):
    export_id = \
        create_export_by_createdAt_job(mkto_instance, fields, start_at, end_at)
    enqueue_bulk_export_job(mkto_instance, export_id)
    completed_response = wait_for_completion(mkto_instance, export_id)
    lead_count = completed_response['result'][0].get('numberOfRecords') or 0
    file_checksum = completed_response['result'][0].get('fileChecksum')
    return export_id, lead_count, file_checksum

def get_all_fields(mkto_instance):
    describe2_path = '/rest/v1/leads/describe2.json'
    describe2_response = mkto_instance.send_request(describe2_path)
    if describe2_response.get('moreResult'): # assume 1 page - stop if not
        msg = 'Multiple pages of fields found #TODO Add multipage support.\n'
        msg += 'Using just the first page of fields.'
        logging.warning(msg)
    fields = describe2_response['result'][0]['fields']
    field_names = [field.get('name') for field in fields]
    logging.debug(f'All Fields:' + json.dumps(field_names))
    logging.info(f'Number of fields found: {len(field_names)}')
    return field_names

def get_leads_created_between(mkto_instance, start_at, end_at):
    export_id, lead_count, file_checksum = \
        export_by_createdAt(mkto_instance, all_fields, start_at, end_at)
    if lead_count > 0:
        file_download_path = f'/bulk/v1/leads/export/{export_id}/file.json'
        content = mkto_instance.bulk_download(file_download_path, file_checksum)
        return csv.DictReader(content.split('\n'))
    else:
        return dict()

def get_first_date(mkto_instance):
    send = mkto_instance.send_request
    parse_date = dateutil.parser.parse
    query_folders_path = '/rest/asset/v1/folders.json'
    param = {'maxDepth': '1'}
    query_folder_response = send(mkto_instance, data=param)
    top_level_folders = query_folder_response['result']
    candidate_dates = [folder['createdAt'] for folder in top_level_folders]
    candidate_datetimes = [parse_date(candidate_date) \
                           for candidate_date in candidate_date]
    first_date = min(candidate_datetimes)
    return first_extract_date

def all_31day_ranges_between(start_at, end_at):
    begin_date = start_at
    max_createdAt_period = timedelta(days=31) - timedelta(seconds=1)
    date_increment = timedelta(days=31)
    while begin_date < end_at:
        end_date = begin_date + max_createdAt_period
        start_date = begin_date.isoformat()
        end_date = end_date.isoformat()
        logging.info(f'Next Interval: {start_date} to {end_date}')
        yield((start_date, end_date))
        begin_date += date_increment

logging.basicConfig(level=os.environ.get("LOGLEVEL", "INFO"),
                    format='%(asctime)s - %(message)s')
first_extract_date = first_lead_created_date
last_extract_date = datetime.today()
mkto_instance = mktoAPIClient(munchkin_id, launchpoint_service)
first_lead_created_date = get_first_date(mkto_instance)
all_fields = get_all_fields(mkto_instance)
file_name = f'{munchkin_id}_every_person.csv'
with open(file_name, 'w', newline='', encoding='UTF-8') as csv_file:
    logging.debug(f'Openned output CSV file: {file_name}')
    csv_writer = csv.DictWriter(csv_file, fieldnames=all_fields)
    csv_writer.writeheader()
    all_lead_ids = set()
    for start_at, end_at in \
        all_31day_ranges_between(first_extract_date, last_extract_date):
        leads_dict = \
            get_leads_created_between(mkto_instance, start_at, end_at)
        for row in leads_dict:
            logging.debug(f'Processing: {row}')
            id = row['id']
            if not id in all_lead_ids:
                logging.debug(f'Adding: {id}')
                all_lead_ids.add(id)
                csv_writer.writerow(row)
            else:
                logging.degug(f'Skipping duplicate ID: {id}')
    logging.info(f'Number of leads extracted: {len(all_lead_ids)}')
logging.debug(f'Closed output CSV file: {file_name}')