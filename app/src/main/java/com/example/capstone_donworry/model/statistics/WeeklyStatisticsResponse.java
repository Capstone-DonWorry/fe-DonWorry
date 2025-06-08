package com.example.capstone_donworry.model.statistics;

import java.util.List;

public class WeeklyStatisticsResponse {
    private String status;
    private int code;
    private String message;
    private List<WeeklyStatistic> data;

    public String getStatus() {
        return status;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public List<WeeklyStatistic> getData() {
        return data;
    }

    public void setData(List<WeeklyStatistic> data) {
        this.data = data;
    }
}

