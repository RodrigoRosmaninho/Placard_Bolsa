package com.firebaseio.placardbolsa;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.recyclerview.view.CardRecyclerView;
import it.gmariotti.cardslib.library.view.CardViewNative;

import static com.firebaseio.placardbolsa.Fragment1.MyPREFERENCES;

public class ConfirmActivity  extends AppCompatActivity {
    static boolean firstHasPassed;
    private static RecyclerView mRecyclerView;
    private static RecyclerView.Adapter mAdapter;
    private static RecyclerView.LayoutManager mLayoutManager;
    public static List<gameCode> myDataset;

    static ArrayList<String> markets= new ArrayList<String>();
    static ArrayList<String> homeOpponents= new ArrayList<String>();
    static ArrayList<String> awayOpponents= new ArrayList<String>();
    static ArrayList<String> prices= new ArrayList<String>();
    static ArrayList<String> codes= new ArrayList<String>();
    static ArrayList<String> types= new ArrayList<String>();
    static ArrayList<String> outcomes= new ArrayList<String>();
    static ArrayList<String> sports = new ArrayList<String>();
    static String[] index = new String[2];
    static int[] suggBodge = new int[1];
    static int[] penBodge = new int[1];
    static String spent = "";
    static String overallType = "";
    static String uName = "";
    static int uID = 001;

    String projected_win;
    String oddTotal;

    String formattedDate = "";

    FirebaseAnalytics mFirebaseAnalytics;

    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firstHasPassed = false;

        SharedPreferences sp = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        uName = sp.getString("UserName", null);
        uID = sp.getInt("UserID", 01);

        markets.clear();
        homeOpponents.clear();
        awayOpponents.clear();
        prices.clear();
        codes.clear();
        types.clear();
        outcomes.clear();

        Bundle b = getIntent().getExtras();

        if (b != null) {
            //TODO: Make snackbar "Critical Error" on else
            markets = b.getStringArrayList("markets");
            homeOpponents = b.getStringArrayList("homeOpponents");
            awayOpponents = b.getStringArrayList("awayOpponents");
            prices = b.getStringArrayList("prices");
            codes = b.getStringArrayList("codes");
            types = b.getStringArrayList("types");
            outcomes = b.getStringArrayList("outcomes");
            spent = b.getString("spent");
            overallType = b.getString("overallType");
            index = b.getStringArray("index");
            sports = b.getStringArrayList("sports");
        }

        setContentView(R.layout.confirm_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        toolbar.setTitle("Confirmar Adição");

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(ConfirmActivity.this);

        final TextView indexTV = (TextView) findViewById(R.id.bet_number);
        indexTV.setText(String.valueOf(Integer.parseInt(index[0]) + 1));

        final TextView date = (TextView) findViewById(R.id.textView_Date);
        final TextView type = (TextView) findViewById(R.id.textView_type);

        final TextView result = (TextView) findViewById(R.id.textView_result);

        final TextView odds = (TextView) findViewById(R.id.oddsTextView);
        final TextView spentTV = (TextView) findViewById(R.id.spentTextView);
        final TextView winnings = (TextView) findViewById(R.id.winningsTextView);
        final TextView balance = (TextView) findViewById(R.id.balanceTextView);


        FloatingActionButton confirmFAB = (FloatingActionButton) findViewById(R.id.fab);
        confirmFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendToDatabase();
            }
        });

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss_dd-MM-yyyy");
        formattedDate = df.format(c.getTime());
        String datef = formattedDate.split("_")[1];

        RecyclerView games_view = (RecyclerView) findViewById(R.id.games_view2);


        balance.setTextColor(Color.parseColor("#FFFF8800"));
        result.setTextColor(Color.parseColor("#FFFF8800"));
        int emoji = 0x23F0;
        String string = "Pendente " + getEmojiByUnicode(emoji);
        result.setText(string);

        balance.setText("Pendente");

        float totalOdd = 1;
        for(int i=1; i <= prices.size(); i++){
            totalOdd = totalOdd * Float.parseFloat(prices.get(i - 1));
        }

        date.setText(datef);
        type.setText("Aposta " + overallType);

        oddTotal = String.format(Locale.ENGLISH, "%.2f", totalOdd);
        odds.setText(oddTotal);

        Log.d("DEBUG", "Spent: " + spent);

        spentTV.setText(String.format(Locale.ENGLISH, "%.2f", Float.parseFloat(spent)));

        projected_win = String.format(Locale.ENGLISH, "%.2f", totalOdd * Float.parseFloat(spent));
        winnings.setText(projected_win);

        myDataset = gameCode.createEmpty2();

        mRecyclerView = games_view;

        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new MyAdapter(this, myDataset);
        mRecyclerView.setAdapter(mAdapter);

        for (int i = 0; i < codes.size(); i++) {
            prePreAdd2(this.findViewById(android.R.id.content));
        }

    }

    public void prePreAdd2(View v) {
        MyAdapter.preAdd(v);
    }

    public static void addLineToList(View v, int pos, Context context) {
        if (firstHasPassed == true) {
            myDataset.add(pos + 1, new gameCode("", "", ""));
            mAdapter.notifyItemInserted(pos + 1);

            Log.d("DEBUG", myDataset.toString());

            mRecyclerView.invalidate();
        }

        else {
            firstHasPassed = true;
        }
    }

    public static class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        static View mView;
        private static Context cont;
        gameCode gameCode;
        static int pos;

        public static class ViewHolder extends RecyclerView.ViewHolder {
            public EditText placardCode;
            public MaterialSpinner outcomeSpinner;
            public MaterialSpinner betTypeSpinner;

            public ViewHolder(View itemView) {
                super(itemView);
                mView = itemView;

                View view = (View) mView.findViewById(R.id.game_result);
                view.setBackgroundColor(Color.parseColor("#FFFF8800"));
            }
        }

        // Provide a suitable constructor (depends on the kind of dataset)
        public MyAdapter(Context context, List<gameCode> game_code) {
            cont = context;

        }

        // Create new views (invoked by the layout manager)
        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {

            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);

            // Inflate the custom layout
            View gamesView = inflater.inflate(R.layout.populate_this_detail, parent, false);

            // Return a new holder instance
            ViewHolder viewHolder = new ViewHolder(gamesView);
            return viewHolder;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(MyAdapter.ViewHolder viewHolder, int position) {
            pos = position;
            gameCode = myDataset.get(position);
            String outcome = "";

            Log.d("DEBUG", "Debugg: " + outcomes.toString());


            if(outcomes.get(pos).equals("1")){
                outcome = homeOpponents.get(pos);
            }
            else if(outcomes.get(pos).equals("x")) {
                outcome = "Empate";
            }
            else if(outcomes.get(pos).equals("2")){
                outcome = awayOpponents.get(pos);
            }
            else if(outcomes.get(pos).equals("+")){
                outcome = "Mais 2.5";
            }
            else if(outcomes.get(pos).equals("-")){
                outcome = "Menos 2.5";
            }

            String code = codes.get(pos);
            String type = types.get(pos);
            String home_opp = homeOpponents.get(pos);
            String away_opp = awayOpponents.get(pos);
            String odd = prices.get(pos);

            TextView field1 = (TextView) mView.findViewById(R.id.game_code);
            field1.setText(code);

            TextView field2 = (TextView) mView.findViewById(R.id.game_type);
            field2.setText(type);

            TextView field3 = (TextView) mView.findViewById(R.id.game_home);
            field3.setText(home_opp);

            TextView field4 = (TextView) mView.findViewById(R.id.game_away);
            field4.setText(away_opp);

            TextView field5 = (TextView) mView.findViewById(R.id.game_outcome);
            field5.setText(outcome);

            TextView field6 = (TextView) mView.findViewById(R.id.game_odd);
            field6.setText(odd);
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

    public void sendToDatabase() {
        betResult resultObj;
        betVotes votesObj = null;

        Bet bet;


        resultObj = new betResult("0", "2");
        bet = new Bet("0" + String.valueOf(Integer.parseInt(index[0]) + 1), formattedDate, spent, String.valueOf(homeOpponents.size()), projected_win, overallType, oddTotal, resultObj);


        mDatabase.child("Pending").child("0" + String.valueOf(Integer.parseInt(index[0]) + 1)).setValue(bet);


        for(int i = 1; i < homeOpponents.size() + 1; i++) {
            int ind = i - 1;
            String outcomeD = "";

            if(outcomes.get(ind).equals("1")){
                outcomeD = homeOpponents.get(ind);
            }
            else if(outcomes.get(ind).equals("x")) {
                outcomeD = "Empate";
            }
            else if(outcomes.get(ind).equals("2")){
                outcomeD = awayOpponents.get(ind);
            }
            else if(outcomes.get(ind).equals("+")){
                outcomeD = "Mais 2.5";
            }
            else if(outcomes.get(ind).equals("-")){
                outcomeD = "Menos 2.5";
            }

            //TODO: Change hardcoded "0"!!!
            Game game = new Game(codes.get(ind), homeOpponents.get(ind), awayOpponents.get(ind), sports.get(ind), "3", outcomeD, outcomes.get(ind), prices.get(ind), "0", types.get(ind));

                mDatabase.child("Pending").child("0" + String.valueOf(Integer.parseInt(index[0]) + 1)).child("games").child("game_0" + i).setValue(game);
                mDatabase.child("Statistics").child("number_ofBets").setValue(String.valueOf(Integer.parseInt(index[0]) + 1));
                mDatabase.child("Statistics").child("pendingBetsExist").setValue("1");
        }
        CharSequence text;

        myDataset.clear();

            mDatabase.child("Pending").child("0" + String.valueOf(Integer.parseInt(index[0]) + 1)).child("votes").setValue(votesObj);
            text = "A aposta foi adicionada à Base de Dados!";

        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "1");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "bet_added");

        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

    }

    public String translateBool(Boolean bool) {
        if(bool) {
            return "1";
        }
        else {
            return "0";
        }
    }

    public String getEmojiByUnicode(int unicode) {
        return new String(Character.toChars(unicode));
    }
}