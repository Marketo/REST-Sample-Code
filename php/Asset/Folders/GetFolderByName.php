<?php
/*
   GetFolderByName.php

   Marketo REST API Sample Code
   Copyright (C) 2016 Marketo, Inc.

   This software may be modified and distributed under the terms
   of the MIT license.  See the LICENSE file for details.
*/
$folder = new FolderByName();
$folder->type = "Folder";
$folder->name = "Templates";
print_r($folder->getData());

class FolderByName{
	private $host = "CHANGE ME";
	private $clientId = "CHANGE ME";
	private $clientSecret = "CHANGE ME";
	public $type; //type to retrieve, Folder or Program
	public $root;//id of parent folder, optional
	public $name;//name of folder to retrieve, required
	public $workspaceName;//workspace to retrieve from
	
	public function getData(){
		$url = $this->host . "/rest/asset/v1/folder/byName.json?access_token=" . $this->getToken() . "&name=" . $this->name;
		if (isset($this->type)){
			$url .= "&type=" . $this->type;
		}
		if(isset($this->root)){
			$url .= "&root=" . $this->root;
		}
		if(isset($this->workspaceName)){
			$url .= "&workspaceName=" . $this->workspaceName;
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