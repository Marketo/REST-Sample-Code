<?php
/*
   DeleteCompanies.php

   Marketo REST API Sample Code
   Copyright (C) 2016 Marketo, Inc.

   This software may be modified and distributed under the terms
   of the MIT license.  See the LICENSE file for details.
*/
$delete = new DeleteCompanies();
$delete->dedupeBy = "dedupeFields";
$delete->externalCompanyIds = ["Company 1"];
print_r($delete->postData());


class DeleteCompanies{
	private $host = "CHANGE ME";
	private $clientId = "CHANGE ME";
	private $clientSecret = "CHANGE ME";
	public $externalCompanyIds;//array of external opportunity ids
	public $idfields; //array of marketo IDs
	public $dedupeBy; //dedupe field, dedupeFields or idField
	
	private function bodyBuilder(){
		$requestBody = new stdClass();
		//set dedupeby parameter in json body
		$requestBody->dedupeBy = $this->dedupeBy;
		$requestBody->input = array();
		$i = 0;
		//if dedupeby is dedupefields, use externalopportunityid
		if ($this->dedupeBy === "dedupeFields"){
			foreach($this->input as $id){
				$obj = new stdClass();
				$obj->externalCompanyId = $id;
				$requestBody->input[$i] = $obj;
				$i++;
			}
		}//else use marketoGUID
		else if ($this->dedupeBy === "idField"){
			foreach($this->input as $id){
				$obj = new stdClass();
				$obj->marketoGUID = $id;
				$requestBody->input[$i] = $obj;
				$i++;
			}
		}
		$json = json_encode($requestBody);
		return $json;
	}
	
	public function postData(){
		$url = $this->host . "/rest/v1/opportunities/delete.json?access_token=" . $this->getToken();
		$ch = curl_init($url);
		$requestBody = $this->bodyBuilder();
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
}