package com.example.capstone_donworry;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;

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

        // Intent에서 expenseGoal 가져오기
        String expenseGoal = getIntent().getStringExtra("expenseGoal");
        String userID = getIntent().getStringExtra("userID");
        viewModelCalendar.setExpenseGoal(expenseGoal);
        viewModelCalendar.setUserId(userID);

        // Intent에서 nickName 가져오기
        String nickName = getIntent().getStringExtra("nickName");
        viewModelPerson.setUserName(nickName);

        // BottomNavigationView 설정
        BottomNavigationView navView = findViewById(R.id.NavView);
        NavController navController = Navigation.findNavController(this, R.id.NavFragment);
        NavigationUI.setupWithNavController(navView, navController);

    }
}