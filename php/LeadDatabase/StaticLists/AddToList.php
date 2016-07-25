<?php
$removeFromList = new AddToList();
$removeFromList->listId = 1001;
$removeFromList->leadIds = [1,2,3,4];
print_r($removeFromList->postData());

class AddToList{
	private $host = "https://299-BYM-827.mktorest.com";
	private $clientId = "b417d98f-9289-47d1-a61f-db141bf0267f";
	private $clientSecret = "0DipOvz4h2wP1ANeVjlfwMvECJpo0ZYc";
	public $listId;//id of list to add to
	public $leadIds;//array of lead ids to add to list
	
	public function postData(){
		$url = $this->host . "/rest/v1/lists/" . $this->listId . "/leads.json?access_token=" . $this->getToken();
		$ch = curl_init($url);
		$requestBody = $this->bodyBuilder();
		curl_setopt($ch,  CURLOPT_RETURNTRANSFER, 1);
		curl_setopt($ch, CURLOPT_HTTPHEADER, array('accept: application/json','Content-Type: application/json'));
		curl_setopt($ch, CURLOPT_POST, 1);
		curl_setopt($ch, CURLOPT_POSTFIELDS, $requestBody);
		curl_getinfo($ch);
		$response = curl_exec($ch);
		return $response;
	}
	private function bodyBuilder(){
		$array = [];
		foreach($this->leadIds as $lead){
			$member = new stdClass();
			$member->id = $lead;
			array_push($array, $member);
		}
		$body = new stdClass();
		$body->input = $array;
		$json = json_encode($body);
		return $json;
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