/*
   Marketo REST API Sample Code
   Copyright (C) 2016 Marketo, Inc.

   This software may be modified and distributed under the terms
   of the MIT license.  See the LICENSE file for details.
*/
package dev.marketo.samples.Lists;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
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

public class ImportLeads {
	public String marketoInstance = ;//Replace this with the host from Admin Web Services
	public String marketoIdURL = marketoInstance + "/identity";	
	public String clientId = ;	//Obtain from your Custom Service in Admin>Launchpoint
	public String clientSecret = ;	//Obtain from your Custom Service in Admin>Launchpoint
	public String idEndpoint = marketoIdURL + "/oauth/token?grant_type=client_credentials&client_id=" + clientId + "&client_secret=" + clientSecret;
	public String format;//format of file included
	public String lookupField;//field to deduplicate by, defaults to email
	public Integer listId; //optional id of list to import to
	public String partitionName; //partition to import to, required if multiple partions exist
	public String filePath; //path of the file to retrieve data from
	
	public static void main(String[] args){
		ImportLeads importLeads = new ImportLeads();
		importLeads.filePath = "C:\\Users\\kelkington\\Documents\\mktoseedlist.csv";
		importLeads.format = "csv";
		String result = importLeads.postData();
		System.out.println(result);
	}
	public String postData(){
		String result = null;
		String boundary =  String.valueOf(System.currentTimeMillis());
		try {
			//Create the endpoint and then append all optional and required parameters
			StringBuilder endpoint = new StringBuilder(marketoInstance + "/bulk/v1/leads.json?access_token=" + getToken() + "&format=" + format);
			//endpoint.append("&file=" + file);
			if (lookupField != null){
				endpoint.append("&lookupField=" + lookupField);
			}
			if (listId != null){
				endpoint.append("&listId=" + listId);
			}
			if(partitionName != null){
				endpoint.append("&partitionName=" + partitionName);
			}
			
			System.out.println(endpoint);
			URL url = new URL(endpoint.toString());
			HttpsURLConnection urlConn = (HttpsURLConnection) url.openConnection();
			urlConn.setRequestMethod("POST");
			urlConn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            urlConn.setRequestProperty("accept", "text/json");
            urlConn.setDoOutput(true);
            String requestBody = buildRequest(); //build the request body
			PrintWriter wr = new PrintWriter(new OutputStreamWriter(urlConn.getOutputStream()));
			//Format and append the multipart data to the writer
			addMultipart(boundary, requestBody, wr);
			closeMultipart(boundary, wr);
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
	//adds the file data as part of a multipart request into the Writer
	private void addMultipart(String boundary, String requestBody,
			PrintWriter wr) {
		wr.append("--" + boundary + "\r\n");
		wr.append("Content-Disposition: form-data; name=\"file\";filename=\"" + filePath + "\";\r\n");
		wr.append("Content-type: text/plain; charset=\"utf-8\"\r\n");
		wr.append("Content-Transfer-Encoding: text/plain\r\n");
		wr.append("MIME-Version: 1.0\r\n");
		wr.append("\r\n");
		wr.append(requestBody);
		wr.append("\r\n");
	}
	private void closeMultipart(String boundary, PrintWriter wr) {
		wr.append("--" + boundary);
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
	//read from the file in filepath and return the read data
	private String buildRequest(){
		String fileOutPut = null;
		try {
			FileReader fr = new FileReader(filePath);
			BufferedReader br = new BufferedReader(fr);
			
			char[] arr = new char[8 * 4096];
		    StringBuilder buffer = new StringBuilder();
		    int numCharsRead;
		    while ((numCharsRead = br.read(arr, 0, arr.length)) != -1) {
		        buffer.append(arr, 0, numCharsRead);
		        
		    }
		    fileOutPut = buffer.toString();
		    br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch(IOException e){
			e.printStackTrace();
		}
		
		return fileOutPut;
	}
	
    private String convertStreamToString(InputStream inputStream) {

        try {
            return new Scanner(inputStream).useDelimiter("\\A").next();
        } catch (NoSuchElementException e) {
            return "";
        }
    }
}
