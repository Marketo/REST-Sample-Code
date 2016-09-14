/*
   Marketo REST API Sample Code
   Copyright (C) 2016 Marketo, Inc.

   This software may be modified and distributed under the terms
   of the MIT license.  See the LICENSE file for details.
*/
package dev.marketo.samples.Programs;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
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

public class CreateProgram {
	public String marketoInstance = "https://299-BYM-827.mktorest.com";//Replace this with the host from Admin Web Services
	public String marketoIdURL = marketoInstance + "/identity";	
	public String clientId = "b417d98f-9289-47d1-a61f-db141bf0267f";	//Obtain from your Custom Service in Admin>Launchpoint
	public String clientSecret = "0DipOvz4h2wP1ANeVjlfwMvECJpo0ZYc";	//Obtain from your Custom Service in Admin>Launchpoint
	public String idEndpoint = marketoIdURL + "/oauth/token?grant_type=client_credentials&client_id=" + clientId + "&client_secret=" + clientSecret;
	
	public String name; //name of new Program
	public JsonObject folder; //a JSON input, with 2 required members, id and type
	public String description; //Description for the folder, up to 2,000 characters
	public String type;//program type, "Default", "Event", or "Event with Webinar"
	public String channel;//channel of program, must be available in Channels list of instance
	public JsonArray tags;//optional list of tags, embedded as a JSON Array of JSON objects with members tagType and tagValue;
	public JsonArray costs;//embedded as a JSON Array of JSON objects with members startDate, cost, and note, optional
	
	public static void main(String[] args){
		CreateProgram program = new CreateProgram();
		program.name = "API Test Program";
		program.folder = new JsonObject().add("id", 1035).add("type", "Folder");
		program.description = "Sample API Program";
		program.type = "Default";
		program.channel = "Email Blast";
		
		String result = program.postData();
		System.out.println(result);
	}
	//Make Request
	public String postData(){
		String result = null;
		try {
			String endpoint = marketoInstance + "/rest/asset/v1/programs.json";
			String body = bodyBuilder();
			URL url = new URL(endpoint);
			System.out.println(url);
			
			
			HttpsURLConnection urlConn = (HttpsURLConnection) url.openConnection();
			urlConn.setRequestMethod("POST");
            urlConn.setRequestProperty("accept", "text/json");
            urlConn.setRequestProperty("Content-Type", "application/json");
            urlConn.setRequestProperty("Authorization", "Bearer " + getToken());
            urlConn.setDoOutput(true);
            
            Writer wr = new OutputStreamWriter(urlConn.getOutputStream());
            wr.write(body);
            
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
	
	public String bodyBuilder(){
		StringBuilder body = new StringBuilder();
		body.append("name=" + name);
		body.append("&folder=" + folder.toString());
		body.append("&description=" + description);
		body.append("&type=" + type);
		body.append("&channel=" + channel);
		if(tags != null){
			body.append("&tags=" + tags.toString());
		}
		if(costs != null){
			body.append("&costs=" + costs.toString());
		}
		System.out.println(body);
		return body.toString();
	}
	public String getToken(){
		String token = null;
		try {
			URL url = new URL(idEndpoint);
			HttpsURLConnection urlConn = (HttpsURLConnection) url.openConnection();
			urlConn.setRequestMethod("GET");
            urlConn.setRequestProperty("accept", "application/json");
            int responseCode = urlConn.getResponseCode();
            System.out.println(idEndpoint);
            if (responseCode == 200) {
                InputStream inStream = urlConn.getInputStream();
                Reader reader = new InputStreamReader(inStream);
                JsonObject jsonObject = JsonObject.readFrom(reader);
                System.out.println(jsonObject);
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