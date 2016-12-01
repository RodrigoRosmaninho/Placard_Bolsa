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

    public String getBet_type() {
        return bet_type;
    }

    public String getHandicap_value() {
        return handicap_value;
    }
}
