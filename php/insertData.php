<?php
    include "connection.php";
    if(isset($_POST['post']) == 'smarttrashbin'){
        $data = $_POST['data'];
        $selectQuery = "SELECT `id` FROM `smarttrashbin_tb`";
        if( $conn->query($selectQuery)->num_rows <= 0){
            $insertQuery = "INSERT INTO `smarttrashbin_tb`( `data`) VALUES ('$data') ";
            if($conn->query($insertQuery)){
                echo "Insert Sucess!";
            }
        }
        else{
            $updateQuery = "UPDATE `smarttrashbin_tb` SET `data`='$data', `date_created` = null ";
            if($conn->query($updateQuery)){
                echo "Update Sucess!";
            }
        }

    }
    else{
        echo "unauthorized access!";
    }
?>