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

import com.eclipsesource.json.JsonObject;

//the Java sample code on dev.marketo.com uses the minimal-json package
//minimal-json provides easy and fast representations of JSON
//for more information check out https://github.com/ralfstx/minimal-json

public class CloneProgram {
	public String marketoInstance = "https://299-BYM-827.mktorest.com";//Replace this with the host from Admin Web Services
	public String marketoIdURL = marketoInstance + "/identity";	
	public String clientId = "b417d98f-9289-47d1-a61f-db141bf0267f";	//Obtain from your Custom Service in Admin>Launchpoint
	public String clientSecret = "0DipOvz4h2wP1ANeVjlfwMvECJpo0ZYc";	//Obtain from your Custom Service in Admin>Launchpoint
	public String idEndpoint = marketoIdURL + "/oauth/token?grant_type=client_credentials&client_id=" + clientId + "&client_secret=" + clientSecret;
	
	public int id;//id of source program to clone
	public String name;//name of resulting program
	public JsonObject folders;//parent folder with id and type
	public String programDescription;//description of resulting program
	
	public static void main(String[] args){
		CloneProgram program = new CloneProgram();
		program.id = 1056;
		program.name = "Cloned Program";
		program.folders = new JsonObject().add("id", 1001).add("type", "Folder");
		String result = program.postData();
		System.out.println(result);
	}
	//Make Request
	public String postData(){
		String result = null;
		try {
			String endpoint = marketoInstance + "/rest/asset/v1/program/" + id + "/clone.json";
			String body = bodyBuilder();
			URL url = new URL(endpoint);
			
			HttpsURLConnection urlConn = (HttpsURLConnection) url.openConnection();
			urlConn.setRequestMethod("POST");
            urlConn.setRequestProperty("accept", "text/json");
            urlConn.setRequestProperty("Content-Type", "application/x-www-url-formencoded");
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
		body.append("name=" + name + "&folders=" + folders.toString());
		if(programDescription != null){
			body.append("&programDescription=" + programDescription);
		}
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