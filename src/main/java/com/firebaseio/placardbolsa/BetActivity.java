package com.firebaseio.placardbolsa;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bet_main);

        Bundle b = getIntent().getExtras();
        String value = "";
        if(b != null)
            value = b.getString("index");

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);

        toolbar.setTitle("Aposta " + value.substring(1));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        final TextView date = (TextView) findViewById(R.id.bet_textDate);
        final TextView type = (TextView) findViewById(R.id.bet_textType);

        final TextView result = (TextView) findViewById(R.id.bet_textResult);

        CardRecyclerView games_view = (CardRecyclerView) findViewById(R.id.games_view);

        Card card = new Card(this);
        CardHeader header = new CardHeader(this);
        CardViewNative resultCard = (CardViewNative) findViewById(R.id.bet_resultCard);
        CardViewNative mainCard = (CardViewNative) findViewById(R.id.bet_mainCard);
        CardViewNative gamesCard = (CardViewNative) findViewById(R.id.bet_gamesCard);
        card.addCardHeader(header);
        header.setButtonExpandVisible(false);
        header.setButtonOverflowVisible(false);
        mainCard.setCard(card);
        header.setTitle("Resultado");
        resultCard.setCard(card);
        header.setTitle("Jogos");
        gamesCard.setCard(card);

        DatabaseReference betRef = FirebaseDatabase.getInstance().getReference();
        betRef = betRef.child("Transactions").child(value);

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Bet bet_class = dataSnapshot.getValue(Bet.class);

                String general_bet_type = "Aposta " + bet_class.getGeneral_betType();

                date.setText(bet_class.getDate().split("_")[1]);
                type.setText(general_bet_type);

                String bet_result = bet_class.getResult().toString().split("\\{")[1].split("\\}")[0].split(", ")[0].split("=")[1];

                if(bet_result.equals("1")) {
                    result.setTextColor(Color.parseColor("#FF669900"));
                    int emoji = 0x1F44D;
                    String string = "Positivo " + getEmijoByUnicode(emoji);
                    result.setText(string);
                }
                else if (bet_result.equals("0")) {
                    result.setTextColor(Color.parseColor("#FFCC0000"));
                    int emoji = 0x1F44E;
                    String string = "Negativo " + getEmijoByUnicode(emoji);
                    result.setText(string);
                }
                else if (bet_result.equals("2")) {
                    result.setTextColor(Color.parseColor("#FFFF8800"));
                    int emoji = 0x23F0;
                    String string = "Pendente " + getEmijoByUnicode(emoji);
                    result.setText(string);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        betRef.addListenerForSingleValueEvent(postListener);

        LinearLayoutManager game_layoutManager = new LinearLayoutManager(this);
        game_layoutManager.setReverseLayout(true);
        games_view.setLayoutManager(game_layoutManager);

        FirebaseRecyclerAdapter mAdapter = new FirebaseRecyclerAdapter<Game, gameHolder>(Game.class, R.layout.populate_this_detail, gameHolder.class, betRef.child("games")) {
            @Override
            public void populateViewHolder(gameHolder gameViewHolder, Game specificGame, int position) {
                // betViewHolder.callExpand(MainActivity.this, specificBet.getBet_index());
                gameViewHolder.setCode(BetActivity.this, specificGame.getEvent_index());
                gameViewHolder.setType(specificGame.getDesired_outcome().toString().split("\\{")[2].split("\\}")[0].split(", ")[1].split("=")[1]);
                gameViewHolder.setHomeOpp(specificGame.getHome_opponent());
                gameViewHolder.setAwayOpp(specificGame.getAway_opponent());
                gameViewHolder.setOutcome(specificGame.getDesired_outcome().toString().split("\\{")[2].split("\\}")[1].split(", ")[2].split("=")[1]);
                gameViewHolder.setOdd(specificGame.getDesired_outcome().toString().split("\\{")[1].split(", ")[0].split("=")[1]);
                gameViewHolder.setResult(specificGame.getOutcome().toString().split("\\{")[1].split("\\}")[0].split(", ")[0].split("=")[1]);
            }
        };
        games_view.setAdapter(mAdapter);

    }

    public String getEmijoByUnicode(int unicode){
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

        public void setCode(Context ctx, String code) {
            // mView.setPadding(20, 0, 20, 0);
            TextView field = (TextView) mView.findViewById(R.id.game_code);
            field.setText(code);

            Card card = new Card(ctx);
            CardHeader header = new CardHeader(ctx);
            CardViewNative gameCard = (CardViewNative) mView.findViewById(R.id.gameCard);
            card.addCardHeader(header);
            header.setButtonExpandVisible(false);
            header.setButtonOverflowVisible(false);
            gameCard.setCard(card);
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
            else if (result.equals("2")) {
                view.setBackgroundColor(Color.parseColor("#FFFF8800"));
            }
        }

    }

}
