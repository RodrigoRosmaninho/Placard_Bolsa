package com.firebaseio.placardbolsa;

public class gameOutcome {
    private String actual_outcome;
    private String game_result;

    public gameOutcome() {
    }

    public gameOutcome(String actual_outcome, String game_result) {
        this.actual_outcome = actual_outcome;
        this.game_result = game_result;
    }

    public String getActual_outcome() {
        return actual_outcome;
    }

    public String getGame_result() {
        return game_result;
    }
}

