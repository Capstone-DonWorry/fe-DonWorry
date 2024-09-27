package com.example.capstone_donworry;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class FindPassword extends AppCompatActivity {

    private TextView AuthPhoneText, AuthNote;
    private EditText InputID, InputName, InputPhone, AuthPhone;
    private Button CertifyButton, ResetPwNextBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_password);

        // id 값 찾기
        AuthPhoneText = (TextView) findViewById(R.id.AuthPhoneText);
        AuthNote = (TextView) findViewById(R.id.AuthNote);

        InputID = (EditText) findViewById(R.id.InputID);
        InputName = (EditText) findViewById(R.id.InputName);
        InputPhone = (EditText) findViewById(R.id.InputPhone);
        AuthPhone = (EditText) findViewById(R.id.AuthPhone);

        CertifyButton = (Button) findViewById(R.id.CertifyButton);
        ResetPwNextBtn = (Button) findViewById(R.id.ResetPwNextBtn);

        ImageView BackArrow = (ImageView) findViewById(R.id.BackArrow);
        BackArrow.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }
        });

        // 본인 인증 버튼 클릭시
        ResetPwNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FindPassword.this, ResetPassword.class);
                startActivity(intent);
            }
        });
    }
}