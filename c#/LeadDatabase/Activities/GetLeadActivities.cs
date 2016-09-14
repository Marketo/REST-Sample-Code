/*
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
    class Activities
    {
        private String host = "CHANGE ME"; //host of your marketo instance, https://AAA-BBB-CCC.mktorest.com
        private String clientId = "CHANGE ME"; //clientId from admin > Launchpoint
        private String clientSecret = "CHANGE ME"; //clientSecret from admin > Launchpoint
        public int[] activityTypeIds;//type ids returned from GetActivityTypes, max 10, required
        public String nextPageToken;//paging token returned from getPaging token, required
        public int batchSize;//max 300 default 300
        public int listId;//optional static list to search for activities

        /*
        public static void Main(string[] args)
        {
            Activities activities = new Activities();
            activities.nextPageToken = "ZX7GSH7IIOPV4SYG7GUREAQZXSFG5F6FHDEIXVRDWFYB6IULXHLA====";
            activities.activityTypeIds = new int[] { 1, 2, 3 };
            String result = activities.getData();
            Console.WriteLine(result);
        }
        */

        public String getData()
        {
            String url = host + "/rest/v1/activities.json?access_token=" + getToken() + "&activityTypeIds=" + csvString(activityTypeIds)
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
