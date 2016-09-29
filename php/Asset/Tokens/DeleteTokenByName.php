<?php
/*
   DeleteTokenByName.php

   Marketo REST API Sample Code
   Copyright (C) 2016 Marketo, Inc.

   This software may be modified and distributed under the terms
   of the MIT license.  See the LICENSE file for details.
*/
$delete = new DeleteTokens();
$delete->id = 1071;
$delete->folderType = "Program";
$delete->name = "New Token - PHP";
$delete->type = "text";
print_r($delete->postData());


class DeleteTokens{
	private $host = "CHANGE ME";
	private $clientId = "CHANGE ME";
	private $clientSecret = "CHANGE ME";
	//all params required
	public $id;//id of folder to delete from
	public $folderType;//type of folder to delete from
	public $name;//name of token to delete
	public $type;//type of token to delete
	
	public function postData(){
		$url = $this->host . "/rest/asset/v1/folder/" . $this->id . "/tokens/delete.json?access_token=" . $this->getToken()
					. "&folderType=" . $this->folderType . "&name=" . $this->name . "&type=" . $this->type;
		$ch = curl_init($url);
		curl_setopt($ch,  CURLOPT_RETURNTRANSFER, 1);
		curl_setopt($ch, CURLOPT_HTTPHEADER, array('accept: application/json'));
		curl_setopt($ch, CURLOPT_POST, 1);
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

	}	
}