package com.example.capstone_donworry;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Login extends AppCompatActivity {

    private EditText LoginID, LoginPassword;
    private TextView SignUp, FindId, RePassword;
    private Button LoginButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // ID 값 찾기
        LoginID = (EditText) findViewById(R.id.LoginID);
        LoginPassword = (EditText) findViewById(R.id.LoginPassword);

        LoginButton = (Button) findViewById(R.id.LoginButton);

        SignUp = (TextView) findViewById(R.id.SignUp);
        FindId = (TextView) findViewById(R.id.FindId);
        RePassword = (TextView) findViewById(R.id.RePassword);

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
                Intent intent = new Intent(Login.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}