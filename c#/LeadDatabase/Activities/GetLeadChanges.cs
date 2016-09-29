/*
   GetLeadChanges.cs

   Marketo REST API Sample Code
   Copyright (C) 2016 Marketo, Inc.

   This software may be modified and distributed under the terms
   of the MIT license.  See the LICENSE file for details.
*/
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Net;
using System.Text;
using System.Threading.Tasks;

namespace Samples
{
    class GetLeadChanges
    {
        private String host = "CHANGE ME"; //host of your marketo instance, https://AAA-BBB-CCC.mktorest.com
        private String clientId = "CHANGE ME"; //clientId from admin > Launchpoint
        private String clientSecret = "CHANGE ME"; //clientSecret from admin > Launchpoint
        public String nextPageToken;//paging token returned from getPaging token, required
        public int batchSize;//max 300 default 300
        public int listId;//optional static list to search for activities
        public String[] fields;//array of fields names to retrieve changes for, required

        /*
        public static void Main(string[] args)
        {
            GetLeadChanges changes = new GetLeadChanges();
            changes.nextPageToken = "ZX7GSH7IIOPV4SYG7GUREAQZXSFG5F6FHDEIXVRDWFYB6IULXHLA====";
            changes.fields = new String[] { "lastName", "firstName", "email" };
            String result = changes.getData();
            Console.WriteLine(result);
        }
        */

        public String getData()
        {
            String url = host + "/rest/v1/activities/leadchanges.json?access_token=" + getToken() + "&fields=" + csvString(fields)
                + "&nextPageToken=" + nextPageToken;
            if (batchSize > 0 && batchSize < 300)
            {
                url += "&batchSize=" + batchSize;
            }
            if (listId > 0)
            {
                url += "&listId=" + listId;
            }
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
            //Dictionary<String, Object> dict = JavaScriptSerializer.DeserializeObject(reader.ReadToEnd);
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
        private String csvString(int[] args)
        {
            StringBuilder sb = new StringBuilder();
            int i = 1;
            foreach (int s in args)
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
