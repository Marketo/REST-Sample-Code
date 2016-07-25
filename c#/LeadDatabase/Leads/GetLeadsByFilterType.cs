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
    class MultipleLeads
    {
        private String host = "CHANGE ME"; //host of your marketo instance, https://AAA-BBB-CCC.mktorest.com
        private String clientId = "CHANGE ME"; //clientId from admin > Launchpoint
        private String clientSecret = "CHANGE ME"; //clientSecret from admin > Launchpoint
        public String filterType; //field to filter by, required
        public String[] filterValues; //values to filter on, required
        public int batchSize;//max 300, default 300
        public String[] fields;//array of field names to retrieve
        public String nextPageToken;//paging token
        /*
        public static void Main(String[] args)
        {
            MultipleLeads leads = new MultipleLeads();
            leads.filterType = "email";
            leads.filterValues = new String[] { "kelkington@marketo.com" };
            String result = leads.getData();
            Console.WriteLine(result);
            while (true)
            {

            }
        }
        */
        public String getData()
        {
            StringBuilder url = new StringBuilder(host + "/rest/v1/leads.json?access_token=" + getToken() + "&filterType=" + filterType + "&filterValues=" + csvString(filterValues));
            if (fields != null)
            {
                url.Append("&fields=" + csvString(fields));
            }
            if (batchSize > 0 && batchSize < 300)
            {
                url.Append("&batchSize=" + batchSize);
            }
            if (nextPageToken != null)
            {
                url.Append("&nextPageToken=" + nextPageToken);
            }
            HttpWebRequest request = (HttpWebRequest)WebRequest.Create(url.ToString());
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
            foreach(String s in args){
                if (i < args.Length){
                    sb.Append(s + ",");
                }else{
                    sb.Append(s);
                }
                i++;
            }
            return sb.ToString();

        }
    }
}using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Net;
using System.Text;
using System.Threading.Tasks;

namespace Samples
{
    class MultipleLeads
    {
        private String host = "CHANGE ME"; //host of your marketo instance, https://AAA-BBB-CCC.mktorest.com
        private String clientId = "CHANGE ME"; //clientId from admin > Launchpoint
        private String clientSecret = "CHANGE ME"; //clientSecret from admin > Launchpoint
        public String filterType; //field to filter by, required
        public String[] filterValues; //values to filter on, required
        public int batchSize;//max 300, default 300
        public String[] fields;//array of field names to retrieve
        public String nextPageToken;//paging token
        /*
        public static void Main(String[] args)
        {
            MultipleLeads leads = new MultipleLeads();
            leads.filterType = "email";
            leads.filterValues = new String[] { "kelkington@marketo.com" };
            String result = leads.getData();
            Console.WriteLine(result);
            while (true)
            {

            }
        }
        */
        public String getData()
        {
            StringBuilder url = new StringBuilder(host + "/rest/v1/leads.json?access_token=" + getToken() + "&filterType=" + filterType + "&filterValues=" + csvString(filterValues));
            if (fields != null)
            {
                url.Append("&fields=" + csvString(fields));
            }
            if (batchSize > 0 && batchSize < 300)
            {
                url.Append("&batchSize=" + batchSize);
            }
            if (nextPageToken != null)
            {
                url.Append("&nextPageToken=" + nextPageToken);
            }
            HttpWebRequest request = (HttpWebRequest)WebRequest.Create(url.ToString());
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
            foreach(String s in args){
                if (i < args.Length){
                    sb.Append(s + ",");
                }else{
                    sb.Append(s);
                }
                i++;
            }
            return sb.ToString();

        }
    }
}