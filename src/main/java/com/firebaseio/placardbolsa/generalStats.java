package com.firebaseio.placardbolsa;

public class generalStats {
    private String bet_number;
    private String lost_number;
    private String win_number;

    public generalStats() {
    }

    public generalStats(String bet_number, String lost_number, String win_number) {
        this.bet_number = bet_number;
        this.lost_number = lost_number;
        this.win_number = win_number;
    }

    public String getBet_number() {
        return bet_number;
    }

    public String getLost_number() {
        return lost_number;
    }

    public String getWin_number() {
        return win_number;
    }
}
