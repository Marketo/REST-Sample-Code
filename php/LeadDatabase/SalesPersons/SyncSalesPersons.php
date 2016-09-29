<?php
/*
   SyncSalesPersons.php

   Marketo REST API Sample Code
   Copyright (C) 2016 Marketo, Inc.

   This software may be modified and distributed under the terms
   of the MIT license.  See the LICENSE file for details.
*/
$upsert = new UpsertSalesPersons();
$salesperson1 = new stdClass();
$salesperson1->externalSalesPersonId = "SalesPerson 1";
$upsert->input = [$salesperson1];
$upsert->dedupeBy = "dedupeFields";
print_r($upsert->postData());

class UpsertSalesPersons{
	private $host = "CHANGE ME";
	private $clientId = "CHANGE ME";
	private $clientSecret = "CHANGE ME";
	public $input;//array of salesperson objects, required
	public $action;//action to take, createOnly, updateOnly, createOrUpdate, default createOrUpdate
	public $dedupeBy;//dedupefields(externalSalesPersonId), or idField(id)
	
	private function bodyBuilder(){
		$requestBody = new stdClass();
		if (isset($this->action)){
			$requestBody->action = $this->action;
		}
		if (isset($this->dedupeBy)){
			$requestBody->dedupeBy = $this->dedupeBy;
		}
		$requestBody->input = $this->input;
		$json = json_encode($requestBody);
		return $json;
	}
	public function postData(){
		$url = $this->host . "/rest/v1/salespersons.json?access_token=" . $this->getToken();
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