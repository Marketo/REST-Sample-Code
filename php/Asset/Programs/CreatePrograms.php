<?php
/*
   CreatePrograms.php

   Marketo REST API Sample Code
   Copyright (C) 2016 Marketo, Inc.

   This software may be modified and distributed under the terms
   of the MIT license.  See the LICENSE file for details.
*/
$program = new CreateProgram();
$program->folder = new stdClass();
$program->folder->id = 5562;
$program->folder->type = "Folder";
$program->name = "New Program PHP";
$program->description = "created with PHP";
$program->type = "Default";
$program->channel = "Content";

print_r($program->postData());


class CreateProgram{
        private $host = "CHANGE ME";
	private $clientId = "CHANGE ME";
	private $clientSecret = "CHANGE ME";

	public $folder;//folders object with id and type
	public $name;//name of new program
	public $description;//description of new program
	public $type;//type of new program
	public $channel;//channel of new Program
	public $tags;//array of tag objects, optional
	public $costs;//array of cost objects, optional
	
	public function postData(){
		$url = $this->host . "/rest/asset/v1/programs.json";
		$ch = curl_init($url);
		$requestBody = $this->bodyBuilder();
		curl_setopt($ch,  CURLOPT_RETURNTRANSFER, 1);
		curl_setopt($ch, CURLOPT_HTTPHEADER, array('accept: application/json', "Authorization: Bearer " . $this->getToken(), "Content-Type: application/json"));
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
// 		$requestBody = new stdClass();
// 		$requestBody->folders = $this->folder;
// 		$requestBody->name = $this->name;
// 		$requestBody->description = $this->description;
// 		$requestBody->type = $this->type;
// 		$requestBody->channel = $this->channel;
// 		if (isset($this->tags)){
// 			$requestBody->tags = $this->tags;
// 		}
// 		if(isset($this->costs)){
// 			$requestBody->costs = $this->costs;
// 		}
// 		$json = json_encode($requestBody);
// 		print_r($json);
// 		return $json;
		$jsonFolder = json_encode($this->folder);
		$requestBody = "name=$this->name&folder=$jsonFolder&description=$this->description&type=$this->type&channel=$this->channel";
		if (isset($this->tags)){
			$jsonTags = json_encode($this->tags);
			$requestBody .= "&tags=$jsonTags";
		}
		if (isset($this->costs)){
			$jsonCosts = json_encode($this->costs);
			$requestBody .= "&costs=$jsonCosts";
		}
		return $requestBody;
	}
}