package com.example.capstone_donworry;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class InitSetting extends AppCompatActivity {
    private FirebaseAuth firebaseAuth; // 파이어 베이스 인증
    private DatabaseReference databaseReference; // 실시간 데이터 베이스

    private EditText InputMoney, InputNickName;
    private Spinner InputAge;
    private Button SignUPBtn;
    public Context context;
    private String loginId, pw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init_setting);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // 회원가입 처리
        Intent intent = getIntent();
        loginId = intent.getStringExtra("loginId");
        pw = intent.getStringExtra("pw");

        // 스택에서 제거할 액티비티를 리스트에 저장
        StartPage startPage = new StartPage();
        startPage.actList().add(this);

        context = this;

        // ID 값 찾기
        InputMoney = (EditText) findViewById(R.id.InputMoney);
        InputNickName = (EditText) findViewById(R.id.InputNickName);

        InputAge = (Spinner) findViewById(R.id.InputAge);

        SignUPBtn = (Button) findViewById(R.id.SignUPBtn);

        // 뒤로가기 버튼
        ImageView BackArrow = (ImageView) findViewById(R.id.BackArrow);
        BackArrow.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SignUp.class);
                startActivity(intent);
                finish();
            }
        });

        SignUPBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);

                firebaseAuth.createUserWithEmailAndPassword(loginId, pw).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                        }

                    }
                });
                /*
                String databaseName = 데이터베이스명;  // 데이터 베이스 이름 설정
                openDatabase(databaseName);

                String name = ((SignUp)SignUp.context).name;
                String id = ((SignUp)SignUp.context).id;
                String pw = ((SignUp)SignUp.context).pw;
                String moneyStr = InputMoney.getText().toString().trim();
                String ageStr = InputAge.getText().toString().trim();
                String nickName = InputNickName.getText().toString().trim();

                int money = -1;
                int age = -1;
                try{
                    money = Integer.parseInt(moneyStr);
                    age = Integer.parseInt(ageStr);
                }catch (Exception e){}

                insertData(name, id, pw, money, age, nickName);
                */
            }
        });

        // editText 리스너
        InputNickName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @SuppressLint("UseCompatLoadingForColorStateLists")
            @Override
            public void afterTextChanged(Editable s) {
                SignUPBtn.setBackgroundResource(R.drawable.round_shape_mid_blue);
                SignUPBtn.setTextColor(context.getResources().getColorStateList(R.color.white));
                SignUPBtn.setEnabled(true);
            }
        });
    }

}