package com.firebaseio.placardbolsa;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntro2Fragment;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.google.firebase.analytics.FirebaseAnalytics;

import static com.firebaseio.placardbolsa.Fragment1.MyPREFERENCES;
import static com.firebaseio.placardbolsa.R.drawable.intro_1;
import static com.firebaseio.placardbolsa.R.drawable.intro_2;
import static com.firebaseio.placardbolsa.R.drawable.intro_3;
import static com.firebaseio.placardbolsa.R.drawable.intro_4;
import static com.firebaseio.placardbolsa.R.drawable.pb_lois;
import static com.firebaseio.placardbolsa.R.drawable.pb_melo;
import static com.firebaseio.placardbolsa.R.drawable.pb_nuno;
import static com.firebaseio.placardbolsa.R.drawable.pb_quico;
import static com.firebaseio.placardbolsa.R.drawable.pb_salgado;
import static com.firebaseio.placardbolsa.R.drawable.web_hi_res_512;

public class IntroActivity extends AppIntro2 {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int backgroundColor = Color.parseColor("#F44336");
        int image = web_hi_res_512;

        String deviceModel = android.os.Build.MODEL;
        String deviceMan = android.os.Build.MANUFACTURER;

        Log.d("DEBUG", "deviceModel = " + deviceModel);
        Log.d("DEBUG", "deviceMan = " + deviceMan);

        String device = "Dispositivo";
        String name = "Utilizador";
        int ID = 1;

        if (deviceMan.toUpperCase().equals("LGE")) {
            device = "LG G3";
            image = pb_lois;
            name = "Lóis";
            ID = 3;
        }

        else if (deviceMan.toUpperCase().equals("SONY")) {
            device = "Sony Xperia M4 Aqua";
            image = pb_melo;
            name = "Melo";
            ID = 4;
        }

        else if (deviceMan.toUpperCase().equals("SAMSUNG")) {
            if (deviceModel.equals("SM-G900F")) {
                device = "Samsung Galaxy S5";
                image = pb_quico;
                name = "Quico";
                ID = 7;
            }
            else if (deviceModel.equals("SM-G800F")){
                device = "Samsung Galaxy S5 Mini";
                image = pb_nuno;
                name = "Nuno";
                ID = 1;
            }
        }

        else if (deviceMan.toUpperCase().equals("ONEPLUS")) {
            device = "OnePlus 3T";
            image = pb_quico;
            name = "Quico";
            ID = 7;
        }

        else if (deviceMan.equals("unknown")) {
            device = "Android Emulator";
            image = pb_quico;
            name = "Quico";
            ID = 7;
        }

        else {
            device = "Xiaomi";
            image = pb_salgado;
            name = "Salgado";
            ID = 5;
        }

        SharedPreferences sp = this.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sp.edit();

        editor.putString("userName", name);
        editor.putInt("UserID", ID);
        editor.putString("lastViewedNote", "0");
        editor.apply();

        // Obtain the FirebaseAnalytics instance.
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        mFirebaseAnalytics.setUserProperty("Name", name);

        addSlide(AppIntroFragment.newInstance("Bem-Vindo à App Placard Bolsa!", "(Também conhecida por DALFNA)\nDesenvolvida por Rodrigo Rosmaninho", web_hi_res_512, backgroundColor));
        addSlide(AppIntro2Fragment.newInstance("Lista de Apostas", "Com esta app tens acesso ao historial completo de apostas, sincronizado entre todos os dispositivos!", intro_1, backgroundColor));
        addSlide(AppIntro2Fragment.newInstance("Vista Detalhada dos Eventos", "Podes também conferir todos os detalhes sobre cada uma das apostas passadas e os seus eventos!", intro_2, backgroundColor));
        addSlide(AppIntro2Fragment.newInstance("Estatísticas Automáticas", "Completa com gráfico detalhado do dinheiro na bolsa ao longo do tempo, a vista de Estatísticas permite acompanhar a evolução da bolsa..", intro_3, backgroundColor));
        addSlide(AppIntro2Fragment.newInstance("Adição de Novas Apostas", "Apenas precisas dos códigos do eventos e a aposta é adicionada e sincronizada entre todos os dispositivos!", intro_4, backgroundColor));
        addSlide(AppIntroFragment.newInstance("Utilizador", device + " detetado. Bem-Vindo, " + name, image, backgroundColor));

        showSkipButton(false);
        setProgressButtonEnabled(true);
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        SharedPreferences sp = this.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sp.edit();

        editor.putBoolean("isFirstTime", false);
        editor.apply();

        Intent intent = new Intent(IntroActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
