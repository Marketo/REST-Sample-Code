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
objects = [
  {
    :dedupe => "1"
  }
]
body = {
  :input => objects,
  :dedupeBy => "dedupeFields"
}

response = RestClient.post "#{host}/rest/v1/customobjects/#{name}.json?access_token=#{get_token(host, client_id, client_secret)}", JSON.generate(body), {:content_type => "application/json"}

puts response