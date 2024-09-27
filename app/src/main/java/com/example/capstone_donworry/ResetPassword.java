package com.example.capstone_donworry;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class ResetPassword extends AppCompatActivity {

    private TextView RightPassword;
    private EditText InputPassword, InputRePassword;
    private Button ResetPwBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        // id 값 찾기
        RightPassword = (TextView) findViewById(R.id.RightPassword);

        InputPassword = (EditText) findViewById(R.id.InputPassword);
        InputRePassword = (EditText) findViewById(R.id.InputRePassword);

        ResetPwBtn = (Button) findViewById(R.id.ResetPwBtn);

        ImageView BackArrow = (ImageView) findViewById(R.id.BackArrow);
        BackArrow.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), FindPassword.class);
                startActivity(intent);
            }
        });

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

        // 비밀번호 재설정 버튼 클릭 시 수행
        ResetPwBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
            }
        });
    }
}