<?php
$response = $_REQUEST['responseCode'];
$response = substr($response, 0, 3);
if(!empty($response)) {
	header("HTTP/1.0 $response");
}
echo "ResponseCode: $response";
?>
