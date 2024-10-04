package com.example.capstone_donworry.fragment.calendar;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ViewModelCalendar extends ViewModel {

    private final MutableLiveData<String> mText;

    public ViewModelCalendar() {
        mText = new MutableLiveData<>();
        mText.setValue("This is Calendar fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}