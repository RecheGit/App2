<?php 

$message = "Bienvenido desde FireBase";
$title = "Notificacion desde FireBase";
$path_to_fcm = 'https://fcm.googleapis.com/fcm/send';
$server_key = "AAAAj7vY8DM:APA91bHRSZYy6ZShFigmoN8H_rdNBjqZNQ5J2lE92nobWWZ7xaMgW6awqJ8rOHFqJP1OElPh-y572TUdrHRoN36ORofcfF7i1ZyMRoU4cAZFPOQNyflyXoTNm5OR3iPGXIY0uYYqoSsg";
$token=$_POST["tokenID"];

$headers = array(
    'Authorization:key=' .$server_key,
    'Content-Type:application/json'
);

$fields = array('to'=>$token,
    'notification'=>array('title'=>$title,'body'=>$message));

$payload = json_encode($fields);

echo $payload;

$curl_session = curl_init();
curl_setopt($curl_session, CURLOPT_URL, $path_to_fcm);
curl_setopt($curl_session, CURLOPT_POST, true);
curl_setopt($curl_session, CURLOPT_HTTPHEADER, $headers);
curl_setopt($curl_session, CURLOPT_RETURNTRANSFER, true);
curl_setopt($curl_session, CURLOPT_SSL_VERIFYPEER, false);
curl_setopt($curl_session, CURLOPT_IPRESOLVE, CURL_IPRESOLVE_V4 );
curl_setopt($curl_session, CURLOPT_POSTFIELDS, $payload);
$result = curl_exec($curl_session);
echo $result;

?>