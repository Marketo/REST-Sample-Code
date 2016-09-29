<?php
/*
   AssociateLead.php

   Marketo REST API Sample Code
   Copyright (C) 2016 Marketo, Inc.

   This software may be modified and distributed under the terms
   of the MIT license.  See the LICENSE file for details.
*/
$associate = new AssociateLead();
$associate->id = 1;
$associate->cookie = urlencode("mkto_trk=id:299-BYM-827&token:_mch-localhost-1435105067262-67189");
print_r($associate->getData());

class AssociateLead{
	private $host = "";//CHANGE ME
	private $clientId = "";//CHANGE ME
	private $clientSecret = "";//CHANGE ME
	public $id;//id of the lead to associate to
	public $cookie;//cookie to associate
	
	public function getData(){
		$url = $this->host . "/rest/v1/leads/" . $this->id . "/associate.json?access_token=" . $this->getToken() . "&cookie=" . $this->cookie;
		$ch = curl_init($url);
		curl_setopt($ch,  CURLOPT_RETURNTRANSFER, 1);
		curl_setopt($ch, CURLOPT_POST, 1);
		curl_setopt($ch, CURLOPT_HTTPHEADER, array('accept: application/json','Content-Type: application/json'));
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