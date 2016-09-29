<?php
/*
   CreateSnippet.php

   Marketo REST API Sample Code
   Copyright (C) 2016 Marketo, Inc.

   This software may be modified and distributed under the terms
   of the MIT license.  See the LICENSE file for details.
*/
$snippet = new CreateSnippet();
$snippet->name = "Sample snippet - PHP";
$snippet->folder = new stdClass();
$snippet->folder->id = 44;
$snippet->folder->type = "Folder";
print_r($snippet->postData());

class CreateSnippet{
	private $host = "CHANGE ME";
	private $clientId = "CHANGE ME";
	private $clientSecret = "CHANGE ME";
	public $name;//name of new snippet, required
	public $folder;//json object with two members, id and type(Folder or Program), required
	public $description;//optional description of new Snippet
	
	public function postData(){
		$url = $this->host . "/rest/asset/v1/snippets.json?access_token=" . $this->getToken();
		$ch = curl_init($url);
		$requestBody = $this->bodyBuilder();
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
	private function bodyBuilder(){
		$requestBody = "&name=" . $this->name . "&folder=" . json_encode($this->folder);
		if (isset($this->description)){
			$requestBody .= "&description=" . $this->description;
		}
		return $requestBody;
	}	
}