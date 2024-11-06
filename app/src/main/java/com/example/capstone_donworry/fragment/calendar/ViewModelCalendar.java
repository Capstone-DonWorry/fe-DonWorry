package com.example.capstone_donworry.fragment.calendar;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ViewModelCalendar extends ViewModel {

    private final MutableLiveData<String> mText;
    private final MutableLiveData<String> expenseGoal;

    public ViewModelCalendar() {
        mText = new MutableLiveData<>();
        expenseGoal = new MutableLiveData<>();
        mText.setValue("This is Calendar fragment");
        expenseGoal.setValue("0");
    }

    public LiveData<String> getText() {
        return mText;
    }

    public void setExpenseGoal(String goal) {

        Log.d("expenseGoal", "viewmodel: " + goal);
        expenseGoal.setValue(goal);
    }

    public LiveData<String> getExpenseGoal() {
        return expenseGoal;
    }
}