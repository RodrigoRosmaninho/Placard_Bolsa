package com.firebaseio.placardbolsa;

public class betResult {
    private String number_wrong;
    private String total_result;

    public betResult() {
    }

    public betResult(String number_wrong, String total_result) {
        this.number_wrong = number_wrong;
        this.total_result = total_result;
    }

    public String getNumber_wrong() {
        return number_wrong;
    }

    public String getTotal_result() {
        return total_result;
    }
}

