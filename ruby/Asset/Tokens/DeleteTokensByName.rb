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

access_token = get_token(host, client_id, client_secret)

id = 1071 #id of folder to delete from
params = {
  :folderType => "Program", #type of folder, Folder or Program
  :name => "Token", #name of token to delete
  :type => "text" #type of token to delete
}

response = RestClient.post "#{host}/rest/asset/v1/folder/#{id}/tokens/delete.json?access_token=#{access_token}", params

puts response