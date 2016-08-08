require 'rest_client'
require 'json'

#Build request URL
#Replace AAA-BBB-CCC with your Marketo instance
marketo_instance = "https://AAA-BBB-CCC.mktorest.com" 
#Specify lead id
leadId = "1234"
endpoint = "/rest/v1/leads/" + leadId + "/associate.json"
#Replace with your access token
auth_token =  "?access_token=" + "ac756f7a-d54d-41ac-8c3c-f2d2a39ee325:ab" 
#Replace with cookie id of anonymous lead
#The _mkto_trk cookie value includes an ampersand and needs to be URL encoded to %26 to be processed by the Marketo API. 
cookie_id = "&cookie=" + "id:287-GTJ-838%26token:_mch-marketo.com-1396310362214-46169"
request_url = marketo_instance + endpoint + auth_token + cookie_id
data = {}

#Make request
response = RestClient.post request_url, data.to_json, :content_type => :json, :accept => :json

#Returns Marketo API response
puts response