<?php
    $con = mysqli_connect("localhost", "donworry", "s32202319!", "donworry");
    mysqli_query($con,'SET NAMES utf8');

    $userID = isset($_POST["userID"]) ? $_POST["userID"] : "";
    $userPW = isset($_POST["userPW"]) ? $_POST["userPW"] : "";
    
    $statement = mysqli_prepare($con, "SELECT * FROM USER WHERE userID = ? AND userPW = ?");
    mysqli_stmt_bind_param($statement, "ss", $userID, $userPW);
    mysqli_stmt_execute($statement);


    mysqli_stmt_store_result($statement);
    mysqli_stmt_bind_result($statement, $userID, $userPW, $nickName, $age, $expenseGoal);

    $response = array();
    $response["success"] = false;
 
    while(mysqli_stmt_fetch($statement)) {
        $response["success"] = true;
        $response["userID"] = $userID;
        $response["userPW"] = $userPW;
        $response["nickName"] = $nickName;
        $response["age"] = $age;
        $response["expenseGoal"] = $expenseGoal;
    }

    echo json_encode($response);



?>