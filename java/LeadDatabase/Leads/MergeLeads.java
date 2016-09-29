/*
   MergeLeads.java

   Marketo REST API Sample Code
   Copyright (C) 2016 Marketo, Inc.

   This software may be modified and distributed under the terms
   of the MIT license.  See the LICENSE file for details.
*/
package dev.marketo.samples.Leads;

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

public class MergeLeads {
	public String marketoInstance = ;//Replace this with the host from Admin Web Services
	public String marketoIdURL = marketoInstance + "/identity";	
	public String clientId = ;	//Obtain from your Custom Service in Admin>Launchpoint
	public String clientSecret = ;	//Obtain from your Custom Service in Admin>Launchpoint
	public String idEndpoint = marketoIdURL + "/oauth/token?grant_type=client_credentials&client_id=" + clientId + "&client_secret=" + clientSecret;
	public int[] leadIds; //one or more lead IDs of losing records, required
	public int winningId; //the winning Lead ID
	public boolean mergeInCrm; //Optional to force a merge in instances with a native CRM sync
	
	public static void main(String[] args){
		MergeLeads mergeLeads = new MergeLeads();
		mergeLeads.winningId = 1;
		mergeLeads.leadIds =  new int[]{10000, 20000, 30000};
		String result = mergeLeads.postData();
		System.out.println(result);
	}
	//Make Request
	public String postData(){
		String result = null;
		try {
			//Assemble the URL to retrieve data from
			StringBuilder endpoint = new StringBuilder(marketoInstance + "/rest/v1/leads/" + winningId + "/merge.json?access_token=" + getToken());
			if (leadIds.length > 1){//if more than 1 id, make a comma separated string of IDs
				endpoint.append("&leadIds=" + csvString(leadIds));
			} else{
				endpoint.append("&leadId=" + leadIds[0]);
			}
			//append optional parameters
			if (mergeInCrm){
				endpoint.append("&mergeInCrm=true");
			}
			URL url = new URL(endpoint.toString());
			HttpsURLConnection urlConn = (HttpsURLConnection) url.openConnection();
			urlConn.setRequestMethod("POST");
			urlConn.setRequestProperty("Content-type", "application/json");//"application/json" content-type is required.
            urlConn.setRequestProperty("accept", "text/json");
            //urlConn.setDoOutput(true);
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
	//takes an array of fields as strings and concatenates them with alternating commas to use in a URL param
	private String csvString(int[] fields) {
		StringBuilder fieldCsv = new StringBuilder();
    	for (int i = 0; i < fields.length; i++){
    		fieldCsv.append(fields[i]);
    		if (i + 1 != fields.length){
    			fieldCsv.append(",");
    		}
    	}
		return fieldCsv.toString();
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
                throw new Exception("Status: " + responseCode);
            }
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}catch (Exception e) {
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
