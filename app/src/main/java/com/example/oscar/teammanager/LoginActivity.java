package com.example.oscar.teammanager;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import com.example.oscar.teammanager.Objects.Jugadores;
import com.example.oscar.teammanager.Utils.ClaseConexion;
import com.example.oscar.teammanager.Utils.GlobalParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;

public class LoginActivity extends AppCompatActivity{

    //Declaro variables
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private JSONArray jSONArray;
    private ClaseConexion devuelveJSON;
    private JSONObject jsonObject;
    private Jugadores jugadores;
    private ArrayList<Jugadores> arrayJugadores;
    public static AlertDialog.Builder alert;
    public static SharedPreferences sp;
    public static SharedPreferences.Editor editor;
    protected String correo;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Inicializo variables
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);
        devuelveJSON = new ClaseConexion();
        arrayJugadores = new ArrayList<>();
        alert = new AlertDialog.Builder(this);
        sp = getSharedPreferences("preferencias", Context.MODE_PRIVATE);
        editor = sp.edit();
        mPasswordView = (EditText) findViewById(R.id.password);

    }


    //Metodo con el que compruebo la conexion, si tengo ejecuto asynctask de logueo
    public void IniciarSesion(View view) {
        if(mEmailView.getText().toString().isEmpty() || mPasswordView.getText().toString().isEmpty()){
            Snackbar.make(findViewById(android.R.id.content), "Rellene todos los campos.", Snackbar.LENGTH_LONG).show();
        }else {
            if (!ClaseConexion.compruebaConexion(this)) {
                Snackbar.make(findViewById(android.R.id.content), "Conexion limitada o nula, comprueba tu conexion.", Snackbar.LENGTH_LONG).show();
            } else {
                //Guardo el email del usuario en preferencias que utilizo en toda aplicacion de clave primaria
                UserLoginTask task = new UserLoginTask(mEmailView.getText().toString(), mPasswordView.getText().toString());
                task.execute();
            }
        }
    }

    //Compruebo datos introducidos, si corresponden con los de la base de datos, dejo entrar
    public void compruebaUsuario(String correo, String pass){
        jugadores = new Jugadores();
        for (int i=0; i<arrayJugadores.size(); i++){
            if (arrayJugadores.get(i).getCorreo().toString().equals(correo.toString()) & arrayJugadores.get(i).getPass().toString().equals(pass.toString())) {
                Intent intent = new Intent(this, MainActivity.class);
                correo = mEmailView.getText().toString();
                GlobalParams.correoUsuario = correo;
                editor.putString("us_email", correo);
                editor.commit();
                startActivity(intent);
            }else{
                Snackbar.make(findViewById(android.R.id.content), "Correo o contraseña erroneos ", Snackbar.LENGTH_LONG).show();;
            }
        }
    }


    //Metodo para inicializar asyntask
    public void registro(View v){
        Intent intent = new Intent(this, RegistroActivity.class);
        startActivity(intent);
    }



    //Asynctask para consultar datos
    class UserLoginTask extends AsyncTask<String, String, JSONArray> {
        private ProgressDialog pDialog;
        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(LoginActivity.this);
            pDialog.setMessage("Comprobando...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected JSONArray doInBackground(String... args) {
            try {
                HashMap<String, String> parametrosPost = new HashMap<>();
                parametrosPost.put("ins_sql","select * from jugadores");
                jSONArray = devuelveJSON.sendRequest(GlobalParams.url_consulta, parametrosPost);
                if (jSONArray.length() > 0) {
                    return jSONArray;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(JSONArray json) {
            if (pDialog != null && pDialog.isShowing()) {
                pDialog.dismiss();
            }
            if (json != null) {
                for (int i = 0; i < json.length(); i++) {
                    try {
                        jsonObject = json.getJSONObject(i);
                        jugadores = new Jugadores();
                        jugadores.setNombre(jsonObject.getString("Nombre"));
                        jugadores.setEdad(jsonObject.getString("Edad"));
                        jugadores.setTipoJug(jsonObject.getString("TipoJugador"));
                        jugadores.setCorreo(jsonObject.getString("Correo"));
                        jugadores.setPass(jsonObject.getString("Pass"));
                        arrayJugadores.add(jugadores);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                compruebaUsuario(mEmail.toString(), mPassword.toString());
            }else{
                Snackbar.make(findViewById(android.R.id.content), "Problema de conexion con el servidor.", Snackbar.LENGTH_LONG).show();

            }

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            finishAffinity();
        }
    }
}

