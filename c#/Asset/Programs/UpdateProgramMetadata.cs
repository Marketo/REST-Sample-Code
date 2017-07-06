/*
   UpdateProgramMetadata.cs

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

    class UpdateProgram
    {
        private String host = "CHANGE ME"; //host of your marketo instance, https://AAA-BBB-CCC.mktorest.com
        private String clientId = "CHANGE ME"; //clientId from admin > Launchpoint
        private String clientSecret = "CHANGE ME"; //clientSecret from admin > Launchpoint

        public int id;//id of program to update
        public Dictionary<string, dynamic> folder;//parent folder of new program with id and type
        public String name;//name of new program
        public String description;//description of new program
        public Dictionary<string, string>[] tags;//array of tags, each with members tagType and tagValue
        public Dictionary<string, string>[] costs;//array of costs, each with members, startDate, cost, and note
        public bool costsDestructiveUpdate;//if set to true will delete all existing costs and replace them with the included ones, default: false
        public DateTime startDate;//start time for email program
        public DateTime endDate;//end time for email program

        public String postData()
        {
            //Assemble the URL
            String url = host + "/rest/asset/v1/program/" + id +  ".json";
            //Serialize the Data into JSON
            String requestBody = bodyBuilder();
            HttpWebRequest request = (HttpWebRequest)WebRequest.Create(url);
            request.Method = "POST";
            request.ContentType = "application/x-www-form-urlencoded";
            request.Accept = "application/json";
            request.Headers["Authorization"] = "Bearer " + getToken();
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
            /*
            var dict = new Dictionary<string, dynamic>();
            if (description != null)
            {
                dict.Add("description", description);
            }
            if (name != null)
            {
                dict.Add("name", name);
            }
            if (tags != null)
            {
                dict.Add("tags", tags);
            }
            if (costs != null)
            {
                dict.Add("costs", costs);
            }
            if (costsDestructiveUpdate)
            {
                dict.Add("costsDestructiveUpdate", costsDestructiveUpdate);
            }
            if (startDate != null)
            {
                dict.Add("startDate", startDate.ToString());
            }
            if (endDate != null)
            {
                dict.Add("endDate", endDate.ToString());
            }
            return JsonConvert.SerializeObject(dict);
            */
            var qs = HttpUtility.ParseQueryString(string.Empty);
            if (description != null)
            {
                qs.Add("description", description);
            }
            if (name != null)
            {
                qs.Add("name", name);
            }
            if (tags != null)
            {
                qs.Add("tags", JsonConvert.SerializeObject(tags));
            }
            if (costs != null)
            {
                qs.Add("costs", JsonConvert.SerializeObject(costs));
            }
            if (costsDestructiveUpdate)
            {
                qs.Add("costsDestructiveUpdate", costsDestructiveUpdate.ToString());
            }
            if (startDate != null)
            {
                qs.Add("startDate", startDate.ToString());
            }
            if (endDate != null)
            {
                qs.Add("endDate", endDate.ToString());
            }
            return qs.ToString();
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
