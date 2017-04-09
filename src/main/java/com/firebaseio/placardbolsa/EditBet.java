package com.firebaseio.placardbolsa;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.ArrayList;
import java.util.Locale;

public class EditBet extends AppCompatActivity {

    String mode;
    String index;

    DatabaseReference betRef;
    String childVar;

    String[] res = new String[1];
    String[] val = new String[2];

    static int bodge = 1;

    static ArrayList<View> viewArray = new ArrayList<View>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_bet);

        viewArray.clear();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendToDatabase();
            }
        });

        RecyclerView games_view = (RecyclerView) findViewById(R.id.games_view2);
        games_view.setNestedScrollingEnabled(false);

        final TextView bet_number = (TextView) findViewById(R.id.bet_number);
        final TextView dateView = (TextView) findViewById(R.id.dateView);
        final MaterialSpinner type_spinner = (MaterialSpinner) findViewById(R.id.type_spinner);
        final MaterialSpinner ammount_spinner = (MaterialSpinner) findViewById(R.id.ammount_spinner);

        Bundle b = getIntent().getExtras();
        mode = "";
        index = "";

        if(b != null) {
            index = b.getString("index");
            mode = b.getString("mode");
        }

        String childVar = "Transactions";

        if(!(mode.equals("1"))) {
            childVar = "Pending";
        }

        String indexTV = index;
        if (index.charAt(0)=='0') {
            indexTV = index.substring(1);
        }

        bet_number.setText("Aposta " + indexTV);

        betRef = FirebaseDatabase.getInstance().getReference().child(childVar).child(index);

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Bet bet_class = dataSnapshot.getValue(Bet.class);

                int typeIndex = 0;
                int ammountIndex = 0;

                res[0] = bet_class.getResult().toString().split("\\{")[1].split("\\}")[0].split(", ")[0].split("=")[1];

                if(res[0].equals("1")) {
                    val[0] = String.valueOf(Double.parseDouble(bet_class.getProjected_winnings()) - Double.parseDouble(bet_class.getBet_price()));
                    val[1] = bet_class.getBet_price();
                }
                else {
                    val[0] = bet_class.getBet_price();
                }

                String[] types = {"Combinada", "Simples", "Múltipla"};
                String[] ammounts = {"5€", "1€", "2€", "10€", "20€", "50€", "75€", "100€"};

                type_spinner.setItems(types);
                ammount_spinner.setItems(ammounts);

                switch(bet_class.getGeneral_bet_type()) {
                    case "Simples":
                        typeIndex = 1;
                        break;

                    case "Múltipla":
                        typeIndex = 2;
                        break;
                }

                switch(bet_class.getBet_price()) {
                    case "1":
                        typeIndex = 1;
                        break;

                    case "2":
                        typeIndex = 2;
                        break;

                    case "10":
                        typeIndex = 2;
                        break;

                    case "20":
                        typeIndex = 2;
                        break;

                    case "50":
                        typeIndex = 2;
                        break;

                    case "75":
                        typeIndex = 2;
                        break;

                    case "100":
                        typeIndex = 2;
                        break;
                }

                dateView.setText(bet_class.getDate());
                type_spinner.setSelectedIndex(typeIndex);
                ammount_spinner.setSelectedIndex(ammountIndex);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        betRef.addListenerForSingleValueEvent(postListener);

        Button pickDate = (Button) findViewById(R.id.pickDate);

        pickDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final MaterialDialog dialog = new MaterialDialog.Builder(EditBet.this)
                        .title("Data da Aposta")
                        .customView(R.layout.dialog_date_transactions, true)
                        .positiveText("Confirmar")
                        .negativeText("Cancelar")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                DatePicker dateP = (DatePicker) dialog.getCustomView().findViewById(R.id.datePicker);
                                dateView.setText(Fragment1.getDateFromDatePicker(dateP));
                            }
                        })
                        .build();

                dialog.show();
            }
        });

        LinearLayoutManager game_layoutManager = new LinearLayoutManager(this);
        games_view.setLayoutManager(game_layoutManager);

        FirebaseRecyclerAdapter mAdapter = new FirebaseRecyclerAdapter<Game, gameHolder>(Game.class, R.layout.populate_newgames, gameHolder.class, betRef.child("games")) {
            @Override
            public void populateViewHolder(gameHolder gameViewHolder, Game specificGame, int position) {
                // betViewHolder.callExpand(MainActivity.this, specificBet.getBet_index());
                gameViewHolder.setCode(specificGame.getEvent_index(), specificGame.getOutcome_description());
                gameViewHolder.setType(specificGame.getBet_type());
                gameViewHolder.setProg(specificGame.getOutcome_index());
                gameViewHolder.setHomeOpp(specificGame.getHome_opponent());
                gameViewHolder.setAwayOpp(specificGame.getAway_opponent());
                gameViewHolder.setOdd(specificGame.getOutcome_odd());
                gameViewHolder.setSport(specificGame.getSport());
                gameViewHolder.setRadio(specificGame.getGame_result());
            }
        };
        games_view.setAdapter(mAdapter);


    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK))
        {
            viewArray.clear();
            bodge = 1;
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                viewArray.clear();
                bodge = 1;
                finish();
                return(true);
        }

        return(super.onOptionsItemSelected(item));
    }

    public void sendToDatabase() {
        int[] numbers = {0, 0, 0};
        double oddTotal_amount = 1.0;
        ArrayList<Game> games = new ArrayList<>();

        final TextView n_da_aposta = (TextView) findViewById(R.id.bet_number);
        final TextView d_da_aposta = (TextView) findViewById(R.id.dateView);
        final MaterialSpinner typeSpinner = (MaterialSpinner) findViewById(R.id.type_spinner);
        final MaterialSpinner ammountSpinner = (MaterialSpinner) findViewById(R.id.ammount_spinner);

        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference statsRef = mDatabase.child("Statistics");
        String[] types = {"Combinada", "Simples", "Múltipla"};
        String[] ammounts = {"5€", "1€", "2€", "10€", "20€", "50€", "75€", "100€"};

        for(int i = 0; i < viewArray.size(); i++) {
            View v = viewArray.get(i);
            RadioGroup rg = (RadioGroup) v.findViewById(R.id.myRadioGroup);
            int id = rg.getCheckedRadioButtonId();
            String result = "3";

            switch (id) {
                case R.id.radioSuccess:
                    numbers[0] ++;
                    result = "1";
                    break;

                case R.id.radioDefeat:
                    numbers[1] ++;
                    result = "0";
                    break;

                case R.id.radioCancelled:
                    numbers[2] ++;
                    result = "2";
                    break;

            }

            EditText placardCode = (EditText) v.findViewById(R.id.placardCode2);
            MaterialSpinner type = (MaterialSpinner) v.findViewById(R.id.betType_spinner2);
            MaterialSpinner outcome = (MaterialSpinner) v.findViewById(R.id.outcome_spinner2);
            MaterialSpinner sport = (MaterialSpinner) v.findViewById(R.id.sport_spinner);
            EditText home = (EditText) v.findViewById(R.id.home);
            EditText away = (EditText) v.findViewById(R.id.away);
            EditText odd = (EditText) v.findViewById(R.id.odd);
            TextView outDes = (TextView) v.findViewById(R.id.outcomeDescription);

            final String[] outcomes = {"1", "x", "2", "+", "-"};
            final String[] types2 = {"TR", "INT", "DV", "+/-"};
            String[] sports = {"Futebol", "Basketbol", "Ténis"};

            Game game = new Game(placardCode.getText().toString(), home.getText().toString(), away.getText().toString(), sports[sport.getSelectedIndex()], result, outDes.getText().toString(), outcomes[outcome.getSelectedIndex()], String.format(Locale.ENGLISH, "%.2f", Double.parseDouble(odd.getText().toString())), "0", types2[type.getSelectedIndex()]);
            games.add(game);

            if(!(result.equals("2"))) {
                oddTotal_amount = oddTotal_amount * Double.parseDouble(odd.getText().toString());
            }
        }

        if(numbers[1] > 0) {
            String amount = ammounts[ammountSpinner.getSelectedIndex()];
            amount = amount.substring(0, amount.length() - 1);

            final String amount2 = amount;
            final double oddTotal_amount2 = oddTotal_amount;

            ValueEventListener postListener10 = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Statistics stats_class = dataSnapshot.getValue(Statistics.class);
                    double pouch = Double.parseDouble(stats_class.getOn_pouch());
                    double all_earn = Double.parseDouble(stats_class.getAll_earnings());
                    double val_spent = Double.parseDouble(stats_class.getValue_spent());

                    if(!(mode.equals("1"))) {
                        mDatabase.child("Pending").child("0" + n_da_aposta.getText().toString().split(" ")[1]).removeValue();
                    }

                    else {

                        // Revert changes to Statistic Variables
                        if (res[0].equals("1")) {
                            pouch = Double.parseDouble(stats_class.getOn_pouch()) - Double.parseDouble(val[0]);
                            all_earn = Double.parseDouble(stats_class.getAll_earnings()) - Double.parseDouble(val[0]);
                            val_spent = Double.parseDouble(stats_class.getValue_spent()) - Double.parseDouble(val[1]);
                        } else {
                            pouch = Double.parseDouble(stats_class.getOn_pouch()) + Double.parseDouble(val[0]);
                            val_spent = Double.parseDouble(stats_class.getValue_spent()) - Double.parseDouble(val[0]);
                        }

                    }

                    double op = pouch - Double.parseDouble(amount2);
                    statsRef.child("on_pouch").setValue(String.format(Locale.ENGLISH, "%.2f", op));
                    statsRef.child("value_spent").setValue(String.format(Locale.ENGLISH, "%.2f", val_spent + Double.parseDouble(amount2)));
                    statsRef.child("current_share").setValue(String.format(Locale.ENGLISH, "%.2f", op / 6));

                    Fragment1.addToDatesNode(d_da_aposta.getText().toString());


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            statsRef.addListenerForSingleValueEvent(postListener10);

            betResult resultObj = new betResult(String.valueOf(numbers[1]), "0");
            Bet bet = new Bet("0" + n_da_aposta.getText().toString().split(" ")[1], d_da_aposta.getText().toString(), amount, String.valueOf(viewArray.size()), String.format(Locale.ENGLISH, "%.2f", Double.parseDouble(amount) * oddTotal_amount), types[typeSpinner.getSelectedIndex()], String.format(Locale.ENGLISH, "%.2f", oddTotal_amount), resultObj);
            mDatabase.child("Transactions").child("0" + n_da_aposta.getText().toString().split(" ")[1]).setValue(bet);

            for (int i = 0; i < viewArray.size(); i++) {
                mDatabase.child("Transactions").child("0" + n_da_aposta.getText().toString().split(" ")[1]).child("games").child("game_0" + (i + 1)).setValue(games.get(i));
            }
        }

        else if((numbers[0] + numbers[2]) == viewArray.size()) {
            String amount = ammounts[ammountSpinner.getSelectedIndex()];
            amount = amount.substring(0, amount.length() - 1);

            final String amount2 = amount;
            final double oddTotal_amount2 = oddTotal_amount;

            ValueEventListener postListener10 = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Statistics stats_class = dataSnapshot.getValue(Statistics.class);
                    double pouch = Double.parseDouble(stats_class.getOn_pouch());
                    double all_earn = Double.parseDouble(stats_class.getAll_earnings());
                    double val_spent = Double.parseDouble(stats_class.getValue_spent());

            if(!(mode.equals("1"))) {
                mDatabase.child("Pending").child("0" + n_da_aposta.getText().toString().split(" ")[1]).removeValue();
            }

            else {

                // Revert changes to Statistic Variables
                if (res[0].equals("1")) {
                    pouch = Double.parseDouble(stats_class.getOn_pouch()) - Double.parseDouble(val[0]);
                    all_earn = Double.parseDouble(stats_class.getAll_earnings()) - Double.parseDouble(val[0]);
                    val_spent = Double.parseDouble(stats_class.getValue_spent()) - Double.parseDouble(val[1]);
                } else {
                    pouch = Double.parseDouble(stats_class.getOn_pouch()) + Double.parseDouble(val[0]);
                    val_spent = Double.parseDouble(stats_class.getValue_spent()) - Double.parseDouble(val[0]);
                }

            }

                    double op = pouch - Double.parseDouble(amount2) + (Double.parseDouble(amount2) * oddTotal_amount2);
                    statsRef.child("on_pouch").setValue(String.format(Locale.ENGLISH, "%.2f", op));
                    statsRef.child("all_earnings").setValue(String.format(Locale.ENGLISH, "%.2f", all_earn + ((Double.parseDouble(amount2) * oddTotal_amount2) - Double.parseDouble(amount2))));
                    statsRef.child("value_spent").setValue(String.format(Locale.ENGLISH, "%.2f", val_spent + Double.parseDouble(amount2)));
                    statsRef.child("current_share").setValue(String.format(Locale.ENGLISH, "%.2f", op / 6));

                    Fragment1.addToDatesNode(d_da_aposta.getText().toString());

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            statsRef.addListenerForSingleValueEvent(postListener10);

            betResult resultObj = new betResult("0", "1");
            Bet bet = new Bet("0" + n_da_aposta.getText().toString().split(" ")[1], d_da_aposta.getText().toString(), amount, String.valueOf(viewArray.size()), String.format(Locale.ENGLISH, "%.2f", Double.parseDouble(amount) * oddTotal_amount), types[typeSpinner.getSelectedIndex()], String.format(Locale.ENGLISH, "%.2f", oddTotal_amount), resultObj);
            mDatabase.child("Transactions").child("0" + n_da_aposta.getText().toString().split(" ")[1]).setValue(bet);

            for (int i = 0; i < viewArray.size(); i++) {
                mDatabase.child("Transactions").child("0" + n_da_aposta.getText().toString().split(" ")[1]).child("games").child("game_0" + (i + 1)).setValue(games.get(i));
            }
        }

        else if(!(mode.equals("1"))) {
            String amount = ammounts[ammountSpinner.getSelectedIndex()];
            amount = amount.substring(0, amount.length() - 1);

            betResult resultObj = new betResult("0", "2");
            Bet bet = new Bet("0" + n_da_aposta.getText().toString().split(" ")[1], d_da_aposta.getText().toString(), amount, String.valueOf(viewArray.size()), String.format(Locale.ENGLISH, "%.2f", Double.parseDouble(amount) * oddTotal_amount), types[typeSpinner.getSelectedIndex()], String.format(Locale.ENGLISH, "%.2f", oddTotal_amount), resultObj);
            mDatabase.child("Pending").child("0" + n_da_aposta.getText().toString().split(" ")[1]).setValue(bet);

            for (int i = 0; i < viewArray.size(); i++) {
                mDatabase.child("Pending").child("0" + n_da_aposta.getText().toString().split(" ")[1]).child("games").child("game_0" + (i + 1)).setValue(games.get(i));
            }
        }

        viewArray.clear();
        bodge = 1;
        finish();

    }


    public static class gameHolder extends RecyclerView.ViewHolder {
        View mView;

        public gameHolder(View itemView) {
            super(itemView);
            mView = itemView;

        }

        public void setCode(String code, String outcomeDesc) {
            viewArray.add(mView);

            TextView bet_number2 = (TextView) mView.findViewById(R.id.bet_number2);
            RelativeLayout metadata = (RelativeLayout) mView.findViewById(R.id.metadata);
            RelativeLayout manual = (RelativeLayout) mView.findViewById(R.id.manual);
            RadioGroup radio = (RadioGroup) mView.findViewById(R.id.myRadioGroup);
            TextView outDes = (TextView) mView.findViewById(R.id.outcomeDescription);

            outDes.setText(outcomeDesc);

            bet_number2.setText("Jogo 0" + bodge);
            bodge ++;

            metadata.setVisibility(View.GONE);
            manual.setVisibility(View.VISIBLE);
            radio.setVisibility(View.VISIBLE);

            EditText field = (EditText) mView.findViewById(R.id.placardCode2);
            field.setText(code);
        }

        public void setType(String type) {
            String[] types2 = {"TR", "INT", "DV", "+/-"};
            int indexToUse = 0;

            if(type.contains("INT")) {
                indexToUse = 1;
            }
            else if(type.contains("DV")) {
                indexToUse = 2;
            }
            else if(type.equals("+/-")) {
                indexToUse = 3;
            }

            MaterialSpinner type_spinner = (MaterialSpinner) mView.findViewById(R.id.betType_spinner2);
            type_spinner.setItems(types2);
            type_spinner.setSelectedIndex(indexToUse);
        }

        public void setProg(String index) {
            String[] outcomes = {"1", "x", "2", "+", "-"};
            int indexToUse = 0;

            switch(index) {
                case "x":
                    indexToUse = 1;
                    break;

                case "2":
                    indexToUse = 2;
                    break;

                case "+":
                    indexToUse = 3;
                    break;

                case "-":
                    indexToUse = 4;
                    break;
            }

            MaterialSpinner outcome_spinner = (MaterialSpinner) mView.findViewById(R.id.outcome_spinner2);
            outcome_spinner.setItems(outcomes);
            outcome_spinner.setSelectedIndex(indexToUse);
        }

        public void setHomeOpp(String home_opp) {
            EditText field = (EditText) mView.findViewById(R.id.home);
            field.setText(home_opp);
        }

        public void setAwayOpp(String away_opp) {
            EditText field = (EditText) mView.findViewById(R.id.away);
            field.setText(away_opp);
        }

        public void setOdd(String odd) {
            EditText field = (EditText) mView.findViewById(R.id.odd);
            field.setText(odd);
        }

        public void setSport(String sport) {
            String[] sports = {"Futebol", "Basketbol", "Ténis"};

            int indexToUse = 0;

            switch(sport) {
                case "BASK":
                    indexToUse = 1;
                    break;

                case "TENN":
                    indexToUse = 2;
                    break;
            }

            MaterialSpinner sport_spinner = (MaterialSpinner) mView.findViewById(R.id.sport_spinner);
            sport_spinner.setItems(sports);
            sport_spinner.setSelectedIndex(indexToUse);
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
