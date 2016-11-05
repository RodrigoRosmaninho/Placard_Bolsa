package com.firebaseio.placardbolsa;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class StatsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stats_main);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        final TextView main_text3 = (TextView) findViewById(R.id.main_text3);
        final TextView share_text42 = (TextView) findViewById(R.id.share_text42);
        final TextView winLost_text3 = (TextView) findViewById(R.id.winLost_text3);
        final TextView winLost_text5 = (TextView) findViewById(R.id.winLost_text5);

        final LineChart chart = (LineChart) findViewById(R.id.chart);
        final String[] dataObjects;

        DatabaseReference statsRef = FirebaseDatabase.getInstance().getReference();
        statsRef = statsRef.child("Statistics");

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Statistics stats_class = dataSnapshot.getValue(Statistics.class);

                String ammount = stats_class.getOn_pouch();
                ammount = String.format(Locale.ENGLISH, "%.2f", Float.parseFloat(ammount)) + "€";

                String share = stats_class.getCurrent_share();

                String win = stats_class.getGeneral_stats().toString().split("\\{")[1].split("\\}")[0].split(", ")[0].split("=")[1];

                String lost = stats_class.getGeneral_stats().toString().split("\\{")[1].split("\\}")[0].split(", ")[2].split("=")[1];

                main_text3.setTextColor(Color.parseColor("#FF669900"));
                main_text3.setText(ammount);

                share_text42.setText(share + "€");

                winLost_text3.setTextColor(Color.parseColor("#FF669900"));
                winLost_text3.setText(win);

                winLost_text5.setTextColor(Color.parseColor("#FFCC0000"));
                winLost_text5.setText(lost);

                /**

                dataObjects = ;

                List<Entry> entries = new ArrayList<Entry>();
                int i;

                while(i < ) {

                    // turn your data into Entry objects
                    entries.add(new Entry(data.getValueX(), data.getValueY()));
                }**/

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        statsRef.addValueEventListener(postListener);
    }

}
