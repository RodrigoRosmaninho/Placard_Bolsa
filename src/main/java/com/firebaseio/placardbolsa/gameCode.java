package com.firebaseio.placardbolsa;

import android.content.Context;
import android.view.View;

import java.util.ArrayList;

public class gameCode {
public String código;
public String prognóstico;
public String betType;

public gameCode(String código, String prognóstico, String betType) {
            this.código = código;
            this.prognóstico = prognóstico;
            this.betType = betType;
        }

public String getCódigo() {
        return código;
        }

public String getPrognóstico() {
        return prognóstico;
        }

public String getBetType() {
        return betType;
        }

public static ArrayList<gameCode> createEmpty() {
    ArrayList<gameCode> gameCodes = new ArrayList<gameCode>();
    gameCodes.add(new gameCode("", "", ""));
    return gameCodes;
}
public static ArrayList<gameCode> createEmpty2() {
    ArrayList<gameCode> gameCodes = new ArrayList<gameCode>();
    gameCodes.add(new gameCode("", "", ""));
    return gameCodes;
    }


}

