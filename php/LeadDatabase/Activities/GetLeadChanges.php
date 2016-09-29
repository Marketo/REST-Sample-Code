<?php
/*
   GetLeadChanges.php

   Marketo REST API Sample Code
   Copyright (C) 2016 Marketo, Inc.

   This software may be modified and distributed under the terms
   of the MIT license.  See the LICENSE file for details.
*/
$changes = new GetLeadChanges();
$changes->nextPageToken = "WQV2VQVPPCKHC6AQYVK7JDSA3I3LCWXH3Y6IIZ7YSGQLXHCPVE5Q====";
$changes->fields = ["email", "firstName", "lastName"];
print_r($changes->getData());


class GetLeadChanges{
	private $host = "CHANGE ME";
	private $clientId = "CHANGE ME";
	private $clientSecret = "CHANGE ME";
	public $fields;//array of field names to retrieve changes for, required
	public $nextPageToken;//paging token to specify beginning date for activities, required
	public $batchSize;//max 300, default 300
	public $listId;//integer id of a static list, if specified will only retrieve from leads in the list
	
	public function getData(){
		$url = $this->host . "/rest/v1/activities/leadchanges.json?access_token=" . $this->getToken() . "&fields=" . $this::csvString($this->fields)
						. "&nextPageToken=" . $this->nextPageToken;
		if (isset($this->batchSize)){
			$url .= "&batchSize=" . "$this->batchSize";
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
		}
		return $csvString;
	}
}
