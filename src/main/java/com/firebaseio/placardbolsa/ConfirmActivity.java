package com.firebaseio.placardbolsa;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

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
    static String spent = "";
    static String overallType = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirm_main);

        Bundle b = getIntent().getExtras();

        FloatingActionButton bet_fab = (FloatingActionButton) findViewById(R.id.bet_fab);

        if (b != null) {
            //TODO: Make snackbar "Critical Error"

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

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss_dd-MM-yyyy");
        String formattedDate = df.format(c.getTime());
        final String datef = formattedDate.split("_")[1];

        CardRecyclerView games_view = (CardRecyclerView) findViewById(R.id.games_view2);

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

        balance.setTextColor(Color.parseColor("#FFFF8800"));
        result.setTextColor(Color.parseColor("#FFFF8800"));
        int emoji = 0x23F0;
        String string = "Pendente " + getEmojiByUnicode(emoji);
        result.setText(string);

        balance.setText("Pendente");

        date.setText(datef);
        type.setText("Aposta " + overallType);
        odds.setText("");
        spentTV.setText(spent);
        winnings.setText("");

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
        if (true) {
            myDataset.add(pos + 1, new gameCode("", "", ""));
            mAdapter.notifyItemInserted(pos + 1);

            Log.d("DEBUG", myDataset.toString());

            mRecyclerView.invalidate();
        }

        else {
            Snackbar.make(v, "Ja tens 8 jogos listados!", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
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

            if(outcomes.get(0).equals("1")){
                outcome = homeOpponents.get(0);
            }
            else if(outcomes.get(pos).equals("x")) {
                outcome = "Empate";
            }
            else if(outcomes.get(pos).equals("2")){
                outcome = awayOpponents.get(pos);
            }

            String code = codes.get(0);
            String type = types.get(0);
            String home_opp = homeOpponents.get(0);
            String away_opp = awayOpponents.get(0);
            String odd = prices.get(0);

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

    public String getEmojiByUnicode(int unicode) {
        return new String(Character.toChars(unicode));
    }
}