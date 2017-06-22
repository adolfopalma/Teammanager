package com.example.oscar.teammanager;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import com.example.oscar.teammanager.Adaptadores.Adapter_gestion_multa;
import com.example.oscar.teammanager.Objects.Multas;
import com.example.oscar.teammanager.Objects.Peñas;
import com.example.oscar.teammanager.Utils.ClaseConexion;
import com.example.oscar.teammanager.Utils.GlobalParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GestionMulta extends AppCompatActivity {

    protected Dialog dialog;
    protected Spinner spinner;
    protected Button bDialogAcept;
    protected ListView lv;
    protected int idPeña;
    protected String nomPeña,correoUsuario;
    private JSONArray jSONArray;
    private ClaseConexion devuelveJSON;
    private JSONObject jsonObject;
    private Multas multa;
    private ArrayList<Multas> arrayListaMultas;
    protected ArrayList<Peñas> arrayPeñas;
    private Peñas peña;
    public static SharedPreferences sp;
    public static SharedPreferences.Editor editor;
    protected TextView tvInfo, tvInfoDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_multa);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        devuelveJSON = new ClaseConexion();
        arrayPeñas = new ArrayList<>();
        arrayListaMultas = new ArrayList<>();
        lv = (ListView)findViewById(R.id.lv);
        tvInfo = (TextView)findViewById(R.id.tvInfo);

        sp = getSharedPreferences("preferencias",Context.MODE_PRIVATE);
        editor = sp.edit();

        correoUsuario = sp.getString("us_email", correoUsuario);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        ConsultEquipTask task= new ConsultEquipTask();
        task.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_gestion_multa, menu);

        MenuItem item = menu.findItem(R.id.spinner);
        spinner = (Spinner) MenuItemCompat.getActionView(item);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int postion, long arg3) {
                idPeña = arrayPeñas.get(spinner.getSelectedItemPosition()).getId();
                GlobalParams.codPeña = idPeña;
                nomPeña = arrayPeñas.get(spinner.getSelectedItemPosition()).getNombre();
                ConsulTask task = new ConsulTask();
                task.execute();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_nueva) {
            Intent i = new Intent(GestionMulta.this, MultaActivity.class);
            startActivity(i);
            return true;
        }

        /*if (id == R.id.action_reset) {
            AlertDialog.Builder builderc = new AlertDialog.Builder(GestionMulta.this);
            builderc.setMessage("Esta seguro que desea resetear las multas, los valores se restauraran a 0");
            builderc.setPositiveButton(getResources().getString(R.string.acept),
                    new DialogInterface.OnClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                        public void onClick(DialogInterface dialog, int which) {
                            ResetTask task = new ResetTask();
                            task.execute();
                        }
                    });
            builderc.setNegativeButton(getResources().getString(R.string.cancel), null);
            builderc.create();
            builderc.show();
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }



    public void rellenaEspinersPeña(){
        List<String> list = new ArrayList<String>();
        for (int i = 0; i <arrayPeñas.size() ; i++) {
            list.add(arrayPeñas.get(i).getNombre().toString());
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(GestionMulta.this, R.layout.spinner_plegado, list);
        dataAdapter.setDropDownViewResource(R.layout.spinner_desplegado);
        spinner.setAdapter(dataAdapter);
    }

    class ConsultEquipTask extends AsyncTask<String, String, JSONArray> {
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(GestionMulta.this);
            pDialog.setMessage(getResources().getString(R.string.load));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected JSONArray doInBackground(String... args) {
            try {

                HashMap<String, String> parametrosPosteriores = new HashMap<>();
                parametrosPosteriores.put("ins_sql","select * from peña where CodAministrador  = "+"'"+correoUsuario+"'");
                jSONArray = devuelveJSON.sendRequest(GlobalParams.url_consulta, parametrosPosteriores);

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
                        peña.setId(jsonObject.getInt("codPeña"));
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

    class ConsulTask extends AsyncTask<String, String, JSONArray> {
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(GestionMulta.this);
            pDialog.setMessage(getResources().getString(R.string.load));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected JSONArray doInBackground(String... args) {
            try {

                HashMap<String, String> parametrosPosteriores = new HashMap<>();
                parametrosPosteriores.put("ins_sql", "select m.*, j.Nombre from multas m, jugadores j where m.codPeña = " + idPeña + " and m.CodigoJug = j.Correo and m.Estado = 'No pagado'");
                jSONArray = devuelveJSON.sendRequest(GlobalParams.url_consulta, parametrosPosteriores);

                if (jSONArray.length() > 0) {
                    return jSONArray;
                } else {
                    System.out.println("Error al obtener datos JSON");
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
                        multa = new Multas();
                        multa.setCodMulta(jsonObject.getInt("CodMulta"));
                        multa.setCodigoJug(jsonObject.getString("CodigoJug"));
                        multa.setNomJug(jsonObject.getString("Nombre"));
                        multa.setCantidad(jsonObject.getInt("Cantidad"));
                        multa.setEstado(jsonObject.getString("Estado"));
                        multa.setTipoMulta(jsonObject.getString("TipoMulta"));
                        arrayListaMultas.add(multa);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                tvInfo.setVisibility(View.GONE);
                lv.setVisibility(View.VISIBLE);
                lv.setAdapter(new Adapter_gestion_multa(GestionMulta.this, arrayListaMultas));
            }else{
                tvInfo.setVisibility(View.VISIBLE);
                lv.setVisibility(View.GONE);
            }
        }

        @Override
        protected void onCancelled() {
            pDialog.dismiss();
        }
    }


}
