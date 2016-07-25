package dev.marketo.samples.Files;

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

public class CreateFile {
	public String marketoInstance = ;//Replace this with the host from Admin Web Services
	public String marketoIdURL = marketoInstance + "/identity";	
	public String clientId = ;	//Obtain from your Custom Service in Admin>Launchpoint
	public String clientSecret = ;	//Obtain from your Custom Service in Admin>Launchpoint
	public String idEndpoint = marketoIdURL + "/oauth/token?grant_type=client_credentials&client_id=" + clientId + "&client_secret=" + clientSecret;
	public String file; //file content to submit
	public String name; //name of file
	public JsonObject folder; //Json of parent folder with two members, id and type(Folder or Program)
	public String description; //optional description
	public Boolean insertOnly; //option to only submit file if there is no file matching the submitted name
	//a filename is required in the content-disposition for file param of request body
	public String filePath = "C:\\Users\\kelkington\\Documents\\mktoseedlist.csv";
	
	public static void main(String[] args){
		CreateFile create = new CreateFile();
		create.file = create.readFile(create.filePath);
		create.folder = new JsonObject().add("id", 5565).add("type", "Folder");
		create.name = "newFile.txt";
		String result = create.postData();
		System.out.println(result);
	}
	public String postData(){
		String result = null;
		String boundary =  "mktoBoundary" + String.valueOf(System.currentTimeMillis());
		try {
			//Create the endpoint and then append all optional and required parameters
			StringBuilder endpoint = new StringBuilder(marketoInstance + "/rest/asset/v1/files.json?access_token=" + getToken());
			URL url = new URL(endpoint.toString());
			HttpsURLConnection urlConn = (HttpsURLConnection) url.openConnection();
			urlConn.setRequestMethod("POST");
			urlConn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            urlConn.setRequestProperty("accept", "text/json");
            urlConn.setDoOutput(true);
			PrintWriter wr = new PrintWriter(new OutputStreamWriter(urlConn.getOutputStream()));
			//Format and append the multipart data to the writer
            addMultipart(boundary, file, wr, "file", "text/plain");
			addMultipart(boundary, name, wr, "name");
			addMultipart(boundary, folder.toString(), wr, "folder");
			if (description != null){
				addMultipart(boundary, description, wr, "description");
			}
			if(insertOnly != null){
				addMultipart(boundary, insertOnly.toString(), wr, "insertOnly");
			}
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
	private void addMultipart(String boundary, String requestBody,
			PrintWriter wr, String paramName) {
		wr.append("--" + boundary + "\r\n");
		wr.append("Content-Disposition: form-data; name=\"" + paramName + "\"");
		wr.append("\r\n");
		wr.append("\r\n");
		wr.append(requestBody);
		wr.append("\r\n");
	}
	private void addMultipart(String boundary, String requestBody,
			PrintWriter wr, String paramName, String contentType) {
		wr.append("--" + boundary + "\r\n");
		wr.append("Content-Disposition: form-data; name=\"" + paramName + "\"; filename=\"" + filePath + "\"");
		wr.append("\r\n");
		wr.append("Content-type: " + contentType + "; charset=\"utf-8\"\r\n");
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
    private String convertStreamToString(InputStream inputStream) {

        try {
            return new Scanner(inputStream).useDelimiter("\\A").next();
        } catch (NoSuchElementException e) {
            return "";
        }
    }
}
