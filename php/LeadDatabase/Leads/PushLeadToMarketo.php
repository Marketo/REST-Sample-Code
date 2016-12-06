<?php
/*
   PushLeadToMarketo.php

   Marketo REST API Sample Code
   Copyright (C) 2016 Marketo, Inc.

   This software may be modified and distributed under the terms
   of the MIT license.  See the LICENSE file for details.
*/
$lead1 = new stdClass();
$lead1->email = "john.doe@marketo.com";
$lead1->firstName = "John";
$lead1->lastName = "Doe";

$upsert = new PushLeads();
$upsert->programName = "Content - Whitepaper";
$upsert->source = "Sales Site";
$upsert->reason = "Downloaded whitepaper.";
$upsert->input = array($lead1);
print_r($upsert->postData());

class PushLeads{
	private $host = "";//CHANGE ME
	private $clientId = "";//CHANGE ME
	private $clientSecret = "";//CHANGE ME
	public $input; //an array of lead records as objects (required)
    public $programName; // program that activity is attributed to (required)
	public $lookupField; //field used for deduplication
    public $reason; // activity metadata
    public $source; // activity metadata
	
	public function postData(){
		$url = $this->host . "/rest/v1/leads/push.json?access_token=" . $this->getToken();
		print_r($url);
		$ch = curl_init($url);
		$requestBody = $this->bodyBuilder();
        print_r($requestBody);
		curl_setopt($ch,  CURLOPT_RETURNTRANSFER, 1);
		curl_setopt($ch, CURLOPT_HTTPHEADER, array('accept: application/json','Content-Type: application/json'));
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
	private function bodyBuilder(){
		$body = new stdClass();
		if (isset($this->programName)){
			$body->programName = $this->programName;
		}
		if (isset($this->reason)){
			$body->reason = $this->reason;
		}        
		if (isset($this->source)){
			$body->source = $this->source;
		}        
		if (isset($this->lookupField)){
			$body->lookupField = $this->lookupField;
		}
		$body->input = $this->input;
		$json = json_encode($body);
		return $json;
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