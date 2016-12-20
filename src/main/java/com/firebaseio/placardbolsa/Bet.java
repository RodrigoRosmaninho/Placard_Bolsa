package com.firebaseio.placardbolsa;

public class Bet {
    private String date;
    private String bet_index;
    private String bet_price;
    private String game_number;
    private String projected_winnings;
    private String general_bet_type;
    private String odd_total;
    private Object result;


    public Bet() {
    }

    public Bet(String bet_index, String date, String bet_price, String game_number, String projected_winnings, String general_bet_type, String odd_total, Object result) {
        this.date = date;
        this.bet_index = bet_index;
        this.bet_price = bet_price;
        this.game_number = game_number;
        this.projected_winnings = projected_winnings;
        this.general_bet_type = general_bet_type;
        this.odd_total = odd_total;
        this.result = result;
    }

    public String getDate() {
        return date;
    }

    public String getBet_index() {
        return bet_index;
    }

    public String getBet_price() {
        return bet_price;
    }

    public String getGame_number() {
        return game_number;
    }

    public String getProjected_winnings() {
        return projected_winnings;
    }

    public String getGeneral_bet_type() {
        return general_bet_type;
    }

    public String getOdd_total() {
        return odd_total;
    }

    public Object getResult() {
        return result;
    }

}

