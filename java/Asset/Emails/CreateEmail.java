/*
   Marketo REST API Sample Code
   Copyright (C) 2016 Marketo, Inc.

   This software may be modified and distributed under the terms
   of the MIT license.  See the LICENSE file for details.
*/
package dev.marketo.samples.Emails;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.NoSuchElementException;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

import com.eclipsesource.json.JsonObject;

//the Java sample code on dev.marketo.com uses the minimal-json package
//minimal-json provides easy and fast representations of JSON
//for more information check out https://github.com/ralfstx/minimal-json

public class CreateEmail {
	public String marketoInstance = ;//Replace this with the host from Admin Web Services
	public String marketoIdURL = marketoInstance + "/identity";	
	public String clientId = ;	//Obtain from your Custom Service in Admin>Launchpoint
	public String clientSecret = ;	//Obtain from your Custom Service in Admin>Launchpoint
	public String idEndpoint = marketoIdURL + "/oauth/token?grant_type=client_credentials&client_id=" + clientId + "&client_secret=" + clientSecret;
	public String name; //name of created email
	public JsonObject folder; //ID and type of folder
	public int template; //ID of the parent template
	public String description;//description of email
	public String subject; //subject line
	public String fromName; //from name
	public String fromEmail; //from email address
	public String replyEmail; //reply to email
	public Boolean operational; //operational flag for email
	
	
	public static void main(String[] args){
		CreateEmail create = new CreateEmail();
		create.folder = new JsonObject().add("id", 1071).add("type", "Program");
		create.template = 1001;
		create.name = "Test EmailTest";
		String result = create.postData();
		System.out.println(result);
	}
	public String postData(){
		String result = null;
		try {
			//assemble the URL
			StringBuilder endpoint = new StringBuilder(marketoInstance + "/rest/asset/v1/emails.json?access_token=" + getToken()
					+ "&name=" + name + "&folder=" + folder.toString() + "&template=" + String.valueOf(template) );
			//append optional parameters
			if(description != null){
				endpoint.append("&description=" + description);
			}
			if(subject != null){
				endpoint.append("&subject=" + subject);
			}
			if(fromName != null){
				endpoint.append("&fromName=" + fromName);
			}
			if(fromEmail != null){
				endpoint.append("&fromEmail=" + fromEmail);
			}
			if(replyEmail != null){
				endpoint.append("&replyEmail=" + replyEmail);
			}
			if(operational != null){
				endpoint.append("&operational=" + operational);
			}
			URL url = new URL(endpoint.toString());
			HttpsURLConnection urlConn = (HttpsURLConnection) url.openConnection();
			urlConn.setRequestMethod("POST");
			urlConn.setRequestProperty("Content-type", "application/json");//"application/json" content-type is required.
            urlConn.setRequestProperty("accept", "text/json");
            urlConn.setDoOutput(true);
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
