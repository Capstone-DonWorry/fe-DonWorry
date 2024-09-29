package com.example.capstone_donworry;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class InitSetting extends AppCompatActivity {

    private EditText InputMoney, InputAge, InputNickName;
    private Button SignUPBtn;
    SQLiteDatabase database;
    public static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 스택에서 제거할 액티비티를 리스트에 저장
        StartPage startPage = new StartPage();
        startPage.actList().add(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init_setting);

        context = this;

        ImageView BackArrow = (ImageView) findViewById(R.id.BackArrow);
        BackArrow.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SignUp.class);
                startActivity(intent);
            }
        });

        SignUPBtn = (Button) findViewById(R.id.SignUPBtn);
        SignUPBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
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

            @Override
            public void afterTextChanged(Editable s) {
                SignUPBtn.setBackgroundResource(R.drawable.round_shape_mid_blue);
                SignUPBtn.setTextColor(context.getResources().getColorStateList(R.color.white));
            }
        });
    }



    /*
    public void openDatabase(String databaseName) {
        database = openOrCreateDatabase(databaseName, MODE_PRIVATE, null);
    }

    public void insertData(String name, String id, String pw, int money, int age, String nickName){
        if(database != null){
            String sql = "insert into 데이터베이스명(name, id, pw, money, age, nickName) values(?, ?, ?, ?, ?, ?)";
            Object[] params = {name, id, pw, money, age, nickName};

            database.execSQL(sql, params);
        }
    }
    */

}