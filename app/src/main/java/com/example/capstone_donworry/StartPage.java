package com.example.capstone_donworry;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class StartPage extends AppCompatActivity {

    // 스택에 쌓인 액티비티들 중 제거할 액티비티 리스트
    public static ArrayList<Activity> actList = new ArrayList<Activity>();

    // 인스턴스 반환 메소드
    public ArrayList<Activity> actList(){
        return actList;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_page);

        Button StartBTN = (Button) findViewById(R.id.StartBtn);
        StartBTN.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SignUp.class);
                startActivity(intent);
            }
        });

        TextView StartLoginBtn = findViewById(R.id.StartLoginBtn);
        StartLoginBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
            }
        });
    }
}