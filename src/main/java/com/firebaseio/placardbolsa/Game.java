package com.firebaseio.placardbolsa;

public class Game {
    private String event_index;
    private String home_opponent;
    private String away_opponent;
    private String sport;
    private String game_result;
    private String outcome_description;
    private String outcome_index;
    private String outcome_odd;
    private String handicap_value;
    private String bet_type;


    public Game() {
    }

    public Game(String event_index, String home_opponent, String away_opponent, String sport, String game_result, String outcome_description, String outcome_index, String outcome_odd, String handicap_value, String bet_type) {
        this.event_index = event_index;
        this.home_opponent = home_opponent;
        this.away_opponent = away_opponent;
        this.sport = sport;
        this.game_result = game_result;
        this.outcome_description = outcome_description;
        this.outcome_index = outcome_index;
        this.outcome_odd = outcome_odd;
        this.handicap_value = handicap_value;
        this.bet_type = bet_type;

    }

    public String getEvent_index() {
        return event_index;
    }

    public String getHome_opponent() {
        return home_opponent;
    }
    public String getAway_opponent() {
        return away_opponent;
    }

    public String getSport() {
        return sport;
    }

    public String getGame_result() {
        return game_result;
    }

    public String getOutcome_description() {
        return outcome_description;
    }

    public String getOutcome_index() {
        return outcome_index;
    }

    public String getOutcome_odd() {
        return outcome_odd;
    }

    public String getHandicap_value() {
        return handicap_value;
    }

    public String getBet_type() {
        return bet_type;
    }
}
