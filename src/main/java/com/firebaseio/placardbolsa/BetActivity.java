package com.firebaseio.placardbolsa;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.recyclerview.view.CardRecyclerView;
import it.gmariotti.cardslib.library.view.CardViewNative;

public class BetActivity  extends AppCompatActivity {
    DatabaseReference betRef;
    RecyclerView edit_view;
    FirebaseRecyclerAdapter mAdapter;

    String value;
    String mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bet_main);

        Bundle b = getIntent().getExtras();
        mode = "";
        value = "";

        if(b != null) {
            //TODO: Make snackbar "Critical Error"

            value = b.getString("index");
            mode = b.getString("mode");
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        String index = value;

        if (index.charAt(0)=='0') {
            index = index.substring(1);
        }

        final TextView indexTV = (TextView) findViewById(R.id.bet_number);
        indexTV.setText(index);

        final TextView date = (TextView) findViewById(R.id.textView_Date);
        final TextView type = (TextView) findViewById(R.id.textView_type);

        final TextView result = (TextView) findViewById(R.id.textView_result);

        final TextView odds = (TextView) findViewById(R.id.oddsTextView);
        final TextView spent = (TextView) findViewById(R.id.spentTextView);
        final TextView winnings = (TextView) findViewById(R.id.winningsTextView);
        final TextView balance = (TextView) findViewById(R.id.balanceTextView);

        RecyclerView games_view = (RecyclerView) findViewById(R.id.games_view2);
        games_view.setNestedScrollingEnabled(false);

        String childVar = "Transactions";

        if(!(mode.equals("1"))) {
            childVar = "Pending";
        }

        betRef = FirebaseDatabase.getInstance().getReference().child(childVar).child(value);

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Bet bet_class = dataSnapshot.getValue(Bet.class);

                String general_bet_type = "Aposta " + bet_class.getGeneral_bet_type();

                date.setText(bet_class.getDate());
                type.setText(general_bet_type);

                String bet_result = bet_class.getResult().toString().split("\\{")[1].split("\\}")[0].split(", ")[0].split("=")[1];

                if(bet_result.equals("1")) {
                    balance.setTextColor(Color.parseColor("#FF669900"));
                    result.setTextColor(Color.parseColor("#FF669900"));
                    int emoji = 0x1F44D;
                    String string = "Positivo " + getEmojiByUnicode(emoji);
                    result.setText(string);

                    String preBalance = String.format(Locale.ENGLISH, "%.2f", Float.parseFloat(bet_class.getProjected_winnings())) + "€";
                    balance.setText("+" + preBalance);
                }
                else if (bet_result.equals("0")) {
                    balance.setTextColor(Color.parseColor("#FFCC0000"));
                    result.setTextColor(Color.parseColor("#FFCC0000"));
                    int emoji = 0x1F44E;
                    String string = "Negativo " + getEmojiByUnicode(emoji);
                    result.setText(string);

                    String preBalance = String.format(Locale.ENGLISH, "%.2f", Float.parseFloat(bet_class.getBet_price())) + "€";
                    balance.setText("-" + preBalance);
                }
                else if (bet_result.equals("2")) {
                    balance.setTextColor(Color.parseColor("#FFFF8800"));
                    result.setTextColor(Color.parseColor("#FFFF8800"));
                    int emoji = 0x23F0;
                    String string = "Pendente " + getEmojiByUnicode(emoji);
                    result.setText(string);

                    balance.setText("Pendente");
                }

                String preSpent = String.format(Locale.ENGLISH, "%.2f", Float.parseFloat(bet_class.getBet_price())) + "€";
                String preWinnings = String.format(Locale.ENGLISH, "%.2f", Float.parseFloat(bet_class.getProjected_winnings())) + "€";
                String preOdds = bet_class.getOdd_total() + "€";

                odds.setText(preOdds);
                spent.setText(preSpent);
                winnings.setText(preWinnings);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        betRef.addListenerForSingleValueEvent(postListener);

        LinearLayoutManager game_layoutManager = new LinearLayoutManager(this);
        games_view.setLayoutManager(game_layoutManager);

        FirebaseRecyclerAdapter mAdapter = new FirebaseRecyclerAdapter<Game, gameHolder>(Game.class, R.layout.populate_this_detail, gameHolder.class, betRef.child("games")) {
            @Override
            public void populateViewHolder(gameHolder gameViewHolder, Game specificGame, int position) {
                // betViewHolder.callExpand(MainActivity.this, specificBet.getBet_index());
                gameViewHolder.setCode(specificGame.getEvent_index());
                gameViewHolder.setType(specificGame.getBet_type());
                gameViewHolder.setHomeOpp(specificGame.getHome_opponent());
                gameViewHolder.setAwayOpp(specificGame.getAway_opponent());
                gameViewHolder.setOutcome(specificGame.getOutcome_description());
                gameViewHolder.setOdd(specificGame.getOutcome_odd());
                gameViewHolder.setResult(specificGame.getGame_result());
            }
        };
        games_view.setAdapter(mAdapter);

    }

    public void editEntries() {
        final MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title("Alterar Resultados")
                .customView(R.layout.dialog_editentries, true)
                .positiveText("Confirmar")
                .negativeText("Cancelar")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        checkEdit();
                    }
                })
                .build();

        edit_view = (RecyclerView) dialog.getCustomView().findViewById(R.id.edit_view);

        LinearLayoutManager game_layoutManager = new LinearLayoutManager(this);
        edit_view.setLayoutManager(game_layoutManager);

        mAdapter = new FirebaseRecyclerAdapter<Game, gameHolder>(Game.class, R.layout.populate_this_detail1, gameHolder.class, betRef.child("games")) {
            @Override
            public void populateViewHolder(gameHolder gameViewHolder, Game specificGame, int position) {
                gameViewHolder.setOutcome(specificGame.getOutcome_description());
                gameViewHolder.setCode(specificGame.getEvent_index());
                gameViewHolder.setRadio(specificGame.getGame_result());
            }
        };
        edit_view.setAdapter(mAdapter);

        dialog.show();
    }

    public void checkEdit() {
        Log.d("DEBUG", "One-One: " + value);
        int number = mAdapter.getItemCount();
        int passes = 0;
        int emptyGroups = 0;
        int failed = 0;

        while(passes != number) {
            View v = edit_view.getLayoutManager().findViewByPosition(passes);
            RadioButton rb1 = (RadioButton) v.findViewById(R.id.radioSuccess);
            RadioButton rb2 = (RadioButton) v.findViewById(R.id.radioDefeat);
            RadioButton rb3 = (RadioButton) v.findViewById(R.id.radioCancelled);

            if (rb1.isChecked()) {
                betRef.child("games").child("game_0" + (passes + 1)).child("game_result").setValue("1");
            }
            else if (rb2.isChecked()) {
                betRef.child("games").child("game_0" + (passes + 1)).child("game_result").setValue("0");
                failed = failed + 1;
            }

            else if (rb3.isChecked()) {
                betRef.child("games").child("game_0" + (passes + 1)).child("game_result").setValue("2");
            }

            else {
                emptyGroups = emptyGroups + 1;
            }

            passes = passes + 1;
        }

        String mode2 = "TODO";
        if (emptyGroups == 0 && mode2.equals("2")) {
            final int failed2 = failed;
            final DatabaseReference betRef2 = FirebaseDatabase.getInstance().getReference().child("Transactions");
            final DatabaseReference betRef3 = FirebaseDatabase.getInstance().getReference().child("Statistics");

            final String[] res = new String[1];
            final String[] projected = new String[1];
            final String[] date = new String[1];
            final String[] price = new String[1];


            ValueEventListener postListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Bet2 bet_class = dataSnapshot.getValue(Bet2.class);

                    betRef2.child(value).setValue(bet_class);

                    if (failed2 == 0) {
                        betRef2.child(value).child("result").child("total_result").setValue("1");
                        res[0] = "1";
                    }
                    else {
                        betRef2.child(value).child("result").child("total_result").setValue("0");
                        res[0] = "0";
                        betRef2.child(value).child("result").child("number_wrong").setValue(String.valueOf(failed2));
                    }

                    projected[0] = bet_class.getProjected_winnings();
                    date[0] = bet_class.getDate();
                    price[0] = bet_class.getBet_price();

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            betRef.addListenerForSingleValueEvent(postListener);

            DatabaseReference betRef10 = betRef2.child(value);

            final String[] onPouch = new String[1];
            final String[] inv = new String[1];
            final String[] earn = new String [1];

            ValueEventListener postListener2 = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Statistics stats_class = dataSnapshot.getValue(Statistics.class);

                    String bet_number = stats_class.getNumber_ofBets();
                    betRef3.child("general_stats").child("bet_number").setValue(bet_number);

                    /**String pen_number = stats_class.getNumber_ofPen();

                    if (pen_number.equals("1")) {
                        betRef3.child("number_ofPen").setValue("0");
                        betRef3.child("pendingBetsExist").setValue("0");
                    }
                    else {
                        betRef3.child("number_ofPen").setValue(String.valueOf(Integer.parseInt(pen_number) - 1));
                    }

                    onPouch[0] = stats_class.getOn_pouch();
                    inv[0] = stats_class.getValue_spent();
                    earn[0] = stats_class.getAll_earnings();**/

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            betRef3.addListenerForSingleValueEvent(postListener2);

            final DatabaseReference betRef4 = betRef3.child("general_stats");

            ValueEventListener postListener3 = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    generalStats stats_class = dataSnapshot.getValue(generalStats.class);

                    if (failed2 == 0) {
                        String win_number = stats_class.getWin_number();
                        betRef4.child("win_number").setValue(String.valueOf(Integer.parseInt(win_number) + 1));
                    }
                    else {
                        String lost_number = stats_class.getLost_number();
                        betRef4.child("lost_number").setValue(String.valueOf(Integer.parseInt(lost_number) + 1));
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            betRef4.addListenerForSingleValueEvent(postListener3);

            ValueEventListener postListener10 = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Bet bet_class = dataSnapshot.getValue(Bet.class);

                    if(res[0].equals("1")) {
                        float op = Float.parseFloat(onPouch[0]) + (Float.parseFloat(projected[0]) - Float.parseFloat(price[0]));
                        betRef3.child("on_pouch").setValue(String.format(Locale.ENGLISH, "%.2f", op));
                        betRef3.child("value_spent").setValue(String.format(Locale.ENGLISH, "%.2f", (Float.parseFloat(inv[0])) + Float.parseFloat(price[0])));
                        // TODO: Fix all_earnings!
                        // betRef3.child("all_earnings").setValue(String.valueOf(Float.parseFloat(earn[0]) + (Float.parseFloat(projected[0]) - Float.parseFloat(price[0]))));
                        betRef3.child("current_share").setValue(String.format(Locale.ENGLISH, "%.2f", op / 6));
                        betRef3.child("pouch_history").child(value).child("event_date").setValue(date[0].split("_")[1]);
                        betRef3.child("pouch_history").child(value).child("pouch_content").setValue(String.format(Locale.ENGLISH, "%.2f", op));
                    }

                    else {
                        float op = Float.parseFloat(onPouch[0]) - Float.parseFloat(price[0]);
                        betRef3.child("on_pouch").setValue(String.format(Locale.ENGLISH, "%.2f", op));
                        betRef3.child("value_spent").setValue(String.format(Locale.ENGLISH, "%.2f", (Float.parseFloat(inv[0])) + Float.parseFloat(price[0])));
                        betRef3.child("current_share").setValue(String.format(Locale.ENGLISH, "%.2f", op / 6));
                        betRef3.child("pouch_history").child(value).child("event_date").setValue(date[0].split("_")[1]);
                        betRef3.child("pouch_history").child(value).child("pouch_content").setValue(String.format(Locale.ENGLISH, "%.2f", op));
                    }


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            betRef2.child(value).addListenerForSingleValueEvent(postListener10);

            //final DatabaseReference betRef11 = betRef3.child("vote_stats");

            final String[] nuno = new String[1];
            final String[] chico = new String[1];
            final String[] lois = new String[1];
            final String[] melo = new String[1];
            final String[] salgado = new String[1];
            final String[] lameiro = new String[1];

            ValueEventListener postListener11 = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    /**voteStats vS = dataSnapshot.getValue(voteStats.class);

                    nuno[0] = vS.getU01();
                    chico[0] = vS.getU02();
                    lois[0] = vS.getU03();
                    melo[0] = vS.getU04();
                    salgado[0] = vS.getU05();
                    lameiro[0] = vS.getU06();**/
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            //betRef11.addListenerForSingleValueEvent(postListener11);



            ValueEventListener postListener12 = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    betVotes bV = dataSnapshot.getValue(betVotes.class);
                    String nuno2 = nuno[0];
                    String chico2 = chico[0];
                    String lois2 = lois[0];
                    String melo2 = melo[0];
                    String salgado2 = salgado[0];
                    String lameiro2 = lameiro[0];


                    if (failed2 == 0) {

                        if(bV.getU01().equals("1")) {
                            nuno2 = String.valueOf(Integer.parseInt(nuno[0]) + 1);
                        }
                        if(bV.getU02().equals("1")) {
                            chico2 = String.valueOf(Integer.parseInt(chico[0]) + 1);
                        }
                        if(bV.getU03().equals("1")) {
                            lois2 = String.valueOf(Integer.parseInt(lois[0]) + 1);
                        }
                        if(bV.getU04().equals("1")) {
                            melo2 = String.valueOf(Integer.parseInt(melo[0]) + 1);
                        }
                        if(bV.getU05().equals("1")) {
                            salgado2 = String.valueOf(Integer.parseInt(salgado[0]) + 1);
                        }
                        if(bV.getU06().equals("1")) {
                            lameiro2 = String.valueOf(Integer.parseInt(lameiro[0]) + 1);
                        }
                    }
                    else {
                        if(bV.getU01().equals("0")) {
                            nuno2 = String.valueOf(Integer.parseInt(nuno[0]) + 1);
                        }
                        if(bV.getU02().equals("0")) {
                            chico2 = String.valueOf(Integer.parseInt(chico[0]) + 1);
                        }
                        if(bV.getU03().equals("0")) {
                            lois2 = String.valueOf(Integer.parseInt(lois[0]) + 1);
                        }
                        if(bV.getU04().equals("0")) {
                            melo2 = String.valueOf(Integer.parseInt(melo[0]) + 1);
                        }
                        if(bV.getU05().equals("0")) {
                            salgado2 = String.valueOf(Integer.parseInt(salgado[0]) + 1);
                        }
                        if(bV.getU06().equals("0")) {
                            lameiro2 = String.valueOf(Integer.parseInt(lameiro[0]) + 1);
                        }

                    }

                    //voteStats vS1 = new voteStats(nuno2, chico2, lois2, melo2, salgado2, lameiro2);
                    //betRef11.setValue(vS1);
                    betRef.removeValue();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            betRef.child("votes").addListenerForSingleValueEvent(postListener12);

            new Handler().postDelayed(new Runnable(){
                @Override
                public void run() {
                    Intent intent = new Intent(BetActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }, 2000);

        }
    }

    public String getEmojiByUnicode(int unicode){
        return new String(Character.toChars(unicode));
    }

    public static class gameHolder extends RecyclerView.ViewHolder {
        View mView;

        public gameHolder(View itemView) {
            super(itemView);
            mView = itemView;

        }

        /**public void callExpand(Context ctx, String index) {
            CardViewNative card_view = (CardViewNative) mView.findViewById(R.id.list_cardId);
            final String finalIndex = index;
            final Context ctxx = ctx;
            card_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ctxx, BetActivity.class);
                    Bundle b = new Bundle();
                    b.putString("index", finalIndex);
                    intent.putExtras(b);
                    ctxx.startActivity(intent);
                }
            });
        }**/

        public void setCode(String code) {
            // mView.setPadding(20, 0, 20, 0);
            TextView field = (TextView) mView.findViewById(R.id.game_code);
            field.setText(code);
        }

        public void setType(String type) {
            TextView field = (TextView) mView.findViewById(R.id.game_type);
            field.setText(type);
        }

        public void setHomeOpp(String home_opp) {
            TextView field = (TextView) mView.findViewById(R.id.game_home);
            field.setText(home_opp);
        }

        public void setAwayOpp(String away_opp) {
            TextView field = (TextView) mView.findViewById(R.id.game_away);
            field.setText(away_opp);
        }

        public void setOutcome(String outcome) {
            TextView field = (TextView) mView.findViewById(R.id.game_outcome);
            field.setText(outcome);
        }

        public void setOdd(String odd) {
            TextView field = (TextView) mView.findViewById(R.id.game_odd);
            field.setText(odd);
        }

        public void setResult(String result) {
            View view = (View) mView.findViewById(R.id.game_result);

            if(result.equals("1")) {
                view.setBackgroundColor(Color.parseColor("#FF669900"));
            }
            else if (result.equals("0")){
                view.setBackgroundColor(Color.parseColor("#FFCC0000"));
            }
            else if (result.equals("3")) {
                view.setBackgroundColor(Color.parseColor("#FFFF8800"));
            }
            else if (result.equals("2")) {
                view.setBackgroundColor(Color.parseColor("#415dae"));
            }
        }

        public void setRadio(String result) {
            RadioButton rb;

            if (result.equals("1")) {
                rb = (RadioButton) mView.findViewById(R.id.radioSuccess);
                rb.setChecked(true);
            }
            else if (result.equals("0")) {
                rb = (RadioButton) mView.findViewById(R.id.radioDefeat);
                rb.setChecked(true);
            }
            else if (result.equals("2")) {
                rb = (RadioButton) mView.findViewById(R.id.radioCancelled);
                rb.setChecked(true);
            }
            else {
                // Do Nothing
            }
        }

    }

}
