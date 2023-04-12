<?php 
    include "connection.php";
    if(isset($_POST['post']) == 'smarttrashbin'){
        $mode = $_POST['mode'];
        $updateQuery = "UPDATE `smarttrashbin_tb` SET `mode`='$mode', `date_created` = null ";
        if($conn->query($updateQuery)){
            echo "Update Sucess!";
        }
    }
?>