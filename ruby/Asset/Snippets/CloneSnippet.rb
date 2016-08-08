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

id = 10 #id of email
params = {
  :access_token => get_token(host, client_id, client_secret),
  :name => "Clone Snippet - Ruby", #name of new template
  :folder => JSON.generate({:id => 5566, :type => "Folder"}) #new folder for clone
}

response = RestClient.post "#{host}/rest/asset/v1/snippet/#{id}/clone.json", params

puts response