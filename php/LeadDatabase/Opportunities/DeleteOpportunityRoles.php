<?php
/*
   DeleteOpportunityRoles.php

   Marketo REST API Sample Code
   Copyright (C) 2016 Marketo, Inc.

   This software may be modified and distributed under the terms
   of the MIT license.  See the LICENSE file for details.
*/
$delete = new DeleteOpportunityRoles();
$role1 = new stdClass();
$role1->externalopportunityid = "Opportunity 1";
$role1->role = "Captain";
$role1->leadId = 1;
$delete->roles = [$role1];
$delete->dedupeBy = "dedupeFields";
print_r($delete->postData());


class DeleteOpportunityRoles{
	private $host = "CHANGE ME";
	private $clientId = "CHANGE ME";
	private $clientSecret = "CHANGE ME";
	public $roles;//array of objects with externalopportunityid, leadid and role
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
			$requestBody->input = $this->roles;
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
}