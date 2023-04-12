<?php 
    include "connection.php";
    if(isset($_POST['post']) == 'smarttrashbin'){
        $selectQuery = "SELECT * FROM `smarttrashbin_tb`";
        $resultSet = $conn->query($selectQuery);
        if($resultSet->num_rows > 0){
            foreach($resultSet as $row){
                $result = array('data' => $row['data'], 'mode' => $row['mode']);
                echo json_encode($result);
            }
        }
    }
?>