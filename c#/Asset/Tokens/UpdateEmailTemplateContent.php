<?php
$emailTemplate = new UpdateEmailTemplateContent();
$emailTemplate->id = 1037;
$emailTemplate->content = new CURLFile("NewTemplate.HTML", "text/html", "content");
print_r($emailTemplate->postData());

class UpdateEmailTemplateContent{
	private $host = "CHANGE ME";
	private $clientId = "CHANGE ME";
	private $clientSecret = "CHANGE ME";
	public $id;//id of the teplate to update
	public $content; //HTML content of Template, required
	
	public function postData(){
		$url = $this->host . "/rest/asset/v1/emailTemplate/" . $this->id . "/content.json?access_token=" . $this->getToken();
		$ch = curl_init($url);
		$requestBody = array("content" => $this->content);
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