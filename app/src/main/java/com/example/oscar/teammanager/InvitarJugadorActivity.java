package com.example.oscar.teammanager;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.oscar.teammanager.Objects.Peñas;
import com.example.oscar.teammanager.Utils.ClaseConexion;
import com.example.oscar.teammanager.Utils.MailJob;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InvitarJugadorActivity extends AppCompatActivity {

    public static SharedPreferences sp;
    public static SharedPreferences.Editor editor;
    private JSONArray jSONArray;
    private ClaseConexion devuelveJSON;
    private JSONObject jsonObject;
    private String url_consulta;
    private String IP_Server;
    private Peñas peña;
    private ArrayList<Peñas> arrayPeñas;
    protected Spinner spinner;
    protected Button benviar;
    protected String user,passwd;
    protected EditText correoInvitado;
    protected String correoUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invitar_jugador);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final AlertDialog.Builder builders = new AlertDialog.Builder(InvitarJugadorActivity.this);
        builders.setMessage("Seleccione una peña para registar un nuevo jugador");
        builders.setPositiveButton(getResources().getString(R.string.acept),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        builders.setCancelable(true);
                    }
                });
        builders.setCancelable(false);
        builders.create();
        builders.show();

        spinner		= (Spinner) findViewById(R.id.spinner_peñas);
        IP_Server = "http://iesayala.ddns.net/19ramajo";
        url_consulta = IP_Server + "/consulta.php";
        arrayPeñas = new ArrayList<>();
        devuelveJSON = new ClaseConexion();
        benviar = (Button)findViewById(R.id.bEnviar);
        user = "soporteteammanager@gmail.com";
        passwd = "TM100517";
        correoInvitado = (EditText) findViewById(R.id.correoInvitado);

        sp = getSharedPreferences("preferencias", Context.MODE_PRIVATE);
        editor = sp.edit();

        correoUsuario = sp.getString("us_email", correoUsuario);

        JugadoresTask task = new JugadoresTask();
        task.execute();

        benviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(correoInvitado.getText().toString().isEmpty()){
                    Snackbar.make(findViewById(android.R.id.content), "Inserte al menos un correo valido", Snackbar.LENGTH_LONG).show();
                }else {
                    enviar();
                }
            }
        });

    }

    //relleno los datos de los spinner de el filtro
    public void rellenaEspiners(){
        List<String> list = new ArrayList<String>();
        for (int i = 0; i <arrayPeñas.size() ; i++) {
            list.add(arrayPeñas.get(i).getNombre().toString());
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(InvitarJugadorActivity.this, R.layout.spinner_plegado, list);
        dataAdapter.setDropDownViewResource(R.layout.spinner_desplegado);
        spinner.setAdapter(dataAdapter);
    }

    public void enviar(){
        String peña = spinner.getSelectedItem().toString();
        new MailJob(user, passwd).execute(
                new MailJob.Mail("soporteteammanager@gmail.com", correoInvitado.getText().toString(), "Invitacion Team Manager", "El admninistrador de la peña " + peña + " le ha invitado a unirse al equipo.\n Descargue aqui la aplicacion Team Manager")
        );

        final AlertDialog.Builder builders = new AlertDialog.Builder(InvitarJugadorActivity.this);
        builders.setMessage("El email ha sido enviado");
        builders.setPositiveButton(getResources().getString(R.string.acept),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        builders.setCancelable(true);
                    }
                });
        builders.setCancelable(false);
        builders.create();
        builders.show();

        spinner.setSelection(0);
        correoInvitado.setText("");

    }


    class JugadoresTask extends AsyncTask<String, String, JSONArray> {
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(InvitarJugadorActivity.this);
            pDialog.setMessage("Cargando...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONArray doInBackground(String... args) {
            try {

                HashMap<String, String> parametrosPosteriores = new HashMap<>();
                parametrosPosteriores.put("ins_sql","select * from peña where CodAministrador = "+"'"+correoUsuario+"'");
                jSONArray = devuelveJSON.sendRequest(url_consulta, parametrosPosteriores);

                if (jSONArray.length() > 0) {
                    return jSONArray;
                }else{
                    System.out.println("Error al obtener datos JSON");
                    Snackbar.make(findViewById(android.R.id.content), "Usuario o contraseña incorrectos  ", Snackbar.LENGTH_LONG).show();
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
                        peña = new Peñas();
                        peña.setNombre(jsonObject.getString("nomPeña"));
                        arrayPeñas.add(peña);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            } else {
                Snackbar.make(findViewById(android.R.id.content), "Error de conexion", Snackbar.LENGTH_LONG).show();
            }
            rellenaEspiners();
        }

        @Override
        protected void onCancelled() {
            pDialog.dismiss();
        }
    }


}
