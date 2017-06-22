package com.example.oscar.teammanager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import com.example.oscar.teammanager.Utils.GlobalParams;
import java.util.Timer;
import java.util.TimerTask;

//clase splash de carga inicial
public class SplashActivity extends Activity {

    public static SharedPreferences sp;
    public static SharedPreferences.Editor editor;

    // Set the duration of the splash screen
    private static final long SPLASH_SCREEN_DELAY = 1500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        sp = getSharedPreferences("preferencias", Context.MODE_PRIVATE);
        editor = sp.edit();


        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                // Start the next activity

                //compruebo sesion si el usuario ya se ha logueado antes directamente paso a mainActivity, si no
                //paso a login activity
                if(sp.getBoolean("is_login", false) == false){
                    Intent intent = new Intent().setClass(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    Intent intent = new Intent().setClass(SplashActivity.this, MainActivity.class);
                    GlobalParams.correoUsuario = sp.getString("us_email", "");
                    GlobalParams.passUsu = sp.getString("us_pass", "");
                    startActivity(intent);
                    finish();
                }
            }
        };

        // Simulate a long loading repartirJugadores on application startup.
        Timer timer = new Timer();
        timer.schedule(task, SPLASH_SCREEN_DELAY);
    }

}
