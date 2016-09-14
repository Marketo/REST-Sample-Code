/*
   Marketo REST API Sample Code
   Copyright (C) 2016 Marketo, Inc.

   This software may be modified and distributed under the terms
   of the MIT license.  See the LICENSE file for details.
*/
package dev.marketo.samples.Campaigns;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.NoSuchElementException;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;

//the Java sample code on dev.marketo.com uses the minimal-json package
//minimal-json provides easy and fast representations of JSON
//for more information check out https://github.com/ralfstx/minimal-json

public class RequestCampaign {
	public String marketoInstance = ;//Replace this with the host from Admin Web Services
	public String marketoIdURL = marketoInstance + "/identity";	
	public String clientId = ;	//Obtain from your Custom Service in Admin>Launchpoint
	public String clientSecret = ;	//Obtain from your Custom Service in Admin>Launchpoint
	public String idEndpoint = marketoIdURL + "/oauth/token?grant_type=client_credentials&client_id=" + clientId + "&client_secret=" + clientSecret;
	public int id;//id of the campaign to request, required
	public int[] leadIds;//one or more lead IDs
	public JsonObject[] tokens;//one or more my tokens, these are JSON objects with two members, "name" and "value"
	
	public static void main(String[] args){
		RequestCampaign requestCampaign = new RequestCampaign();
		requestCampaign.id = 1001;
		requestCampaign.leadIds = new int[]{1,2,3,4,5,6,7,8,9,10};
		JsonObject myToken = new JsonObject()
								.add("name", "{{my.token}}")
								.add("value", "myValue");
		requestCampaign.tokens = new JsonObject[]{myToken};
		System.out.println(requestCampaign.buildRequest());
		String result = requestCampaign.postData();
		System.out.println(result);
	}
	//Make request
	public String postData(){
		String result = null;
		try {
			//Build the request body
			JsonObject requestBody = buildRequest();
			//Assemble the URL
			String endpoint = marketoInstance + "/rest/v1/campaigns/" + id + "/trigger.json?access_token=" + getToken();
			URL url = new URL(endpoint.toString());
			HttpsURLConnection urlConn = (HttpsURLConnection) url.openConnection();
			urlConn.setRequestMethod("POST");
			urlConn.setRequestProperty("Content-type", "application/json");//"application/json" content-type is required.
            urlConn.setRequestProperty("accept", "text/json");
            urlConn.setDoOutput(true);
			OutputStreamWriter wr = new OutputStreamWriter(urlConn.getOutputStream());
			wr.write(requestBody.toString());
			wr.flush();
			int responseCode = urlConn.getResponseCode();
			
			if (responseCode == 200){
				InputStream inStream = urlConn.getInputStream();
				result = convertStreamToString(inStream);
			}else{
				result = "Status Code: " + responseCode;
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
		
	}
	//Takes the input and assembles a json representation to submit
	private JsonObject buildRequest(){
		JsonObject jo = new JsonObject();//parent object
		JsonObject input = new JsonObject();//inut object to hold arrays of tokens and leads
		JsonArray leads = new JsonArray();
		int i;
		for (i = 0; i < leadIds.length; i++) {
			leads.add(leadIds[i]);
		}
		input.add("leads", leads);
		//assemble array of tokens and add to input if present
		if (tokens != null){
			JsonArray tokensArray = new JsonArray();
			for (JsonObject jsonObject : tokens) {
				tokensArray.add(jsonObject);
				}
			input.add("tokens", tokensArray);
			}
		//add input as a member of the parent
		jo.add("input", input);
		return jo;
		}
	public String getToken(){
		String token = null;
		try {
			URL url = new URL(idEndpoint);
			HttpsURLConnection urlConn = (HttpsURLConnection) url.openConnection();
			urlConn.setRequestMethod("GET");
            urlConn.setRequestProperty("accept", "application/json");
            int responseCode = urlConn.getResponseCode();
            if (responseCode == 200) {
                InputStream inStream = urlConn.getInputStream();
                Reader reader = new InputStreamReader(inStream);
                JsonObject jsonObject = JsonObject.readFrom(reader);
                token = jsonObject.get("access_token").asString();
            }else {
                throw new IOException("Status: " + responseCode);
            }
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}catch (IOException e) {
            e.printStackTrace();
        }
		return token;
	}


    private String convertStreamToString(InputStream inputStream) {

        try {
            return new Scanner(inputStream).useDelimiter("\\A").next();
        } catch (NoSuchElementException e) {
            return "";
        }
    }
}
