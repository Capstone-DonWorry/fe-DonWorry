<?php
    $con = mysqli_connect("localhost", "donworry", "s32202319!", "donworry");
    mysqli_query($con,'SET NAMES utf8');

    // post로 데이터 받기
    $userID = isset($_POST["userID"]) ? $_POST["userID"] : "";
    $newExpenseGoal = isset($_POST["newExpenseGoal"]) ? $_POST["newExpenseGoal"] : "";
    
    $statement = mysqli_prepare($con, "UPDATE USER SET expenseGoal = ? WHERE userID = ?");
    mysqli_stmt_bind_param($statement, "is", $newExpenseGoal, $userID);
    mysqli_stmt_execute($statement);

    // 응답 결과
    $response = array();
    $response["success"] = false;
 
    if (mysqli_stmt_affected_rows($statement) > 0) {
        $response["success"] = true;
    }

    echo json_encode($response);



?>