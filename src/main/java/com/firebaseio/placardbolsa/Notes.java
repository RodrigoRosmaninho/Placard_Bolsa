package com.firebaseio.placardbolsa;

public class Notes {
    private String last_author;
    private String last_edited;
    private String text;

    public Notes() {
    }

    public Notes(String last_author, String last_edited, String text) {
        this.last_author = last_author;
        this.last_edited = last_edited;
        this.text = text;
    }

    public String getLast_author() {
        return last_author;
    }

    public String getLast_edited() {
        return last_edited;
    }

    public String getText() {
        return text;
    }
}
