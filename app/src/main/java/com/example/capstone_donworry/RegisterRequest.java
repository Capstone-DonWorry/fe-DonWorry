package com.example.capstone_donworry;

import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class RegisterRequest extends StringRequest {

    // 스프링 백엔드 API 주소 설정 (에뮬레이터에서는 10.0.2.2 사용)
    private static final String URL = "http://10.0.2.2:8080/api/member/signup";
    private final Map<String, String> map;

    public RegisterRequest(String loginId, String password, String passwordCheck,
                           String nickname, int age, int goalAmount,
                           Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);

        map = new HashMap<>();
        map.put("loginId", loginId);
        map.put("password", password);
        map.put("passwordCheck", passwordCheck);
        map.put("nickname", nickname);
        map.put("ageGroup", getAgeGroup(age));
        map.put("goalAmount", String.valueOf(goalAmount));

        // 테스트용 하드코딩 (실제 입력 필드에서 받아야 함)
        map.put("name", "홍길동");
        map.put("phoneNumber", "01012345678");
    }

    @Nullable
    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }

    // 연령대 계산 함수 (20대, 30대 등)
    private String getAgeGroup(int age) {
        int group = (age / 10) * 10;
        return String.valueOf(group);
    }
}
