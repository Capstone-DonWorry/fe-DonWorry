package com.example.capstone_donworry;

import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class LoginRequest extends StringRequest {

    private static final String URL = "http://10.0.2.2:8080/api/jwt/login";
    private final Map<String, String> params;

    public LoginRequest(String loginId, String password, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);

        params = new HashMap<>();
        params.put("loginId", loginId);
        params.put("password", password);
    }

    @Override
    public String getBodyContentType() {
        return "application/json; charset=UTF-8";
    }

    @Override
    public byte[] getBody() {
        JSONObject jsonBody = new JSONObject(params);
        return jsonBody.toString().getBytes(StandardCharsets.UTF_8);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return null; // JSON 전송 시 사용 안 함
    }
}