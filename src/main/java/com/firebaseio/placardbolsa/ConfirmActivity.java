package com.firebaseio.placardbolsa;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
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

public class ConfirmActivity  extends AppCompatActivity {
    static boolean firstHasPassed = false;
    private static RecyclerView mRecyclerView;
    private static RecyclerView.Adapter mAdapter;
    private static RecyclerView.LayoutManager mLayoutManager;
    public static List<gameCode> myDataset;

    static String mode = "";
    static ArrayList<String> markets= new ArrayList<String>();
    static ArrayList<String> homeOpponents= new ArrayList<String>();
    static ArrayList<String> awayOpponents= new ArrayList<String>();
    static ArrayList<String> prices= new ArrayList<String>();
    static ArrayList<String> codes= new ArrayList<String>();
    static ArrayList<String> types= new ArrayList<String>();
    static ArrayList<String> outcomes= new ArrayList<String>();
    static ArrayList<String> sports = new ArrayList<String>();
    static String[] index = new String[1];
    static String spent = "";
    static String overallType = "";

    String projected_win;
    String oddTotal;

    String datef = "";

    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirm_main);

        Bundle b = getIntent().getExtras();

        if (b != null) {
            //TODO: Make snackbar "Critical Error" on else

            mode = b.getString("mode");
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

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);

        toolbar.setTitle("Confirmar Adição");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        final TextView date = (TextView) findViewById(R.id.bet_textDate);
        final TextView type = (TextView) findViewById(R.id.bet_textType);

        final TextView result = (TextView) findViewById(R.id.bet_textResult);

        final TextView odds = (TextView) findViewById(R.id.oddsTextView);
        final TextView spentTV = (TextView) findViewById(R.id.spentTextView);
        final TextView winnings = (TextView) findViewById(R.id.winningsTextView);
        final TextView balance = (TextView) findViewById(R.id.balanceTextView);

        final ImageView nuno = (ImageView) findViewById(R.id.imageView2);
        final ImageView chico = (ImageView) findViewById(R.id.imageView3);
        final ImageView lois = (ImageView) findViewById(R.id.imageView4);
        final ImageView melo = (ImageView) findViewById(R.id.imageView5);
        final ImageView salgado = (ImageView) findViewById(R.id.imageView6);

        final CheckBox nunoV = (CheckBox) findViewById(R.id.votes_nunoCheck);
        final CheckBox chicoV = (CheckBox) findViewById(R.id.votes_chicoCheck);
        final CheckBox loisV = (CheckBox) findViewById(R.id.votes_lóisCheck);
        final CheckBox meloV = (CheckBox) findViewById(R.id.votes_meloCheck);
        final CheckBox salgadoV = (CheckBox) findViewById(R.id.votes_salgadoCheck);

        FloatingActionButton confirmFAB = (FloatingActionButton) findViewById(R.id.confirm_fab);
        confirmFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validationChecks(nunoV, chicoV, loisV, meloV, salgadoV, view);
            }
        });

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss_dd-MM-yyyy");
        String formattedDate = df.format(c.getTime());
        datef = formattedDate.split("_")[1];

        CardRecyclerView games_view = (CardRecyclerView) findViewById(R.id.games_view2);

        Card card = new Card(this);
        CardHeader header = new CardHeader(this);
        CardViewNative resultCard = (CardViewNative) findViewById(R.id.bet_resultCard);
        CardViewNative mainCard = (CardViewNative) findViewById(R.id.bet_mainCard);
        CardViewNative gamesCard = (CardViewNative) findViewById(R.id.bet_gamesCard);
        CardViewNative votesCard = (CardViewNative) findViewById(R.id.stats_votesCard);
        card.addCardHeader(header);
        header.setButtonExpandVisible(false);
        header.setButtonOverflowVisible(false);
        mainCard.setCard(card);
        header.setTitle("Resultado");
        resultCard.setCard(card);
        header.setTitle("Jogos");
        gamesCard.setCard(card);
        header.setTitle("Votos");
        votesCard.setCard(card);

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
            prePreAdd(this.findViewById(android.R.id.content));
        }

    }

    public void prePreAdd(View v) {
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
        private List<gameCode> myDataset;
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

            Log.d("DEBUG", "Debugg: " + homeOpponents.toString());

            if(outcomes.get(pos).equals("1")){
                outcome = homeOpponents.get(pos);
            }
            else if(outcomes.get(pos).equals("x")) {
                outcome = "Empate";
            }
            else if(outcomes.get(pos).equals("2")){
                outcome = awayOpponents.get(pos);
            }

            String code = codes.get(pos);
            String type = types.get(pos);
            String home_opp = homeOpponents.get(pos);
            String away_opp = awayOpponents.get(pos);
            String odd = prices.get(pos);

            Log.d("DEBUG", "Strings: " + code + ", " + type + ", " + home_opp + ", " + away_opp + ", " + odd + ", " + outcome);

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

    public void validationChecks(CheckBox nunoV, CheckBox chicoV, CheckBox loisV, CheckBox meloV, CheckBox salgadoV, View v) {
        int numberYes = 0;

        boolean[] votes = new boolean[5];
        votes[0] = nunoV.isChecked();
        votes[1] = chicoV.isChecked();
        votes[2] = loisV.isChecked();
        votes[3] = meloV.isChecked();
        votes[4] = salgadoV.isChecked();

        for(int i = 0; i < 5; i++) {
            if (votes[i]) {
                numberYes = numberYes + 1;
            }
        }

        if(numberYes == 4 | numberYes == 5) {
            sendToDatabase(votes);
        }
        else {
            Snackbar.make(v, "Apenas pode haver 1 voto contra a aposta!", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    }

    public void sendToDatabase(boolean[] votes) {
        betResult resultObj = new betResult("0", "2");
        betVotes votesObj = new betVotes(translateBool(votes[0]), translateBool(votes[1]), translateBool(votes[2]), translateBool(votes[3]), translateBool(votes[4]));
        Bet bet = new Bet("0" + index[0], datef, spent, String.valueOf(homeOpponents.size()), projected_win, overallType, oddTotal, resultObj, votesObj);

        mDatabase.child("Pending").child("0" + index[0]).setValue(bet);

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

            gameOutcome gOut = new gameOutcome("3", "2");
            gameType gType = new gameType(types.get(ind), "0");
            desiredOutcome outcome = new desiredOutcome(outcomeD, outcomes.get(ind), prices.get(ind), gType);
            Game game = new Game(codes.get(ind), homeOpponents.get(ind), awayOpponents.get(ind), outcome, sports.get(ind), gOut);

            mDatabase.child("Pending").child("0" + index[0]).child("games").child("game_0" + i).setValue(game);

            Context context = getApplicationContext();
            CharSequence text = "A aposta foi adicionada à Base de Dados!";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
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