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

    class CloneEmail
    {
        private String host = "CHANGE ME"; //host of your marketo instance, https://AAA-BBB-CCC.mktorest.com
        private String clientId = "CHANGE ME"; //clientId from admin > Launchpoint
        private String clientSecret = "CHANGE ME"; //clientSecret from admin > Launchpoint
        public int id;//id of template to clone
        public String name;//name of new template, required
        public Dictionary<string, dynamic> folder;//dict with two members, id and type, must be Folder, required
        public String description;//optional description
        public Boolean isOperational;//set to true to make resulting email operational

        /*
        public static void Main(string[] args)
        {
            var email = new CloneEmail();
            email.id = 1001;
            email.name = "Cloned C# Example Email";
            email.folder = new Dictionary<string, dynamic>();
            email.folder.Add("id", 15);
            email.folder.Add("type", "Folder");
            String result = email.postData();
            Console.Write(result);
        }
        */

        public String postData()
        {
            //Assemble the URL
            String url = host + "/rest/asset/v1/email/" + id + "/clone.json?";
            HttpWebRequest request = (HttpWebRequest)WebRequest.Create(url);
            String requestBody = bodyBuilder();
            request.Method = "POST";
            request.ContentType = "application/x-www-form-urlencoded";
            request.Accept = "application/json";
            StreamWriter wr = new StreamWriter(request.GetRequestStream());
            wr.Write(requestBody);
            wr.Flush();
            HttpWebResponse response = (HttpWebResponse)request.GetResponse();
            Stream resStream = response.GetResponseStream();
            StreamReader reader = new StreamReader(resStream);
            return reader.ReadToEnd();
        }
        private String bodyBuilder()
        {
            var body = HttpUtility.ParseQueryString(string.Empty);
            body.Add("access_token", getToken());
            body.Add("name", name);
            body.Add("folder", JsonConvert.SerializeObject(folder));
            if (description != null)
            {
                body.Add("description", description);
            }
            if (isOperational != null)
            {
                body.Add("isOperational", isOperational.ToString());
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
