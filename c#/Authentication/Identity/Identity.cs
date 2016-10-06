using System;
using System.Net;
using System.IO;
using Newtonsoft.Json; // Json.NET (http://www.newtonsoft.com/json)

namespace Marketo
{
    public class Identity
    {
        public static void Main(string[] args)
        {
            String host = "CHANGE ME";
            String clientId = "CHANGE ME";
            String clientSecret = "CHANGE ME";

            String url = string.Format("https://{0}/identity/oauth/token?grant_type=client_credentials&client_id={1}&client_secret={2}", host, clientId, clientSecret);
            String accessToken = Identity.getAccessToken(url);
            String message = String.Format("Access Token: {0}", accessToken);
            Console.WriteLine(message);
        }

        public static String getAccessToken(String url)
        {
            HttpWebRequest request = (HttpWebRequest)WebRequest.Create(url);
            request.ContentType = "application/json";
            HttpWebResponse response = (HttpWebResponse)request.GetResponse();
            Stream resStream = response.GetResponseStream();
            StreamReader reader = new StreamReader(resStream);
            String json = reader.ReadToEnd();
            dynamic parsedJson = JsonConvert.DeserializeObject(json);
            return parsedJson.access_token;
        }
    }
}


