package com.example.oscar.teammanager;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
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

/**
 * Created by oscar on 28/05/2017.
 */

//Clase de la activity administracion
public class AdministrarActivity extends AppCompatActivity {

    protected Toolbar tb;
    protected LinearLayout multas,invitaciones,equipos,estadisticas,cuenta,partidos;
    private JSONArray jSONArray;
    private ClaseConexion devuelveJSON;
    private JSONObject jsonObject;
    private Peñas peña;
    protected ArrayList<Peñas> arrayPeñas;
    protected Dialog dialog;
    protected Spinner spinner;
    protected Button bAcept;
    protected int idPeña;
    protected TextView tvInfoDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administrar);
        tb = (Toolbar)findViewById(R.id.toolbar);

        tb.setTitle("Panel de administracion");
        multas = (LinearLayout)findViewById(R.id.ibMultas);
        equipos = (LinearLayout)findViewById(R.id.ibEquipos);
        invitaciones = (LinearLayout)findViewById(R.id.ibInvitacion);
        estadisticas = (LinearLayout)findViewById(R.id.ibEstadisticas);
        cuenta = (LinearLayout)findViewById(R.id.ibCuenta);
        partidos = (LinearLayout)findViewById(R.id.ibPartidos);
        arrayPeñas = new ArrayList<>();
        devuelveJSON = new ClaseConexion();

        multas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AdministrarActivity.this, GestionMulta.class);
                startActivity(i);
            }
        });

        equipos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //ejecuto task para consultar los equipos que administro
                ConsultEquipTask task = new ConsultEquipTask();
                task.execute();
            }
        });

        invitaciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AdministrarActivity.this, InvitarJugadorActivity.class);
                startActivity(i);
            }
        });

        estadisticas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AdministrarActivity.this, GestionEstadisticas.class);
                startActivity(i);
            }
        });

        cuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AdministrarActivity.this, GestionCuenta.class);
                startActivity(i);
            }
        });


        partidos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AdministrarActivity.this, GestionPartidos.class);
                startActivity(i);
            }
        });


    }

    //Metodo que crea un cuadro de dialogo donde selecciono equipo del que soy administrador
    public void dialogAdd() {

        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_list_equipo);
        dialog.setCancelable(true);

        spinner = (Spinner)dialog.findViewById(R.id.spinner);
        bAcept = (Button)dialog.findViewById(R.id.bAceptar);
        tvInfoDialog = (TextView)dialog.findViewById(R.id.tvInfoDialog);
        tvInfoDialog.setText(R.string.seleccion_equipo);



        dialog.findViewById(R.id.bAceptar).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                idPeña = arrayPeñas.get(spinner.getSelectedItemPosition()).getId();
                Intent intent = new Intent(AdministrarActivity.this, GestionEquipo.class);
                intent.putExtra("id",idPeña);
                startActivity(intent);
            }
        });

        dialog.show();
    }

    //relleno los datos de el spinner
    public void rellenaEspiners(){
        List<String> list = new ArrayList<String>();
        for (int i = 0; i <arrayPeñas.size() ; i++) {
            list.add(arrayPeñas.get(i).getNombre().toString());
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(AdministrarActivity.this, R.layout.spinner_plegado, list);
        dataAdapter.setDropDownViewResource(R.layout.spinner_desplegado);
        spinner.setAdapter(dataAdapter);
    }


    //task que consulta los equipos que administro
    class ConsultEquipTask extends AsyncTask<String, String, JSONArray> {
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(AdministrarActivity.this);
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

                dialogAdd();
                rellenaEspiners();
            }

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

}
