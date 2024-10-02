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
import android.widget.ImageView;
import android.widget.TextView;

public class FindPassword extends AppCompatActivity {

    private EditText InputID, InputName;
    private Button ResetPwNextBtn;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_password);

        // 스택에서 제거할 액티비티를 리스트에 저장
        StartPage startPage = new StartPage();
        startPage.actList().add(this);

        // id 값 찾기
        InputID = (EditText) findViewById(R.id.InputID);
        InputName = (EditText) findViewById(R.id.InputName);

        ResetPwNextBtn = (Button) findViewById(R.id.ResetPwNextBtn);

        context = this;

        ImageView BackArrow = (ImageView) findViewById(R.id.BackArrow);
        BackArrow.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }
        });

        // editText 리스너
        InputName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                ResetPwNextBtn.setBackgroundResource(R.drawable.round_shape_mid_blue);
                ResetPwNextBtn.setTextColor(context.getResources().getColorStateList(R.color.white));
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