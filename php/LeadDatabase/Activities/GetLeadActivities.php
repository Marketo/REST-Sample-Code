<?php
$activities = new GetLeadActivities();
$activities->nextPageToken = "WQV2VQVPPCKHC6AQYVK7JDSA3I3LCWXH3Y6IIZ7YSGQLXHCPVE5Q====";
$activities->activityTypeIds = [1,2,3];
print_r($activities->getData());


class GetLeadActivities{
	private $host = "https://299-BYM-827.mktorest.com";
	private $clientId = "b417d98f-9289-47d1-a61f-db141bf0267f";
	private $clientSecret = "0DipOvz4h2wP1ANeVjlfwMvECJpo0ZYc";
	public $activityTypeIds; //array of integer IDs of activity types, required
	public $nextPageToken;//paging token to specify beginning date for activities, required
	public $batchSize;//max 300, default 300
	public $listId;//integer id of a static list, if specified will only retrieve from leads in the list
	
	public function getData(){
		$url = $this->host . "/rest/v1/activities.json?access_token=" . $this->getToken() . "&activityTypeIds=" . $this::csvString($this->activityTypeIds)
					. "&nextPageToken=" . $this->nextPageToken;
		if (isset($this->batchSize)){
			$url .= "&batchSize=" . $this->batchSize;
		}
		if (isset($this->listId)){
			$url .= "&listId=" . $this->listId;
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
		curl_setopt($ch, CURLOPT_HTTPHEADER, array('accept: application/json',));
		$response = json_decode(curl_exec($ch));
		curl_close($ch);
		$token = $response->access_token;
		return $token;
	}
	private static function csvString($fields){
		$csvString = "";
		$i = 0;
		foreach($fields as $field){
			if ($i > 0){
				$csvString = $csvString . "," . $field;
			}elseif ($i === 0){
				$csvString = $field;
			}
               $i++;
		}
		return $csvString;
	}
}