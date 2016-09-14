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

    class CreateFile
    {
        private String host = "CHANGE ME"; //host of your marketo instance, https://AAA-BBB-CCC.mktorest.com
        private String clientId = "CHANGE ME"; //clientId from admin > Launchpoint
        private String clientSecret = "CHANGE ME"; //clientSecret from admin > Launchpoint
        public String name;//name of file to create
        public String file;//content of file to input
        public Dictionary<string, dynamic> folder;//folder with id and type(must be folder)
        public String description;//optional description
        public Boolean insertOnly;//optional to skip overwriting existing files

        /*
        public static void Main(string[] args)
        {
            var file = new CreateFile();
            file.name = "C# Example File";
            file.file = File.ReadAllText("C:\\Users\\kelkington\\Desktop\\Box Sync\\Dev Dot Marketo\\template.html");
            file.folder = new Dictionary<string, dynamic>();
            file.folder.Add("id", 5565);
            file.folder.Add("type", "Folder");
            String result = file.postData();
            Console.Write(result);
        }
        */


        public String postData()
        {
            //Assemble the URL
            String url = host + "/rest/asset/v1/files.json?access_token=" + getToken();
            HttpWebRequest request = (HttpWebRequest)WebRequest.Create(url);
            request.Method = "POST";
            String boundary = "--mktoBoundary" + DateTime.Now.Ticks.ToString("x");
            request.ContentType = "multipart/form-data; boundary=" + boundary;
            request.Accept = "application/json";
            StreamWriter wr = new StreamWriter(request.GetRequestStream());
            AddMultipartParam(wr, boundary, "name", name);
            AddMultipartFile(wr, boundary, "text/plain", "file", file);
            AddMultipartParam(wr, boundary, "folder", JsonConvert.SerializeObject(folder));
            wr.Write("--" + boundary + "--");
            wr.Flush();
            HttpWebResponse response = (HttpWebResponse)request.GetResponse();
            Stream resStream = response.GetResponseStream();
            StreamReader reader = new StreamReader(resStream);
            return reader.ReadToEnd();
        }
        private void AddMultipartFile(TextWriter wr, String boundary, String contentType, String paramName, String content)
        {
            wr.Write("--" + boundary + "\r\n");
            wr.Write("Content-Disposition: form-data; name=\"" + paramName + "\"; filename=\"file.txt\"\r\n");
            wr.Write("Content-type: " + contentType + "; charset=\"utf-8\"\r\n");
            wr.Write("\r\n");
            wr.Write(content + "\r\n");
        }
        private void AddMultipartParam(TextWriter wr, String boundary, String paramName, String content)
        {
            wr.Write("--" + boundary + "\r\n");
            wr.Write("Content-Disposition: form-data; name=\"" + paramName + "\"\r\n");
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
