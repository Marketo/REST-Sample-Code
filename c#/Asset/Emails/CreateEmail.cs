/*
   Marketo REST API Sample Code
   Copyright (C) 2016 Marketo, Inc.

   This software may be modified and distributed under the terms
   of the MIT license.  See the LICENSE file for details.
*/
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

    class CreateEmail
    {
        private String host = "CHANGE ME"; //host of your marketo instance, https://AAA-BBB-CCC.mktorest.com
        private String clientId = "CHANGE ME"; //clientId from admin > Launchpoint
        private String clientSecret = "CHANGE ME"; //clientSecret from admin > Launchpoint
        public String name;//name of new template, required
        public Dictionary<string, dynamic> folder;//dict with two members, id and type, must be Folder, required
        public int template;//id of template to create email from, required
        public String description;// optional description of new template
        public String subject;//subject line for email
        public String fromName;//from name for email
        public String fromEmail;//from email address for email
        public String replyEmail;//reply-to field for email
        public Boolean operational;//operational status for email, default false

        /*
        public static void Main(string[] args)
        {
            var email = new CreateEmail();
            email.name = "C# Example Template";
            email.folder = new Dictionary<string, dynamic>();
            email.folder.Add("id", 15);
            email.folder.Add("type", "Folder");
            email.template = 1001;
            String result = email.postData();
            Console.Write(result);
        }
        */

        public String postData()
        {
            //Assemble the URL
            String url = host + "/rest/asset/v1/emails.json?";
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
            body.Add("access_token", getToken());
            body.Add("name", name);
            body.Add("folder", JsonConvert.SerializeObject(folder));
            body.Add("template", template.ToString());
            if (description != null)
            {
                body.Add("description", description );
            }
            if (subject != null)
            {
                body.Add("subject", subject);
            }
            if (fromName != null)
            {
                body.Add("fromName", fromName);
            }
            if (fromEmail != null)
            {
                body.Add("fromEmail", fromEmail);
            }
            if (replyEmail != null)
            {
                body.Add("replyEmail", replyEmail);
            }
            if (operational != null)
            {
                body.Add("operational", operational.ToString());
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
