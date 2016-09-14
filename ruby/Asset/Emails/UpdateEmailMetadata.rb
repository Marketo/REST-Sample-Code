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



id = 1001 # id of email to update
params = {
  :access_token => get_token(host, client_id, client_secret),
  :description => "Description", #optional description
  :name => "New Name - Ruby" #optional new name
}

response = RestClient.post "#{host}/rest/asset/v1/email/#{id}.json", params

puts response