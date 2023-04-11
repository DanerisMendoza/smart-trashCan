<?php
    include "connection.php";
    if(isset($_POST['post']) == 'smarttrashbin'){
        $data = $_POST['data'];
        $sql = "INSERT INTO `smarttrashbin_tb`( `data`) VALUES ('$data') ";
        if($conn->query($sql)) 
            echo "Sucess insert!";
        else
            die($conn->error);
    }
    else{
        echo "unauthorized access!";
    }
?>