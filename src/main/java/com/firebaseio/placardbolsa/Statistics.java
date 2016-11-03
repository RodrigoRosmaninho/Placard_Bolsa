package com.firebaseio.placardbolsa;

import java.util.Objects;

public class Statistics {
    private String all_earnings;
    private String current_share;
    private String on_pouch;
    private String value_spent;
    private Object general_stats;
    private Object vote_stats;
    private Object best_winnings;
    private Object worst_fuckUp;
    private String number_ofBets;

    public Statistics() {
    }

    public Statistics(String all_earnings, String current_share, String on_pouch, String value_spent, Object general_stats, Object vote_stats, Object best_winnings, Object worst_fuckUp, String number_ofBets) {
        this.all_earnings = all_earnings;
        this.current_share = current_share;
        this.on_pouch = on_pouch;
        this.value_spent = value_spent;
        this.general_stats = general_stats;
        this.vote_stats = vote_stats;
        this.best_winnings = best_winnings;
        this.worst_fuckUp = worst_fuckUp;
        this.number_ofBets = number_ofBets;


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

    public Object getBest_winnings() {
        return best_winnings;
    }

    public Object getWorst_fuckUp() {
        return worst_fuckUp;
    }

    public String getNumber_ofBets() {
        return number_ofBets;
    }

    
    
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


    public class bestWinnings {
        private String best_bet;
        private String best_value;

        public bestWinnings() {
        }

        public bestWinnings(String best_bet, String best_value) {
            this.best_bet = best_bet;
            this.best_value = best_value;
        }

        public String getBest_bet() {
            return best_bet;
        }

        public String getBest_value() {
            return best_value;
        }
    }


    public class worstFuckUp {
        private String worst_bet;
        private String worst_value;

        public worstFuckUp() {
        }

        public worstFuckUp(String worst_bet, String worst_value) {
            this.worst_bet = worst_bet;
            this.worst_value = worst_value;
        }

        public String getWorst_bet() {
            return worst_bet;
        }

        public String getWorst_value() {
            return worst_value;
        }
    }

}
