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

name = "pet_c"
params = {
  :access_token => get_token(host, client_id, client_secret),
  :filterType => "dedupe", #field to filter on
  :filterValues => "1,2,3,4,5", #list of values separated by commas
  :batchSize => 50
  
}

response = RestClient.get "#{host}/rest/v1/customobjects/#{name}.json", {:params => params}

puts response