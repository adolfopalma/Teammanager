package com.example.oscar.teammanager;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.example.oscar.teammanager.Adaptadores.GestionListAdapter;
import com.example.oscar.teammanager.Objects.Jugadores;
import com.example.oscar.teammanager.Objects.Peñas;
import com.example.oscar.teammanager.Utils.ClaseConexion;
import com.example.oscar.teammanager.Utils.GlobalParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MultaActivity extends AppCompatActivity {

    protected CheckBox checkRetraso,checkAsistencia;
    protected Dialog dialog;
    protected Spinner spinner,spinner_jug;
    protected Button bDialogAcept,bDialogCancel;
    public static SharedPreferences sp;
    public static SharedPreferences.Editor editor;
    private JSONArray jSONArray;
    private ClaseConexion devuelveJSON;
    private JSONObject jsonObject;
    private String url_consulta;
    private String IP_Server;
    private Jugadores jugador;
    private Peñas peña;
    private ArrayList<Peñas> arrayPeñas;
    private ArrayList<Jugadores> arrayListJugadores;
    protected String correoUsuario;
    protected int idPeña;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multa);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        IP_Server = "http://iesayala.ddns.net/19ramajo";
        url_consulta = IP_Server + "/consulta.php";
        devuelveJSON = new ClaseConexion();
        arrayPeñas = new ArrayList<>();
        arrayListJugadores = new ArrayList<>();
        checkAsistencia = (CheckBox)findViewById(R.id.checkBox);
        checkRetraso    = (CheckBox)findViewById(R.id.checkBox2);
        spinner_jug = (Spinner) findViewById(R.id.spinner_jug);

        sp = getSharedPreferences("preferencias", Context.MODE_PRIVATE);
        editor = sp.edit();

        correoUsuario = sp.getString("us_email", correoUsuario);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        ConsultEquipTask task= new ConsultEquipTask();
        task.execute();
        dialog();

        checkAsistencia.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(checkAsistencia.isChecked()){
                    checkRetraso.setChecked(false);
                }

            }
        });

        checkRetraso.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(checkRetraso.isChecked()){
                    checkAsistencia.setChecked(false);
                }

            }
        });

    }

    public void dialog() {
        //Creacion dialog de filtrado
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_list_equipo);
        dialog.setCancelable(false);

        spinner = (Spinner) dialog.findViewById(R.id.spinner);
        bDialogAcept = (Button)dialog.findViewById(R.id.bAceptar);
        bDialogCancel = (Button)dialog.findViewById(R.id.bCancelar);

        //Accion de boton guardar filtrado
        dialog.findViewById(R.id.bAceptar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                idPeña = arrayPeñas.get(spinner.getSelectedItemPosition()).getId();
                ConsultJugTask task = new ConsultJugTask();
                task.execute();
                dialog.dismiss();
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



    public void rellenaEspinersPeña(){
        List<String> list = new ArrayList<String>();
        for (int i = 0; i <arrayPeñas.size() ; i++) {
            list.add(arrayPeñas.get(i).getNombre().toString());
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(MultaActivity.this, R.layout.spinner_plegado, list);
        dataAdapter.setDropDownViewResource(R.layout.spinner_desplegado);
        spinner.setAdapter(dataAdapter);
    }

    public void rellenaEspinersJug(){
        List<String> list = new ArrayList<String>();
        for (int i = 0; i <arrayListJugadores.size() ; i++) {
            list.add(arrayListJugadores.get(i).getNombre());
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(MultaActivity.this, R.layout.spinner_plegado, list);
        dataAdapter.setDropDownViewResource(R.layout.spinner_desplegado);
        spinner_jug.setAdapter(dataAdapter);
    }


    class ConsultEquipTask extends AsyncTask<String, String, JSONArray> {
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(MultaActivity.this);
            pDialog.setMessage("Cargando...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected JSONArray doInBackground(String... args) {
            try {

                HashMap<String, String> parametrosPosteriores = new HashMap<>();
                parametrosPosteriores.put("ins_sql","select * from peña where CodAministrador  = "+"'"+correoUsuario+"'");
                jSONArray = devuelveJSON.sendRequest(url_consulta, parametrosPosteriores);

                if (jSONArray.length() > 0) {
                    return jSONArray;
                }else{
                    System.out.println("Error al obtener datos JSON");
                    Snackbar.make(findViewById(android.R.id.content), "Error de conexion", Snackbar.LENGTH_LONG).show();
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
                        peña.setId(jsonObject.getInt("codPeña"));
                        arrayPeñas.add(peña);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                rellenaEspinersPeña();
            } else {

            }
        }

        @Override
        protected void onCancelled() {
            pDialog.dismiss();
        }
    }

    class ConsultJugTask extends AsyncTask<String, String, JSONArray> {
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(MultaActivity.this);
            pDialog.setMessage("Cargando...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected JSONArray doInBackground(String... args) {
            try {

                HashMap<String, String> parametrosPosteriores = new HashMap<>();
                parametrosPosteriores.put("ins_sql","select Nombre, Ruta_Foto, Correo from jugadores where Correo in (SELECT CodigoJug FROM componente_peña WHERE CodPeña = "+idPeña+")");
                jSONArray = devuelveJSON.sendRequest(url_consulta, parametrosPosteriores);

                if (jSONArray.length() > 0) {
                    return jSONArray;
                }else{
                    System.out.println("Error al obtener datos JSON");
                    Snackbar.make(findViewById(android.R.id.content), "Error de conexion", Snackbar.LENGTH_LONG).show();
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
                        jugador = new Jugadores();
                        jugador.setNombre(jsonObject.getString("Nombre"));
                        jugador.setRutaFoto(jsonObject.getString("Ruta_Foto"));
                        jugador.setCorreo(jsonObject.getString("Correo"));
                        arrayListJugadores.add(jugador);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                rellenaEspinersJug();
            } else {

            }
        }

        @Override
        protected void onCancelled() {
            pDialog.dismiss();
        }
    }


}
