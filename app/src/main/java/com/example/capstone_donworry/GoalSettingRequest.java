package com.example.capstone_donworry;

import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class GoalSettingRequest extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... strings) {
        String userId = strings[0];
        String newExpenseGoal = strings[1];

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String responseJson = "";

        try {
            URL url = new URL("http://donworry.ivyro.net/expenseGoalSet.php");

            // HttpURLConnection 설정
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            urlConnection.setDoOutput(true);

            // POST 데이터 전송
            String postData = "userID=" + userId + "&newExpenseGoal=" + newExpenseGoal;
            DataOutputStream outputStream = new DataOutputStream(urlConnection.getOutputStream());
            outputStream.writeBytes(postData);
            outputStream.flush();
            outputStream.close();

            // 응답 받기
            reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String line;
            StringBuilder response = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            responseJson = response.toString();

        } catch (Exception e) {
            Log.e("UpdateExpenseGoal", "Error", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            try {
                if (reader != null) {
                    reader.close();
                }
            }catch (Exception e) {
                Log.e("UpdateExpenseGoal", "Error", e);
            }
        }
        return responseJson;
    }

    @Override
    protected void onPostExecute(String s) {
        try {
            JSONObject responseJson = new JSONObject(s);
            String success = responseJson.getString("success");

            if (success.equals("true")) {
                Log.d("UpdateGoal", "Update success");
            } else {
                Log.d("UpdateGoal", "Update fail");
            }
        } catch (Exception e) {
            Log.e("UpdateExpenseGoal", "Json parsing error", e);
        }
    }
}