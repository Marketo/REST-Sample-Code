/*
   GetFolderByName.cs

   Marketo REST API Sample Code
   Copyright (C) 2016 Marketo, Inc.

   This software may be modified and distributed under the terms
   of the MIT license.  See the LICENSE file for details.
*/
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Collections.Specialized;
using System.IO;
using System.Linq;
using System.Net;
using System.Text;
using System.Threading.Tasks;
using System.Web;

namespace Samples
{
    class FolderByName
    {
        private String host = "CHANGE ME"; //host of your marketo instance, https://AAA-BBB-CCC.mktorest.com
        private String clientId = "CHANGE ME"; //clientId from admin > Launchpoint
        private String clientSecret = "CHANGE ME"; //clientSecret from admin > Launchpoint
        public String name;//Name of Folder to retrieve, required
        public String type;//type of Folder to retrieve, Program or Folder
        public Dictionary<string, dynamic> root;//root folder with two members, id, and type(Folder or Program)
        public String workSpace;//optional workspace filter

        /*
         public static void Main(string[] args)
        {
            var folder = new FolderByName();
            folder.name = "Snippets";
            String result = folder.getData();
            Console.WriteLine(result);
         */

        public String getData()
        {
            
            var qs = HttpUtility.ParseQueryString(string.Empty);
            qs.Add("access_token", getToken());
            qs.Add("name", name);
            if (type != null)
            {
                qs.Add("type", type);
            }
            if (root != null)
            {
                qs.Add("root", JsonConvert.SerializeObject(root));
            }
            if (workSpace != null)
            {
                qs.Add("workSpace", workSpace);
            }
            String url = host + "/rest/asset/v1/folders.json?" + qs.ToString();
            Console.Write(url);
            HttpWebRequest request = (HttpWebRequest)WebRequest.Create(url);
            request.ContentType = "application/json";
            request.Accept = "application/json";
            HttpWebResponse response = (HttpWebResponse)request.GetResponse();
            Stream resStream = response.GetResponseStream();
            StreamReader reader = new StreamReader(resStream);
            return reader.ReadToEnd();
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
            Dictionary<String, String> dict = JsonConvert.DeserializeObject<Dictionary<String, String>>(json);
            return dict["access_token"];
        }
        private String csvString(String[] args)
        {
            StringBuilder sb = new StringBuilder();
            int i = 1;
            foreach (String s in args)
            {
                if (i < args.Length)
                {
                    sb.Append(s + ",");
                }
                else
                {
                    sb.Append(s);
                }
                i++;
            }
            return sb.ToString();

        }
    }
}
