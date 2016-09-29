/*
   ScheduleCampaign.cs

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

    class ScheduleCampaign
    {
        private String host = "CHANGE ME"; //host of your marketo instance, https://AAA-BBB-CCC.mktorest.com
        private String clientId = "CHANGE ME"; //clientId from admin > Launchpoint
        private String clientSecret = "CHANGE ME"; //clientSecret from admin > Launchpoint
        public int id;//id of campaign to schedule, required
        public String runAt; //datetime to schedule campaign for, 5 mins later if unspecified
        public String cloneToProgramName;//if specified will clone all content and parent program to a new program with the specified name
        public List<Dictionary<String, String>> tokens; //list of token dictionaries with members name and value

        /*
        public static void Main(string[] args)
        {
            ScheduleCampaign sched = new ScheduleCampaign();
            sched.id = 1573;
            sched.cloneToProgramName = "New API Test Program";
            List<Dictionary<string, string>> tokenList = new List<Dictionary<string, string>>();
            Dictionary<string, string> token1 = new Dictionary<string, string>();
            token1.Add("name", "{{my.token}}");
            token1.Add("value", "This token has been replaced");
            tokenList.Add(token1);
            String result = sched.postData();
            Console.WriteLine(result);
        }
        */


        public String postData()
        {
            //Assemble the URL
            String url = host + "/rest/v1/campaigns/" + id + "/schedule.json?access_token=" + getToken();
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
            Dictionary<String, Object> input = new Dictionary<string,object>();
            if (runAt != null){
                input.Add("runAt", runAt);
            }
            if (cloneToProgramName != null){
                input.Add("cloneToProgramName", cloneToProgramName);
            }
            if (tokens != null){
                input.Add("tokens", tokens);
            }
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
