package com.firebaseio.placardbolsa;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.view.CardViewNative;

public class Fragment2 extends Fragment {
    String[] betNumb = new String[1];

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.stats_main, container, false);

        Card card = new Card(getContext());

        //Create a CardHeader
        CardHeader header = new CardHeader(getContext());

        CardViewNative geralView = (CardViewNative) rootView.findViewById(R.id.stats_mainCard);
        CardViewNative winLostView = (CardViewNative) rootView.findViewById(R.id.stats_winLostCard);
        CardViewNative shareView = (CardViewNative) rootView.findViewById(R.id.stats_shareCard);
        CardViewNative chartView = (CardViewNative) rootView.findViewById(R.id.stats_chartCard);
        CardViewNative votesView = (CardViewNative) rootView.findViewById(R.id.stats_votesCard);

        card.addCardHeader(header);

        header.setButtonExpandVisible(false);
        header.setButtonOverflowVisible(false);

        header.setTitle("Geral");
        geralView.setCard(card);
        header.setTitle("Apostas");
        winLostView.setCard(card);
        header.setTitle("Divisão");
        shareView.setCard(card);
        header.setTitle("Na Bolsa");
        chartView.setCard(card);
        header.setTitle("Votos");
        votesView.setCard(card);

        final TextView main_text3 = (TextView) rootView.findViewById(R.id.main_text3);
        final TextView main_text7 = (TextView) rootView.findViewById(R.id.main_text7);
        final TextView share_text42 = (TextView) rootView.findViewById(R.id.share_text42);
        final TextView winLost_text3 = (TextView) rootView.findViewById(R.id.winLost_text3);
        final TextView winLost_text5 = (TextView) rootView.findViewById(R.id.winLost_text5);

        final TextView votes_nunoText = (TextView) rootView.findViewById(R.id.votes_nunoText);
        final TextView votes_chicoText = (TextView) rootView.findViewById(R.id.votes_chicoText);
        final TextView votes_lóisText = (TextView) rootView.findViewById(R.id.votes_lóisText);
        final TextView votes_meloText = (TextView) rootView.findViewById(R.id.votes_meloText);
        final TextView votes_salgadoText = (TextView) rootView.findViewById(R.id.votes_salgadoText);
        final TextView votes_lameiroText = (TextView) rootView.findViewById(R.id.votes_lameiroText);

        final LineChart chart = (LineChart) rootView.findViewById(R.id.chart);
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

        final String[] ammounty = new String[1];

        DatabaseReference pouchRef = statsRef.child("pouch_history");

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Statistics stats_class = dataSnapshot.getValue(Statistics.class);

                betNumb[0] = stats_class.getNumber_ofBets();

                String ammount = stats_class.getOn_pouch();
                ammounty[0] = ammount;
                ammount = String.format(Locale.ENGLISH, "%.2f", Float.parseFloat(ammount)) + "€";

                String share = stats_class.getCurrent_share();

                main_text3.setTextColor(Color.parseColor("#FF669900"));
                main_text3.setText(ammount);

                main_text7.setText(String.format(Locale.ENGLISH, "%.2f", Float.parseFloat(stats_class.getValue_spent())) + "€");

                share_text42.setText(String.format(Locale.ENGLISH, "%.2f", Float.parseFloat(share)) + "€");

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
                int pass = 0;

                for (DataSnapshot childSnapshot: dataSnapshot.getChildren()) {
                    pouchHistory pouchHistory = childSnapshot.getValue(pouchHistory.class);

                    SimpleDateFormat myFormat = new SimpleDateFormat("dd-MM-yyyy");
                    String inputString1 = "01-10-2016";
                    String inputString2 = pouchHistory.getEvent_date();

                    String days = "0";

                    try {
                        Date date1 = myFormat.parse(inputString1);
                        Date date2 = myFormat.parse(inputString2);
                        long diff = date2.getTime() - date1.getTime();
                        days = String.valueOf(TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    String stupidFix = pouchHistory.getPouch_content();

                    if (pass == Integer.parseInt(betNumb[0])) {
                        stupidFix = ammounty[0];
                    }
                    pass = pass + 1;

                    entries.add(new Entry(Float.parseFloat(days), Float.parseFloat(stupidFix)));

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

        DatabaseReference votesRef = statsRef.child("vote_stats");

        ValueEventListener postListener3 = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                voteStats stats_class = dataSnapshot.getValue(voteStats.class);

                String nuno = stats_class.getU01();
                String chico = stats_class.getU02();
                String lóis = stats_class.getU03();
                String melo = stats_class.getU04();
                String salgado = stats_class.getU05();
                String lameiro = stats_class.getU06();

                float lameiroBodge = Float.parseFloat(betNumb[0]) - 21;

                if (lameiroBodge == 0) {
                    lameiro = "0%";
                }
                else {
                    lameiro = String.format(Locale.ENGLISH, "%.0f", (Float.parseFloat(lameiro) / lameiroBodge) * 100 ) + "%";
                }

                nuno = String.format(Locale.ENGLISH, "%.0f", (Float.parseFloat(nuno) / Float.parseFloat(betNumb[0])) * 100 ) + "%";
                chico = String.format(Locale.ENGLISH, "%.0f", (Float.parseFloat(chico) / Float.parseFloat(betNumb[0])) * 100 ) + "%";
                lóis = String.format(Locale.ENGLISH, "%.0f", (Float.parseFloat(lóis) / Float.parseFloat(betNumb[0])) * 100 ) + "%";
                melo = String.format(Locale.ENGLISH, "%.0f", (Float.parseFloat(melo) / Float.parseFloat(betNumb[0])) * 100 ) + "%";
                salgado = String.format(Locale.ENGLISH, "%.0f", (Float.parseFloat(salgado) / Float.parseFloat(betNumb[0])) * 100 ) + "%";

                votes_nunoText.setText(nuno);
                votes_chicoText.setText(chico);
                votes_lóisText.setText(lóis);
                votes_meloText.setText(melo);
                votes_salgadoText.setText(salgado);
                votes_lameiroText.setText(lameiro);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        votesRef.addValueEventListener(postListener3);

        DatabaseReference wonLostRef = statsRef.child("general_stats");

        ValueEventListener postListener4 = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                generalStats stats_class = dataSnapshot.getValue(generalStats.class);

                String win = stats_class.getWin_number();
                String lost = stats_class.getLost_number();

                winLost_text3.setTextColor(Color.parseColor("#FF669900"));
                winLost_text3.setText(win);

                winLost_text5.setTextColor(Color.parseColor("#FFCC0000"));
                winLost_text5.setText(lost);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        wonLostRef.addValueEventListener(postListener4);

        return rootView;
    }

}

