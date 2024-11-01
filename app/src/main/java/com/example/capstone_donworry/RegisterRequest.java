package com.example.capstone_donworry;

import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class RegisterRequest extends StringRequest {

    // 서버 URL 설정
    final static private String URL = "http://donworry.ivyro.net/Register.php";
    private Map<String, String> map;

    public RegisterRequest(String userID, String userPW, String nickName, int age, int expenseGoal, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);

        map = new HashMap<>();
        map.put("userID", userID);
        map.put("userPW", userPW);
        map.put("nickName", nickName);
        map.put("age", age + "");
        map.put("expenseGoal", expenseGoal + "");
    }

    @Nullable
    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}
