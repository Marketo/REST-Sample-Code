<?php
/*
   UpdateProgramMetadata.php

   Marketo REST API Sample Code
   Copyright (C) 2016 Marketo, Inc.

   This software may be modified and distributed under the terms
   of the MIT license.  See the LICENSE file for details.
*/
$program = new UpdateProgramMetadata();
$program->id = 5562;
$program->name = "Update Program Metadata PHP";
$program->description = "Updated with PHP";
$program->tags = new stdClass();
$program->tags->tagType = "Program Owner";
$program->tags->tagValue = "David";
$program->costs = new stdClass();
$program->costs->startDate = "2016-07-01";
$program->costs->cost = 1000;
$program->costs->note = "Illustrator for eBook";
$program->costsDestructiveUpdate = FALSE;

print_r($program->postData());

class UpdateProgramMetadata{
	private $host = "CHANGE ME";
	private $clientId = "CHANGE ME";
	private $clientSecret = "CHANGE ME";

	//all params optional
	public $name;//new name of program
	public $description;//new description of program
	public $tags;//array of tag objects, tagType must exist
	public $costs;//array of period cost objects
	public $costsDestructiveUpdate;//boolean flag to destroy existing costs and replace with specified costs
	
	public function postData(){
		$url = $this->host . "/rest/asset/v1/program/" . this->id . ".json";
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
		$requestBody = "";
		if(isset($this->name)){
			$requestBody .= "&name=$this->name";
		}
		if(isset($this->description)){
			$requestBody .= "&description=$this->description";
		}
		if (isset($this->tags)){
			$jsonTags = json_encode($this->tags);
			$requestBody .= "&tags=$jsonTags";
		}
		if (isset($this->costs)){
			$jsonCosts = json_encode($this->costs);
			$requestBody .= "&costs=$jsonCosts";
		}
		if (isset($this->costsDestructiveUpdate)){
			$requestBody .= "&costsDestructiveUpdate=true";
		}
		return $requestBody;
	}
}