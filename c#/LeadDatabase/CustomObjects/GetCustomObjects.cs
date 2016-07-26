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
    class GetCustomObjects
    {
        private String host = "CHANGE ME"; //host of your marketo instance, https://AAA-BBB-CCC.mktorest.com
        private String clientId = "CHANGE ME"; //clientId from admin > Launchpoint
        private String clientSecret = "CHANGE ME"; //clientSecret from admin > Launchpoint
        public String name;//custom object type
        public String filterType;//field from searchableFields of object, required
        public String[] filterValues;//one or more filter values, required
        public String[] fields;//fields to retrieve
        public String nextPageToken;//paging token return from previous call
        public int batchSize;//default 300, max 300

        /*
        public static void Main(string[] args)
        {
            var objects = new GetCustomObjects();
            objects.name = "pet";
            objects.filterType = "name";
            objects.filterValues = new String[] { "Fido", "Spot" };
            objects.batchSize = 100;
            String result = objects.getData();
            Console.Write(result);
        }
        */

        public String getData()
        {
            var qs = HttpUtility.ParseQueryString(string.Empty);
            qs.Add("access_token", getToken());
            qs.Add("filterType", filterType);
            qs.Add("filterValues", csvString(filterValues));
            if (fields != null)
            {
                qs.Add("fields", csvString(fields));
            }
            if (nextPageToken != null)
            {
                qs.Add("nextPageToken", nextPageToken);
            }
            if (batchSize > 0)
            {
                qs.Add("batchSize", batchSize.ToString());
            }
            String url = host + "/rest/v1/customobjects/" + name + ".json?" + qs.ToString();
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
    }
}
