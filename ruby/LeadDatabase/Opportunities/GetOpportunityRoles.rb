require 'rest_client'
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


params = {
  :access_token => get_token(host, client_id, client_secret),
  :filterType => "externalopportunityid", #field to filter on
  :filterValues => "Ruby Opportunity 1", #list of values separated by commas
  :batchSize => 50
  
}

response = RestClient.get "#{host}/rest/v1/opportunities/roles.json", {:params => params}

puts response