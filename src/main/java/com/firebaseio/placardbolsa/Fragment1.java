package com.firebaseio.placardbolsa;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jaredrummler.materialspinner.MaterialSpinner;

import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Fragment1 extends Fragment {

    public static Fragment1 newInstance() {
        Fragment1 fragment = new Fragment1();
        return fragment;
    }

    View rootView;

    com.github.clans.fab.FloatingActionMenu fam;

    final String[] code = new String[2];

    static boolean calledAlready = false;

    public static final String MyPREFERENCES = "PlacardBolsaPrefs" ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final SharedPreferences sp = this.getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        final int uID = sp.getInt("UserID", 01);

        rootView = inflater.inflate(R.layout.fragment_main, container, false);

        fam = (com.github.clans.fab.FloatingActionMenu) rootView.findViewById(R.id.fab);

        com.github.clans.fab.FloatingActionButton fab = (com.github.clans.fab.FloatingActionButton) rootView.findViewById(R.id.add_bet_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addEntries();
            }
        });

        com.github.clans.fab.FloatingActionButton fab2 = (com.github.clans.fab.FloatingActionButton) rootView.findViewById(R.id.add_trans_fab);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addTransactions();
            }
        });

        RecyclerView transactions_view = (RecyclerView) rootView.findViewById(R.id.transactions_view);
        LinearLayoutManager bet_layoutManager = new LinearLayoutManager(getContext());
        bet_layoutManager.setReverseLayout(true);
        bet_layoutManager.setStackFromEnd(true);
        transactions_view.setLayoutManager(bet_layoutManager);
        transactions_view.setNestedScrollingEnabled(false);

        if (!calledAlready) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            calledAlready = true;
        }

        final RelativeLayout new_note = (RelativeLayout) rootView.findViewById(R.id.new_note);
        new_note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(rootView.getContext(), NotesActivity.class);
                startActivity(intent);
            }
        });

        final TextView hello = (TextView) rootView.findViewById(R.id.Hello);
        final ImageView user_photo = (ImageView) rootView.findViewById(R.id.user_photo);

        hello.setText("Bem Vindo, " + getUserName(uID));
        user_photo.setImageResource(getUserPhoto(uID));

        DatabaseReference statsRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference notesRef = statsRef.child("Notes");
        statsRef = statsRef.child("Statistics");

        final RecyclerView pending_view = (RecyclerView) rootView.findViewById(R.id.pending_view);
        final RelativeLayout pending = (RelativeLayout) rootView.findViewById(R.id.pending);

        final TextView ganhos = (TextView) rootView.findViewById(R.id.ganhos);
        final TextView investimento = (TextView) rootView.findViewById(R.id.investimento);
        final TextView na_bolsa = (TextView) rootView.findViewById(R.id.na_bolsa);

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Statistics stats_class = dataSnapshot.getValue(Statistics.class);

                code[0] = stats_class.getNumber_ofBets();

                ganhos.setText(stats_class.getAll_earnings() + "€");
                investimento.setText(stats_class.getValue_spent() + "€");
                na_bolsa.setText(stats_class.getOn_pouch() + "€");

                if(stats_class.getPendingBetsExist().equals("1")) {
                    pending.setVisibility(View.VISIBLE);
                }
                else{
                    pending.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        statsRef.addValueEventListener(postListener);

        ValueEventListener postListener4 = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Notes notes_class = dataSnapshot.getValue(Notes.class);
                int nID = Integer.parseInt(sp.getString("lastViewedNote", "0"));

                if(Integer.parseInt(notes_class.getNote_id()) > nID) {
                    new_note.setVisibility(View.VISIBLE);
                }
                else {
                    new_note.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        notesRef.addValueEventListener(postListener4);

        DatabaseReference betsRef = FirebaseDatabase.getInstance().getReference().child("Pending");

        LinearLayoutManager bet_layoutManager3 = new LinearLayoutManager(getContext());
        bet_layoutManager3.setReverseLayout(true);
        pending_view.setLayoutManager(bet_layoutManager3);
        pending_view.setNestedScrollingEnabled(false);

        FirebaseRecyclerAdapter mAdapter2 = new FirebaseRecyclerAdapter<Bet, betHolder>(Bet.class, R.layout.populate_this, betHolder.class, betsRef) {
            @Override
            public void populateViewHolder(betHolder betViewHolder, Bet specificBet, int position) {
                betViewHolder.callExpand(getContext(), specificBet.getBet_index(), "2");
                betViewHolder.setIndex(specificBet.getBet_index());
                betViewHolder.setBalance(specificBet.getProjected_winnings(), specificBet.getResult().toString().split("\\{")[1].split("\\}")[0].split(", ")[0].split("=")[1], specificBet.getBet_price());
                betViewHolder.setGeneralBetType(specificBet.getGeneral_bet_type());
                betViewHolder.setGameNumber(specificBet.getGame_number());
            }
        };
        pending_view.setAdapter(mAdapter2);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref = ref.child("Dates");

        FirebaseRecyclerAdapter mAdapter = new FirebaseRecyclerAdapter<Dates, betHolder>(Dates.class, R.layout.populate_by_date, betHolder.class, ref) {
            @Override
            public void populateViewHolder(betHolder betViewHolder, Dates specificDate, int position) {
                try {
                    betViewHolder.setDate(specificDate.getEvent_date());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                betViewHolder.setGeneralBalance(specificDate.getVariation(), specificDate.getResult());
                betViewHolder.setRecyclerView(specificDate.getEvent_date(), getContext());
            }
        };
        transactions_view.setAdapter(mAdapter);

        return rootView;
    }

    public void addEntries() {
        Intent intent = new Intent(rootView.getContext(), NewBetActivity.class);
        startActivity(intent);
    }

    public void addTransactions() {
        final MaterialDialog dialog = new MaterialDialog.Builder(getContext())
                .title("Data da Transação")
                .customView(R.layout.dialog_date_transactions, true)
                .positiveText("Continuar")
                .negativeText("Cancelar")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                        DatePicker dateP = (DatePicker) dialog.getCustomView().findViewById(R.id.datePicker);
                        final String date = getDateFromDatePicker(dateP);

                        MaterialDialog dialog2 = new MaterialDialog.Builder(getContext())
                                .title("Adicionar Transação")
                                .customView(R.layout.dialog_edit_transactions, true)
                                .positiveText("Confirmar")
                                .negativeText("Cancelar")
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull final MaterialDialog dialog2, @NonNull DialogAction which) {

                                        final DatabaseReference statsRef = FirebaseDatabase.getInstance().getReference().child("Statistics");

                                        ValueEventListener postListener5 = new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                Statistics stats_class = dataSnapshot.getValue(Statistics.class);
                                                String bodge = "t0";

                                                if(Integer.parseInt(stats_class.getNumber_ofTransactions().toString()) < 10) {
                                                    bodge = "t00";
                                                }

                                                String index = bodge + (Integer.parseInt(stats_class.getNumber_ofTransactions().toString()) + 1);

                                                EditText description = (EditText) dialog2.getCustomView().findViewById(R.id.n_da_aposta);
                                                EditText value = (EditText) dialog2.getCustomView().findViewById(R.id.n_da_aposta2);
                                                MaterialSpinner type = (MaterialSpinner) dialog2.getCustomView().findViewById(R.id.type_spinner);

                                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Transactions").child(index);
                                                String bet_index = index;
                                                String game_number = description.getText().toString();
                                                String projected_winnings = value.getText().toString();
                                                String date2 = date;
                                                String bet_price;

                                                statsRef.child("number_ofTransactions").setValue(String.valueOf(Integer.parseInt(stats_class.getNumber_ofTransactions()) + 1));

                                                double pouch = Double.parseDouble(stats_class.getOn_pouch());

                                                if(type.getSelectedIndex() == 0){
                                                    bet_price = "1";
                                                    statsRef.child("on_pouch").setValue(String.valueOf(pouch + Double.parseDouble(value.getText().toString())));
                                                    statsRef.child("current_share").setValue(String.format(Locale.ENGLISH, "%.2f", (pouch + Double.parseDouble(value.getText().toString())) / 6.0));
                                                    statsRef.child("all_earnings").setValue(String.valueOf(Double.parseDouble(stats_class.getAll_earnings()) + Double.parseDouble(value.getText().toString())));
                                                    addToDatesNode(date);
                                                }
                                                else {
                                                    bet_price = "0";
                                                    statsRef.child("on_pouch").setValue(String.valueOf(pouch - Double.parseDouble(value.getText().toString())));
                                                    statsRef.child("current_share").setValue(String.format(Locale.ENGLISH, "%.2f", (pouch - Double.parseDouble(value.getText().toString())) / 6.0));
                                                    statsRef.child("value_spent").setValue(String.valueOf(Double.parseDouble(stats_class.getValue_spent()) + Double.parseDouble(value.getText().toString())));
                                                    addToDatesNode(date);
                                                }

                                                Bet bet = new Bet(bet_index, date2, bet_price, game_number, projected_winnings, null, null, null);
                                                ref.setValue(bet);

                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        };
                                        statsRef.addListenerForSingleValueEvent(postListener5);
                                    }
                                })
                                .build();

                        EditText description = (EditText) dialog2.getCustomView().findViewById(R.id.n_da_aposta);
                        EditText value = (EditText) dialog2.getCustomView().findViewById(R.id.n_da_aposta2);

                        description.setHint("Descrição Breve");
                        value.setHint("25.00");

                        MaterialSpinner type = (MaterialSpinner) dialog2.getCustomView().findViewById(R.id.type_spinner);
                        type.setItems("Lucro", "Despesa");

                        dialog2.show();
                    }
                })
                .build();

        dialog.show();
        fam.toggle(true);

    }

    public static void addToDatesNode(final String date) {
        final DatabaseReference statsRef = FirebaseDatabase.getInstance().getReference().child("Statistics");
        final DatabaseReference datesRef = FirebaseDatabase.getInstance().getReference().child("Dates");

        ValueEventListener postListener5 = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final Statistics stats_class = dataSnapshot.getValue(Statistics.class);

                final String index2 = stats_class.getNumber_ofDates();

                final String prev_index = String.valueOf(Integer.parseInt(index2) - 1);
                final double new_on_pouch = Double.parseDouble(stats_class.getOn_pouch());

                final DatabaseReference prev_datesRef = datesRef.child("0" + prev_index);

                ValueEventListener postListener5 = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Dates dates_class = dataSnapshot.getValue(Dates.class);

                        String index = index2;

                        if(dates_class.getEvent_date().equals(date)) {

                            final String index3 = String.valueOf(Integer.parseInt(index) - 1);

                            final DatabaseReference prev_datesRef = datesRef.child("0" + String.valueOf(Integer.parseInt(index3) - 1));

                            ValueEventListener postListener5 = new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    Dates dates_class = dataSnapshot.getValue(Dates.class);

                                    DatabaseReference datesRef2 = datesRef.child("0" + index3);

                                    String event_date = date;

                                    double pouch = Double.parseDouble(dates_class.getPouch_content());
                                    double res = new_on_pouch - pouch;

                                    String pouch_content = String.valueOf(new_on_pouch);
                                    String result;
                                    String variation;

                                    if (res > 0) {
                                        result = "1";
                                        variation = String.valueOf(res);
                                    } else {
                                        result = "0";
                                        variation = String.valueOf(Math.abs(res));
                                    }

                                    Dates dates = new Dates(event_date, pouch_content, result, variation);
                                    datesRef2.setValue(dates);

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            };
                            prev_datesRef.addListenerForSingleValueEvent(postListener5);

                        }

                        else {

                            DatabaseReference datesRef2 = datesRef.child("0" + index);

                            statsRef.child("number_ofDates").setValue(String.valueOf(Integer.parseInt(index) + 1));

                            String event_date = date;

                            double pouch = Double.parseDouble(dates_class.getPouch_content());
                            double res = new_on_pouch - pouch;

                            String pouch_content = String.valueOf(new_on_pouch);
                            String result;
                            String variation;

                            if (res > 0) {
                                result = "1";
                                variation = String.valueOf(res);
                            } else {
                                result = "0";
                                variation = String.valueOf(Math.abs(res));
                            }

                            Dates dates = new Dates(event_date, pouch_content, result, variation);
                            datesRef2.setValue(dates);
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                };
                prev_datesRef.addListenerForSingleValueEvent(postListener5);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        statsRef.addListenerForSingleValueEvent(postListener5);
    }

    public static class betHolder extends RecyclerView.ViewHolder {
        View mView;

        public betHolder(View itemView) {
            super(itemView);
            mView = itemView;

        }

        public void callExpand(Context ctx, String index, String mode2) {
            RelativeLayout view = (RelativeLayout) mView.findViewById(R.id.individual_bet);

            final String finalIndex = index;
            final Context ctxx = ctx;
            final String mode22 = mode2;
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ctxx, BetActivity.class);
                    Bundle b = new Bundle();
                    b.putString("index", finalIndex);
                    b.putString("mode", mode22);
                    intent.putExtras(b);
                    ctxx.startActivity(intent);
                }
            });
        }

        public void setIndex(String index) {
            TextView field = (TextView) mView.findViewById(R.id.bet_number);

            if (index.charAt(0)=='0') {
                index = index.substring(1);
            }

            else if (index.charAt(0)=='t') {
                if (index.charAt(1)=='0') {
                    index = index.substring(2);
                }
                else {
                    index = index.substring(1);
                }
                index = "Transação " + index;
            }
            
            field.setText(index);
        }

        public void setDate(String date) throws ParseException {
            TextView field1 = (TextView) mView.findViewById(R.id.textView_type);
            TextView field2 = (TextView) mView.findViewById(R.id.textView_Date);

            Calendar c = Calendar.getInstance();
            c.setTime(new SimpleDateFormat("dd-MM-yyyy").parse(date));
            int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);

            field1.setText(getPortugueseDay(dayOfWeek));
            field2.setText(date.split("-")[0] + " " + getPortugueseMonth(date.split("-")[1]) + " " + date.split("-")[2]);
        }

        public void setGeneralBalance(String amount, String result) {
            TextView field = (TextView) mView.findViewById(R.id.textView_Amount);
            String balance = "error";

            if (result.equals("1")) {
                balance = String.format(Locale.ENGLISH, "%.2f", Float.parseFloat(amount)) + "€";
                field.setTextColor(Color.parseColor("#FF669900"));
            } else if (result.equals("0")){
                balance = "-" + String.format(Locale.ENGLISH, "%.2f", Float.parseFloat(amount)) + "€";
                field.setTextColor(Color.parseColor("#FFCC0000"));
            }
            else if (result.equals("2")) {
                balance = String.format(Locale.ENGLISH, "%.2f", Float.parseFloat(amount)) + "€";
                field.setTextColor(Color.parseColor("#FFFF8800"));
            }

            field.setText(balance);
        }


        public void setBalance(String winnings, String result, String losses) {
            TextView field = (TextView) mView.findViewById(R.id.bet_amount);
            String balance = "error";

            if (result.equals("1")) {
                float math_result = Float.parseFloat(winnings) - Float.parseFloat(losses);
                balance = String.format(Locale.ENGLISH, "%.2f", math_result) + "€";
                field.setTextColor(Color.parseColor("#FF669900"));
            } else if (result.equals("0")){
                balance = "-" + String.format(Locale.ENGLISH, "%.2f", Float.parseFloat(losses)) + "€";
                field.setTextColor(Color.parseColor("#FFCC0000"));
            }
            else if (result.equals("2")) {
                float math_result = Float.parseFloat(winnings) - Float.parseFloat(losses);
                balance = String.format(Locale.ENGLISH, "%.2f", math_result) + "€";
                field.setTextColor(Color.parseColor("#FFFF8800"));
            }
            else if (result.equals("3")) {
                float math_result = Float.parseFloat(winnings) - Float.parseFloat(losses);
                balance = String.format(Locale.ENGLISH, "%.2f", math_result) + "€";
                field.setTextColor(Color.parseColor("#415dae"));
            }

            field.setText(balance);
        }

        public void setGeneralBetType(String type) {
            TextView field = (TextView) mView.findViewById(R.id.bet_type);
            field.setText("Aposta " + type);
        }

        public void setGameNumber(String number) {
            TextView field = (TextView) mView.findViewById(R.id.bet_game_number);
            String jogos = " Jogos";

            if(number.equals("1")) {
                jogos = " Jogo";
            }

            field.setText(number + jogos);
        }

        public void callDialog(final Context ctx, final String descrip, final String res, final String val, final String index, final String date) {
            RelativeLayout view = (RelativeLayout) mView.findViewById(R.id.individual_bet);
            TextView bet_type = (TextView) mView.findViewById(R.id.bet_type);
            TextView bet_game_number = (TextView) mView.findViewById(R.id.bet_game_number);
            TextView bet_number = (TextView) mView.findViewById(R.id.bet_number);

            bet_type.setVisibility(View.GONE);
            bet_game_number.setVisibility(View.GONE);

            bet_number.setTextSize(TypedValue.COMPLEX_UNIT_SP, 21);
            bet_number.setTextColor(Color.parseColor("#0084ff"));

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final MaterialDialog dialog = new MaterialDialog.Builder(ctx)
                            .title("Detalhes da Transação")
                            .customView(R.layout.dialog_transactions, true)
                            .positiveText("Editar")
                            .negativeText("Concluído")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                    MaterialDialog dialog2 = new MaterialDialog.Builder(ctx)
                                            .title("Editar Transação")
                                            .customView(R.layout.dialog_edit_transactions, true)
                                            .positiveText("Confirmar")
                                            .negativeText("Cancelar")
                                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                                @Override
                                                public void onClick(@NonNull final MaterialDialog dialog2, @NonNull DialogAction which) {

                                                    final DatabaseReference statsRef = FirebaseDatabase.getInstance().getReference().child("Statistics");

                                                    ValueEventListener postListener5 = new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            Statistics stats_class = dataSnapshot.getValue(Statistics.class);

                                                            EditText description = (EditText) dialog2.getCustomView().findViewById(R.id.n_da_aposta);
                                                            EditText value = (EditText) dialog2.getCustomView().findViewById(R.id.n_da_aposta2);
                                                            MaterialSpinner type = (MaterialSpinner) dialog2.getCustomView().findViewById(R.id.type_spinner);

                                                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Transactions").child(index);
                                                            String bet_index = index;
                                                            String game_number = description.getText().toString();
                                                            String projected_winnings = value.getText().toString();
                                                            String date2 = date;
                                                            String bet_price;

                                                            double pouch;
                                                            double all_earn = 0.0;
                                                            double val_spent = 0.0;

                                                            // Revert changes to Statistic Variables
                                                            if(res.equals("1")) {
                                                                pouch = Double.parseDouble(stats_class.getOn_pouch()) - Double.parseDouble(val);
                                                                all_earn = Double.parseDouble(stats_class.getAll_earnings()) - Double.parseDouble(val);
                                                            }
                                                            else {
                                                                pouch = Double.parseDouble(stats_class.getOn_pouch()) + Double.parseDouble(val);
                                                                val_spent = Double.parseDouble(stats_class.getValue_spent()) - Double.parseDouble(val);
                                                            }

                                                            if(type.getSelectedIndex() == 0){
                                                                bet_price = "1";
                                                                statsRef.child("on_pouch").setValue(String.valueOf(pouch + Double.parseDouble(value.getText().toString())));
                                                                statsRef.child("current_share").setValue(String.format(Locale.ENGLISH, "%.2f", (pouch + Double.parseDouble(value.getText().toString())) / 6.0));
                                                                statsRef.child("all_earnings").setValue(String.valueOf(all_earn + Double.parseDouble(value.getText().toString())));
                                                                addToDatesNode(date);
                                                            }
                                                            else {
                                                                bet_price = "0";
                                                                statsRef.child("on_pouch").setValue(String.valueOf(pouch - Double.parseDouble(value.getText().toString())));
                                                                statsRef.child("current_share").setValue(String.format(Locale.ENGLISH, "%.2f", (pouch - Double.parseDouble(value.getText().toString())) / 6.0));
                                                                statsRef.child("value_spent").setValue(String.valueOf(val_spent + Double.parseDouble(value.getText().toString())));
                                                                addToDatesNode(date);
                                                            }

                                                            Bet bet = new Bet(bet_index, date2, bet_price, game_number, projected_winnings, null, null, null);
                                                            ref.setValue(bet);

                                                        }

                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {

                                                        }
                                                    };
                                                    statsRef.addListenerForSingleValueEvent(postListener5);

                                                }
                                            })
                                            .build();

                                    EditText description = (EditText) dialog2.getCustomView().findViewById(R.id.n_da_aposta);
                                    EditText value = (EditText) dialog2.getCustomView().findViewById(R.id.n_da_aposta2);

                                    description.setText(descrip);
                                    value.setText(val);

                                    MaterialSpinner type = (MaterialSpinner) dialog2.getCustomView().findViewById(R.id.type_spinner);
                                    type.setItems("Lucro", "Despesa");

                                    if(res.equals("0")) {
                                        type.setSelectedIndex(1);
                                    }

                                    dialog2.show();
                                }
                            })
                            .build();

                    TextView description = (TextView) dialog.getCustomView().findViewById(R.id.n_da_aposta);
                    TextView value = (TextView) dialog.getCustomView().findViewById(R.id.n_da_aposta2);
                    TextView type = (TextView) dialog.getCustomView().findViewById(R.id.n_da_aposta3);

                    String fRes = "Lucro";

                    if(res.equals("0")) {
                        fRes = "Despesa";
                    }

                    description.setText(descrip);
                    value.setText(val + "€");
                    type.setText(fRes);

                    dialog.show();
                }
            });
        }

        public void setRecyclerView(String date, final Context ctx) {

            Query betsRef = FirebaseDatabase.getInstance().getReference().child("Transactions").orderByChild("date").equalTo(date);

            RecyclerView games_view = (RecyclerView) mView.findViewById(R.id.games_view);

            LinearLayoutManager bet_layoutManager2 = new LinearLayoutManager(ctx);
            bet_layoutManager2.setReverseLayout(true);
            games_view.setLayoutManager(bet_layoutManager2);
            games_view.setNestedScrollingEnabled(false);

            FirebaseRecyclerAdapter mAdapter = new FirebaseRecyclerAdapter<Bet, betHolder>(Bet.class, R.layout.populate_this, betHolder.class, betsRef) {
                @Override
                public void populateViewHolder(betHolder betViewHolder, Bet specificBet, int position) {
                    if(specificBet.getBet_index().contains("t")) {
                        betViewHolder.callDialog(ctx, specificBet.getGame_number(), specificBet.getBet_price(), specificBet.getProjected_winnings(), specificBet.getBet_index(), specificBet.getDate());
                        betViewHolder.setIndex(specificBet.getBet_index());
                        if(specificBet.getBet_price().equals("1")){
                            betViewHolder.setBalance(specificBet.getProjected_winnings(), "1", "0");
                        }
                        else {
                            betViewHolder.setBalance("0", "0", specificBet.getProjected_winnings());
                        }
                    }
                    else {
                        betViewHolder.callExpand(ctx, specificBet.getBet_index(), "1");
                        betViewHolder.setIndex(specificBet.getBet_index());
                        betViewHolder.setBalance(specificBet.getProjected_winnings(), specificBet.getResult().toString().split("\\{")[1].split("\\}")[0].split(", ")[0].split("=")[1], specificBet.getBet_price());
                        betViewHolder.setGeneralBetType(specificBet.getGeneral_bet_type());
                        betViewHolder.setGameNumber(specificBet.getGame_number());
                    }
                }
            };
            games_view.setAdapter(mAdapter);
        }

    }

    static String getPortugueseDay(int day) {
        String dia = "erro";

        switch (day){
            case 1:
                dia = "Domingo";
                break;

            case 2:
                dia = "Segunda";
                break;

            case 3:
                dia = "Terça";
                break;

            case 4:
                dia = "Quarta";
                break;

            case 5:
                dia = "Quinta";
                break;

            case 6:
                dia = "Sexta";
                break;

            case 7:
                dia = "Sábado";
                break;
        }

        return dia;
    }

    static String getPortugueseMonth(String month) {
        String mes = "erro";

        switch (month){
            case "01":
                mes = "Janeiro";
                break;

            case "02":
                mes = "Fevereiro";
                break;

            case "03":
                mes = "Março";
                break;

            case "04":
                mes = "Abril";
                break;

            case "05":
                mes = "Maio";
                break;

            case "06":
                mes = "Junho";
                break;

            case "07":
                mes = "Julho";
                break;

            case "08":
                mes = "Agosto";
                break;

            case "09":
                mes = "Setembro";
                break;

            case "10":
                mes = "Outubro";
                break;

            case "11":
                mes = "Novembro";
                break;

            case "12":
                mes = "Dezembro";
                break;

        }

        return mes;
    }

    static String getUserName(int uid) {
        String user = "Error";

        switch (uid) {
            case 1:
                user = "Nuno";
                break;

            case 2:
                user = "Chico";
                break;

            case 3:
                user = "Lóis";
                break;

            case 4:
                user = "Melo";
                break;

            case 5:
                user = "Salgado";
                break;

            case 6:
                user = "Lameiro";
                break;

            default:
                user = "Quico";
                break;
        }

        return user;
    }

    static int getUserPhoto(int uid) {
        int id;

        switch (uid) {
            case 1:
                id = R.drawable.pb_nuno;
                break;

            case 2:
                id = R.drawable.pb_chico;
                break;

            case 3:
                id = R.drawable.pb_lois;
                break;

            case 4:
                id = R.drawable.pb_melo;
                break;

            case 5:
                id = R.drawable.pb_salgado;
                break;

            case 6:
                id = R.drawable.pb_lameiro;
                break;

            default:
                id = R.drawable.pb_quico;
                break;
        }

        return id;
    }

    public static String getDateFromDatePicker(DatePicker datePicker){
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth();
        int year =  datePicker.getYear();

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);

        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        return df.format(calendar.getTime());
    }

}

