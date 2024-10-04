package com.example.capstone_donworry.fragment.chart;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ViewModelChart extends ViewModel {

    private final MutableLiveData<String> mText;

    public ViewModelChart() {
        mText = new MutableLiveData<>();
        mText.setValue("This is Calendar fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}