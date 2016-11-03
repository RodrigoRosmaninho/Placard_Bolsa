package com.firebaseio.placardbolsa;

public class Bet {
    private String Date;
    private String bet_index;
    private String bet_price;
    private Object game_01;
    private Object game_02;
    private Object game_03;
    private Object game_04;
    private Object game_05;
    private Object game_06;
    private Object game_07;
    private Object game_08;
    private String game_number;
    private String projected_winnings;
    private String general_betType;
    private Object result;
    private Object votes;


    public Bet() {
    }

    public Bet(String bet_index, String Date, String bet_price, Object game_01, Object game_02, Object game_03, Object game_04, Object game_05, Object game_06, Object game_07, Object game_08, String game_number, String projected_winnings, String general_betType, Object result, Object votes) {
        this.Date = Date;
        this.bet_index = bet_index;
        this.bet_price = bet_price;
        this.game_01 = game_01;
        this.game_02 = game_02;
        this.game_03 = game_03;
        this.game_04 = game_04;
        this.game_05 = game_05;
        this.game_06 = game_06;
        this.game_07 = game_07;
        this.game_08 = game_08;
        this.game_number = game_number;
        this.projected_winnings = projected_winnings;
        this.general_betType = general_betType;
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

    public Object getGame_01() {
        return game_01;
    }

    public Object getGame_02() {
        return game_02;
    }

    public Object getGame_03() {
        return game_03;
    }

    public Object getGame_04() {
        return game_04;
    }

    public Object getGame_05() {
        return game_05;
    }

    public Object getGame_06() {
        return game_06;
    }

    public Object getGame_07() {
        return game_07;
    }

    public Object getGame_08() {
        return game_08;
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

    public Object getResult() {
        return result;
    }

    public Object getVotes() {
        return votes;
    }

}

