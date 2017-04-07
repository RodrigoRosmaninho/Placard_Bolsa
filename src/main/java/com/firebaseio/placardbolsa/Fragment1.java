package com.firebaseio.placardbolsa;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
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
import org.w3c.dom.Text;

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

    public static int number_of_games;
    int passes = 0;
    int number;
    int goForIt = 0;
    int pass = 0;
    int typeBodge = 0;

    String modey;

    ProgressDialog pd;

    private static RecyclerView mRecyclerView;
    private static RecyclerView.Adapter mAdapter;
    private static RecyclerView.LayoutManager mLayoutManager;
    public static List<gameCode> myDataset;

    final String[] code = new String[2];

    ArrayList<String> markets = new ArrayList<String>();
    ArrayList<String> homeOpponents = new ArrayList<String>();
    ArrayList<String> awayOpponents = new ArrayList<String>();
    ArrayList<String> prices = new ArrayList<String>();
    ArrayList<String> codes = new ArrayList<String>();
    ArrayList<String> types = new ArrayList<String>();
    ArrayList<String> sports = new ArrayList<String>();
    ArrayList<String> outcomes = new ArrayList<String>();

    static ArrayList<View> viewArray = new ArrayList<View>();

    String spent = "";
    String overallType = "";

    static boolean calledAlready = false;

    public static final String MyPREFERENCES = "PlacardBolsaPrefs" ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final SharedPreferences sp = this.getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        final int uID = sp.getInt("UserID", 01);

        rootView = inflater.inflate(R.layout.fragment_main, container, false);

        markets.clear();
        homeOpponents.clear();
        awayOpponents.clear();
        prices.clear();
        codes.clear();
        types.clear();
        outcomes.clear();

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
        final MaterialDialog dialog = new MaterialDialog.Builder(getContext())
                .title("Adicionar Nova Aposta")
                .customView(R.layout.dialog_addentries, true)
                .positiveText("Adicionar Jogos")
                .negativeText("Cancelar")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        MaterialSpinner ammount = (MaterialSpinner) dialog.getCustomView().findViewById(R.id.ammount_spinner);
                        MaterialSpinner type = (MaterialSpinner) dialog.getCustomView().findViewById(R.id.type_spinner);

                        int ammountInt = ammount.getSelectedIndex();
                        int typeInt = type.getSelectedIndex();

                        ArrayList<String> a = new ArrayList<String>();
                        ArrayList<String> t = new ArrayList<String>();

                        a.add("5");
                        a.add("1");
                        a.add("2");
                        a.add("10");
                        a.add("20");
                        a.add("50");
                        a.add("75");
                        a.add("100");
                        t.add("Combinada");
                        t.add("Simples");
                        t.add("Múltipla");

                        spent = a.get(ammountInt);
                        overallType = t.get(typeInt);

                        MaterialDialog dialog2 = new MaterialDialog.Builder(getContext())
                                .title("Adicionar Jogos")
                                .customView(R.layout.dialog_addgames, true)
                                .positiveText("Confirmar")
                                .negativeText("Cancelar")
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog2, @NonNull DialogAction which) {
                                        //TODO: Check 1 x 2 + - compatibility with betType
                                        parseInput("1");
                                    }
                                })
                                .build();

                        viewArray.clear();
                        myDataset = gameCode.createEmpty();
                        number_of_games = 1;

                        mRecyclerView = (RecyclerView) dialog2.getCustomView().findViewById(R.id.games_list);

                        mRecyclerView.setHasFixedSize(true);

                        mLayoutManager = new LinearLayoutManager(getContext());
                        mRecyclerView.setLayoutManager(mLayoutManager);

                        mAdapter = new MyAdapter(getContext(), myDataset);
                        mRecyclerView.setAdapter(mAdapter);

                        Button add = (Button) dialog2.getCustomView().findViewById(R.id.add_button);
                        add.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                prePreAdd(v);
                            }
                        });


                        dialog2.show();
                    }
                })
                .build();

        final TextView n_da_aposta = (TextView) dialog.getCustomView().findViewById(R.id.n_da_aposta);
        final TextView d_da_aposta = (TextView) dialog.getCustomView().findViewById(R.id.d_da_aposta);

        DatabaseReference statsRef = FirebaseDatabase.getInstance().getReference();
        statsRef = statsRef.child("Statistics");

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss_dd-MM-yyyy");
        String formattedDate = df.format(c.getTime());
        final String date = formattedDate.split("_")[1];

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Statistics stats_class = dataSnapshot.getValue(Statistics.class);

                String bet_number_string = stats_class.getNumber_ofBets();
                int bet_number_int = Integer.parseInt(bet_number_string) + 1;
                String bet_number_result = "Aposta " + bet_number_int;

                n_da_aposta.setText(bet_number_result);
                d_da_aposta.setText(date);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        statsRef.addListenerForSingleValueEvent(postListener);

        MaterialSpinner typeSpinner = (MaterialSpinner) dialog.getCustomView().findViewById(R.id.type_spinner);
        View spinner_tipo = (View) dialog.getCustomView().findViewById(R.id.type_spinner);
        spinner_tipo.isInEditMode();
        typeSpinner.setItems("Combinada", "Simples", "Múltipla");

        MaterialSpinner ammountSpinner = (MaterialSpinner) dialog.getCustomView().findViewById(R.id.ammount_spinner);
        ammountSpinner.setItems("5€", "1€", "2€", "10€", "20€", "50€", "75€", "100€");

        dialog.show();
        fam.toggle(true);

    }

    //TODO: Change
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
                                                ref.child("bet_index").setValue(index);
                                                ref.child("game_number").setValue(description.getText().toString());
                                                ref.child("projected_winnings").setValue(value.getText().toString());
                                                ref.child("date").setValue(date);

                                                if(type.getSelectedIndex() == 0){
                                                    ref.child("bet_price").setValue("1");
                                                }
                                                else {
                                                    ref.child("bet_price").setValue("0");
                                                }

                                                statsRef.child("number_ofTransactions").setValue(String.valueOf(Integer.parseInt(stats_class.getNumber_ofTransactions().toString()) + 1));

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

    public void parserJSON(JsonElement root) throws JSONException {

        JsonObject mainObject = root.getAsJsonObject();

        if (mainObject.has("Error")) {
            MaterialDialog dialog = new MaterialDialog.Builder(getContext())
                    .title("Erro!")
                    .customView(R.layout.dialog_fourzerofour, true)
                    .positiveText("OK")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull final MaterialDialog dialog2, @NonNull DialogAction which) {
                            // Dismiss
                        }
                    })
                    .build();

            TextView codeView = (TextView) dialog.getCustomView().findViewById(R.id.textView11);

            codeView.setText(mainObject.get("eID").getAsString());

            pd.dismiss();
            pass = 0;
            passes = 0;
            goForIt = 0;
            typeBodge = 0;
            codes.clear();
            types.clear();
            outcomes.clear();
            dialog.show();
        }

        else {

            JsonArray marketsArray = mainObject.get("markets").getAsJsonArray();

            int indexToUseOne = 0;

            if (types.get(typeBodge).equals("INT")) {
                indexToUseOne = 1;
            } else if (types.get(typeBodge).equals("DV")) {
                indexToUseOne = 2;
            } else if (types.get(typeBodge).equals("+/-")) {
                indexToUseOne = 3;
            }


            JsonObject marketsObject = marketsArray.get(indexToUseOne).getAsJsonObject();
            JsonArray outcomesArray = marketsObject.get("outcomes").getAsJsonArray();

            String homeOpponent = mainObject.get("homeOpponentDescription").getAsString();
            Log.d("DEBUG", "ALLOALLO: " + homeOpponent);
            String awayOpponent = mainObject.get("awayOpponentDescription").getAsString();

            String sport = mainObject.get("sportCode").getAsString();

            int indexToUseTwo = 0;

            if (outcomes.get(typeBodge).equals("x")) {
                indexToUseTwo = 1;
            } else if (outcomes.get(typeBodge).equals("2")) {
                indexToUseTwo = 2;
            } else if (outcomes.get(typeBodge).equals("-")) {
                indexToUseTwo = 1;
            }

            JsonObject outcomeObject = outcomesArray.get(indexToUseTwo).getAsJsonObject();
            typeBodge = typeBodge + 1;
            JsonObject outcomePriceObj = outcomeObject.get("price").getAsJsonObject();
            String price = outcomePriceObj.get("decimalPrice").getAsString();

            markets.add(marketsObject.toString());
            homeOpponents.add(homeOpponent);
            awayOpponents.add(awayOpponent);
            prices.add(price);
            sports.add(sport);

            goForIt = goForIt + 1;
            pd.setMessage("A obter dados dos servidores do Placard...    (" + goForIt + "/" + (number + 1) + ")");

            Log.d("DEBUG", "HERE: " + homeOpponents.toString());
            if (goForIt == number + 1) {
                Log.d("DEBUG", "Wait is over");
                showConfirmActivity(modey);
            }

        }

    }

    public void showConfirmActivity(String mode) {
        Log.d("DEBUG", "ALLO: " + outcomes.toString());
        Intent intent = new Intent(getContext(), ConfirmActivity.class);
        Bundle b = new Bundle();
        b.putString("mode", mode);
        b.putStringArrayList("markets", markets);
        b.putStringArrayList("homeOpponents", homeOpponents);
        b.putStringArrayList("awayOpponents", awayOpponents);
        b.putStringArrayList("prices", prices);
        b.putStringArrayList("codes", codes);
        b.putStringArrayList("types", types);
        b.putStringArrayList("outcomes", outcomes);
        b.putString("spent", spent);
        b.putString("overallType", overallType);
        b.putStringArray("index", code);
        b.putStringArrayList("sports", sports);
        intent.putExtras(b);

        pd.dismiss();
        pass = 0;
        goForIt = 0;
        startActivity(intent);

        markets.clear();
        homeOpponents.clear();
        awayOpponents.clear();
        prices.clear();
        codes.clear();
        types.clear();
        outcomes.clear();
        sports.clear();
    }

    public void parseInput(String mode) {
        pass = 0;
        passes = 0;
        goForIt = 0;
        typeBodge = 0;
        number = MyAdapter.getPosition();
        modey = mode;

        while (passes != number + 1) {
            View v = viewArray.get(passes);
            Log.d("DEBUG", "Child number: " + mRecyclerView.getAdapter().getItemCount());
            Log.d("DEBUG", "passes: " + passes);
            EditText placardCode = (EditText) v.findViewById(R.id.placardCode);
            MaterialSpinner type = (MaterialSpinner) v.findViewById(R.id.betType_spinner);
            MaterialSpinner outcome = (MaterialSpinner) v.findViewById(R.id.outcome_spinner);

            int tipoBodge = type.getSelectedIndex();
            int outcomeBodge = outcome.getSelectedIndex();

            ArrayList<String> t = new ArrayList<String>();
            ArrayList<String> o = new ArrayList<String>();

            t.add("1x2 TR");
            t.add("INT");
            t.add("DV");
            t.add("+/-");
            o.add("1");
            o.add("x");
            o.add("2");
            o.add("+");
            o.add("-");

            String tt = t.get(tipoBodge);
            String oo = o.get(outcomeBodge);

            t.clear();
            o.clear();

            String código = placardCode.getText().toString();
            Log.d("DEBUG", "Código: " + código + ", Posição: " + number);

            String api_url = "https://runkit.io/rodrigorosmaninho/pb-placard-api/branches/master/" + código;
            codes.add(código);

            types.add(tt);
            outcomes.add(oo);
            Log.d("DEBUG", "tt: " + types.toString() + " oo: " + outcomes.toString());

            TestAsyncTask testAsyncTask = new TestAsyncTask(api_url);
            testAsyncTask.execute();

            Log.d("DEBUG", "Executed loop");

            passes = passes + 1;
        }

    }

    public void prePreAdd(View v) {
        MyAdapter.preAdd(v);
    }

    public static void addLineToList(View v, int pos, Context context) {
        if (number_of_games < 8) {
            myDataset.add(pos + 1, new gameCode("", "", ""));
            mAdapter.notifyItemInserted(pos + 1);

            Log.d("DEBUG", myDataset.toString());

            number_of_games = number_of_games + 1;
            mRecyclerView.invalidate();
        } else {
            Snackbar.make(v, "Ja tens 8 jogos listados!", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    }

    public class TestAsyncTask extends AsyncTask<Void, Void, JsonElement> {
        private String mUrl;

        public TestAsyncTask(String url) {
            mUrl = url;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pass = pass + 1;

            if (pass == 1) {
                pd = new ProgressDialog(getContext());
                pd.setMessage("A obter dados dos servidores do Placard...    (1/" + (number + 1) + ")");
            }

            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected JsonElement doInBackground(Void... params) {
            Log.d("DEBUG", "doInBackground started");
            // Connect to the URL using java's native library
            URL url = null;
            try {
                url = new URL(mUrl);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            HttpURLConnection request = null;
            try {
                request = (HttpURLConnection) url.openConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                request.connect();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Convert to a JSON object to print data
            JsonParser jp = new JsonParser(); //from gson
            JsonElement root = null;
            try {
                root = jp.parse(new InputStreamReader((InputStream) request.getContent()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d("DEBUG", "COME_SEE_THIS: " + root.getAsJsonObject().toString());
            return root;
        }

        @Override
        protected void onPostExecute(JsonElement root) {
            super.onPostExecute(root);
            try {
                parserJSON(root);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
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

        public void callDialog(final Context ctx, final String descrip, final String res, final String val, final String index) {
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
                                                public void onClick(@NonNull MaterialDialog dialog2, @NonNull DialogAction which) {
                                                    EditText description = (EditText) dialog2.getCustomView().findViewById(R.id.n_da_aposta);
                                                    EditText value = (EditText) dialog2.getCustomView().findViewById(R.id.n_da_aposta2);
                                                    MaterialSpinner type = (MaterialSpinner) dialog2.getCustomView().findViewById(R.id.type_spinner);

                                                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Transactions").child(index);
                                                    ref.child("game_number").setValue(description.getText().toString());
                                                    ref.child("projected_winnings").setValue(value.getText().toString());

                                                    if(type.getSelectedIndex() == 0){
                                                        ref.child("bet_price").setValue("1");
                                                    }
                                                    else {
                                                        ref.child("bet_price").setValue("0");
                                                    }
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
                        betViewHolder.callDialog(ctx, specificBet.getGame_number(), specificBet.getBet_price(), specificBet.getProjected_winnings(), specificBet.getBet_index());
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


    public static class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        private List<gameCode> myDataset;
        private static Context cont;
        gameCode gameCode;
        static int pos;

        public class ViewHolder extends RecyclerView.ViewHolder {
            public EditText placardCode;
            public MaterialSpinner outcomeSpinner;
            public MaterialSpinner betTypeSpinner;

            public ViewHolder(View itemView) {
                super(itemView);

                placardCode = (EditText) itemView.findViewById(R.id.placardCode);
                outcomeSpinner = (MaterialSpinner) itemView.findViewById(R.id.outcome_spinner);
                betTypeSpinner = (MaterialSpinner) itemView.findViewById(R.id.betType_spinner);
            }
        }

        // Provide a suitable constructor (depends on the kind of dataset)
        public MyAdapter(Context context, List<gameCode> game_code) {
            myDataset = game_code;
            cont = context;

        }

        // Create new views (invoked by the layout manager)
        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {

            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);

            // Inflate the custom layout
            View gamesView = inflater.inflate(R.layout.populate_newgames, parent, false);

            // Return a new holder instance
            ViewHolder viewHolder = new ViewHolder(gamesView);
            return viewHolder;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(MyAdapter.ViewHolder viewHolder, int position) {
            pos = position;
            gameCode = myDataset.get(position);

            viewArray.add(viewHolder.itemView);

            EditText placardCode = (EditText) viewHolder.placardCode;
            MaterialSpinner outcome_spinner = (MaterialSpinner) viewHolder.outcomeSpinner;
            MaterialSpinner betType_spinner = (MaterialSpinner) viewHolder.betTypeSpinner;

            outcome_spinner.setItems("1", "x", "2", "+", "-");
            betType_spinner.setItems("TR", "INT", "DV", "+/-");

        }

        public static void preAdd(View v) {
            addLineToList(v, pos, cont);
        }

        public static int getPosition() {
            return pos;
        }


        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return myDataset.size();
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

