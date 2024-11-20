package com.example.capstone_donworry.fragment.calendar;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ViewModelCalendar extends ViewModel {

    private final MutableLiveData<String> mText;
    private final MutableLiveData<String> expenseGoal;
    private final MutableLiveData<String> userId;

    public ViewModelCalendar() {
        mText = new MutableLiveData<>();
        expenseGoal = new MutableLiveData<>();
        userId = new MutableLiveData<>();
        mText.setValue("This is Calendar fragment");
        expenseGoal.setValue("0");
        userId.setValue("");
    }

    public LiveData<String> getText() {
        return mText;
    }

    public void setExpenseGoal(String goal) {
        expenseGoal.setValue(goal);
    }

    public LiveData<String> getExpenseGoal() {
        return expenseGoal;
    }

    public void setUserId(String userid) {
        userId.setValue(userid);
    }

    public MutableLiveData<String> getUserId() {
        return userId;
    }
}