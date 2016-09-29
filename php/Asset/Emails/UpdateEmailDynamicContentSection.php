<?php
/*
   UpdateEmailDynamicContentSection.php

   Marketo REST API Sample Code
   Copyright (C) 2016 Marketo, Inc.

   This software may be modified and distributed under the terms
   of the MIT license.  See the LICENSE file for details.
*/
$content = new UpdateEmailContentByEditable();
$content->id = 1211;
$content->htmlId = "edit_text_1";
$content->dynamicContentId= "RVMtZWRpdF90ZXh0XzE=";
$content->type = "Text";
$content->value = "This content has been updated again!";
$content->segment = 1003;
print_r($content->postData());

class UpdateEmailContentByEditable{
	private $host = "CHANGE ME";
	private $clientId = "CHANGE ME";
	private $clientSecret = "CHANGE ME";
	public $id;//id of email to update, required
	public $dynamicContentId;//id of dynamic content section
	public $type;//type of section to update
	public $value;//content
	public $segment;//segment name to update content for
	
	public function postData(){
		$url = $this->host . "/rest/asset/v1/email/" . $this->id . "/dynamicContent/" . $this->dynamicContentId . ".json?access_token=" . $this->getToken();
		$ch = curl_init($url);
		$requestBody = $this->bodyBuilder();
		print_r($requestBody);
		curl_setopt($ch,  CURLOPT_RETURNTRANSFER, 1);
		curl_setopt($ch, CURLOPT_HTTPHEADER, array('accept: application/json'));
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
		if (isset($this->type)){
			$requestBody .= "&type=" . $this->type;
		}
		if (isset($this->value)){
			$requestBody .= "&value=" . $this->value;
		}
		if (isset($this->textValue)){
			$requestBody .= "&textValue=" . $this->textValue;
		}
		return $requestBody;
	}	
}