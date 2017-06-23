package com.example.oscar.teammanager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.oscar.teammanager.Objects.Jugadores;
import com.example.oscar.teammanager.Utils.ClaseConexion;
import com.example.oscar.teammanager.Utils.GlobalParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ContactoActivity extends AppCompatActivity {

    protected ArrayList<Jugadores> arrayJugadores;
    protected EditText edMotivo;
    protected CheckBox checkSugerencia,checkError;
    protected String tipo= "";
    protected Bundle extras;
    protected String nombre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacto);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        edMotivo = (EditText)findViewById(R.id.etMotivo);
        checkError    = (CheckBox)findViewById(R.id.checkBox2);
        checkSugerencia    = (CheckBox)findViewById(R.id.checkBox);
        extras = getIntent().getExtras();
        nombre = extras.getString("nombreUsuario");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = edMotivo.getText().toString()+"\n Usuario:"+nombre;

                String[] TO = {"soporteteammanager@gmail.com"}; //Direcciones email  a enviar.
                String[] CC = {""}; //Direcciones email con copia.

                Intent emailIntent = new Intent(Intent.ACTION_SEND);

                emailIntent.setData(Uri.parse("mailto:"));
                emailIntent.setType("text/plain");
                emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
                emailIntent.putExtra(Intent.EXTRA_CC, CC);
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, tipo);
                emailIntent.putExtra(Intent.EXTRA_TEXT, text);

                try {
                    startActivity(Intent.createChooser(emailIntent, "Enviar email."));
                    Log.i("EMAIL", "Enviando email...");
                }
                catch (android.content.ActivityNotFoundException e) {
                    Toast.makeText(ContactoActivity.this, "NO existe ningún cliente de email instalado!.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        checkError.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(checkError.isChecked()){
                    checkSugerencia.setChecked(false);
                    tipo = "Error";
                }

            }
        });

        checkSugerencia.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(checkSugerencia.isChecked()){
                    checkError.setChecked(false);
                    tipo = "Sugerencia";
                }

            }
        });
    }

}
