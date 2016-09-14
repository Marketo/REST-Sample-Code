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
marketo_instance = "https://AAA-BBB-CCC.mktorest.com/identity/oauth/token?grant_type=client_credentials"
#Relace with your client id
client_id = "CHANGE ME"
#Replace with your your  client secret
client_secret = "CHANGE ME"
request_url = marketo_instance + "&client_id=" + client_id + "&client_secret=" + client_secret

#Make request
response = RestClient.get request_url

#Parse reponse and return only access token
results = JSON.parse(response.body)
access_token = results["access_token"]
puts access_token
