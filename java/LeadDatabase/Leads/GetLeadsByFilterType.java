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

public class MultipleLeads {
	public String marketoInstance = ;//Replace this with the host from Admin Web Services
	public String marketoIdURL = marketoInstance + "/identity";	
	public String clientId = ;	//Obtain from your Custom Service in Admin>Launchpoint
	public String clientSecret = ;	//Obtain from your Custom Service in Admin>Launchpoint
	public String idEndpoint = marketoIdURL + "/oauth/token?grant_type=client_credentials&client_id=" + clientId + "&client_secret=" + clientSecret;
	
	public Integer batchSize = null;
	public String nextPageToken = null;
	public String[] fields = null;
	public String filterType; //required, must match the REST API name of a Marketo field
	public String[] filterValues;
	
	public static void main(String[] args) {
		MultipleLeads leads = new MultipleLeads();
		leads.filterType = "email"; //add a filterType, email
		leads.filterValues = new String[]{"kelkington@marketo.com"}; //add an email address to search for
		String result = leads.getData();
		System.out.println(result);
		}
    //Make request
    private String getData() {
        String data = null;
        try {
        	//Assemble the URL to retrieve data from
        	StringBuilder endpoint = new StringBuilder(marketoInstance + "/rest/v1/leads.json?access_token=" + getToken() + "&filterType=" + filterType + "&filterValues=" + csvString(filterValues));
        	//append optional params
            if (batchSize != null){
            	endpoint.append("&batchSize=" + batchSize);
            }
            if (nextPageToken != null){
            	endpoint.append("&nextPageToken=" + nextPageToken);
            }
            if (fields != null){
            	endpoint.append("&fields=" + csvString(fields));
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
	//takes an array of fields as strings and concatenates them with alternating commas to use in a URL param
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
