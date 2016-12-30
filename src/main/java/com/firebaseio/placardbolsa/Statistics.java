package com.firebaseio.placardbolsa;

import java.util.Objects;

public class Statistics {
    private String all_earnings;
    private String current_share;
    private String on_pouch;
    private String value_spent;
    private Object general_stats;
    private Object vote_stats;
    private String number_ofBets;
    private Object pouch_history;
    private String pendingBetsExist;
    private String number_ofSugg;
    private String number_ofPen;
    private String suggestionsExist;

    public Statistics() {
    }

    public Statistics(String all_earnings, String current_share, String on_pouch, String value_spent, Object general_stats, Object vote_stats, String number_ofBets, Object pouch_history, String pendingBetsExist, String number_ofSugg, String number_ofPen, String suggestionsExist) {
        this.all_earnings = all_earnings;
        this.current_share = current_share;
        this.on_pouch = on_pouch;
        this.value_spent = value_spent;
        this.general_stats = general_stats;
        this.vote_stats = vote_stats;
        this.number_ofBets = number_ofBets;
        this.pouch_history = pouch_history;
        this.pendingBetsExist = pendingBetsExist;
        this.number_ofSugg = number_ofSugg;
        this.number_ofPen = number_ofPen;
        this.suggestionsExist = suggestionsExist;


    }

    public String getAll_earnings() {
        return all_earnings;
    }

    public String getCurrent_share() {
        return current_share;
    }

    public String getOn_pouch() {
        return on_pouch;
    }

    public String getValue_spent() {
        return value_spent;
    }

    public Object getGeneral_stats() {
        return general_stats;
    }

    public Object getVote_stats() {
        return vote_stats;
    }

    public String getNumber_ofBets() {
        return number_ofBets;
    }

    public Object getPouch_history() {
        return pouch_history;
    }

    public String getPendingBetsExist() {
        return pendingBetsExist;
    }

    public String getNumber_ofSugg() {return number_ofSugg;}

    public String getNumber_ofPen() {
        return number_ofPen;
    }

    public String getSuggestionsExist() {return suggestionsExist;}

}
