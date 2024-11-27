package com.example.capstone_donworry.fragment.person;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ViewModelPerson extends ViewModel {

    private final MutableLiveData<String> mText;
    private final MutableLiveData<String> userName;

    public ViewModelPerson() {
        mText = new MutableLiveData<>();
        userName = new MutableLiveData<>();
        mText.setValue("This is Calendar fragment");
        userName.setValue("");
    }

    public LiveData<String> getText() {
        return mText;
    }

    public void setUserName(String userid) {
        userName.setValue(userid);
    }

    public MutableLiveData<String> getUserName() {
        return userName;
    }
}