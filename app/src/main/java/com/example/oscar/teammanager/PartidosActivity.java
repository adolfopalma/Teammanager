package com.example.oscar.teammanager;

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
import android.widget.ListView;

import com.example.oscar.teammanager.Adaptadores.Adapter_list_partidos;
import com.example.oscar.teammanager.Adaptadores.PeñaListAdapter;
import com.example.oscar.teammanager.Objects.Partidos;
import com.example.oscar.teammanager.Objects.Peñas;
import com.example.oscar.teammanager.Utils.ClaseConexion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class PartidosActivity extends AppCompatActivity {

    public static SharedPreferences sp;
    public static SharedPreferences.Editor editor;
    String correoUsuario;
    private JSONArray jSONArray;
    private ClaseConexion devuelveJSON;
    private JSONObject jsonObject;
    private String url_consulta;
    private String IP_Server;
    protected Partidos partido;
    private ArrayList<Partidos> arrayPartidos;
    ListView lvPartido;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partidos);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sp = getSharedPreferences("preferencias", Context.MODE_PRIVATE);
        editor = sp.edit();

        correoUsuario = sp.getString("us_email", correoUsuario);

        IP_Server = "http://iesayala.ddns.net/19ramajo";
        url_consulta = IP_Server + "/consulta.php";
        devuelveJSON = new ClaseConexion();
        arrayPartidos = new ArrayList<>();

        lvPartido = (ListView) findViewById(R.id.lvPartidos);

        PartidosTask task = new PartidosTask();
        task.execute();

    }

    class PartidosTask extends AsyncTask<String, String, JSONArray> {
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(PartidosActivity.this);
            pDialog.setMessage("Cargando...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONArray doInBackground(String... args) {
            try {

                HashMap<String, String> parametrosPosteriores = new HashMap<>();
                parametrosPosteriores.put("ins_sql","select p.*, pe.nomPeña from partido p, peña pe where p.codPeña in (SELECT codPeña FROM componente_peña WHERE CodigoJug = "+"'"+correoUsuario+"') and p.nomPeña = pe.nomPeña");
                jSONArray = devuelveJSON.sendRequest(url_consulta, parametrosPosteriores);

                if (jSONArray.length() > 0) {
                    return jSONArray;
                }else{
                    System.out.println("Error al obtener datos JSON");
                    //Snackbar.make(findViewById(android.R.id.content), "Error de conexion ", Snackbar.LENGTH_LONG).show();
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
                        partido = new Partidos();
                        partido.setNomPeña(jsonObject.getString("nomPeña"));
                        partido.setFechaPartido(jsonObject.getString("fechaPartido"));
                        partido.setResultado(jsonObject.getString("resultado"));
                        partido.setGanador(jsonObject.getString("ganador"));
                        arrayPartidos.add(partido);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }


                lvPartido.setAdapter(new Adapter_list_partidos(PartidosActivity.this, arrayPartidos));

            } else {

            }
        }

        @Override
        protected void onCancelled() {
            pDialog.dismiss();
        }
    }

}
