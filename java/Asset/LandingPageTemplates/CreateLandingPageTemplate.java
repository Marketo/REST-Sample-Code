/*
   CreateLandingPageTemplate.java

   Marketo REST API Sample Code
   Copyright (C) 2016 Marketo, Inc.

   This software may be modified and distributed under the terms
   of the MIT license.  See the LICENSE file for details.
*/
package dev.marketo.samples.LandingPageTemplates;

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

public class CreateLandingPageTemplate {
	public String marketoInstance = "CHANGE ME";//Replace this with the host from Admin Web Services
	public String marketoIdURL = marketoInstance + "/identity";	
	public String clientId = "CHANGE ME";	//Obtain from your Custom Service in Admin>Launchpoint
	public String clientSecret = "CHANGE ME";	//Obtain from your Custom Service in Admin>Launchpoint
	public String idEndpoint = marketoIdURL + "/oauth/token?grant_type=client_credentials&client_id=" + clientId + "&client_secret=" + clientSecret;
	public String file; //path of the file to retrieve content from
	public JsonObject folder;//folder with id and type(Folder or Program)
	public String name;//name of new template
	
	public static void main(String[] args){
		CreateLandingPageTemplate create = new CreateLandingPageTemplate();
		create.folder = new JsonObject();
		create.folder.add("id", 12);
		create.folder.add("type", "Folder");
		create.name = "New LP Template";
		create.file = "C:\\LandingPageContent\\template.html";
		String result = create.postData();
		System.out.println(result);
	}
	//Make Request
	public String postData(){
		String result = null;
		String boundary =  "mktoBoundary" + String.valueOf(System.currentTimeMillis());
		try {
			//Read target file
			String requestBody = readFile(file);
			//Assemble the URL
			String endpoint = marketoInstance + "/rest/asset/v1/landingPageTemplates.json?access_token=" + getToken();
			System.out.println(endpoint);
			URL url = new URL(endpoint.toString());
			HttpsURLConnection urlConn = (HttpsURLConnection) url.openConnection();
			urlConn.setRequestMethod("POST");
			urlConn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            urlConn.setRequestProperty("accept", "text/json");
            urlConn.setDoOutput(true);
			PrintWriter wr = new PrintWriter(new OutputStreamWriter(urlConn.getOutputStream()));
			//format content as multpart and insert into output stream
			addMultipart(boundary, requestBody, wr, "content", "text/html");
			addMultipart(boundary, folder.toString(), wr, "folder");
			addMultipart(boundary, name, wr, "name");
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
	//Add content as multipart form-data
	private void addMultipart(String boundary, String requestBody,
			PrintWriter wr, String paramName, String contentType) {
		wr.append("--" + boundary + "\r\n");
		wr.append("Content-Disposition: form-data; name=\"" + paramName + "\"; filename=\"" + file + "\"");
		wr.append("\r\n");
		wr.append("Content-type: " + contentType + "; charset=\"utf-8\"\r\n");
		wr.append("\r\n");
		wr.append(requestBody);
		wr.append("\r\n");
	}
	private void addMultipart(String boundary, String requestBody,
			PrintWriter wr, String paramName) {
		wr.append("--" + boundary + "\r\n");
		wr.append("Content-Disposition: form-data; name=\"" + paramName + "\"");
		wr.append("\r\n");
		wr.append("Content-type: text/plain; charset=\"utf-8\"\r\n");
		wr.append("\r\n");
		wr.append(requestBody);
		wr.append("\r\n");
	}
	//close multipart content
	private void closeMultipart(String boundary, PrintWriter wr) {
		wr.append("--" + boundary);
	}
	private String readFile(String filePath){
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
