package dev.marketo.samples.Activities;

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

public class Activities {
	public String marketoInstance = ;//Replace this with the host from Admin Web Services
	public String marketoIdURL = marketoInstance + "/identity";	
	public String clientId = ;	//Obtain from your Custom Service in Admin>Launchpoint
	public String clientSecret = ;	//Obtain from your Custom Service in Admin>Launchpoint
	public String idEndpoint = marketoIdURL + "/oauth/token?grant_type=client_credentials&client_id=" + clientId + "&client_secret=" + clientSecret;
	public int[] activityTypeIds; //one or more activity type to retrieve
	public String nextPageToken; //paging token retrieved with get paging token
	public Integer batchSize; //max 300, default 300
	public Integer listId; //will retrieve only for leads present in this list, if set
	
	public static void main(String[] args){
		Activities activities = new Activities();
		activities.activityTypeIds = new int[]{1,2};
		activities.nextPageToken = "ZX7GSH7IIOPV4SYG7GUREAQZXSFG5F6FHDEIXVRDWFYB6IULXHLA====";
		String result = activities.getData();
		System.out.println(result);
	}
	//make request
	private String getData() {
        String data = null;
        try {
        	//assemble the URL
        	StringBuilder endpoint = new StringBuilder(marketoInstance + "/rest/v1/activities.json?access_token=" + getToken());
        	endpoint.append("&activityTypeIds=" + csvString(activityTypeIds));
        	endpoint.append("&nextPageToken=" + nextPageToken);
        	//add optional params
        	if (batchSize != null && batchSize > 0 && batchSize <=300){
        		endpoint.append("&batchSize=" +batchSize);
        	}
        	if (listId != null){
        		endpoint.append("&listId=" + listId);
        	}
            URL url = new URL(endpoint.toString());
            HttpsURLConnection urlConn = (HttpsURLConnection) url.openConnection();
            urlConn.setRequestMethod("GET");
            urlConn.setRequestProperty("accept", "text/json");
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
    }
	// takes an array of fields as strings and concatenates them with
	// alternating commas to use in a URL param
	private String csvString(int[] fields) {
		StringBuilder fieldCsv = new StringBuilder();
		for (int i = 0; i < fields.length; i++) {
			fieldCsv.append(fields[i]);
			if (i + 1 != fields.length) {
				fieldCsv.append(",");
			}
		}
		return fieldCsv.toString();
	}

	public String getToken() {
		String token = null;
		try {
			URL url = new URL(idEndpoint);
			HttpsURLConnection urlConn = (HttpsURLConnection) url
					.openConnection();
			urlConn.setRequestMethod("GET");
			urlConn.setRequestProperty("accept", "application/json");
			int responseCode = urlConn.getResponseCode();
			if (responseCode == 200) {
				InputStream inStream = urlConn.getInputStream();
				Reader reader = new InputStreamReader(inStream);
				JsonObject jsonObject = JsonObject.readFrom(reader);
				token = jsonObject.get("access_token").asString();
			} else {
				throw new IOException("Status: " + responseCode);
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
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
