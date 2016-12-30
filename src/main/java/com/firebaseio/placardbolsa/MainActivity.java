package com.firebaseio.placardbolsa;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
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

import static com.firebaseio.placardbolsa.Fragment1.MyPREFERENCES;

// Rodrigo Rosmaninho - 2016

public class MainActivity extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Apostas"));
        tabLayout.addTab(tabLayout.newTab().setText("Estatísticas"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final com.firebaseio.placardbolsa.PagerAdapter adapter = new com.firebaseio.placardbolsa.PagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
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

     if (id == R.id.bug_report) {
         Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://airtable.com/shrF9li4uv8gR4i0z"));
         startActivity(browserIntent);
     }

     if (id == R.id.open_notes) {
         final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Notes");
         SharedPreferences sp = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
         final int uID = sp.getInt("UserID", 01);

         final MaterialDialog dialog = new MaterialDialog.Builder(this)
                 .title("Editar Notas")
                 .customView(R.layout.dialog_notes, true)
                 .positiveText("Confirmar")
                 .negativeText("Cancelar")
                 .onPositive(new MaterialDialog.SingleButtonCallback() {
                     @Override
                     public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                         EditText ET = (EditText) dialog.getCustomView().findViewById(R.id.editText);
                         TextView date = (TextView) dialog.getCustomView().findViewById(R.id.textView9);
                         TextView author = (TextView) dialog.getCustomView().findViewById(R.id.textView8);

                         ref.child("text").setValue(ET.getText().toString());

                         Calendar c = Calendar.getInstance();
                         SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss_dd-MM-yyyy");
                         String formattedDate = df.format(c.getTime());

                         ref.child("last_edited").setValue(formattedDate);

                         String user;

                         switch (uID) {
                             case 1:
                                 user = "Nuno";
                                 break;

                             case 2:
                                 user = "Chico";
                                 break;

                             case 3:
                                 user = "Lóis";
                                 break;

                             case 4:
                                 user = "Melo";
                                 break;

                             case 5:
                                 user = "Salgado";
                                 break;

                             case 6:
                                 user = "Lameiro";
                                 break;

                             default:
                                 user = "Quico";
                                 break;
                         }

                         ref.child("last_author").setValue(user);

                         Snackbar.make(findViewById(android.R.id.content), "Nota alterada com sucesso!", Snackbar.LENGTH_LONG)
                                 .setAction("Action", null).show();

                     }
                 })
                 .build();

         final EditText ET = (EditText) dialog.getCustomView().findViewById(R.id.editText);
         final TextView date = (TextView) dialog.getCustomView().findViewById(R.id.textView9);
         final TextView author = (TextView) dialog.getCustomView().findViewById(R.id.textView8);

         ValueEventListener postListener = new ValueEventListener() {
             @Override
             public void onDataChange(DataSnapshot dataSnapshot) {
                 Notes notes_class = dataSnapshot.getValue(Notes.class);

                 ET.setText(notes_class.getText());

                 String str = notes_class.getLast_edited().split("_")[0];
                 String string1 = "Última Edição a:  " + notes_class.getLast_edited().split("_")[1] + ", às " + str.substring(0, str.length()-3);

                 date.setText(string1);

                 String string2 = "Por:  " + notes_class.getLast_author();

                 author.setText(string2);

             }

             @Override
             public void onCancelled(DatabaseError databaseError) {

             }
         };
         ref.addListenerForSingleValueEvent(postListener);

         dialog.show();
     }

     if (id == R.id.placard_link) {
     Intent launchIntent = getPackageManager().getLaunchIntentForPackage("pt.scml.placard");
     if (launchIntent != null) {
     startActivity(launchIntent);//null pointer check in case package name was not found
     } else {
     Snackbar.make(findViewById(android.R.id.content), "A app Placard não está instalada!", Snackbar.LENGTH_LONG)
     .setAction("Action", null).show();
     }
     }

     return super.onOptionsItemSelected(item);
     }
}





