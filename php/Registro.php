<?php

$DB_SERVER="127.0.0.1"; #la dirección del servidor
$DB_USER="Xkreche001"; #el usuario para esa base de datos
$DB_PASS="9IJ1RYEdy5"; #la clave para ese usuario
$DB_DATABASE="Xkreche001_BDPROYECTO"; #la base de datos a la que hay que conectarse
# Se establece la conexión:
$con = mysqli_connect($DB_SERVER, $DB_USER, $DB_PASS, $DB_DATABASE);

#Comprobamos conexión
if (mysqli_connect_errno()) {
echo 'Error de conexion: ' . mysqli_connect_error();
exit();
}
$nombre = $_POST["nombreUsuario"];
$email =$_POST['email'];
$contraseña = $_POST["contraseña"];
$token = $_POST["tokenID"];

$contraseñaHash = password_hash($contraseña, PASSWORD_DEFAULT);


$resultado = mysqli_query($con, "INSERT INTO usuarios (Nombre, Contraseña, DEVICEID, Correo) VALUES ('$nombre', '$contraseñaHash', '$token', '$email')");



# Comprobar si se ha ejecutado correctamente
if (!$resultado) {
    echo 'Ha ocurrido algún error: ' . mysqli_error($con);
}

?>
