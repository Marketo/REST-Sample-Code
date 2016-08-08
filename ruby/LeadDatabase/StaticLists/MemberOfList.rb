require 'rest_client'
require 'json'

#Build request URL
#Replace AAA-BBB-CCC with your Marketo instance
marketo_instance = "https://AAA-BBB-CCC.mktorest.com" 
#Specify list id
listId = 1234
endpoint = "/rest/v1/lists/" + listId + "/leads/ismember.json"
#Replace with your access token
auth_token =  "?access_token=" + "ac756f7a-d54d-41ac-8c3c-f2d2a39ee325:ab"
specify_post_method = "_method=GET" 
request_url = marketo_instance + endpoint + auth_token + specify_post_method

#Build request body
data = { "input" => [ { "id" => "1" }, { "id" => "2" } ] } 

#Make request
response = RestClient.post request_url, data.to_json, :content_type => :json, :accept => :json

#Returns Marketo API response
puts response