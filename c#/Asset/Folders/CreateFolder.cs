/*
   CreateFolder.cs

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

namespace Samples
{

    class CreateFolder
    {
        private String host = "CHANGE ME"; //host of your marketo instance, https://AAA-BBB-CCC.mktorest.com
        private String clientId = "CHANGE ME"; //clientId from admin > Launchpoint
        private String clientSecret = "CHANGE ME"; //clientSecret from admin > Launchpoint
        public String name;//name of folder to create, required
        public Dictionary<String, dynamic> parent; //parent folder to create with two members, id and Type(Folder or Program), required
        public String description;//optional description of new folder

        /*
        public static void Main(string[] args)
        {
            var folder = new CreateFolder();
            folder.name = "New Folder CSharp";
            folder.parent = new Dictionary<string,dynamic>();
            folder.parent.Add("id", 5565);
            folder.parent.Add("type", "Folder");
            String result = folder.postData();
            Console.WriteLine(result);
        }
        */

        public String postData()
        {
            //Assemble the URL
            String url = host + "/rest/asset/v1/folders.json?access_token=" + getToken();
            //Form Encode the data
            String requestBody = bodyBuilder();
            Console.WriteLine(requestBody);
            HttpWebRequest request = (HttpWebRequest)WebRequest.Create(url);
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
            var sb = new StringBuilder();
            sb.Append("&name=" + name);
            sb.Append("&parent=" + JsonConvert.SerializeObject(parent));
            if (description != null)
            {
                sb.Append("&description=" + description);
            }
            return sb.ToString();
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
