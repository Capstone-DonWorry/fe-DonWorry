package com.example.capstone_donworry.fragment.message;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ViewModelMessage extends ViewModel {

    private final MutableLiveData<String> mText;

    public ViewModelMessage() {
        mText = new MutableLiveData<>();
        mText.setValue("This is Calendar fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}