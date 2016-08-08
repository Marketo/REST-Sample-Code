require 'rest_client'
require 'json'

#Build request URL
#Replace AAA-BBB-CCC with your Marketo instance
marketo_instance = "https://AAA-BBB-CCC.mktorest.com" 
endpoint = "/rest/v1/activities/pagingtoken.json"
#Replace with your access token
auth_token =  "?access_token=" + "ac756f7a-d54d-41ac-8c3c-f2d2a39ee325:ab"
#Specify date
since_date_time = "&sinceDatetime=2014-10-06T13:22:17-08:00"
request_url = marketo_instance + endpoint + auth_token + since_date_time

#Make request
response = RestClient.get request_url

#Returns Marketo API response
puts response