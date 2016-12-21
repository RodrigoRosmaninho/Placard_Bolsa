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
    private String suggestionsExist;

    public Statistics() {
    }

    public Statistics(String all_earnings, String current_share, String on_pouch, String value_spent, Object general_stats, Object vote_stats, String number_ofBets, Object pouch_history, String pendingBetsExist, String number_ofSugg, String suggestionsExist) {
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

    public String getSuggestionsExist() {return suggestionsExist;}



    public class voteStats {
        private String u01;
        private String u02;
        private String u03;
        private String u04;
        private String u05;

        public voteStats() {
        }

        public voteStats(String u01, String u02, String u03, String u04, String u05) {
            this.u01 = u01;
            this.u02 = u02;
            this.u03 = u03;
            this.u04 = u04;
            this.u05 = u05;
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
    }


    public class generalStats {
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

}
