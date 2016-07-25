// A simple Java Toolkit for JSON (https://code.google.com/p/json-simple/)
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.TimeZone;

public class Activities {
    // Replace marketoInstance and accessToken
    public String marketoInstance = "https://AAA-BBB-CCC.mktorest.com";
    public String accessToken = "cdf01657-110d-4155-99a7-f986b2ff13a0:int";

    public static void main(String[] args) {
        String lead = new Activities().addActivity();
        System.exit(0);
    }

    // Call Add Custom Activities API endpoint
    public String addActivity() {
        String url = marketoInstance + "/rest/v1/activities/external.json?access_token=" + accessToken;
        System.out.println(url);
        String result = addData(url);
        return result;
    }

    private String addData(String endpoint) {
        String data = null;
        try {
            // Construct request payload
            JSONObject attrObj=new JSONObject();
            attrObj.put("name", "URL");
            attrObj.put("value", "http://www.nvidia.com/game-giveaway");

            JSONArray attrArray = new JSONArray();
            attrArray.add(attrObj);

            TimeZone tz = TimeZone.getTimeZone("UTC");
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
            df.setTimeZone(tz);
            String dateAsISO = df.format(new Date());

            // Required attributes
            JSONObject obj=new JSONObject();
            obj.put("leadId", "1001");
            obj.put("activityDate", dateAsISO);
            obj.put("activityTypeId", "1001");
            obj.put("primaryAttributeValue","Game Giveaway");
            obj.put("attributes", attrArray);
            System.out.println(obj);

            // Make request
            URL url = new URL(endpoint);
            HttpsURLConnection urlConn = (HttpsURLConnection) url.openConnection();
            urlConn.setRequestMethod("POST");
            urlConn.setAllowUserInteraction(false);
            urlConn.setDoOutput(true);
            urlConn.setRequestProperty("Content-type", "application/json");
            urlConn.setRequestProperty("accept", "application/json");
            urlConn.connect();
            OutputStream os = urlConn.getOutputStream();
            os.write(obj.toJSONString().getBytes());
            os.close();

            // Inspect response
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
