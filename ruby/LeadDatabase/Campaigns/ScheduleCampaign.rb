require 'rest-client'
require 'json'

#Build request URL
#Replace AAA-BBB-CCC with your Marketo instance
marketo_instance = "https://AAA-BBB-CCC.mktorest.com" 
#Specify campaign id
campaignId = 1234
endpoint = "/rest/v1/campaigns/" + campaignId + "/schedule.json"
#Replace with your access token
auth_token =  "?access_token=" + "ac756f7a-d54d-41ac-8c3c-f2d2a39ee325:ab"
request_url = marketo_instance + endpoint + auth_token

#Build request body
data = { "input" => [ { "runAt" => "2014-03-26T18:04:10+0000" }, { "cloneToProgramName" => "TestProgramFromRest" }, { "tokens" => [ { "name" => "{{my.message}}", "value" => "Updated Message"}, { "name" => "{{my.other token}}", "value" => "Value for other token"} ] } ] } 

#Make request
response = RestClient.post request_url, data.to_json, :content_type => :json, :accept => :json

#Returns Marketo API response
puts response