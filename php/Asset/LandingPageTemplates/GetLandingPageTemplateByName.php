<?php
$landingPageTemplate = new LandingPageTemplateByName();
$landingPageTemplate->name = "Blank";
print_r($landingPageTemplate->getData());


class LandingPageTemplateByName{
	private $host = "https://299-BYM-827.mktorest.com";
	private $clientId = "b417d98f-9289-47d1-a61f-db141bf0267f";
	private $clientSecret = "0DipOvz4h2wP1ANeVjlfwMvECJpo0ZYc";
	public $name;//name of template to retrieve
	public $status; //optional status filter
	
	public function getData(){
		$url = $this->host . "/rest/asset/v1/landingPageTemplate/byName.json?access_token=" . $this->getToken() . "&name=" . $this->name;
		if (isset($this->status)){
			$url .= "&status=" . $this->status;
		}
		$ch = curl_init($url);
		curl_setopt($ch,  CURLOPT_RETURNTRANSFER, 1);
		curl_setopt($ch, CURLOPT_HTTPHEADER, array('accept: application/json',));
		$response = curl_exec($ch);
		return $response;
	}
	
	private function getToken(){
		$ch = curl_init($this->host . "/identity/oauth/token?grant_type=client_credentials&client_id=" . $this->clientId . "&client_secret=" . $this->clientSecret);
		curl_setopt($ch,  CURLOPT_RETURNTRANSFER, 1);
		curl_setopt($ch, CURLOPT_HTTPHEADER, array('accept: application/json'));
		$response = json_decode(curl_exec($ch));
		curl_close($ch);
		$token = $response->access_token;
		return $token;
	}
}

