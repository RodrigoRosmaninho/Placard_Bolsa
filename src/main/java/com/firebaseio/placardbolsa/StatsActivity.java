package com.firebaseio.placardbolsa;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.view.CardViewNative;


public class StatsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stats_main);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        Card card = new Card(this);

        //Create a CardHeader
        CardHeader header = new CardHeader(this);

        CardViewNative cardView = (CardViewNative) this.findViewById(R.id.stats_mainCard);

        card.addCardHeader(header);

        header.setButtonExpandVisible(false);
        header.setButtonOverflowVisible(false);
        header.setTitle("Geral");

        cardView.setCard(card);

        final TextView main_text3 = (TextView) findViewById(R.id.main_text3);
        final TextView main_text7 = (TextView) findViewById(R.id.main_text7);
        final TextView share_text42 = (TextView) findViewById(R.id.share_text42);
        final TextView winLost_text3 = (TextView) findViewById(R.id.winLost_text3);
        final TextView winLost_text5 = (TextView) findViewById(R.id.winLost_text5);

        final TextView votes_nunoText = (TextView) findViewById(R.id.votes_nunoText);
        final TextView votes_chicoText = (TextView) findViewById(R.id.votes_chicoText);
        final TextView votes_lóisText = (TextView) findViewById(R.id.votes_lóisText);
        final TextView votes_meloText = (TextView) findViewById(R.id.votes_meloText);
        final TextView votes_salgadoText = (TextView) findViewById(R.id.votes_salgadoText);

        final LineChart chart = (LineChart) findViewById(R.id.chart);
        final String[] dataObjects;

        // chart.setAutoScaleMinMaxEnabled(true);
        chart.setBackgroundColor(getResources().getColor(R.color.buttonTextColor));
        chart.setDrawGridBackground(false);
        chart.setDrawBorders(false);


        chart.setTouchEnabled(true);
        // chart.setDragEnabled(false);
        // chart.setScaleEnabled(false);

        chart.getDescription().setEnabled(false);

        YAxis yyAxis1 = chart.getAxisLeft();
        YAxis yyAxis2 = chart.getAxisRight();
        XAxis xxAxis = chart.getXAxis();

        yyAxis1.setDrawAxisLine(false);

        Legend legend = chart.getLegend();
        legend.setEnabled(false);

        yyAxis1.setTextSize(10f);

        yyAxis1.setEnabled(true);
        yyAxis2.setEnabled(false);
        xxAxis.setEnabled(false);


        DatabaseReference statsRef = FirebaseDatabase.getInstance().getReference();
        statsRef = statsRef.child("Statistics");

        DatabaseReference pouchRef = statsRef.child("pouch_history");

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Statistics stats_class = dataSnapshot.getValue(Statistics.class);

                String ammount = stats_class.getOn_pouch();
                ammount = String.format(Locale.ENGLISH, "%.2f", Float.parseFloat(ammount)) + "€";

                String share = stats_class.getCurrent_share();

                String nuno = stats_class.getVote_stats().toString().split("\\{")[1].split("\\}")[0].split(", ")[1].split("=")[1];
                String chico = stats_class.getVote_stats().toString().split("\\{")[1].split("\\}")[0].split(", ")[4].split("=")[1];
                String lóis = stats_class.getVote_stats().toString().split("\\{")[1].split("\\}")[0].split(", ")[2].split("=")[1];
                String melo = stats_class.getVote_stats().toString().split("\\{")[1].split("\\}")[0].split(", ")[3].split("=")[1];
                String salgado = stats_class.getVote_stats().toString().split("\\{")[1].split("\\}")[0].split(", ")[0].split("=")[1];

                nuno = String.format(Locale.ENGLISH, "%.0f", (Float.parseFloat(nuno) / Float.parseFloat(stats_class.getNumber_ofBets())) * 100 ) + "%";
                chico = String.format(Locale.ENGLISH, "%.0f", (Float.parseFloat(chico) / Float.parseFloat(stats_class.getNumber_ofBets())) * 100 ) + "%";
                lóis = String.format(Locale.ENGLISH, "%.0f", (Float.parseFloat(lóis) / Float.parseFloat(stats_class.getNumber_ofBets())) * 100 ) + "%";
                melo = String.format(Locale.ENGLISH, "%.0f", (Float.parseFloat(melo) / Float.parseFloat(stats_class.getNumber_ofBets())) * 100 ) + "%";
                salgado = String.format(Locale.ENGLISH, "%.0f", (Float.parseFloat(salgado) / Float.parseFloat(stats_class.getNumber_ofBets())) * 100 ) + "%";

                String win = stats_class.getGeneral_stats().toString().split("\\{")[1].split("\\}")[0].split(", ")[0].split("=")[1];

                String lost = stats_class.getGeneral_stats().toString().split("\\{")[1].split("\\}")[0].split(", ")[2].split("=")[1];

                main_text3.setTextColor(Color.parseColor("#FF669900"));
                main_text3.setText(ammount);

                main_text7.setText(String.format(Locale.ENGLISH, "%.2f", Float.parseFloat(stats_class.getValue_spent())) + "€");

                share_text42.setText(String.format(Locale.ENGLISH, "%.2f", Float.parseFloat(share)) + "€");

                winLost_text3.setTextColor(Color.parseColor("#FF669900"));
                winLost_text3.setText(win);

                winLost_text5.setTextColor(Color.parseColor("#FFCC0000"));
                winLost_text5.setText(lost);

                votes_nunoText.setText(nuno);
                votes_chicoText.setText(chico);
                votes_lóisText.setText(lóis);
                votes_meloText.setText(melo);
                votes_salgadoText.setText(salgado);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        statsRef.addValueEventListener(postListener);

        final List<Entry> entries = new ArrayList<Entry>();

        ValueEventListener postListener2 = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapshot: dataSnapshot.getChildren()) {
                    pouchHistory pouchHistory = childSnapshot.getValue(pouchHistory.class);

                    SimpleDateFormat myFormat = new SimpleDateFormat("dd-MM-yyyy");
                    String inputString1 = "01-10-2016";
                    String inputString2 = pouchHistory.getEvent_date();
                    Log.d("DEBUG", "date: " + inputString2 + ", content: " + pouchHistory.getPouch_content());

                    String days = "0";

                    try {
                        Date date1 = myFormat.parse(inputString1);
                        Date date2 = myFormat.parse(inputString2);
                        long diff = date2.getTime() - date1.getTime();
                        days = String.valueOf(TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    Log.d("DEBUG", "entry: " + Float.parseFloat(days) + ",, " + days + ",, " + Float.parseFloat(pouchHistory.getPouch_content()));
                    entries.add(new Entry(Float.parseFloat(days), Float.parseFloat(pouchHistory.getPouch_content())));
                    Log.d("DEBUG", "list: " + entries.toString());

                }

                LineDataSet dataSet = new LineDataSet(entries, "Label");

                dataSet.setLineWidth(1.75f);
                dataSet.setHighlightEnabled(false);
                dataSet.setDrawValues(true);
                dataSet.setDrawFilled(true);

                LineData lineData = new LineData(dataSet);
                chart.setData(lineData);
                chart.invalidate();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        pouchRef.addValueEventListener(postListener2);

    }

}
