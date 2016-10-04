<?php
/*
   GetPrograms.php

   Marketo REST API Sample Code
   Copyright (C) 2016 Marketo, Inc.

   This software may be modified and distributed under the terms
   of the MIT license.  See the LICENSE file for details.
*/

$programs = new GetPrograms();
print_r($programs->getData());

class GetPrograms{
	private $host = "CHANGE ME";
	private $clientId = "CHANGE ME";
	private $clientSecret = "CHANGE ME";
	
	public $status;//status filter for Engagement and Email programs, can be used to filter on "on", "off", or "unlocked"
	public $offset;//integer offset for paging
	public $maxReturn;//number of results to return, default 20, max 200

	public function getData(){
		$url = $this->host . "/rest/asset/v1/programs.json?";
		if (isset($status)){
			$url .= "status=" . $status . "&";
		}
		if (isset($offset)){
			$url .= "offset=" . $offset . "&";
		}
		if(isset($maxReturn)){
			$url .= "maxReturn=" . $maxReturn;
		}
		$ch = curl_init($url);
		curl_setopt($ch,  CURLOPT_RETURNTRANSFER, 1);
		curl_setopt($ch, CURLOPT_HTTPHEADER, array('accept: application/json', "Authorization: Bearer " . $this->getToken()));
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