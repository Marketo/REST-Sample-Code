/*
   DeleteOpportunityRoles.cs

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

    class DeleteRoles
    {
        private String host = "CHANGE ME"; //host of your marketo instance, https://AAA-BBB-CCC.mktorest.com
        private String clientId = "CHANGE ME"; //clientId from admin > Launchpoint
        private String clientSecret = "CHANGE ME"; //clientSecret from admin > Launchpoint
        public String name;//name of custom object type
        public List<Dictionary<string, dynamic>> input;//list of records to delete
        public String deleteBy;//dedupeFields, or idFields, defaults to dedupeFields

        /*
        public static void Main(string[] args)
        {
            var delete = new DeleteRoles();
            delete.name = "pet";
            delete.input = new List<Dictionary<string, dynamic>>();
            var opp1 = new Dictionary<string, dynamic>();
            opp1.Add("externalopportunityid", "C# Test Opp");
            delete.input.Add(opp1);
            String result = delete.postData();
            Console.Write(result);
        }
        */

        public String postData()
        {
            //Assemble the URL
            String url = host + "/rest/v1/opportunities/roles/delete.json?access_token=" + getToken();
            HttpWebRequest request = (HttpWebRequest)WebRequest.Create(url);
            request.Method = "POST";
            request.ContentType = "application/json";
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
            var parent = new Dictionary<string, dynamic>();
            parent.Add("input", input);
            if (deleteBy != null)
            {
                parent.Add("deleteBy", deleteBy);
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
            Dictionary<String, String> dict = JsonConvert.DeserializeObject<Dictionary<String, String>>(json);
            return dict["access_token"];
        }
    }
}
