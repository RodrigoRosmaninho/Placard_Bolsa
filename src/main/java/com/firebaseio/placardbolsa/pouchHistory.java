package com.firebaseio.placardbolsa;

public class pouchHistory {
    private String event_date;
    private String pouch_content;

    public pouchHistory() {
    }

    public pouchHistory(String event_date, String pouch_content) {
        this.event_date = event_date;
        this.pouch_content = pouch_content;
    }

    public String getEvent_date() {
        return event_date;
    }

    public String getPouch_content() {
        return pouch_content;

    }
}
