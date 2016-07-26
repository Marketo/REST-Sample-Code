<?php
$templates = new MultipleLandingPageTemplates();
print_r($templates->getData());


class MultipleLandingPageTemplates{
	private $host = "https://299-BYM-827.mktorest.com";
	private $clientId = "b417d98f-9289-47d1-a61f-db141bf0267f";
	private $clientSecret = "0DipOvz4h2wP1ANeVjlfwMvECJpo0ZYc";
	public $offset;//integer offset for paging
	public $maxreturn;//max number of templates to return
	public $status;//optional status filter
	public $folder;//filter, stdObject folder with id and type(Folder or Program)
	
	public function getData(){
		$url = $this->host . "/rest/asset/v1/landingPageTemplates.json?access_token=" . $this->getToken();
		if (isset($this->offset)){
			$url .= "&offset=" . $this->offset;
		}
		if (isset($this->maxreturn)){
			$url .= "&maxreturn=" . $this->maxreturn;
		}
		if (isset($this->status)){
			$url .= "&status=" . $this->status;
		}
		if (isset($this->folder)){
			$url .= "&folder=" . json_encode($this->folder);
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

