package com.example.capstone_donworry;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class InitSetting extends AppCompatActivity {

    private EditText InputMoney, InputAge, InputNickName;
    private Button SignUPBtn;
    private Context context;
    private String loginId, pw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init_setting);

        StartPage startPage = new StartPage();
        startPage.actList().add(this);

        context = this;

        // 이전 화면에서 받은 값
        Intent intent = getIntent();
        loginId = intent.getStringExtra("loginId");
        pw = intent.getStringExtra("password");

        // 뷰 연결
        InputMoney = findViewById(R.id.InputMoney);
        InputAge = findViewById(R.id.InputAge);
        InputNickName = findViewById(R.id.InputNickName);
        SignUPBtn = findViewById(R.id.SignUPBtn);

        // 버튼 활성화 로직 연결 (중요!!)
        setupTextWatchers();

        // 뒤로가기 버튼
        ImageView BackArrow = findViewById(R.id.BackArrow);
        BackArrow.setOnClickListener(v -> {
            Intent backIntent = new Intent(getApplicationContext(), SignUp.class);
            startActivity(backIntent);
            finish();
        });

        // 회원가입 버튼 클릭 시
        SignUPBtn.setOnClickListener(v -> {
            int expenseGoal = Integer.parseInt(InputMoney.getText().toString().replace(",", ""));
            int age = Integer.parseInt(InputAge.getText().toString());
            String nickName = InputNickName.getText().toString().trim();
            int ageGroup = (age / 10 * 10);

            Response.Listener<String> responseListener = response -> {
                Log.d("회원가입", "서버 응답 수신: " + response);
                Toast.makeText(getApplicationContext(), "회원 가입 성공! 로그인 페이지로 이동합니다.", Toast.LENGTH_SHORT).show();

                // ✅ 여기만 수정!
                Intent loginIntent = new Intent(getApplicationContext(), Login.class); // ← 너의 로그인 Activity
                loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(loginIntent);
                finish();
            };

            RegisterRequest registerRequest = new RegisterRequest(
                    loginId, pw, pw, nickName, age, expenseGoal, responseListener
            );

            RequestQueue queue = Volley.newRequestQueue(context);
            queue.add(registerRequest);
        });
    }

    private void setupTextWatchers() {
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String money = InputMoney.getText().toString().trim();
                String age = InputAge.getText().toString().trim();
                String nickname = InputNickName.getText().toString().trim();

                boolean isAllFilled = !money.isEmpty() && !age.isEmpty() && !nickname.isEmpty();

                if (isAllFilled) {
                    SignUPBtn.setEnabled(true);
                    SignUPBtn.setBackgroundResource(R.drawable.round_shape_mid_blue);
                    SignUPBtn.setTextColor(context.getResources().getColorStateList(R.color.white));
                } else {
                    SignUPBtn.setEnabled(false);
                    SignUPBtn.setBackgroundResource(R.drawable.round_shape_gray);
                    SignUPBtn.setTextColor(context.getResources().getColorStateList(R.color.dark_gray));
                }
            }

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
        };

        InputMoney.addTextChangedListener(watcher);
        InputAge.addTextChangedListener(watcher);
        InputNickName.addTextChangedListener(watcher);
    }
}
