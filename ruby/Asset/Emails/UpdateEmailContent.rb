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

id = 1211 #id of email, required
params = {
  :access_token => get_token(host, client_id, client_secret),
  :subject => JSON.generate({:type => "Text", :value => "Updated Subject Line"}) 
}

response = RestClient.post "#{host}/rest/asset/v1/email/#{id}/content.json", params

puts response