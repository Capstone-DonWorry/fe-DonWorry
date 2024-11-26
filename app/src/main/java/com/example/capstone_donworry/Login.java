package com.example.capstone_donworry;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

        // 로그인 버튼 클릭 시 수행
        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String loginId = LoginID.getText().toString().trim();
                String pw = LoginPassword.getText().toString().trim();

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");
                            if (success) {
                                String expenseGoal = jsonObject.getString("expenseGoal");
                                String userID = jsonObject.getString("userID");
                                String nickName = jsonObject.getString("nickName");

                                Toast.makeText(getApplicationContext(), "로그인 성공", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(Login.this, MainActivity.class);
                                intent.putExtra("expenseGoal", expenseGoal);
                                intent.putExtra("userID", userID);
                                intent.putExtra("nickName", nickName);

                                startActivity(intent);
                            } else {
                                Toast.makeText(getApplicationContext(), "로그인 실패", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
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