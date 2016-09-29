<?php
/*
   MergeLeads.php

   Marketo REST API Sample Code
   Copyright (C) 2016 Marketo, Inc.

   This software may be modified and distributed under the terms
   of the MIT license.  See the LICENSE file for details.
*/
//DO NOT test this code on production
//it will merge leads permanently
$delete = new MergeLead();
$delete->id = 40000;
$delete->leadIds = array(10000, 20000, 30000);
print_r($delete->postData());

class MergeLead{
	private $host = "";//CHANGE ME
	private $clientId = "";//CHANGE ME
	private $clientSecret = "";//CHANGE ME
	public $id;//winning lead id
	public $leadIds; //array of one or more losing IDs
	
	public function postData(){
		$url = $this->host . "/rest/v1/leads/" . $this->id ."/merge.json?access_token=" . $this->getToken() . "&leadIds=" . $this::csvString($this->leadIds);
		$ch = curl_init($url);
		curl_setopt($ch,  CURLOPT_RETURNTRANSFER, 1);
		curl_setopt($ch, CURLOPT_HTTPHEADER, array('accept: application/json','Content-Type: application/json'));
		curl_setopt($ch, CURLOPT_POST, 1);
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