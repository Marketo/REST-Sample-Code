<?php
/*
   RequestCampaign.php

   Marketo REST API Sample Code
   Copyright (C) 2016 Marketo, Inc.

   This software may be modified and distributed under the terms
   of the MIT license.  See the LICENSE file for details.
*/
$request = new RequestCampaign();
$request->id = 1001;
$token1 = new stdClass();
$token1->name = "{{my.token}}";
$token1->value = "Hello World!";
$lead1 = new stdClass();
$lead1->id = 1;
$myLeads = new stdClass();
$myLeads->leads = array($lead1);
$request->leads = $myLeads;
print_r($request->postData());

class RequestCampaign{
	private $host = "CHANGE ME";
	private $clientId = "CHANGE ME";
	private $clientSecret = "CHANGE ME";
	public $leads;//array of stdClass objects with one member, id, required
	public $tokens;//array of stdClass objects with two members, name and value

	public function postData(){
		$url = $this->host . "/rest/v1/campaigns/" . $this->id . "/trigger.json?access_token=" . $this->getToken();
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
		$input = new stdClass();
		if (isset($this->tokens)){
			$body->tokens = $this->tokens;
		}
		$body->input = $this->leads;
		$json = json_encode($body);
		return $json;
	}
}
