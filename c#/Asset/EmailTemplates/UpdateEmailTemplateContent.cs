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

    class UpdateEmailTemplateContent
    {
        private String host = "CHANGE ME"; //host of your marketo instance, https://AAA-BBB-CCC.mktorest.com
        private String clientId = "CHANGE ME"; //clientId from admin > Launchpoint
        private String clientSecret = "CHANGE ME"; //clientSecret from admin > Launchpoint
        public int id;//id of template to update
        public String content;//content to update

        /*
        public static void Main(string[] args)
        {
            var content = new UpdateEmailTemplateContent();
            content.id = 1001;
            content.content = File.ReadAllText("C:\\Users\\kelkington\\Desktop\\Box Sync\\Dev Dot Marketo\\template.html");
            String result = content.postData();
            Console.Write(result);
        }
        */

        public String postData()
        {
            //Assemble the URL
            String url = host + "/rest/asset/v1/emailTemplate/" + id + "/content.json?access_token=" + getToken();
            HttpWebRequest request = (HttpWebRequest)WebRequest.Create(url);
            request.Method = "POST";
            String boundary = "--mktoBoundary" + DateTime.Now.Ticks.ToString("x");
            request.ContentType = "multipart/form-data; boundary=" + boundary;
            request.Accept = "application/json";
            StreamWriter wr = new StreamWriter(request.GetRequestStream());
            AddMultipartFile(wr, boundary, "content", content, "template.html");
            wr.Write("--" + boundary + "--");
            wr.Flush();
            HttpWebResponse response = (HttpWebResponse)request.GetResponse();
            Stream resStream = response.GetResponseStream();
            StreamReader reader = new StreamReader(resStream);
            return reader.ReadToEnd();
        }
        private void AddMultipartFile(TextWriter wr, String boundary, String paramName, String content, String filename)
        {
            wr.Write("--" + boundary + "\r\n");
            wr.Write("Content-Disposition: form-data; name=\"" + paramName + "\"; filename=\"" + filename + "\"\r\n");
            wr.Write("Content-type: text/html; charset=\"utf-8\"\r\n");
            wr.Write("\r\n");
            wr.Write(content + "\r\n");
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
