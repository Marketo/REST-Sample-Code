/*
   AddToList.cs

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
    class AddToList
    {
        private String host = "CHANGE ME"; //host of your marketo instance, https://AAA-BBB-CCC.mktorest.com
        private String clientId = "CHANGE ME"; //clientId from admin > Launchpoint
        private String clientSecret = "CHANGE ME"; //clientSecret from admin > Launchpoint
        public int listId;//id of list to add members to
        public int[] ids;//array of lead ids to add to list

        /*
        public static void Main(string[] args)
        {
            AddToList add = new AddToList();
            add.listId = 1050;
            add.ids = new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
            String result = add.postData();
            Console.WriteLine(result);
        }
        */
        public String postData()
        {
            //Assemble the URL
            String url = host + "/rest/v1/lists/" + listId + "/leads.json?access_token=" + getToken();
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
            Dictionary<String, Object> parent = new Dictionary<string, object>();
            List<Dictionary<String, int>> input = new List<Dictionary<string,int>>();
            foreach(int lead in ids){
                Dictionary<string, int> leadObject = new Dictionary<string,int>();
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
    }
}
