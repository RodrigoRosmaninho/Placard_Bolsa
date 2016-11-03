package com.firebaseio.placardbolsa;

public class gameType {
    private String bet_type;
    private String handicap_value;

    public gameType() {
    }

    public gameType(String bet_type, String handicap_value) {
        this.bet_type = bet_type;
        this.handicap_value = handicap_value;
    }

    public String getNumber_wrong() {
        return bet_type;
    }

    public String getTotal_result() {
        return handicap_value;
    }
}
