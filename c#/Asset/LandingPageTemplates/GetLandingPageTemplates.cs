/*
   GetLandingPageTemplates.cs

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
using System.Web;

namespace Samples
{
    class MultipleLandingPageTemplates
    {
        private String host = "CHANGE ME"; //host of your marketo instance, https://AAA-BBB-CCC.mktorest.com
        private String clientId = "CHANGE ME"; //clientId from admin > Launchpoint
        private String clientSecret = "CHANGE ME"; //clientSecret from admin > Launchpoint
        public int offset;//integer offset for paging
        public int maxReturn;//max number of templates to return, max 200, default 20
        public String status;//optional status filter, draft or approved
        public Dictionary<string, dynamic> folder;//folder to search in with two members, id and type(must be Folder)

        /*
        public static void Main(string[] args)
        {
            var templates = new MultipleLandingPageTemplates();
            templates.status = "approved";
            String result = templates.getData();
            Console.Write(result);
        }
        */

        public String getData()
        {
            var qs = HttpUtility.ParseQueryString(string.Empty);
            qs.Add("access_token", getToken());
            if (offset > 0)
            {
                qs.Add("offset", offset.ToString());
            }
            if (maxReturn > 0)
            {
                qs.Add("maxReturn", maxReturn.ToString());
            }
            if (status != null)
            {
                qs.Add("status", status);
            }
            if (folder != null)
            {
                qs.Add("folder", JsonConvert.SerializeObject(folder));
            }
            String url = host + "/rest/asset/v1/landingPageTemplates.json?" + qs.ToString();
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
    }
}
