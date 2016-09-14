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

id = 1037
access_token = get_token(host, client_id, client_secret)
params = {
  :folderType => "Program", #type of folder, Folder or Program
  :type => "text", # type of token to create
  :name => "new Token - Ruby", # name of token
  :value => "Token value goes here"
}

response = RestClient.post "#{host}/rest/asset/v1/folder/#{id}/tokens.json?access_token=#{access_token}", params

puts response