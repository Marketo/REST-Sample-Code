import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.NoSuchElementException;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.bind.DatatypeConverter;

public class Auth {
    //Replace marketoInstance, clientId, and clientSecret values
    public String marketoInstance = "https://AAA-BBB-CCC.mktorest.com/identity/oauth/token?grant_type=client_credentials";
    public String clientId = "99985d09-22a9-3jl2-84av-f5baae7c3a45";
    public String clientSecret = "tZPVrKiEmUDezE18yZfeaPlTJ2vKn2fw";

    public static void main(String[] args) {
        String lead = new Auth().getToken();
        System.exit(0);
    }

    //Build request URL to Get Lead API endpoint
    public String getToken() {
        String url = marketoInstance + "&client_id=" + clientId + "&client_secret=" + clientSecret;
        System.out.println(url);
        String result = getData(url);
        return result;
    }

    //Make request
    private String getData(String endpoint) {
        String data = null;
        try {
            URL url = new URL(endpoint);
            HttpsURLConnection urlConn = (HttpsURLConnection) url.openConnection();
            urlConn.setRequestMethod("GET");
            urlConn.setAllowUserInteraction(false);
            urlConn.setDoOutput(true);
            urlConn.setRequestProperty("Content-type", "application/json");
            urlConn.setRequestProperty("accept", "application/json");
            int responseCode = urlConn.getResponseCode();
            if (responseCode == 200) {
                System.out.println("Status: 200");
                InputStream inStream = urlConn.getInputStream();
                data = convertStreamToString(inStream);
                System.out.println(data);
            } else {
                System.out.println(responseCode);
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

    private String convertStreamToString(InputStream inputStream) {

        try {
            return new Scanner(inputStream).useDelimiter("\\A").next();
        } catch (NoSuchElementException e) {
            return "";
        }
    }
}