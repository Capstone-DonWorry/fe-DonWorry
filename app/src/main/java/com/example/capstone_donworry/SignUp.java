package com.example.capstone_donworry;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SignUp extends AppCompatActivity {
    private TextView RightPassword;
    private EditText InputID, InputPassword, InputRePassword;
    private Button SignUPNextBtn;

    public String loginId, pw, pwcheck;
    public Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // 스택에서 제거할 액티비티를 리스트에 저장
        StartPage startPage = new StartPage();
        startPage.actList().add(this);

        // id 값 찾기
        RightPassword = (TextView) findViewById(R.id.RightPassword);

        InputID = (EditText) findViewById(R.id.InputID);
        InputPassword = (EditText) findViewById(R.id.InputPassword);
        InputRePassword = (EditText) findViewById(R.id.InputRePassword);

        SignUPNextBtn = (Button) findViewById(R.id.SignUPNextBtn);

        context = this;

        // editText 리스너
        InputRePassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (InputPassword.getText().toString().equals(InputRePassword.getText().toString())) {
                    RightPassword.setText("");
                } else {
                    RightPassword.setText("비밀번호가 일치하지 않습니다.");
                }
            }

            @SuppressLint("UseCompatLoadingForColorStateLists")
            @Override
            public void afterTextChanged(Editable s) {
                if (InputPassword.getText().toString().equals(InputRePassword.getText().toString())) {
                    SignUPNextBtn.setBackgroundResource(R.drawable.round_shape_mid_blue);
                    SignUPNextBtn.setTextColor(context.getResources().getColorStateList(R.color.white));
                    SignUPNextBtn.setEnabled(true);
                }else {
                    SignUPNextBtn.setBackgroundResource(R.drawable.round_shape_gray);
                    SignUPNextBtn.setTextColor(context.getResources().getColorStateList(R.color.dark_gray));
                    SignUPNextBtn.setEnabled(false);
                }
            }
        });

        // 회원가입 다음 버튼 클릭 시 수행
        SignUPNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 회원가입 처리
                loginId = InputID.getText().toString().trim();
                pw = InputPassword.getText().toString().trim();
                pwcheck = RightPassword.getText().toString().trim();


                Intent intent = new Intent(getApplicationContext(), InitSetting.class);
                intent.putExtra("loginId", loginId);
                intent.putExtra("password", pw);
                intent.putExtra("passwordCheck", pwcheck);

                startActivity(intent);
            }
        });

    }
}