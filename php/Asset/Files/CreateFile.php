<?php
/*
   CreateFile.php

   Marketo REST API Sample Code
   Copyright (C) 2016 Marketo, Inc.

   This software may be modified and distributed under the terms
   of the MIT license.  See the LICENSE file for details.
*/
$file = new CreateFile();
$file->file = new CURLFile("File.txt", "text/plain", "file");
$file->folder = new stdClass();
$file->folder->id = 5565;
$file->folder->type = "Folder";
$file->name = "Test File.txt";
print_r($file->postData());

class CreateFile{
	private $host = "CHANGE ME";
	private $clientId = "CHANGE ME";
	private $clientSecret = "CHANGE ME";
	public $name;//name of file to create, required
	public $file;//CURLFile of file to input
	public $folder;//json object with two members, id and type(Folder or Program)
	public $description;//option description of file
	public $insertOnly;//boolean option to only perform an Insert
	
	public function postData(){
		$url = $this->host . "/rest/asset/v1/files.json?access_token=" . $this->getToken();
		$ch = curl_init($url);
		$requestBody = $this->bodyBuilder();
		curl_setopt($ch,  CURLOPT_RETURNTRANSFER, 1);
		curl_setopt($ch, CURLOPT_HTTPHEADER, array('accept: application/json','Content-Type: multipart/form-data'));
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
		$requestBody = array("file" => $this->file, "name" => $this->name, "folder" => json_encode($this->folder));
		if (isset($this->description)){
			$requestBody["description"] = $this->description;
		}
		if(isset($this->insertOnly)){
			$requestBody["insertOnly"] = $this->insertOnly;
		}
		return $requestBody;
	}	
}