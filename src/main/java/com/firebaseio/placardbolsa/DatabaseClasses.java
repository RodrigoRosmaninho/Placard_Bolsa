package com.firebaseio.placardbolsa;

import java.util.ArrayList;

public class DatabaseClasses {
}

class Dates {
    private String event_date;
    private String pouch_content;
    private String result;
    private String variation;

    public Dates() {
    }

    public Dates(String event_date, String pouch_content, String result, String variation) {
        this.event_date = event_date;
        this.pouch_content = pouch_content;
        this.result = result;
        this.variation = variation;
    }

    public String getEvent_date() {
        return event_date;
    }

    public String getPouch_content() {
        return pouch_content;
    }

    public String getResult() {
        return result;
    }

    public String getVariation() {
        return variation;
    }
}

class generalStats {
    private String bet_number;
    private String lost_number;
    private String win_number;

    public generalStats() {
    }

    public generalStats(String bet_number, String lost_number, String win_number) {
        this.bet_number = bet_number;
        this.lost_number = lost_number;
        this.win_number = win_number;
    }

    public String getBet_number() {
        return bet_number;
    }

    public String getLost_number() {
        return lost_number;
    }

    public String getWin_number() {
        return win_number;
    }
}


class Notes {
    private String last_author;
    private String last_edited;
    private String text;
    private String note_id;

    public Notes() {
    }

    public Notes(String last_author, String last_edited, String text, String note_id) {
        this.last_author = last_author;
        this.last_edited = last_edited;
        this.text = text;
        this.note_id = note_id;
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

    public String getNote_id() {
        return note_id;
    }
}

class Statistics {
    private String all_earnings;
    private String current_share;
    private String on_pouch;
    private String value_spent;
    private Object general_stats;
    private String number_ofBets;
    private Object pouch_history;
    private String pendingBetsExist;
    private String number_ofTransactions;
    private String number_ofDates;

    public Statistics() {
    }

    public Statistics(String all_earnings, String current_share, String on_pouch, String value_spent, Object general_stats, String number_ofBets, Object pouch_history, String pendingBetsExist, String number_ofTransactions, String number_ofDates) {
        this.all_earnings = all_earnings;
        this.current_share = current_share;
        this.on_pouch = on_pouch;
        this.value_spent = value_spent;
        this.general_stats = general_stats;
        this.number_ofBets = number_ofBets;
        this.pouch_history = pouch_history;
        this.pendingBetsExist = pendingBetsExist;
        this.number_ofTransactions = number_ofTransactions;
        this.number_ofDates = number_ofDates;

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

    public String getNumber_ofBets() {
        return number_ofBets;
    }

    public Object getPouch_history() {
        return pouch_history;
    }

    public String getPendingBetsExist() {
        return pendingBetsExist;
    }

    public String getNumber_ofTransactions() {
        return number_ofTransactions;
    }

    public String getNumber_ofDates() {
        return number_ofDates;
    }
}

class gameCode {
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
        gameCodes.add(0, new gameCode("", "", ""));
        return gameCodes;
    }


}

class Game {
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

class betResult {
    private String number_wrong;
    private String total_result;

    public betResult() {
    }

    public betResult(String number_wrong, String total_result) {
        this.number_wrong = number_wrong;
        this.total_result = total_result;
    }

    public String getNumber_wrong() {
        return number_wrong;
    }

    public String getTotal_result() {
        return total_result;
    }
}

class betVotes {
    private String u01;
    private String u02;
    private String u03;
    private String u04;
    private String u05;
    private String u06;

    public betVotes() {
    }

    public betVotes(String u01, String u02, String u03, String u04, String u05, String u06) {
        this.u01 = u01;
        this.u02 = u02;
        this.u03 = u03;
        this.u04 = u04;
        this.u05 = u05;
        this.u06 = u06;
    }

    public String getU01() {
        return u01;
    }

    public String getU02() {
        return u02;
    }

    public String getU03() {
        return u03;
    }

    public String getU04() {
        return u04;
    }

    public String getU05() {
        return u05;
    }

    public String getU06() {
        return u06;
    }
}

class Bet2 {
    private String date;
    private String bet_index;
    private String bet_price;
    private String game_number;
    private String projected_winnings;
    private String general_bet_type;
    private String odd_total;
    private Object result;
    private Object games;


    public Bet2() {
    }

    public Bet2(String bet_index, String date, String bet_price, String game_number, String projected_winnings, String general_bet_type, String odd_total, Object result, Object games) {
        this.date = date;
        this.bet_index = bet_index;
        this.bet_price = bet_price;
        this.game_number = game_number;
        this.projected_winnings = projected_winnings;
        this.general_bet_type = general_bet_type;
        this.odd_total = odd_total;
        this.result = result;
        this.games = games;
    }

    public String getDate() {
        return date;
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

    public String getGeneral_bet_type() {
        return general_bet_type;
    }

    public String getOdd_total() {
        return odd_total;
    }

    public Object getResult() {
        return result;
    }

    public Object getGames() {
        return games;
    }
}

    class Bet {
        private String date;
        private String bet_index;
        private String bet_price;
        private String game_number;
        private String projected_winnings;
        private String general_bet_type;
        private String odd_total;
        private Object result;


        public Bet() {
        }

        public Bet(String bet_index, String date, String bet_price, String game_number, String projected_winnings, String general_bet_type, String odd_total, Object result) {
            this.date = date;
            this.bet_index = bet_index;
            this.bet_price = bet_price;
            this.game_number = game_number;
            this.projected_winnings = projected_winnings;
            this.general_bet_type = general_bet_type;
            this.odd_total = odd_total;
            this.result = result;
        }

        public String getDate() {
            return date;
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

        public String getGeneral_bet_type() {
            return general_bet_type;
        }

        public String getOdd_total() {
            return odd_total;
        }

        public Object getResult() {
            return result;
        }

    }



