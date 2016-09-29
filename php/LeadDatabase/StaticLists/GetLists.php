<?php
/*
   GetLists.php

   Marketo REST API Sample Code
   Copyright (C) 2016 Marketo, Inc.

   This software may be modified and distributed under the terms
   of the MIT license.  See the LICENSE file for details.
*/
$lists = new MultipleLists();
$lists->ids = [1,2,3,4,5,6,7,8, 1001, 1007];
print_r($lists->getData());

class MultipleLists{
	private $host = "CHANGE ME";
	private $clientId = "CHANGE ME";
	private $clientSecret = "CHANGE ME";
	public $ids; //array of list Ids to retrieve
	public $names;//array of names to to retrieve
	public $programName;//array of program names to retrieve lists from
	public $workspaceName; //array of Workspace names to retrieve lists from
	public $batchSize; //max 300, default 300
	public $nextPageToken; //token retrieved from previous call for paging
	
	public function getData(){
		$url = $this->host . "/rest/v1/lists.json?access_token=" . $this->getToken();
		if (isset($this->ids)){
			$url .= "&id=" . $this::csvString($this->ids);
		}
		if (isset($this->programName)){
			$url .= "&programName=" . $this::csvString($this->programName);
		}
		if (isset($this->workspaceName)){
			$url .= "&workspaceName=" . $this::csvString($this->workspaceName); 
		}
		if (isset($this->names)){
			$url .= "&name=" . $this::csvString($this->names);
		}
		if (isset($this->batchSize)){
			$url .= "&batchSize=" . $this::csvString($this->batchSize);
		}
		if (isset($this->nextPageToken)){
			$url .= "&nextPageToken=" . $this::csvString($this->batchSize);
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
