package com.example.oscar.teammanager;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.oscar.teammanager.Objects.Peñas;
import com.example.oscar.teammanager.Utils.ClaseConexion;
import com.example.oscar.teammanager.Utils.GlobalParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GestionEstadisticas extends AppCompatActivity {

    protected Dialog dialog;
    protected Spinner spinner;
    protected Button bDialogAcept;
    protected TextView tvInfoDialog;
    private JSONArray jSONArray;
    private ClaseConexion devuelveJSON;
    private JSONObject jsonObject;
    protected ArrayList<Peñas> arrayPeñas;
    private Peñas peña;
    protected String correoUsuario;
    public static SharedPreferences sp;
    public static SharedPreferences.Editor editor;
    protected List<String> list;
    protected ImageButton button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_estadisticas);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        devuelveJSON = new ClaseConexion();
        arrayPeñas = new ArrayList<>();
        button = (ImageButton)findViewById(R.id.ibReset);

        sp = getSharedPreferences("preferencias", Context.MODE_PRIVATE);
        editor = sp.edit();

        correoUsuario = sp.getString("us_email", correoUsuario);

        final ConsultEquipTask task = new ConsultEquipTask();
        task.execute();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.app.AlertDialog.Builder builderc = new android.app.AlertDialog.Builder(GestionEstadisticas.this);
                builderc.setMessage(getResources().getString(R.string.reset_estad));
                builderc.setPositiveButton(getResources().getString(R.string.acept),
                        new DialogInterface.OnClickListener() {
                            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                            public void onClick(DialogInterface dialog, int which) {
                                ResetTask task1 = new ResetTask();
                                task1.execute();
                            }
                        });
                builderc.setNegativeButton(getResources().getString(R.string.cancel), null);
                builderc.create();
                builderc.show();

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_spinner, menu);

        MenuItem item = menu.findItem(R.id.spinner);
        spinner = (Spinner) MenuItemCompat.getActionView(item);


        return true;
    }

    public void rellenaEspinersPeña(){
        list = new ArrayList<>();
        for (int i = 0; i <arrayPeñas.size() ; i++) {
            list.add(arrayPeñas.get(i).getNombre().toString());
        }


        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);

    }




    class ConsultEquipTask extends AsyncTask<String, String, JSONArray> {
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(GestionEstadisticas.this);
            pDialog.setMessage(getResources().getString(R.string.load));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected JSONArray doInBackground(String... args) {
            try {

                HashMap<String, String> parametrosPosteriores = new HashMap<>();
                parametrosPosteriores.put("ins_sql", "select * from peña where CodAministrador  = " + "'" + GlobalParams.correoUsuario + "'");
                jSONArray = devuelveJSON.sendRequest(GlobalParams.url_consulta, parametrosPosteriores);

                if (jSONArray.length() > 0) {
                    return jSONArray;
                } else {
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
                        peña.setId(jsonObject.getInt("codPeña"));
                        peña.setNombre(jsonObject.getString("nomPeña"));
                        peña.setId(jsonObject.getInt("codPeña"));
                        arrayPeñas.add(peña);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                rellenaEspinersPeña();
            }
        }

            @Override
            protected void onCancelled () {
                pDialog.dismiss();
            }
        }

        class ResetTask extends AsyncTask<String, String, JSONArray> {
            private ProgressDialog pDialog;
            int idPeña = arrayPeñas.get(spinner.getSelectedItemPosition()).getId();

            @Override
            protected void onPreExecute() {
                pDialog = new ProgressDialog(GestionEstadisticas.this);
                pDialog.setMessage(getResources().getString(R.string.reset));
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(false);
                pDialog.show();
            }

        @Override
        protected JSONArray doInBackground(String... args) {
            try {

                HashMap<String, String> parametrosPosteriores = new HashMap<>();
                parametrosPosteriores.put("ins_sql"," UPDATE estadisticas SET Goles = 0, TarjetaAmarilla = 0, TarjetaRoja = 0 , PartidosJugados = 0, PartidosGanados = 0, PartidosPerdidos = 0, PartidosEmpatados = 0, Puntos = 0 WHERE codPeña = "+idPeña);
                jsonObject = devuelveJSON.sendInsert(GlobalParams.url_insert, parametrosPosteriores);

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            if(jsonObject != null) {
                try {
                    switch (jsonObject.getInt("added")){
                        case 1:
                            AlertDialog.Builder builders = new AlertDialog.Builder(GestionEstadisticas.this);
                            builders.setMessage(getResources().getString(R.string.datos_res));
                            builders.setPositiveButton(getResources().getString(R.string.acept),
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    });
                            builders.setCancelable(false);
                            builders.create();
                            builders.show();
                            break;
                        default:
                            Snackbar.make(findViewById(android.R.id.content), "Error reseteando datos", Snackbar.LENGTH_LONG).show();
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

