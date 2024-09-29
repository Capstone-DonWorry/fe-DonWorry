package com.example.capstone_donworry;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class FindID extends AppCompatActivity {

    private TextView AuthPhoneText, AuthNote;
    private EditText InputName, InputPhone, AuthPhone;
    private Button CertifyButton, FindIdBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 스택에서 제거할 액티비티를 리스트에 저장
        StartPage startPage = new StartPage();
        startPage.actList().add(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_id);

        // id 값 찾기
        //AuthPhoneText = (TextView) findViewById(R.id.AuthPhoneText);
        //AuthNote = (TextView) findViewById(R.id.AuthNote);

        InputName = (EditText) findViewById(R.id.InputName);
        //InputPhone = (EditText) findViewById(R.id.InputPhone);
        //AuthPhone = (EditText) findViewById(R.id.AuthPhone);

        //CertifyButton = (Button) findViewById(R.id.CertifyButton);
        FindIdBtn = (Button) findViewById(R.id.FindIdBtn);

        ImageView BackArrow = (ImageView) findViewById(R.id.BackArrow);
        BackArrow.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }
        });
    }
}