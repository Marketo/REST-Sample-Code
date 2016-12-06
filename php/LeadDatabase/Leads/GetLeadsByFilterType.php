<?php
/*
   GetLeadsByFilterType.php

   Marketo REST API Sample Code
   Copyright (C) 2016 Marketo, Inc.

   This software may be modified and distributed under the terms
   of the MIT license.  See the LICENSE file for details.
*/
$leads = new MultipleLeads();
$leads->filterType = "email";
$leads->filterValues = array("john.doe@marketo.com");
$leads->fields = array("email", "firstName", "lastName");
print_r($leads->getData());

class MultipleLeads{
	private $host = "";//CHANGE ME
	private $clientId = "";//CHANGE ME
	private $clientSecret = "";//CHANGE ME
	public $filterType; //field to filter off of, required
	public $filterValues; //one or more values for filter, required
	public $fields;//one or more fields to return
	public $batchSize;
	public $nextPageToken;//token returned from previous call for paging

	public function getData(){
		$url = $this->host . "/rest/v1/leads.json?access_token=" . $this->getToken()
						. "&filterType=" . $this->filterType . "&filterValues=" . $this::csvString($this->filterValues);
		if (isset($this->batchSize)){
			$url = $url . "&batchSize=" . $this->batchSize;
		}
		if (isset($this->nextPageToken)){
			$url = $url . "&nextPageToken=" . $this->nextPageToken;
		}
		if(isset($this->fields)){
			$url = $url . "&fields=" . $this::csvString($this->fields);
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
                       $i++
		}
		return $csvString;
	}
}
