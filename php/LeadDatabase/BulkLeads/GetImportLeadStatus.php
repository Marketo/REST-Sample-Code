<?php
$import = new ImportLeads();
$import->format = "csv";
$import->file = "mktoseedlist.csv"; 
print_r($import->postData());

class ImportLeads{
	private $host = "https://299-BYM-827.mktorest.com";
	private $clientId = "b417d98f-9289-47d1-a61f-db141bf0267f";
	private $clientSecret = "0DipOvz4h2wP1ANeVjlfwMvECJpo0ZYc";
	public $file; //name of the file to import, required
	public $format;//file format, csv, tsv or ssv, required
	public $listId;//optional id of list to import to
	public $lookupField; //field to dedupe on, defaults to email
	
	public function postData(){
		$url = $this->host . "/bulk/v1/leads.json?access_token=" . $this->getToken() . "&format=" . $this->format;
		if (isset($this->listId)){
			$url .= "&listId=" . $this->listId;
		}
		if(isset($this->lookupField)){
			$url .= "&lookupField=" . $this->lookupField;
		}
		$ch = curl_init($url);
		//create a new CURLFile representation of the import file
		$cfile = new CURLFile($this->file, "text/plain", "file");
		//set the CURLFile in the "file parameter of the multipart post fields.
		$requestBody = array("file" => $cfile);
		curl_setopt($ch,  CURLOPT_RETURNTRANSFER, 1);
		curl_setopt($ch, CURLOPT_HTTPHEADER, array('accept: application/json','Content-Type: multipart/form-data'));
		curl_setopt($ch, CURLOPT_POST, 1);
		curl_setopt($ch, CURLOPT_POSTFIELDS, $requestBody);
		curl_getinfo($ch);
		$response = curl_exec($ch);
		return $response;
	}
	private function getToken(){
		$ch = curl_init($this->host . "/identity/oauth/token?grant_type=client_credentials&client_id=" . $this->clientId . "&client_secret=" . $this->clientSecret);
		curl_setopt($ch,  CURLOPT_RETURNTRANSFER, 1);
		curl_setopt($ch, CURLOPT_HTTPHEADER, array('accept: application/json',));
		$response = json_decode(curl_exec($ch));
		curl_close($ch);
		$token = $response->access_token;
		return $token;
	}	
}