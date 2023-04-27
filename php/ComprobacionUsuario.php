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

//echo 'NOmbre:';
//var_export($nombre);
//echo 'Contra: ';
//var_export($contraseña);

$resultado = mysqli_query($con, "SELECT Contraseña FROM usuarios WHERE Nombre='$nombre'AND Correo='$email'");

$row=mysqli_fetch_row($resultado);

$passwordHash = $row[0];
header('Content-Type: application/json; charset=utf-8');

if(password_verify($contraseña, $passwordHash)){
  echo json_encode("1");
}else{
  echo json_encode("0");

}



?>
