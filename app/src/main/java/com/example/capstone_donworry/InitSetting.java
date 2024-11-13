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
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import org.json.JSONException;
import org.json.JSONObject;

public class InitSetting extends AppCompatActivity {
    private FirebaseAuth firebaseAuth; // 파이어 베이스 인증
    private DatabaseReference databaseReference; // 실시간 데이터 베이스

    private String loginId, pw;
    private EditText InputMoney, InputNickName;
    private EditText InputAge;
    private Button SignUPBtn;
    public Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init_setting);

        // 스택에서 제거할 액티비티를 리스트에 저장
        StartPage startPage = new StartPage();
        startPage.actList().add(this);

        context = this;

        // 회원가입 처리
        Intent intent = getIntent();
        loginId = intent.getStringExtra("loginId");
        pw = intent.getStringExtra("pw");

        // ID 값 찾기
        InputMoney = (EditText) findViewById(R.id.InputMoney);
        InputMoney.addTextChangedListener(new CustomComma(InputMoney));
        InputNickName = (EditText) findViewById(R.id.InputNickName);
        InputAge = (EditText) findViewById(R.id.InputAge);

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

        // 회원 가입 버튼
        SignUPBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int expenseGoal = Integer.parseInt(InputMoney.getText().toString().replace(",", ""));
                int age = Integer.parseInt(InputAge.getText().toString());
                String nickName = InputNickName.getText().toString().trim();
                Toast.makeText(getApplicationContext(), expenseGoal + nickName+ age+ loginId+ pw, Toast.LENGTH_SHORT).show();

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");
                            if (success) {
                                Toast.makeText(getApplicationContext(), "회원 가입 성공", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(getApplicationContext(), Login.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(getApplicationContext(), "회원 가입 실패", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                };
                // Volley를 이용해서 서버로 요청
                RegisterRequest registerRequest = new RegisterRequest(loginId, pw, nickName, age, expenseGoal, responseListener);
                RequestQueue queue = Volley.newRequestQueue(context);
                queue.add(registerRequest);
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