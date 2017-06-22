package com.example.oscar.teammanager;

import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
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

//clase que gestiona si eres administrador y poder cambiarlo
public class GestionAdministrador extends AppCompatActivity {

    protected Spinner spinner,spinnerJug;
    protected List<String> list;
    private JSONArray jSONArray;
    private ClaseConexion devuelveJSON;
    private JSONObject jsonObject, jsonObject2;
    protected ArrayList<Peñas> arrayPeñas;
    private Peñas peña;
    protected Jugadores jugador;
    protected ArrayList<Jugadores> arrayListaJugadores;
    protected ArrayAdapter<String> dataAdapter;
    protected Button cambiar;
    protected LinearLayout ln;
    protected TextView tvInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_administrador);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        spinner = (Spinner)findViewById(R.id.spinner);
        spinnerJug = (Spinner)findViewById(R.id.spinnerJug);
        devuelveJSON = new ClaseConexion();
        arrayPeñas = new ArrayList<>();
        arrayListaJugadores = new ArrayList<>();
        cambiar = (Button)findViewById(R.id.bCambiar);
        ln = (LinearLayout)findViewById(R.id.lnInfo);
        tvInfo = (TextView)findViewById(R.id.tvInfo);

        final ConsultEquipTask task = new ConsultEquipTask();
        task.execute();

        cambiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.app.AlertDialog.Builder builderc = new android.app.AlertDialog.Builder(GestionAdministrador.this);
                builderc.setMessage(getResources().getString(R.string.administrador_no));
                builderc.setPositiveButton(getResources().getString(R.string.acept),
                        new DialogInterface.OnClickListener() {
                            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                            public void onClick(DialogInterface dialog, int which) {
                                UpdateTask task1 = new UpdateTask();
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

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int postion, long arg3) {
                arrayListaJugadores.clear();
                ConsultCompTask task = new ConsultCompTask();
                task.execute();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });

        return true;
    }

    public void rellenaEspinersPeña(){
        list = new ArrayList<>();
        for (int i = 0; i <arrayPeñas.size() ; i++) {
            list.add(arrayPeñas.get(i).getNombre().toString());
        }


        dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
    }

    public void rellenaSpinnerJug(){
        List<String> list = new ArrayList<String>();
        for (int i = 0; i <arrayListaJugadores.size() ; i++) {
            list.add(arrayListaJugadores.get(i).getNombre().toString());
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(GestionAdministrador.this, R.layout.spinner_plegado, list);
        dataAdapter.setDropDownViewResource(R.layout.spinner_desplegado);
        spinnerJug.setAdapter(dataAdapter);
    }





    class ConsultEquipTask extends AsyncTask<String, String, JSONArray> {
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(GestionAdministrador.this);
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
            }else {
                ln.setVisibility(View.GONE);
                tvInfo.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected void onCancelled () {
            pDialog.dismiss();
        }
    }

    class ConsultCompTask extends AsyncTask<String, String, JSONArray> {
        private ProgressDialog pDialog;
        int idPeña = arrayPeñas.get(spinner.getSelectedItemPosition()).getId();

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(GestionAdministrador.this);
            pDialog.setMessage(getResources().getString(R.string.load));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected JSONArray doInBackground(String... args) {
            try {

                HashMap<String, String> parametrosPosteriores = new HashMap<>();
                parametrosPosteriores.put("ins_sql", "select * from jugadores where Correo in (SELECT CodigoJug FROM componente_peña WHERE CodPeña = "+idPeña+")");
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
                        jugador = new Jugadores();
                        jugador.setNombre(jsonObject.getString("Nombre"));
                        jugador.setRutaFoto(jsonObject.getString("Ruta_Foto"));
                        jugador.setCorreo(jsonObject.getString("Correo"));
                        jugador.setTipoJug(jsonObject.getString("TipoJugador"));
                        arrayListaJugadores.add(jugador);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                rellenaSpinnerJug();
            }

        }

        @Override
        protected void onCancelled () {
            pDialog.dismiss();
        }
    }

    class UpdateTask extends AsyncTask<String, String, JSONArray> {
        private ProgressDialog pDialog;
        int idPeña = arrayPeñas.get(spinner.getSelectedItemPosition()).getId();
        String correoAdmin = arrayListaJugadores.get(spinnerJug.getSelectedItemPosition()).getCorreo();

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(GestionAdministrador.this);
            pDialog.setMessage(getResources().getString(R.string.act));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected JSONArray doInBackground(String... args) {
            try {

                HashMap<String, String> parametrosPosteriores = new HashMap<>();
                parametrosPosteriores.put("ins_sql"," UPDATE peña SET CodAministrador = "+"'"+correoAdmin+"' WHERE codPeña = "+idPeña);
                jsonObject = devuelveJSON.sendInsert(GlobalParams.url_insert, parametrosPosteriores);

                HashMap<String, String> parametrosPosteriores1 = new HashMap<>();
                parametrosPosteriores1.put("ins_sql"," UPDATE administrador SET CodAdministrador = "+"'"+correoAdmin+"' WHERE CodPeña = "+idPeña);
                jsonObject2 = devuelveJSON.sendInsert(GlobalParams.url_insert, parametrosPosteriores1);



            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            if(jsonObject != null && jsonObject2 != null) {
                try {
                    switch (jsonObject.getInt("added")){
                        case 1:
                            AlertDialog.Builder builders = new AlertDialog.Builder(GestionAdministrador.this);
                            builders.setMessage(getResources().getString(R.string.datos_act));
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
                            Snackbar.make(findViewById(android.R.id.content), "Error actualizando datos", Snackbar.LENGTH_LONG).show();
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
