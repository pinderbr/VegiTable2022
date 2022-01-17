<?php

$servername= "softsquad-vegitable-mysql.crexxnpndfnv.ca-central-1.rds.amazonaws.com";
$username="jasonbeattie";
$password="VegiTable_2021";
$dbname="VegiTable";
$table="BucketHistory";

$bucketId_fk = 1;
$deviceID=$_POST["deviceID"];
$ph = $_POST["phValue"];
$temperature = $_POST["tempValue"];
$ppm = $_POST["ppmValue"];
$water = $_POST["waterValue"];
$humidity = $_POST["humValue"];
$light = $_POST["lightValue"];

$conn = new mysqli($servername, $username, $password, $dbname);
//select statement here
$sql = "INSERT INTO $table
        (currentDateTime, deviceID, phValue, temperatureValue, ppmValue, waterValue,  humidityValue, lightValue, bucketId_fk)
        VALUES(default, $deviceID, $ph, $temperature, $ppm, $water, $humidity, $light, $bucketId_fk);";

if ($conn->connect_error) {
  die("Connection failed: " . $conn->connect_error);
}

if($conn->query($sql) === TRUE){
        echo "New record created successfully";
}else{
        echo "Error: " . $sql . "<br>" . $conn->error;
}

$conn->close();

?>
