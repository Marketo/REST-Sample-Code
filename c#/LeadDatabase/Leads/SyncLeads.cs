/*
   SyncLeads.cs

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

    class UpsertLeads
    {
        private String host = "CHANGE ME"; //host of your marketo instance, https://AAA-BBB-CCC.mktorest.com
        private String clientId = "CHANGE ME"; //clientId from admin > Launchpoint
        private String clientSecret = "CHANGE ME"; //clientSecret from admin > Launchpoint
        public String lookupField;//field to deduplicate on
        public String action;//action to take, createOnly, updateOnly, createOrUpdate, createDuplicate
        public List<Dictionary<string, string>> input;//list of dictionaries which represent field/value pairs of leads
        public String partitionName;

        /*
        public static void Main(String[] args)
        {
            UpsertLeads upsert = new UpsertLeads();
            upsert.lookupField = "email";
            Dictionary<string, string> lead1 = new Dictionary<string,string>();
            lead1.Add("email", "test@example.com");
            upsert.input = new List<Dictionary<string, string>>();
            upsert.input.Add(lead1);
            String result = upsert.postData();
            Console.WriteLine(result);
            while (true)
            {

            }
        }
        */ 

        public String postData()
        {
            //Assemble the URL
            String url = host + "/rest/v1/leads.json?access_token=" + getToken();
            //Serialize the Data into JSON
            String requestBody = bodyBuilder();
            HttpWebRequest request = (HttpWebRequest)WebRequest.Create(url);
            request.Method = "POST";
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
            //Create a new Dict as the parent
            Dictionary<String, Object> body = new Dictionary<string,object>();
            //Append optional fields
            if (lookupField != null){
                body.Add("lookupField", lookupField);
            }
            if (action != null){
                body.Add("action", action);
            }
            if (partitionName != null){
                body.Add("partitionName", partitionName);
            }
            //Add the list of leads into the input member
            body.Add("input", input);
            //serialize the body object into JSON
            String json = JsonConvert.SerializeObject(body);
            return json;
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