/*
   DeleteLeads.cs

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

    class DeleteLeads
    {
        private String host = "CHANGE ME"; //host of your marketo instance, https://AAA-BBB-CCC.mktorest.com
        private String clientId = "CHANGE ME"; //clientId from admin > Launchpoint
        private String clientSecret = "CHANGE ME"; //clientSecret from admin > Launchpoint
        public int[] leadIds;//ids of leads to delete
        /*
        public static void Main(string[] args)
        {
            DeleteLeads delete = new DeleteLeads();
            delete.leadIds = new int[] { 50000, 60000, 70000 };
            result = delete.postData();
            Console.WriteLine(result);
        }
         */
        public String postData()
        {
            //Assemble the URL
            String url = host + "/rest/v1/leads.json?access_token=" + getToken();
            //Serialize the Data into JSON
            String requestBody = bodyBuilder();
            Console.WriteLine(requestBody);
            HttpWebRequest request = (HttpWebRequest)WebRequest.Create(url);
            request.Method = "DELETE";
            request.ContentType = "application/json";
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
            Dictionary<String, Object> parent = new Dictionary<string,object>();
            List<Object> input = new List<Object>();
            foreach(int lead in leadIds){
                Dictionary<String, int> leadObject = new Dictionary<string,int>();
                leadObject.Add("id", lead);
                input.Add(leadObject);
            }
            parent.Add("input", input);
            return JsonConvert.SerializeObject(parent);
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
