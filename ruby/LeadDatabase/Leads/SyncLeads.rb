#
# SyncLeads.rb
#
# Marketo REST API Sample Code
# Copyright (C) 2016 Marketo, Inc.
#
# This software may be modified and distributed under the terms
# of the MIT license.  See the LICENSE file for details.
#
require 'rest-client'
require 'json'

#Build request URL
#Replace AAA-BBB-CCC with your Marketo instance
marketo_instance = "https://AAA-BBB-CCC.mktorest.com" 
endpoint = "/rest/v1/leads.json"
#Replace with your access token
auth_token =  "?access_token=" + "ac756f7a-d54d-41ac-8c3c-f2d2a39ee325:ab" 
request_url = marketo_instance + endpoint + auth_token

#Build request body
data = { "action" => "createOnly", "input" => [ { "email" => "example1@example.com", "firstName" => "examplea" }, { "email" => "example2@example.com", "firstName" => "exampleb" } ] } 

#Make request
response = RestClient.post request_url, data.to_json, :content_type => :json, :accept => :json

#Returns Marketo API response
puts response