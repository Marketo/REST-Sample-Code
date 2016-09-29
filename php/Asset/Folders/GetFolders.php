<?php
/*
   GetFolders.php

   Marketo REST API Sample Code
   Copyright (C) 2016 Marketo, Inc.

   This software may be modified and distributed under the terms
   of the MIT license.  See the LICENSE file for details.
*/
$folders = new BrowseFolders();
$folders->root = new stdClass();
$folders->root->type = "Program";
$folders->root->id = 1071;
print_r($folders->getData());

class BrowseFolders{
	private $host = "CHANGE ME";
	private $clientId = "CHANGE ME";
	private $clientSecret = "CHANGE ME";
	public $root; //root directory, json object with two members, id and type, Folder or Program, required
	public $offset; //integer offset for paging, default 0
	public $maxDepth; //max depth of tree to examine, default 2
	public $maxReturn; //max number of results, default 20, max 200
	public $workSpace;//workspace to examine
	
	public function getData(){
		$url = $this->host . "/rest/asset/v1/folders.json?access_token=" . $this->getToken() . "&root=" . json_encode($this->root);
		if(isset($this->offset)){
			$url .= "&offset=" . $this->offset;
		}
		if(isset($this->maxDepth)){
			$url .= "&maxDepth=" . $this->maxDepth;
		}
		if(isset($this->maxReturn)){
			$url .= "&maxReturn=" . $this->maxReturn;
		}
		if(isset($this->workSpace)){
			$url .= "&workSpace=" . $this->workSpace;
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
}