package com.example.capstone_donworry;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class Login extends AppCompatActivity {

    private EditText LoginID, LoginPassword;
    private TextView SignUp, FindId, RePassword;
    private Button LoginButton;

    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 스택에서 제거할 액티비티를 리스트에 저장
        StartPage startPage = new StartPage();
        // ArrayList에 저장한 액티비티 finish한다.
        for(int i=0; i<startPage.actList().size(); i++){
            startPage.actList().get(i).finish();
        }

        // ID 값 찾기
        LoginID = (EditText) findViewById(R.id.LoginID);
        LoginPassword = (EditText) findViewById(R.id.LoginPassword);

        LoginButton = (Button) findViewById(R.id.LoginButton);

        SignUp = (TextView) findViewById(R.id.SignUp);
        FindId = (TextView) findViewById(R.id.FindId);
        RePassword = (TextView) findViewById(R.id.RePassword);

        context = this;

        // 회원가입 버튼 클릭시
        SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, SignUp.class);
                startActivity(intent);
            }
        });

        // 아이디 찾기 버튼 클릭시
        FindId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, FindID.class);
                startActivity(intent);
            }
        });

        // 비밀번호 재설정 버튼 클릭시
        RePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, FindPassword.class);
                startActivity(intent);
            }
        });

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String loginId = LoginID.getText().toString().trim();
                String pw = LoginPassword.getText().toString().trim();

                if (loginId.isEmpty() || pw.isEmpty()) {
                    Toast.makeText(context, "아이디와 비밀번호를 입력해 주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            // 1. "status" 확인
                            String status = jsonObject.getString("status");
                            if (!"Success".equalsIgnoreCase(status)) {
                                Toast.makeText(getApplicationContext(), "로그인 실패", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            // 2. "data" 파싱
                            JSONObject data = jsonObject.getJSONObject("data");

                            Log.d("data", "data: " + data);

                            String token = data.getString("token");
                            String userID = data.getString("loginId");
                            String name = data.getString("name");
                            String role = data.getString("role");
                            String nickname = data.getString("nickname");
                            String monthGoal = data.getString("monthGoal");

                            // 로그 출력
                            Log.d("LoginResponse", "token: " + token);
                            Log.d("LoginResponse", "userID: " + userID);
                            Log.d("LoginResponse", "name: " + name);
                            Log.d("LoginResponse", "role: " + role);
                            Log.d("LoginResponse", "nickname: " + nickname);


                            // 3. 토큰 및 유저정보 저장
                            SharedPreferences sharedPreferences = getSharedPreferences("UserInfo", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("token", token);
                            editor.putString("userID", userID);
                            editor.putString("name", name);
                            editor.putString("role", role);
                            editor.apply();

                            // 4. 다음 화면으로 이동
                            Intent intent = new Intent(Login.this, MainActivity.class);
                            intent.putExtra("userID", userID);
                            intent.putExtra("nickName", nickname);         // name은 사용자의 이름
                            intent.putExtra("expenseGoal", monthGoal);
                            startActivity(intent);
                            finish();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "서버 응답 파싱 오류", Toast.LENGTH_SHORT).show();
                        }
                    }
                };

                LoginRequest loginRequest = new LoginRequest(loginId, pw, responseListener);
                RequestQueue queue = Volley.newRequestQueue(context);
                queue.add(loginRequest);
            }
        });

        // editText 리스너
        LoginPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                LoginButton.setBackgroundResource(R.drawable.round_shape_mid_blue);
                LoginButton.setTextColor(context.getResources().getColorStateList(R.color.white));
            }
        });
    }
}