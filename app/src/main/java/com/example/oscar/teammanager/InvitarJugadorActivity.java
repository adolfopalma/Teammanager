package com.example.oscar.teammanager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.oscar.teammanager.Objects.Peñas;
import com.example.oscar.teammanager.Utils.ClaseConexion;
import com.example.oscar.teammanager.Utils.GlobalParams;
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
    private Peñas peña;
    private ArrayList<Peñas> arrayPeñas;
    private ArrayList<String> arrayCorreos;
    protected Spinner spinner;
    protected Button benviar,bAdd,bDialogAcept,bDialogCancel;
    protected String user,passwd;
    protected TextView correoInvitado;
    protected String correoUsuario;
    protected EditText correo;
    protected Dialog dialog;
    protected int cont=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invitar_jugador);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final AlertDialog.Builder builders = new AlertDialog.Builder(InvitarJugadorActivity.this);
        builders.setMessage("Seleccione el equipo del que se va enviar una invitacion");
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
        arrayPeñas = new ArrayList<>();
        arrayCorreos = new ArrayList<String>();
        devuelveJSON = new ClaseConexion();
        benviar = (Button)findViewById(R.id.bEnviar);
        bAdd = (Button)findViewById(R.id.bAddInvit);
        user = "soporteteammanager@gmail.com";
        passwd = "TM100517";
        correoInvitado = (TextView) findViewById(R.id.correoInvitado);

        sp = getSharedPreferences("preferencias", Context.MODE_PRIVATE);
        editor = sp.edit();

        correoUsuario = sp.getString("us_email", correoUsuario);

        JugadoresTask task = new JugadoresTask();
        task.execute();

        benviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cont =0;
                if(correoInvitado.getText().toString().isEmpty()){
                    Snackbar.make(findViewById(android.R.id.content), "Inserte al menos un correo valido", Snackbar.LENGTH_LONG).show();
                }else {
                    enviar();
                }
            }
        });

        bAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogAdd();
            }
        });

    }

    public void dialogAdd() {
        //Creacion dialog de filtrado
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_correo_invitacion);
        dialog.setCancelable(false);

        correo = (EditText)dialog.findViewById(R.id.correoAdd);
        bDialogAcept = (Button)dialog.findViewById(R.id.bGuardar);
        bDialogCancel = (Button)dialog.findViewById(R.id.bCancelarFiltro);

        //Accion de boton guardar filtrado
        dialog.findViewById(R.id.bGuardar).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if(arrayCorreos.size() <= 0) {
                    arrayCorreos.add(correo.getText().toString());
                }else{
                    arrayCorreos.add(","+correo.getText().toString());
                }

                dialog.dismiss();

                correoInvitado.setText(correoInvitado.getText()+arrayCorreos.get(cont));
                cont++;

            }
        });

        dialog.findViewById(R.id.bCancelar).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
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
                jSONArray = devuelveJSON.sendRequest(GlobalParams.url_consulta, parametrosPosteriores);

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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        cont = 0;
        finish();
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

}
