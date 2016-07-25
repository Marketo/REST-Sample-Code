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

public class Leads {
	public String marketoInstance = ;//Replace this with the host from Admin Web Services
	public String marketoIdURL = marketoInstance + "/identity";	
	public String clientId = ;	//Obtain from your Custom Service in Admin>Launchpoint
	public String clientSecret = ;	//Obtain from your Custom Service in Admin>Launchpoint
	public String idEndpoint = marketoIdURL + "/oauth/token?grant_type=client_credentials&client_id=" + clientId + "&client_secret=" + clientSecret;
	public int id; //id of the lead being retrieve
	public String[] fields; //optional list of fields to retrieve
	
	public static void main(String[] args) {
		Leads lead = new Leads();
		lead.id = 1;
		String result = lead.getData();
		System.out.println(result);
    }
    //Make request
    private String getData() {
        String data = null;
        try {
        	//Assemble the URL to retrieve data from
        	StringBuilder endpoint = new StringBuilder(marketoInstance + "/rest/v1/lead/" + id + ".json?access_token=" + getToken());
        	//append the fields parameter if included
        	if (fields != null){
        		endpoint.append("&fields=" + csvString(fields));
        	}
            URL url = new URL(endpoint.toString());
            HttpsURLConnection urlConn = (HttpsURLConnection) url.openConnection();
            urlConn.setRequestMethod("GET");
            urlConn.setDoOutput(true);
            urlConn.setRequestProperty("accept", "text/json");
            int responseCode = urlConn.getResponseCode();
            if (responseCode == 200) {
                InputStream inStream = urlConn.getInputStream();
                data = convertStreamToString(inStream);
            } else {
                System.out.println(responseCode);
                data = "Status:" + responseCode;
            }
        } catch (MalformedURLException e) {
            System.out.println("URL not valid.");
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
            e.printStackTrace();
        }

        return data;
    }

    private String convertStreamToString(InputStream inputStream) {

        try {
            return new Scanner(inputStream).useDelimiter("\\A").next();
        } catch (NoSuchElementException e) {
            return "";
        }
    }
    //takes an array of fields as strings and concatenates them with alternating commas to use in fields URL param
	private String csvString(String[] fields) {
		StringBuilder fieldCsv = new StringBuilder();
    	for (int i = 0; i < fields.length; i++){
    		fieldCsv.append(fields[i]);
    		if (i + 1 != fields.length){
    			fieldCsv.append(",");
    		}
    	}
		return fieldCsv.toString();
	}
	//Authenticates with Marketo and returns the access token as a string
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

}
