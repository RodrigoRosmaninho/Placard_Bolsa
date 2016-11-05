package com.firebaseio.placardbolsa;

import android.app.ProgressDialog;
import android.content.Context;
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
import android.widget.EditText;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import it.gmariotti.cardslib.library.view.CardViewNative;

// Rodrigo Rosmaninho - 2016

public class MainActivity extends AppCompatActivity {

    public static int number_of_games;

    ProgressDialog pd;

    private static RecyclerView mRecyclerView;
    private static RecyclerView.Adapter mAdapter;
    private static RecyclerView.LayoutManager mLayoutManager;
    public static List<gameCode> myDataset;

    static boolean calledAlready = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        com.github.clans.fab.FloatingActionButton fab = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.add_bet_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addEntries();
            }
        });

        com.github.clans.fab.FloatingActionButton fab2 = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.add_sug_fab);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addSuggestions();
            }
        });

        RecyclerView transactions_view = (RecyclerView) findViewById(R.id.transactions_view);
        transactions_view.setHasFixedSize(true);
        LinearLayoutManager bet_layoutManager = new LinearLayoutManager(this);
        bet_layoutManager.setReverseLayout(true);
        transactions_view.setLayoutManager(bet_layoutManager);

        if (!calledAlready) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            calledAlready = true;
        }

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref = ref.child("Transactions");

        DatabaseReference statsRef = FirebaseDatabase.getInstance().getReference();
        statsRef = statsRef.child("Statistics");

        CardViewNative stats = (CardViewNative) findViewById(R.id.stats_winLostCard);
        final TextView stats_text3 = (TextView) findViewById(R.id.stats_text3);

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Statistics stats_class = dataSnapshot.getValue(Statistics.class);

                String ammount = stats_class.getOn_pouch();
                ammount = String.format(Locale.ENGLISH, "%.2f", Float.parseFloat(ammount)) + "€";

                stats_text3.setTextColor(Color.parseColor("#FF669900"));
                stats_text3.setText(ammount);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        statsRef.addValueEventListener(postListener);


        FirebaseRecyclerAdapter mAdapter = new FirebaseRecyclerAdapter<Bet, betHolder>(Bet.class, R.layout.populate_this, betHolder.class, ref) {
            @Override
            public void populateViewHolder(betHolder betViewHolder, Bet specificBet, int position) {
                betViewHolder.setIndex(specificBet.getBet_index());
                betViewHolder.setDate(specificBet.getDate());
                betViewHolder.setBalance(specificBet.getProjected_winnings(), specificBet.getResult().toString().split("\\{")[1].split("\\}")[0].split(", ")[0].split("=")[1], specificBet.getBet_price());
                betViewHolder.setGeneralBetType(specificBet.getGeneral_betType());
                betViewHolder.setGameNumber(specificBet.getGame_number());
            }
        };
        transactions_view.setAdapter(mAdapter);

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

    }
        public void addEntries() {
            final MaterialDialog dialog = new MaterialDialog.Builder(this)
                    .title("Adicionar Nova Aposta")
                    .customView(R.layout.dialog_addentries, true)
                    .positiveText("Adicionar Jogos")
                    .negativeText("Cancelar")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            MaterialDialog dialog2 = new MaterialDialog.Builder(MainActivity.this)
                                    .title("Adicionar Jogos")
                                    .customView(R.layout.dialog_addgames, true)
                                    .positiveText("Confirmar")
                                    .negativeText("Cancelar")
                                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                            parseInput();
                                        }
                                    })
                                    .build();

                            myDataset = gameCode.createEmpty();
                            number_of_games = 1;

                            mRecyclerView = (RecyclerView) dialog2.getCustomView().findViewById(R.id.games_list);

                            mRecyclerView.setHasFixedSize(true);

                            mLayoutManager = new LinearLayoutManager(MainActivity.this);
                            mRecyclerView.setLayoutManager(mLayoutManager);

                            mAdapter = new MyAdapter(MainActivity.this, myDataset);
                            mRecyclerView.setAdapter(mAdapter);



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

        }

    public void addSuggestions() {
        final MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title("Adicionar Nova Sugestão")
                .customView(R.layout.dialog_addentries, true)
                .positiveText("Adicionar Jogos")
                .negativeText("Cancelar")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        MaterialDialog dialog2 = new MaterialDialog.Builder(MainActivity.this)
                                .title("Adicionar Jogos")
                                .customView(R.layout.dialog_addgames, true)
                                .positiveText("Confirmar")
                                .negativeText("Cancelar")
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        parseInput();
                                    }
                                })
                                .build();

                        myDataset = gameCode.createEmpty();
                        number_of_games = 1;

                        mRecyclerView = (RecyclerView) dialog2.getCustomView().findViewById(R.id.games_list);

                        mRecyclerView.setHasFixedSize(true);

                        mLayoutManager = new LinearLayoutManager(MainActivity.this);
                        mRecyclerView.setLayoutManager(mLayoutManager);

                        mAdapter = new MyAdapter(MainActivity.this, myDataset);
                        mRecyclerView.setAdapter(mAdapter);



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

    }

    public void parseJSON(String JSON_String) throws JSONException {
        JSONObject mainObject = new JSONObject(JSON_String);

        JSONObject marketsObject = mainObject.getJSONObject("markets");
        JSONArray outcomesArray = marketsObject.getJSONArray("outcomes");

        String homeOpponent = mainObject.getString("homeOpponentDescription");
        String awayOpponent = mainObject.getString("awayOpponentDescription");

        JSONObject outcomeObject = outcomesArray.getJSONObject(0);
        JSONObject outcomePriceObj = outcomeObject.getJSONObject("price");

        String price = outcomePriceObj.getString("decimalPrice");



        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss_dd-MM-yyyy");
        String formattedDate = df.format(c.getTime());
        final String date = formattedDate.split("_")[1];

    }

    public void parseInput() {
        int number = MyAdapter.getPosition();
        int i = 0;

        while(i != number + 1) {
            View v = mRecyclerView.getLayoutManager().findViewByPosition(i);
            EditText placardCode = (EditText) v.findViewById(R.id.placardCode);
            String código = placardCode.getText().toString();
            Log.d("DEBUG", "Código: " + código + ", Posição: " + number);

            String api_url = "https://runkit.io/rodrigorosmaninho/pb-placard-api/branches/master/" + código;

            new JsonTask().execute(api_url);

            i = i + 1;
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
        }

        else {
            Snackbar.make(v, "Ja tens 8 jogos listados!", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Snackbar.make(findViewById(android.R.id.content), "[Ainda não faz nada]", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }

        if (id == R.id.placard_link) {
            Intent launchIntent = getPackageManager().getLaunchIntentForPackage("pt.scml.placard");
            if (launchIntent != null) {
                startActivity(launchIntent);//null pointer check in case package name was not found
            }
            else {
                Snackbar.make(findViewById(android.R.id.content), "A app Placard não está instalada!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    public void expandStats(View v) {
        startActivity(new Intent(this, StatsActivity.class));
    }

    public static class betHolder extends RecyclerView.ViewHolder {
        View mView;

        public betHolder(View itemView) {
            super(itemView);
            mView = itemView;

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
            String balance;

            if(result.equals("1")) {
                float math_result = Float.parseFloat(winnings) - Float.parseFloat(losses);
                balance = String.format(Locale.ENGLISH, "%.2f", math_result) + "€";
                field.setTextColor(Color.parseColor("#FF669900"));
            }
            else {
                balance = "-" + String.format(Locale.ENGLISH, "%.2f", Float.parseFloat(losses)) + "€";
                field.setTextColor(Color.parseColor("#FFCC0000"));
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

    private class JsonTask extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();

            pd = new ProgressDialog(MainActivity.this);
            pd.setMessage("A obter informações dos servidores do Placard...");
            pd.setCancelable(false);
            pd.show();
        }

        protected String doInBackground(String... params) {


            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();


                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line+"\n");
                    Log.d("Response: ", "> " + line);

                }

                return buffer.toString();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (pd.isShowing()){
                pd.dismiss();
            }
            try {
                parseJSON(result);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}





