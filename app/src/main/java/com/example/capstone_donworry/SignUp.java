package com.example.capstone_donworry;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SignUp extends AppCompatActivity {
    private TextView RightPassword, AuthPhoneText, AuthNote;
    private EditText InputName, InputID, InputPassword, InputRePassword, InputPhone, AuthPhone;
    private Button CertifyButton, SignUPNextBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // id 값 찾기
        RightPassword = (TextView) findViewById(R.id.RightPassword);
        AuthPhoneText = (TextView) findViewById(R.id.AuthPhoneText);
        AuthNote = (TextView) findViewById(R.id.AuthNote);

        InputName = (EditText) findViewById(R.id.InputName);
        InputID = (EditText) findViewById(R.id.InputID);
        InputPassword = (EditText) findViewById(R.id.InputPassword);
        InputRePassword = (EditText) findViewById(R.id.InputRePassword);
        InputPhone = (EditText) findViewById(R.id.InputPhone);
        AuthPhone = (EditText) findViewById(R.id.AuthPhone);

        CertifyButton = (Button) findViewById(R.id.CertifyButton);
        SignUPNextBtn = (Button) findViewById(R.id.SignUPNextBtn);

        // 비밀번호 일치 여부 확인
        InputRePassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(InputPassword.getText().toString().equals(InputRePassword.getText().toString())){
                    RightPassword.setText("");
                } else {
                    RightPassword.setText("비밀번호가 일치하지 않습니다.");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // 회원가입 버튼 클릭 시 수행
        SignUPNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), InitSetting.class);
                startActivity(intent);
            }
        });

    }
}