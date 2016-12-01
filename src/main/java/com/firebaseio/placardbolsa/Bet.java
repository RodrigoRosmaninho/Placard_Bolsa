package com.firebaseio.placardbolsa;

public class Bet {
    private String Date;
    private String bet_index;
    private String bet_price;
    private String game_number;
    private String projected_winnings;
    private String general_betType;
    private String odd_total;
    private Object result;
    private Object votes;


    public Bet() {
    }

    public Bet(String bet_index, String Date, String bet_price, String game_number, String projected_winnings, String general_betType, String odd_total, Object result, Object votes) {
        this.Date = Date;
        this.bet_index = bet_index;
        this.bet_price = bet_price;
        this.game_number = game_number;
        this.projected_winnings = projected_winnings;
        this.general_betType = general_betType;
        this.odd_total = odd_total;
        this.result = result;
        this.votes = votes;
    }

    public String getDate() {
        return Date;
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

    public String getGeneral_betType() {
        return general_betType;
    }

    public String getOdd_total() {
        return odd_total;
    }

    public Object getResult() {
        return result;
    }

    public Object getVotes() {
        return votes;
    }

}

