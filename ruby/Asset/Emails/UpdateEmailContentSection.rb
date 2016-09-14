#
# Marketo REST API Sample Code
# Copyright (C) 2016 Marketo, Inc.
#
# This software may be modified and distributed under the terms
# of the MIT license.  See the LICENSE file for details.
#
require 'rest-client'
require 'json'

host = "CHANGE ME"
client_id = "CHANGE ME"
client_secret = "CHANGE ME"

def get_token(host, client_id, client_secret)
  url = "#{host}/identity/oauth/token?grant_type=client_credentials&client_id=#{client_id}&client_secret=#{client_secret}"
  response = RestClient.get url
  json = JSON.parse(response)
  return json["access_token"]
end

id = 1211
htmlId = "edit_text_1"
params = {
  :access_token => get_token(host, client_id, client_secret),
  :type => "DynamicContent",
  :value => JSON.generate({:type => "DynamicContent", :segmentation => 1007, :default => "<div>Default Content</div>"})
}

response = RestClient.post "#{host}/rest/asset/v1/email/#{id}/content/#{htmlId}.json", params

puts response