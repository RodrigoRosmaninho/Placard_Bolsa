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
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
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

        card.addCardHeader(header);

        header.setButtonExpandVisible(false);
        header.setButtonOverflowVisible(false);

        final PieChart chart2 = (PieChart) rootView.findViewById(R.id.chart2);
        chart2.setUsePercentValues(true);
        chart2.getDescription().setEnabled(false);
        chart2.setExtraOffsets(5, 10, 5, 5);
        chart2.setDragDecelerationFrictionCoef(0.95f);
        chart2.setCenterText("Apostas");

        final LineChart chart = (LineChart) rootView.findViewById(R.id.chart);

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
        DatabaseReference pouchRef = statsRef.child("Dates");
        statsRef = statsRef.child("Statistics");

        final String[] ammounty = new String[1];

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Statistics stats_class = dataSnapshot.getValue(Statistics.class);

                betNumb[0] = stats_class.getNumber_ofBets();

                String ammount = stats_class.getOn_pouch();
                ammounty[0] = ammount;
                ammount = String.format(Locale.ENGLISH, "%.2f", Float.parseFloat(ammount)) + "€";

                String share = stats_class.getCurrent_share();

                //main_text3.setTextColor(Color.parseColor("#FF669900"));
                //main_text3.setText(ammount);

                //main_text7.setText(String.format(Locale.ENGLISH, "%.2f", Float.parseFloat(stats_class.getValue_spent())) + "€");

                //share_text42.setText(String.format(Locale.ENGLISH, "%.2f", Float.parseFloat(share)) + "€");

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

                    Dates pouchHistory = childSnapshot.getValue(Dates.class);

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

                    if (betNumb[0] != null && pass == Integer.parseInt(betNumb[0])) {
                        stupidFix = ammounty[0];
                    }
                    pass = pass + 1;

                    entries.add(new Entry(Float.parseFloat(days), Float.parseFloat(stupidFix)));

                }

                LineDataSet dataSet = new LineDataSet(entries, "Label");

                dataSet.setLineWidth(1.75f);
                dataSet.setHighlightEnabled(false);
                dataSet.setDrawValues(false);
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

        DatabaseReference wonLostRef = statsRef.child("general_stats");
        final ArrayList<PieEntry> entries2 = new ArrayList<PieEntry>();

        ValueEventListener postListener4 = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                generalStats stats_class = dataSnapshot.getValue(generalStats.class);

                String win = stats_class.getWin_number();
                String lost = stats_class.getLost_number();

                entries2.add(new PieEntry(Integer.parseInt(win), "Ganhas"));
                entries2.add(new PieEntry(Integer.parseInt(lost), "Perdidas"));

                PieDataSet pds = new PieDataSet(entries2, "");
                pds.setValueLinePart1OffsetPercentage(80.f);
                pds.setValueLinePart1Length(0.2f);
                pds.setValueLinePart2Length(0.4f);
                pds.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
                pds.setXValuePosition(null);

                ArrayList<Integer> colors = new ArrayList<Integer>(2);
                colors.add(Color.parseColor("#FF669900"));
                colors.add(Color.parseColor("#FFCC0000"));
                pds.setColors(colors);
                pds.setValueTextSize(5f);

                PieData data = new PieData(pds);
                data.setValueFormatter(new PercentFormatter());
                data.setValueTextSize(11f);
                data.setValueTextColor(Color.BLACK);
                chart2.setData(data);
                chart2.invalidate();
                chart2.animateX(1400);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        wonLostRef.addValueEventListener(postListener4);

        return rootView;
    }

}

