<?php 
    include "connection.php";
    if(isset($_POST['post']) == 'smarttrashcan'){
        $mode = $_POST['mode'];
        $updateQuery = "UPDATE `smarttrashcan_tb` SET `mode`='$mode', `date_created` = null ";
        if($conn->query($updateQuery)){
            echo "Update Sucess!";
        }
    }
?>