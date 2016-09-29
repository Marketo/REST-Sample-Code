<?php
/*
   CreateFolder.php

   Marketo REST API Sample Code
   Copyright (C) 2016 Marketo, Inc.

   This software may be modified and distributed under the terms
   of the MIT license.  See the LICENSE file for details.
*/
$folder = new CreateFolder();
$parent = new stdClass();
$parent->id = 5566;
$parent->type = "Folder";
$folder->parent = $parent;
$folder->name = "New Folder - PHP";
print_r($folder->postData());

class CreateFolder{
	private $host = "CHANGE ME";
	private $clientId = "CHANGE ME";
	private $clientSecret = "CHANGE ME";
	public $name;//name of folder to create
	public $parent;//JSON object representing a folder with two members, id and type(Folder or Program)
	public $description;//optional description for new folder
	
	public function postData(){
		$url = $this->host . "/rest/asset/v1/folders.json?access_token=" . $this->getToken();
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
		$requestBody = "";
		$requestBody .= "name=" . $this->name;
		$requestBody .= "&parent=" . json_encode($this->parent);
		if (isset($this->description)){
			$requestBody .= "&description=" . $this->description;
		}
		return $requestBody;
	}	
}