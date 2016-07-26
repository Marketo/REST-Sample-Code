<?php
$landingPageTemplate = new UpdateLandingPageTemplate();
$landingPageTemplate->id = 1001;
$landingPageTemplate->description = "This description has been updated";
print_r($landingPageTemplate->postData());

class UpdateLandingPageTemplate{
	private $host = "https://299-BYM-827.mktorest.com";
	private $clientId = "b417d98f-9289-47d1-a61f-db141bf0267f";
	private $clientSecret = "0DipOvz4h2wP1ANeVjlfwMvECJpo0ZYc";
	public $id;//id of template, required
	public $name;//name of template
	public $description;//optional description of template
	
	public function postData(){
		$url = $this->host . "/rest/asset/v1/landingPageTemplate/" . $this->id . ".json?access_token=" . $this->getToken();
		$ch = curl_init($url);
		$requestBody = "";
		if (isset($this->name)){
			$requestBody .= "&name=" . $this->name;
		}
		if (isset($this->description)){
			$requestBody .= "&description=" . $this->description;
		}
		curl_setopt($ch,  CURLOPT_RETURNTRANSFER, 1);
		curl_setopt($ch, CURLOPT_HTTPHEADER, array('accept: application/json'));
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

