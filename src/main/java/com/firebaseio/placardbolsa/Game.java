package com.firebaseio.placardbolsa;

public class Game {
    private String event_index;
    private String date;
    private String home_opponent;
    private String away_opponent;
    private Object desired_outcome;
    private String sport;
    private Object outcome;

    public Game() {
    }

    public Game(String event_index, String date, String home_opponent, String away_opponent, Object desired_outcome, String sport, Object outcome) {
        this.event_index = event_index;
        this.date = date;
        this.home_opponent = home_opponent;
        this.away_opponent = away_opponent;
        this.desired_outcome = desired_outcome;
        this.sport = sport;
        this.outcome = outcome;


    }

    public String getEvent_index() {
        return event_index;
    }

    public String getDate() {
        return date;
    }

    public String getHome_opponent() {
        return home_opponent;
    }
    public String getAway_opponent() {
        return away_opponent;
    }

    public Object getDesired_outcome() {
        return desired_outcome;
    }

    public String getSport() {
        return sport;
    }
    public Object getOutcome() {
        return outcome;
    }
}
