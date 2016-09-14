/*
   Marketo REST API Sample Code
   Copyright (C) 2016 Marketo, Inc.

   This software may be modified and distributed under the terms
   of the MIT license.  See the LICENSE file for details.
*/
package dev.marketo.samples.CustomObjects;

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

public class UpsertCustomObjects {
	public String marketoInstance = ;//Replace this with the host from Admin Web Services
	public String marketoIdURL = marketoInstance + "/identity";	
	public String clientId = ;	//Obtain from your Custom Service in Admin>Launchpoint
	public String clientSecret = ;	//Obtain from your Custom Service in Admin>Launchpoint
	public String idEndpoint = marketoIdURL + "/oauth/token?grant_type=client_credentials&client_id=" + clientId + "&client_secret=" + clientSecret;
	public JsonObject[] input; //an array of Opportunities to use for input.  Each must have a member "externalopportunityid".  Each can optionally include "externalCompanyId" for company association
	public String action; //specify the action to be undertaken, createOnly, updateOnly, createOrUpdate 
	public String dedupeBy; //select mode of Deduplication, dedupeFields for all dedupe parameters, idField for marketoId
	public String name; //name of custom object type

	public static void main(String[] args){
		UpsertCustomObjects upsert = new UpsertCustomObjects();
		JsonObject obj1 = new JsonObject().add("name", "custom").add("externalopportunityid", "Opportunity1Test");
		upsert.input = new JsonObject[]{obj1};
		String result = upsert.postData();
		System.out.println(result);
	}
	public String postData(){
		String result = null;
		try {
			
			JsonObject requestBody = buildRequest(); //builds the Json Request Body
			String endpoint =  marketoInstance + "/rest/v1/customobjects/" + name + ".json?access_token=" + getToken(); //takes the endpoint URL and appends the access_token parameter to authenticate
			URL url = new URL(endpoint); 
			HttpsURLConnection urlConn = (HttpsURLConnection) url.openConnection(); //Return a URL connection and cast to HttpsURLConnection
			urlConn.setRequestMethod("POST");
			urlConn.setRequestProperty("Content-type", "application/json");
            urlConn.setRequestProperty("accept", "text/json");
            urlConn.setDoOutput(true);
			OutputStreamWriter wr = new OutputStreamWriter(urlConn.getOutputStream());
			wr.write(requestBody.toString());
			wr.flush();
			InputStream inStream = urlConn.getInputStream(); //get the inputStream from the URL connection
			result = convertStreamToString(inStream);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	//assembles the JSON request body
	private JsonObject buildRequest(){
		JsonObject requestBody = new JsonObject(); //Create a new JsonObject for the Request Body
		JsonArray in = new JsonArray(); //Create a JsonArray for the "input" member to hold Opp records
		for (JsonObject jo : input) {
			in.add(jo); //add our Opportunity records to the input array
		}
		requestBody.add("input", in);
		if (this.action != null){
			requestBody.add("action", action); //add the action member if available
		}
		if (this.dedupeBy != null){
			requestBody.add("dedupeBy", dedupeBy); //add the dedupeBy member if available
		}
		return requestBody;
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
