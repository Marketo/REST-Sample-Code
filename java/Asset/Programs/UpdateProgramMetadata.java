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
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;

//the Java sample code on dev.marketo.com uses the minimal-json package
//minimal-json provides easy and fast representations of JSON
//for more information check out https://github.com/ralfstx/minimal-json

public class UpdateProgram {
	public String marketoInstance = "https://299-BYM-827.mktorest.com";//Replace this with the host from Admin Web Services
	public String marketoIdURL = marketoInstance + "/identity";	
	public String clientId = "b417d98f-9289-47d1-a61f-db141bf0267f";	//Obtain from your Custom Service in Admin>Launchpoint
	public String clientSecret = "0DipOvz4h2wP1ANeVjlfwMvECJpo0ZYc";	//Obtain from your Custom Service in Admin>Launchpoint
	public String idEndpoint = marketoIdURL + "/oauth/token?grant_type=client_credentials&client_id=" + clientId + "&client_secret=" + clientSecret;
	
	public int id;//id of program to update, required
	public String description;//new description for the program
	public String name;//new name for the program
	public JsonArray tags;//tags to upsert for program, may have multiple tags as JsonObjects with members tagType and tagValue
	public JsonArray costs;//array of cost JsonObjects with members startDate, cost, and note
	public boolean costsDestructiveUpdate;//defaults to false, if set to true, will clear existing costs and replace them with the included costs
	public Date startDate;
	public Date endDate;
	
	
	public static void main(String[] args){
		UpdateProgram program = new UpdateProgram();
		program.id = 1073;
		program.name = "Updated Program Name";
		program.description = "Updated with Java";
		String result  = program.postData();
		System.out.println(result);
	}
	//Make Request
	public String postData(){
		String result = null;
		try {
			String endpoint = marketoInstance + "/rest/asset/v1/program/" + id + ".json";
			String body = bodyBuilder();
			URL url = new URL(endpoint);
			
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
		if(description != null){
			body.append("&description=" + description);
		}
		if (name != null){
			body.append("&name=" + name);
		}
		if (tags != null){
			body.append("&tags=" + tags.toString());
		}
		if (costs != null){
			body.append("&costs=" + costs.toString());
		}
		if (costsDestructiveUpdate){
			body.append("&costsDesctructiveUpdate=" + Boolean.toString(costsDestructiveUpdate));
		}
		if (startDate != null){
			body.append("&startDate=" + startDate.toString());
		}
		if (endDate != null){
			body.append("&endDate=" + endDate.toString());
		}
		System.out.println(body.toString());
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