package com.example.capstone_donworry.model.statistics;

public class WeeklyStatistic {
    private int year;
    private int week;
    private Long totalExpense;
    private String startDate;
    private String endDate;
    private Long dailyGoal;

    public int getYear() {
        return year;
    }


    public int getWeek() {
        return week;
    }


    public Long getTotalExpense() {
        return totalExpense;
    }

    public void setTotalExpense(Long totalExpense) {
        this.totalExpense = totalExpense;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public Long getDailyGoal() {
        return dailyGoal;
    }

    public void setDailyGoal(Long dailyGoal) {
        this.dailyGoal = dailyGoal;
    }
}


