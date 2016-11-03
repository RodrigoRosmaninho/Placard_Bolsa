package com.firebaseio.placardbolsa;

public class desiredOutcome {
    private String description;
    private String index;
    private String outcome_odd;
    private Object type;

    public desiredOutcome() {
    }

    public desiredOutcome(String description, String index, String outcome_odd, Object type) {
        this.description = description;
        this.index = index;
        this.outcome_odd = outcome_odd;
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public String getIndex() {
        return index;
    }

    public String getOutcome_odd() {
        return index;
    }

    public Object getType() {
        return index;
    }
}

