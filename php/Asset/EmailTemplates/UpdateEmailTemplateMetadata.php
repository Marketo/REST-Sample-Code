<?php
/*
   UpdateEmailTemplateMetadata.php

   Marketo REST API Sample Code
   Copyright (C) 2016 Marketo, Inc.

   This software may be modified and distributed under the terms
   of the MIT license.  See the LICENSE file for details.
*/
$emailTemplate = new UpdateEmailTemplate();
$emailTemplate->id = 1001;
$emailTemplate->description = "This description has been updated";
print_r($emailTemplate->postData());

class UpdateEmailTemplate{
	private $host = "CHANGE ME";
	private $clientId = "CHANGE ME";
	private $clientSecret = "CHANGE ME";
	public $id;//id of template, required
	public $name;//name of template
	public $description;//optional description of template
	
	public function postData(){
		$url = $this->host . "/rest/asset/v1/emailTemplate/" . $this->id . ".json?access_token=" . $this->getToken();
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