package com.firebaseio.placardbolsa;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.firebase.analytics.FirebaseAnalytics;
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

import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class NewBetActivity extends AppCompatActivity {

    private static RecyclerView mRecyclerView;
    private static RecyclerView.Adapter mAdapter;
    private static RecyclerView.LayoutManager mLayoutManager;
    public static List<gameCode> myDataset;
    static ArrayList<View> viewArray = new ArrayList<View>();

    public static int number_of_games;
    static Context ctxxx;

    TextView n_da_aposta;
    TextView d_da_aposta;
    MaterialSpinner typeSpinner;
    MaterialSpinner ammountSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new);

        ctxxx = this;
        n_da_aposta = (TextView) findViewById(R.id.bet_number);
        d_da_aposta = (TextView) findViewById(R.id.dateView);
        typeSpinner = (MaterialSpinner) findViewById(R.id.type_spinner);
        ammountSpinner = (MaterialSpinner) findViewById(R.id.ammount_spinner);

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

        Button pickDate = (Button) findViewById(R.id.pickDate);

        pickDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final MaterialDialog dialog = new MaterialDialog.Builder(NewBetActivity.this)
                        .title("Data da Aposta")
                        .customView(R.layout.dialog_date_transactions, true)
                        .positiveText("Confirmar")
                        .negativeText("Cancelar")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                DatePicker dateP = (DatePicker) dialog.getCustomView().findViewById(R.id.datePicker);
                                d_da_aposta.setText(Fragment1.getDateFromDatePicker(dateP));
                            }
                        })
                        .build();

                dialog.show();
            }
        });

        DatabaseReference statsRef = FirebaseDatabase.getInstance().getReference();
        statsRef = statsRef.child("Statistics");

        final TextView n_da_aposta2 = n_da_aposta;
        final TextView d_da_aposta2 = d_da_aposta;

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
                Log.d("DEBUG", "NEWNUMBER: " + bet_number_result);

                n_da_aposta2.setText(bet_number_result);
                d_da_aposta2.setText(date);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        statsRef.addListenerForSingleValueEvent(postListener);

        View spinner_tipo = (View) findViewById(R.id.type_spinner);
        spinner_tipo.isInEditMode();
        typeSpinner.setItems("Combinada", "Simples", "Múltipla");
        ammountSpinner.setItems("5€", "1€", "2€", "10€", "20€", "50€", "75€", "100€");

        viewArray.clear();
        myDataset = gameCode.createEmpty();
        number_of_games = 1;

        mRecyclerView = (RecyclerView) findViewById(R.id.games_view2);

        mRecyclerView.setNestedScrollingEnabled(false);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new MyAdapter(this, myDataset);
        mRecyclerView.setAdapter(mAdapter);

    }

    public static void parserJSON(JsonElement root, String type, String outcome, final MyAdapter.ViewHolder viewHolder, Context ctx) throws JSONException {

        JsonObject mainObject = root.getAsJsonObject();

        if (mainObject.has("Error")) {
            MaterialDialog dialog = new MaterialDialog.Builder(ctx)
                    .title("Erro!")
                    .customView(R.layout.dialog_fourzerofour, true)
                    .positiveText("OK")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull final MaterialDialog dialog2, @NonNull DialogAction which) {
                            RelativeLayout pickerLayout = viewHolder.picker;
                            RelativeLayout autoLayout = viewHolder.autoL;
                            autoLayout.setVisibility(View.VISIBLE);
                        }
                    })
                    .build();

            TextView codeView = (TextView) dialog.getCustomView().findViewById(R.id.textView11);

            codeView.setText(mainObject.get("eID").getAsString());

            dialog.show();
        }

        else {

            JsonArray marketsArray = mainObject.get("markets").getAsJsonArray();

            int indexToUseOne = 0;

            if (type.equals("INT")) {
                indexToUseOne = 1;
            } else if (type.equals("DV")) {
                indexToUseOne = 2;
            } else if (type.equals("+/-")) {
                indexToUseOne = 3;
            }


            JsonObject marketsObject = marketsArray.get(indexToUseOne).getAsJsonObject();
            JsonArray outcomesArray = marketsObject.get("outcomes").getAsJsonArray();

            String homeOpponent = mainObject.get("homeOpponentDescription").getAsString();
            String awayOpponent = mainObject.get("awayOpponentDescription").getAsString();

            String sport = mainObject.get("sportCode").getAsString();

            int indexToUseTwo = 0;

            if (outcome.equals("x")) {
                indexToUseTwo = 1;
            } else if (outcome.equals("2")) {
                indexToUseTwo = 2;
            } else if (outcome.equals("-")) {
                indexToUseTwo = 1;
            }

            JsonObject outcomeObject = outcomesArray.get(indexToUseTwo).getAsJsonObject();
            JsonObject outcomePriceObj = outcomeObject.get("price").getAsJsonObject();
            String price = outcomePriceObj.get("decimalPrice").getAsString();
            String outcomeDescription = outcomeObject.get("outcomeDescription").getAsString();

            if (outcome.equals("+")) {
                indexToUseTwo = 3;
            }
            else if(outcome.equals("-")) {
                indexToUseTwo = 4;
            }

            String[] data = new String[]{homeOpponent, awayOpponent, sport, price};
            MyAdapter.autoFill(viewHolder, data, indexToUseOne, indexToUseTwo, outcomeDescription);

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

    public static class TestAsyncTask extends AsyncTask<Void, Void, JsonElement> {
        private String mUrl;
        RelativeLayout pb;
        MyAdapter.ViewHolder v;
        String type;
        String outcome;
        Context ctx;

        public TestAsyncTask(String url, RelativeLayout pb2, String type2, String outcome2, MyAdapter.ViewHolder viewHolder, Context ctxx) {
            mUrl = url;
            pb = pb2;
            type = type2;
            outcome = outcome2;
            v = viewHolder;
            ctx = ctxx;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pb.setVisibility(View.VISIBLE);
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
            return root;
        }

        @Override
        protected void onPostExecute(JsonElement root) {
            super.onPostExecute(root);
            try {
                pb.setVisibility(View.GONE);
                parserJSON(root, type, outcome, v, ctx);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public static void parseInput(String código, RelativeLayout pb, String type, String outcome, MyAdapter.ViewHolder viewHolder, Context ctx) {
        String api_url = "https://runkit.io/rodrigorosmaninho/pb-placard-api/branches/master/" + código;

        TestAsyncTask testAsyncTask = new TestAsyncTask(api_url, pb, type, outcome, viewHolder, ctx);
        testAsyncTask.execute();
    }

    public void sendToDatabase() {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        String[] types = {"Combinada", "Simples", "Múltipla"};
        String[] ammounts = {"5€", "1€", "2€", "10€", "20€", "50€", "75€", "100€"};
        int passes = 0;
        double oddTotal_amount = 1.0;
        ArrayList<Game> games = new ArrayList<Game>();
        boolean okToGo = true;

        while (passes != number_of_games) {
            View v = viewArray.get(passes);

            RelativeLayout autoL  = (RelativeLayout) v.findViewById(R.id.auto);
            if(autoL.isShown()) {
                okToGo = false;
                Snackbar.make(findViewById(android.R.id.content), "Clica em OK!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
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

            String strToUse = "Erro";
            if(outDes.getText().toString().equals("Bug")) {
                switch (outcomes[outcome.getSelectedIndex()]) {
                    case "1":
                        strToUse = home.getText().toString();
                        break;

                    case "x":
                        strToUse = "Empate";
                        break;

                    case "2":
                        strToUse = away.getText().toString();
                        break;

                    case "+":
                        strToUse = "Mais";
                        break;

                    case "-":
                        strToUse = "Menos";
                        break;
                }
            }
            else {
                strToUse = outDes.getText().toString();
            }

            Game game = new Game(placardCode.getText().toString(), home.getText().toString(), away.getText().toString(), sports[sport.getSelectedIndex()], "3", strToUse, outcomes[outcome.getSelectedIndex()], String.format(Locale.ENGLISH, "%.2f", Double.parseDouble(odd.getText().toString())), "0", types2[type.getSelectedIndex()]);
            games.add(game);

            oddTotal_amount = oddTotal_amount * Double.parseDouble(odd.getText().toString());
            passes ++;
        }

        if(okToGo) {

            String amount = ammounts[ammountSpinner.getSelectedIndex()];
            amount = amount.substring(0, amount.length() - 1);


            betResult resultObj = new betResult("0", "2");
            Bet bet = new Bet("0" + n_da_aposta.getText().toString().split(" ")[1], d_da_aposta.getText().toString(), amount, String.valueOf(number_of_games), String.format(Locale.ENGLISH, "%.2f", Double.parseDouble(amount) * oddTotal_amount), types[typeSpinner.getSelectedIndex()], String.format(Locale.ENGLISH, "%.2f", oddTotal_amount), resultObj);
            mDatabase.child("Pending").child("0" + n_da_aposta.getText().toString().split(" ")[1]).setValue(bet);

            for (int i = 0; i < number_of_games; i++) {
                mDatabase.child("Pending").child("0" + n_da_aposta.getText().toString().split(" ")[1]).child("games").child("game_0" + (i + 1)).setValue(games.get(i));
            }

            mDatabase.child("Statistics").child("number_ofBets").setValue(n_da_aposta.getText().toString().split(" ")[1]);
            mDatabase.child("Statistics").child("pendingBetsExist").setValue("1");

            String text = "A aposta foi adicionada à Base de Dados!";

            Context context = getApplicationContext();
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();

            number_of_games = 0;

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
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
            public EditText placardCode2;
            public MaterialSpinner outcomeSpinner2;
            public MaterialSpinner betTypeSpinner2;
            public Button autoB;
            public Button manualB;
            public RelativeLayout picker;
            public RelativeLayout autoL;
            public RelativeLayout manualL;
            public Button okB;
            public RelativeLayout pb;
            public EditText home;
            public EditText away;
            public EditText odd;
            public MaterialSpinner sport_spinner;
            public TextView bet_number2;
            public TextView outcomeDescription;

            public ViewHolder(View itemView) {
                super(itemView);

                placardCode = (EditText) itemView.findViewById(R.id.placardCode);
                outcomeSpinner = (MaterialSpinner) itemView.findViewById(R.id.outcome_spinner);
                betTypeSpinner = (MaterialSpinner) itemView.findViewById(R.id.betType_spinner);
                placardCode2 = (EditText) itemView.findViewById(R.id.placardCode2);
                outcomeSpinner2 = (MaterialSpinner) itemView.findViewById(R.id.outcome_spinner2);
                betTypeSpinner2 = (MaterialSpinner) itemView.findViewById(R.id.betType_spinner2);
                autoB = (Button) itemView.findViewById(R.id.button6);
                manualB = (Button) itemView.findViewById(R.id.button7);
                picker = (RelativeLayout) itemView.findViewById(R.id.metadata);
                autoL = (RelativeLayout) itemView.findViewById(R.id.auto);
                manualL = (RelativeLayout) itemView.findViewById(R.id.manual);
                okB = (Button) itemView.findViewById(R.id.button5);
                pb = (RelativeLayout) itemView.findViewById(R.id.load);
                home = (EditText) itemView.findViewById(R.id.home);
                away = (EditText) itemView.findViewById(R.id.away);
                odd = (EditText) itemView.findViewById(R.id.odd);
                sport_spinner = (MaterialSpinner) itemView.findViewById(R.id.sport_spinner);
                bet_number2 = (TextView) itemView.findViewById(R.id.bet_number2);
                outcomeDescription = (TextView) itemView.findViewById(R.id.outcomeDescription);
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
        public void onBindViewHolder(final MyAdapter.ViewHolder viewHolder, int position) {
            pos = position;
            gameCode = myDataset.get(position);

            viewArray.add(viewHolder.itemView);

            TextView bet_number = viewHolder.bet_number2;
            bet_number.setText("Jogo 0" + (position + 1));

            final EditText placardCode = viewHolder.placardCode;
            final MaterialSpinner outcome_spinner = viewHolder.outcomeSpinner;
            final MaterialSpinner betType_spinner = viewHolder.betTypeSpinner;
            final MaterialSpinner outcome_spinner2 = viewHolder.outcomeSpinner2;
            final MaterialSpinner betType_spinner2 = viewHolder.betTypeSpinner2;
            Button auto = viewHolder.autoB;
            Button manual = viewHolder.manualB;
            final RelativeLayout pickerLayout = viewHolder.picker;
            final RelativeLayout autoLayout = viewHolder.autoL;
            final RelativeLayout manualLayout = viewHolder.manualL;
            Button okButton = viewHolder.okB;
            final RelativeLayout pb = viewHolder.pb;
            final MaterialSpinner sport_spinner = viewHolder.sport_spinner;

            final String[] outcomes = {"1", "x", "2", "+", "-"};
            final String[] types = {"TR", "INT", "DV", "+/-"};
            final String[] sports = {"Futebol", "Basketbol", "Ténis"};

            manual.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pickerLayout.setVisibility(View.GONE);
                    outcome_spinner2.setItems(outcomes);
                    betType_spinner2.setItems(types);
                    sport_spinner.setItems(sports);
                    manualLayout.setVisibility(View.VISIBLE);
                }
            });

            auto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pickerLayout.setVisibility(View.GONE);
                    outcome_spinner.setItems(outcomes);
                    betType_spinner.setItems(types);
                    autoLayout.setVisibility(View.VISIBLE);
                }
            });

            okButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    autoLayout.setVisibility(View.GONE);
                    parseInput(placardCode.getText().toString(), pb, outcomes[outcome_spinner.getSelectedIndex()], types[betType_spinner.getSelectedIndex()], viewHolder, ctxxx);
                }
            });

        }

        public static void autoFill(ViewHolder viewHolder, String[] data, int typeIndex, int outcomeIndex, String outcomeDescription) {
            EditText placardCode = viewHolder.placardCode2;
            MaterialSpinner outcome_spinner = viewHolder.outcomeSpinner2;
            MaterialSpinner betType_spinner = viewHolder.betTypeSpinner2;
            MaterialSpinner sport_spinner = viewHolder.sport_spinner;
            EditText home = viewHolder.home;
            EditText away = viewHolder.away;
            EditText odd = viewHolder.odd;
            TextView outDes = viewHolder.outcomeDescription;

            EditText prev_placardCode = viewHolder.placardCode;

            RelativeLayout manual = viewHolder.manualL;

            String[] outcomes = {"1", "x", "2", "+", "-"};
            String[] types = {"TR", "INT", "DV", "+/-"};
            String[] sports = {"Futebol", "Basketbol", "Ténis"};

            outcome_spinner.setItems(outcomes);
            betType_spinner.setItems(types);
            sport_spinner.setItems(sports);

            if(data[2].equals("FOOT")) {
                sport_spinner.setSelectedIndex(0);
            }
            else if(data[2].equals("BASK")) {
                sport_spinner.setSelectedIndex(1);
            }
            else {
                sport_spinner.setSelectedIndex(2);
            }

            placardCode.setText(prev_placardCode.getText().toString());
            home.setText(data[0]);
            away.setText(data[1]);
            odd.setText(data[3]);
            betType_spinner.setSelectedIndex(typeIndex);
            outcome_spinner.setSelectedIndex(outcomeIndex);
            outDes.setText(outcomeDescription);

            manual.setVisibility(View.VISIBLE);

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
