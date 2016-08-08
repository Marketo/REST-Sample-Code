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

access_token = get_token(host, client_id, client_secret)


params = {
  :name => "new template - Ruby", #name of new template
  :folder => JSON.generate({:id => 12, :type => "Folder"}), #folder record as json
  :content => File.new("testFile.html"), #HTML content of template
  :description => "Landing Page Template created by Ruby" #optional description
}


response = RestClient.post "#{host}/rest/asset/v1/landingPageTemplates.json?access_token=#{access_token}", params, {:multipart => true}

puts response