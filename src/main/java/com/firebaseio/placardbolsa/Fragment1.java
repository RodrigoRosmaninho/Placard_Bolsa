package com.firebaseio.placardbolsa;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jaredrummler.materialspinner.MaterialSpinner;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import it.gmariotti.cardslib.library.recyclerview.view.CardRecyclerView;
import it.gmariotti.cardslib.library.view.CardViewNative;

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

    String modey;

    ProgressDialog pd;

    private static RecyclerView mRecyclerView;
    private static RecyclerView.Adapter mAdapter;
    private static RecyclerView.LayoutManager mLayoutManager;
    public static List<gameCode> myDataset;

    final String[] code = new String[1];

    ArrayList<String> markets = new ArrayList<String>();
    ArrayList<String> homeOpponents = new ArrayList<String>();
    ArrayList<String> awayOpponents = new ArrayList<String>();
    ArrayList<String> prices = new ArrayList<String>();
    ArrayList<String> codes = new ArrayList<String>();
    ArrayList<String> types = new ArrayList<String>();
    ArrayList<String> sports = new ArrayList<String>();
    ArrayList<String> outcomes = new ArrayList<String>();

    String spent = "";
    String overallType = "";

    static boolean calledAlready = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_main, container, false);

        markets.clear();
        homeOpponents.clear();
        awayOpponents.clear();
        prices.clear();
        codes.clear();
        types.clear();
        outcomes.clear();

        // Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        // setSupportActionBar(toolbar);

        fam = (com.github.clans.fab.FloatingActionMenu) rootView.findViewById(R.id.fab);

        com.github.clans.fab.FloatingActionButton fab = (com.github.clans.fab.FloatingActionButton) rootView.findViewById(R.id.add_bet_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addEntries();
            }
        });

        com.github.clans.fab.FloatingActionButton fab2 = (com.github.clans.fab.FloatingActionButton) rootView.findViewById(R.id.add_sug_fab);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addSuggestions();
            }
        });

        CardRecyclerView transactions_view = (CardRecyclerView) rootView.findViewById(R.id.transactions_view);
        // transactions_view.setHasFixedSize(true);
        LinearLayoutManager bet_layoutManager = new LinearLayoutManager(getContext());
        bet_layoutManager.setReverseLayout(true);
        transactions_view.setLayoutManager(bet_layoutManager);

        final CardRecyclerView pending_transactions_view = (CardRecyclerView) rootView.findViewById(R.id.pending_transactions_view);

        transactions_view.setNestedScrollingEnabled(false);
        pending_transactions_view.setNestedScrollingEnabled(false);

        // pending_transactions_view.setHasFixedSize(true);
        LinearLayoutManager bet_layoutManager2 = new LinearLayoutManager(getContext());
        bet_layoutManager2.setReverseLayout(true);
        pending_transactions_view.setLayoutManager(bet_layoutManager2);

        if (!calledAlready) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            calledAlready = true;
        }

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref = ref.child("Transactions");

        DatabaseReference statsRef = FirebaseDatabase.getInstance().getReference();
        statsRef = statsRef.child("Statistics");

        final ProgressBar pb2 = (ProgressBar) rootView.findViewById(R.id.progressBar2);

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Statistics stats_class = dataSnapshot.getValue(Statistics.class);

                // DO NOT REMOVE!!!
                code[0] = stats_class.getNumber_ofBets();

                if(stats_class.getPendingBetsExist().equals("0")) {
                    pb2.setVisibility(View.GONE);
                    pending_transactions_view.setVisibility(View.GONE);
                }
                else {
                    pb2.setVisibility(View.VISIBLE);
                    pending_transactions_view.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        statsRef.addValueEventListener(postListener);


        FirebaseRecyclerAdapter mAdapter = new FirebaseRecyclerAdapter<Bet, betHolder>(Bet.class, R.layout.populate_this, betHolder.class, ref) {
            @Override
            public void populateViewHolder(betHolder betViewHolder, Bet specificBet, int position) {
                betViewHolder.callExpand(getContext(), specificBet.getBet_index(), "1");
                betViewHolder.setIndex(specificBet.getBet_index());
                betViewHolder.setDate(specificBet.getDate());
                betViewHolder.setBalance(specificBet.getProjected_winnings(), specificBet.getResult().toString().split("\\{")[1].split("\\}")[0].split(", ")[0].split("=")[1], specificBet.getBet_price());
                betViewHolder.setGeneralBetType(specificBet.getGeneral_bet_type());
                betViewHolder.setGameNumber(specificBet.getGame_number());
            }
        };
        transactions_view.setAdapter(mAdapter);

        DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference().child("Pending");

        FirebaseRecyclerAdapter mAdapter2 = new FirebaseRecyclerAdapter<Bet, betHolder>(Bet.class, R.layout.populate_this, betHolder.class, ref2) {
            @Override
            public void populateViewHolder(betHolder betViewHolder, Bet specificBet, int position) {
                betViewHolder.callExpand(getContext(), specificBet.getBet_index(), "2");
                betViewHolder.setIndex(specificBet.getBet_index());
                betViewHolder.setDate(specificBet.getDate());
                betViewHolder.setBalance(specificBet.getProjected_winnings(), specificBet.getResult().toString().split("\\{")[1].split("\\}")[0].split(", ")[0].split("=")[1], specificBet.getBet_price());
                betViewHolder.setGeneralBetType(specificBet.getGeneral_bet_type());
                betViewHolder.setGameNumber(specificBet.getGame_number());
            }
        };
        pending_transactions_view.setAdapter(mAdapter2);

        /**Handler handler = new Handler();
         handler.postDelayed(new Runnable() {
         public void run() {
         ProgressBar spinner;
         spinner = (ProgressBar)findViewById(R.id.progressBar1);
         spinner.setVisibility(View.GONE);
         }
         }, 2700);**/

        //final EditText mMessage = (EditText) findViewById(R.id.message_text);
        //findViewById(R.id.send_button).setOnClickListener(new View.OnClickListener() {
        //@Override
        //public void onClick(View v) {
        //ref.push().setValue(new Chat("puf", "1234", mMessage.getText().toString()));
        //mMessage.setText("");
        //}
        //});

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

    public void addSuggestions() {
        final MaterialDialog dialog = new MaterialDialog.Builder(getContext())
                .title("Adicionar Nova Sugestão")
                .customView(R.layout.dialog_addentries, true)
                .positiveText("Adicionar Jogos")
                .negativeText("Cancelar")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        MaterialDialog dialog2 = new MaterialDialog.Builder(getContext())
                                .title("Adicionar Jogos")
                                .customView(R.layout.dialog_addgames, true)
                                .positiveText("Confirmar")
                                .negativeText("Cancelar")
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull final MaterialDialog dialog2, @NonNull DialogAction which) {
                                        //TODO: Check 1 x 2 + - compatibility with betType
                                        parseInput("2");
                                    }
                                })
                                .build();

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
        MaterialSpinner spinner_tipo = (MaterialSpinner) dialog.getCustomView().findViewById(R.id.type_spinner);
        spinner_tipo.isInEditMode();
        typeSpinner.setItems("Combinada", "Simples", "Múltipla");

        MaterialSpinner ammountSpinner = (MaterialSpinner) dialog.getCustomView().findViewById(R.id.ammount_spinner);
        ammountSpinner.setItems("5€", "1€", "2€", "10€", "20€", "50€", "75€", "100€");

        dialog.show();
        fam.toggle(true);

    }

    public void parserJSON(JsonElement root) throws JSONException {
        JsonObject mainObject = root.getAsJsonObject();

        JsonArray marketsArray = mainObject.get("markets").getAsJsonArray();
        JsonObject marketsObject = marketsArray.get(0).getAsJsonObject();
        JsonArray outcomesArray = marketsObject.get("outcomes").getAsJsonArray();

        String homeOpponent = mainObject.get("homeOpponentDescription").getAsString();
        Log.d("DEBUG", "ALLOALLO: " + homeOpponent);
        String awayOpponent = mainObject.get("awayOpponentDescription").getAsString();

        JsonObject outcomeObject = outcomesArray.get(0).getAsJsonObject();
        JsonObject outcomePriceObj = outcomeObject.get("price").getAsJsonObject();

        String price = outcomePriceObj.get("decimalPrice").getAsString();

        String sport = mainObject.get("sportCode").getAsString();

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
    }

    public void expandBet(String index) {
        Intent intent = new Intent(getContext(), BetActivity.class);
        Bundle b = new Bundle();
        b.putString("index", index);
        intent.putExtras(b);
        startActivity(intent);
    }

    public void parseInput(String mode) {
        number = MyAdapter.getPosition();
        modey = mode;

        while (passes != number + 1) {
            View v = mRecyclerView.getLayoutManager().findViewByPosition(passes);
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

    public void expandStats(View v) {
        startActivity(new Intent(getContext(), StatsActivity.class));
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
            CardViewNative card_view = (CardViewNative) mView.findViewById(R.id.list_cardId);
            final String finalIndex = index;
            final Context ctxx = ctx;
            final String mode22 = mode2;
            card_view.setOnClickListener(new View.OnClickListener() {
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
            // mView.setPadding(20, 0, 20, 0);
            TextView field = (TextView) mView.findViewById(R.id.bet_text1);
            index = "Aposta " + index.substring(1);
            field.setText(index);
        }

        public void setDate(String date) {
            TextView field = (TextView) mView.findViewById(R.id.bet_text2);
            date = date.split("_")[1];
            field.setText(date);
        }

        public void setBalance(String winnings, String result, String losses) {
            TextView field = (TextView) mView.findViewById(R.id.bet_text3);
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

            field.setText(balance);
        }

        public void setGeneralBetType(String type) {
            TextView field = (TextView) mView.findViewById(R.id.bet_text4);
            field.setText(type);
        }

        public void setGameNumber(String number) {
            TextView field = (TextView) mView.findViewById(R.id.bet_text5);
            field.setTextSize(17);

            field.setText("Jogos: " + number);
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


}

