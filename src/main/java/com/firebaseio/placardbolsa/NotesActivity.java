package com.firebaseio.placardbolsa;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import static com.firebaseio.placardbolsa.Fragment1.MyPREFERENCES;

public class NotesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        final EditText ET = (EditText) findViewById(R.id.editText);
        final TextView date = (TextView) findViewById(R.id.data);
        final TextView author = (TextView) findViewById(R.id.last_modifier);
        final ImageView user_photo2 = (ImageView) findViewById(R.id.user_photo2);

        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Notes");
        final SharedPreferences sp = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        final int uID = sp.getInt("UserID", 01);

        final SharedPreferences.Editor editor = sp.edit();

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Notes notes_class = dataSnapshot.getValue(Notes.class);

                ET.setText(notes_class.getText());

                String str = notes_class.getLast_edited().split("_")[0];
                String string1 = notes_class.getLast_edited().split("_")[1] + ", às " + str.substring(0, str.length()-3);

                date.setText(string1);

                String string2 = notes_class.getLast_author().toString();

                author.setText(string2);

                user_photo2.setImageResource(getUserPhoto(string2));

                editor.putString("lastViewedNote", notes_class.getNote_id());
                editor.apply();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        ref.addListenerForSingleValueEvent(postListener);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ref.child("text").setValue(ET.getText().toString());

                editor.putString("lastViewedNote", String.valueOf(Integer.parseInt(sp.getString("lastViewedNote", "100")) + 1));
                editor.apply();

                ref.child("note_id").setValue(sp.getString("lastViewedNote", "100"));

                Calendar c = Calendar.getInstance();
                SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss_dd-MM-yyyy");
                String formattedDate = df.format(c.getTime());

                ref.child("last_edited").setValue(formattedDate);

                ref.child("last_author").setValue(Fragment1.getUserName(uID));

                Snackbar.make(findViewById(android.R.id.content), "Nota alterada com sucesso!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                Intent intent = new Intent(NotesActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    static int getUserPhoto(String name) {
        int id;

        switch (name) {
            case "Nuno":
                id = R.drawable.pb_nuno;
                break;

            case "Chico":
                id = R.drawable.pb_chico;
                break;

            case "Lóis":
                id = R.drawable.pb_lois;
                break;

            case "Melo":
                id = R.drawable.pb_melo;
                break;

            case "Salgado":
                id = R.drawable.pb_salgado;
                break;

            case "Lameiro":
                id = R.drawable.pb_lameiro;
                break;

            default:
                id = R.drawable.pb_quico;
                break;
        }

        return id;
    }

}
