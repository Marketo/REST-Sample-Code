package dev.marketo.samples.Programs;


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

public class BrowsePrograms{
	public String marketoInstance = "https://299-BYM-827.mktorest.com";//Replace this with the host from Admin Web Services
	public String marketoIdURL = marketoInstance + "/identity";	
	public String clientId = "b417d98f-9289-47d1-a61f-db141bf0267f";	//Obtain from your Custom Service in Admin>Launchpoint
	public String clientSecret = "0DipOvz4h2wP1ANeVjlfwMvECJpo0ZYc";	//Obtain from your Custom Service in Admin>Launchpoint
	public String idEndpoint = marketoIdURL + "/oauth/token?grant_type=client_credentials&client_id=" + clientId + "&client_secret=" + clientSecret;
	
	public String status;//status filter for programs
	public int offset;//integer offset for paging
	public int maxReturn;//number of results to return, default 20, max 200
	
	public static void main(String[] args){
		BrowsePrograms programs = new BrowsePrograms();
		programs.offset = 1;
		String result = programs.getData();
		System.out.println(result);
	}
	//Make Request
	public String getData() {
        String data = null;
        try {
        	//assemble the URL
        	StringBuilder endpoint = new StringBuilder(marketoInstance + "/rest/asset/v1/programs.json");
        	StringBuilder qs = new StringBuilder();
        	if(status != null || offset > 0 || maxReturn > 0){
	        	if(status != null){
	        		qs.append("status=" + status + "&");
	        	}
	        	if(offset > 0){
	        		qs.append("offset=" + offset + "&");
	        	}
	        	if(maxReturn > 0){
	        		qs.append("");
	        	}
        	}
        	if (qs.toString().length() > 0){
        		endpoint.append("?" + qs.toString());
        	}
            URL url = new URL(endpoint.toString());
            HttpsURLConnection urlConn = (HttpsURLConnection) url.openConnection();
            urlConn.setRequestMethod("GET");
            urlConn.setRequestProperty("accept", "text/json");
            urlConn.setRequestProperty("Authorization", "Bearer " + getToken());
            int responseCode = urlConn.getResponseCode();
            if (responseCode == 200) {
                InputStream inStream = urlConn.getInputStream();
                data = convertStreamToString(inStream);
            } else {
                data = "Status:" + responseCode;
            }
        } catch (MalformedURLException e) {
            System.out.println("URL not valid.");
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
            e.printStackTrace();
        }
        return data;
    }public String getToken(){
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