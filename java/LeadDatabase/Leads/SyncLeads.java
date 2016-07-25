package dev.marketo.samples.Leads;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.NoSuchElementException;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;

//the Java sample code on dev.marketo.com uses the minimal-json package
//minimal-json provides easy and fast representations of JSON
//for more information check out https://github.com/ralfstx/minimal-json

public class UpsertLeads {
	public String marketoInstance = ;//Replace this with the host from Admin Web Services
	public String marketoIdURL = marketoInstance + "/identity";	
	public String clientId = ;	//Obtain from your Custom Service in Admin>Launchpoint
	public String clientSecret = ;	//Obtain from your Custom Service in Admin>Launchpoint
	public String idEndpoint = marketoIdURL + "/oauth/token?grant_type=client_credentials&client_id=" + clientId + "&client_secret=" + clientSecret;
	public String lookupField; //defines the field to deduplicate off of
	public String action; //createOnly, updateOnly, createOrUpdate, or createDuplicate
	public JsonObject[] leads; //a set of leads as JsonObjects, has to be non-empty
	
	public static void main(String[] args){
		//Create two JsonObjects representing leads
		JsonObject lead1 = new JsonObject().add("email", "kelkington@marketo.com");
		JsonObject lead2 = new JsonObject().add("email", "mkto@mkto.mkto");
		UpsertLeads upsertLeads = new UpsertLeads();
		upsertLeads.leads = new JsonObject[]{lead1, lead2};// add out leads 
		upsertLeads.lookupField = "email"; //set the lookupField
		String result = upsertLeads.postData();
		System.out.println(result);
	}
	//submits the request to create/update/upsert leads to the leads.json endpoint
	public String postData(){
		String result = null;
		try {
			JsonObject requestBody = buildRequest();
			String endpoint = marketoInstance + "/rest/v1/leads.json?access_token=" + getToken();
			URL url = new URL(endpoint.toString());
			HttpsURLConnection urlConn = (HttpsURLConnection) url.openConnection();
			urlConn.setRequestMethod("POST");
			urlConn.setRequestProperty("Content-type", "application/json");//"application/json" content-type is required.
            urlConn.setRequestProperty("accept", "text/json");
            urlConn.setDoOutput(true);
			OutputStreamWriter wr = new OutputStreamWriter(urlConn.getOutputStream());
			wr.write(requestBody.toString());
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
	private JsonObject buildRequest() {
		JsonObject requestBody = new JsonObject(); //JsonObject container for the request body
		//add optional params
		if (action != null){
			requestBody.add("action", action);
		}
		if (lookupField != null){
			requestBody.add("lookupField", lookupField);
		}
		//assemble the input from leads into a JsonArray
		JsonArray input = new JsonArray();
		int i;
		for (i = 0; i < leads.length; i++){
			input.add(leads[i]);
		}
		//add our array to the input parameter of the body
		requestBody.add("input", input);
		return requestBody;
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
