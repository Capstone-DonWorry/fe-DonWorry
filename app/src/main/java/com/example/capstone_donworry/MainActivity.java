package com.example.capstone_donworry;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;
import android.util.Log;

import com.example.capstone_donworry.fragment.calendar.ViewModelCalendar;
import com.example.capstone_donworry.fragment.person.ViewModelPerson;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private ViewModelCalendar viewModelCalendar;
    private ViewModelPerson viewModelPerson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ViewModel 초기화
        viewModelCalendar = new ViewModelProvider(this).get(ViewModelCalendar.class);
        viewModelPerson = new ViewModelProvider(this).get(ViewModelPerson.class);

        // Intent에서 값 가져오기
        String userID = getIntent().getStringExtra("userID");
        String nickName = getIntent().getStringExtra("nickName");
        String expenseGoal = getIntent().getStringExtra("expenseGoal");

        Log.i("MainActivity", "userID = " + userID);
        Log.i("MainActivity", "nickName = " + nickName);
        Log.i("MainActivity", "expenseGoal = " + expenseGoal);

        // Null 체크 후 ViewModel에 값 전달
        if (userID != null) viewModelCalendar.setUserId(userID);
        if (expenseGoal != null) viewModelCalendar.setExpenseGoal(expenseGoal);
        if (nickName != null) viewModelPerson.setUserName(nickName);

        // BottomNavigationView 설정
        BottomNavigationView navView = findViewById(R.id.NavView);
        NavController navController = Navigation.findNavController(this, R.id.NavFragment);
        NavigationUI.setupWithNavController(navView, navController);
    }
}
