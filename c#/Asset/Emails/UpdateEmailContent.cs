using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Net;
using System.IO;
using System.Collections;
using Newtonsoft.Json;
using System.Web;

namespace Samples
{

    class UpdateEmailContent
    {
        private String host = "CHANGE ME"; //host of your marketo instance, https://AAA-BBB-CCC.mktorest.com
        private String clientId = "CHANGE ME"; //clientId from admin > Launchpoint
        private String clientSecret = "CHANGE ME"; //clientSecret from admin > Launchpoint
        public int id;//id of email to update
        //see docs for param definitions
        public Dictionary<string, string> subject;
        public Dictionary<string, string> fromEmail;
        public Dictionary<string, string> fromName;
        public Dictionary<string, string> replyTO;

        /*
        public static void Main(string[] args)
        {
            var content = new UpdateEmailContent();
            content.id = 1211;
            var subject = new Dictionary<string, string>();
            String result = content.postData();
            Console.Write(result);
        }
        */

        public String postData()
        {
            //Assemble the URL
            String url = host + "/rest/asset/v1/email/" + id + "/content.json?access_token=" + getToken();
            HttpWebRequest request = (HttpWebRequest)WebRequest.Create(url);
            request.Method = "POST";
            request.ContentType = "application/x-www-form-urlencoded";
            request.Accept = "application/json";
            StreamWriter wr = new StreamWriter(request.GetRequestStream());
            wr.Write(bodyBuilder());
            wr.Flush();
            HttpWebResponse response = (HttpWebResponse)request.GetResponse();
            Stream resStream = response.GetResponseStream();
            StreamReader reader = new StreamReader(resStream);
            return reader.ReadToEnd();
        }
        private String bodyBuilder()
        {
            var body = HttpUtility.ParseQueryString(string.Empty);
            if (subject != null)
            {
                body.Add("subject", JsonConvert.SerializeObject(subject));
            }
            if (fromName != null)
            {
                body.Add("fromName", JsonConvert.SerializeObject(fromName));
            }
            if (fromEmail != null)
            {
                body.Add("fromEmail", JsonConvert.SerializeObject(fromEmail));
            }
            if (replyTO != null)
            {
                body.Add("replyTO", JsonConvert.SerializeObject(replyTO));
            }
            return body.ToString();
        }
        private String getToken()
        {
            String url = host + "/identity/oauth/token?grant_type=client_credentials&client_id=" + clientId + "&client_secret=" + clientSecret;
            HttpWebRequest request = (HttpWebRequest)WebRequest.Create(url);
            request.ContentType = "application/json";
            HttpWebResponse response = (HttpWebResponse)request.GetResponse();
            Stream resStream = response.GetResponseStream();
            StreamReader reader = new StreamReader(resStream);
            String json = reader.ReadToEnd();
            //Dictionary<String, Object> dict = JavaScriptSerializer.DeserializeObject(reader.ReadToEnd);
            Dictionary<String, String> dict = JsonConvert.DeserializeObject<Dictionary<String, String>>(json);
            return dict["access_token"];
        }
    }
}
