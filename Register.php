<?php 
    $con = mysqli_connect("localhost", "donworry", "s32202319!", "donworry");
    mysqli_query($con,'SET NAMES utf8');

    $userID = isset($_POST["userID"]) ? $_POST["userID"] : "";
    $userPW = isset($_POST["userPW"]) ? $_POST["userPW"] : "";
    $nickName = isset($_POST["nickName"]) ? $_POST["nickName"] : "";
    $age = isset($_POST["age"]) ? $_POST["age"] : "";
    $expenseGoal = isset($_POST["expenseGoal"]) ? $_POST["expenseGoal"] : "";

    $statement = mysqli_prepare($con, "INSERT INTO USER VALUES (?,?,?,?,?)");
    mysqli_stmt_bind_param($statement, "sssii", $userID, $userPW, $nickName, $age, $expenseGoal);
    mysqli_stmt_execute($statement);


    $response = array();
    $response["success"] = true;
 
   
    echo json_encode($response);



?>