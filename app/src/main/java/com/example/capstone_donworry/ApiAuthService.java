package com.example.capstone_donworry;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiAuthService {
    @POST("/register")
    Call<String> registerUser(@Body ApiUserRegistrationDto registrationDto);

    @POST("/login")
    Call<String> loginUser(@Body ApiUserLoginDto loginDto);
}
