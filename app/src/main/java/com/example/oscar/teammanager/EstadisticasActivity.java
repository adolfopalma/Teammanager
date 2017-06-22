package com.example.oscar.teammanager;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.example.oscar.teammanager.Adaptadores.EstadisticasAdapter;
import com.example.oscar.teammanager.Objects.Peñas;
import com.example.oscar.teammanager.Utils.ClaseConexion;
import com.example.oscar.teammanager.Utils.GlobalParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EstadisticasActivity extends AppCompatActivity {

    protected Toolbar tbt;
    protected ViewPager vp;
    protected TabLayout tabs;
    protected int tab_activa;
    protected Bundle bundle;
    protected Dialog dialog;
    protected Spinner spinner;
    protected Button bDialogAcept;
    protected int idPeña;
    protected String nomPeña, correoUsuario;
    public static SharedPreferences sp;
    public static SharedPreferences.Editor editor;
    private JSONArray jSONArray;
    private ClaseConexion devuelveJSON;
    private JSONObject jsonObject;
    private Peñas peña;
    private ArrayList<Peñas> arrayPeñas;
    protected TextView tvTitulo, tvInfoDialog;
    protected LinearLayout ln;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estadisticas);

        //añadimos toolbar
        tbt = (Toolbar) findViewById(R.id.toolbartabs);

        if(tbt != null){
            tbt.setTitle("Estadisticas");
            //tbt.setSubtitleTextColor(getResources().getColor(R.color.Gainsboro));
        }

        setSupportActionBar(tbt);
        //  getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sp = getSharedPreferences("preferencias", Context.MODE_PRIVATE);
        editor = sp.edit();

        correoUsuario = sp.getString("us_email", correoUsuario);

        devuelveJSON = new ClaseConexion();
        arrayPeñas = new ArrayList<>();

        ConsultEquipTask task= new ConsultEquipTask();
        task.execute();

        vp = (ViewPager) findViewById(R.id.viewpager);


        //añadimos tabs
        tabs = (TabLayout) findViewById(R.id.tabs);
        tabs.setupWithViewPager(vp);
        tabs.setBackgroundColor(getResources().getColor(R.color.DimGray));
        tabs.setSelectedTabIndicatorColor(getResources().getColor(R.color.DeepSkyBlue));
        tabs.setSelectedTabIndicatorHeight(15);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_spinner, menu);

        MenuItem item = menu.findItem(R.id.spinner);
        spinner = (Spinner) MenuItemCompat.getActionView(item);


        return true;
    }

    private void setupViewPager(final ViewPager viewPager) {

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int postion, long arg3) {
                final EstadisticasAdapter adapter = new EstadisticasAdapter(getSupportFragmentManager());

                idPeña = arrayPeñas.get(spinner.getSelectedItemPosition()).getId();
                GlobalParams.codPeña = idPeña;
                nomPeña = arrayPeñas.get(spinner.getSelectedItemPosition()).getNombre();

                //Enviamos datos
               // bundle = new Bundle();
                //bundle.putInt("id", idPeña);

                //Creamos los fragment
                final EstadisiticasTab1 pt1 = new EstadisiticasTab1();
                //pt1.setArguments(bundle);
                final EstadisticasTab2 pt2 = new EstadisticasTab2();
                //pt2.setArguments(bundle);
                final EstadisticasTab3 pt3 = new EstadisticasTab3();
                //pt3.setArguments(bundle);

                //Cargamos los fragment
                adapter.addFragment(pt1, getResources().getString(R.string.goleadores));
                adapter.addFragment(pt2, getResources().getString(R.string.puntuaciones));
                adapter.addFragment(pt3, getResources().getString(R.string.title_activity_multa));
                viewPager.setAdapter(adapter);

                viewPager.setCurrentItem(tab_activa);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });

    }

    public void rellenaEspinersPeña(){
        List<String> list = new ArrayList<String>();
        for (int i = 0; i <arrayPeñas.size() ; i++) {
            list.add(arrayPeñas.get(i).getNombre().toString());
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(EstadisticasActivity.this, R.layout.spinner_plegado, list);
        dataAdapter.setDropDownViewResource(R.layout.spinner_desplegado);
        spinner.setAdapter(dataAdapter);
    }

    class ConsultEquipTask extends AsyncTask<String, String, JSONArray> {
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(EstadisticasActivity.this);
            pDialog.setMessage(getResources().getString(R.string.load));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected JSONArray doInBackground(String... args) {
            try {

                HashMap<String, String> parametrosPosteriores = new HashMap<>();
                parametrosPosteriores.put("ins_sql","select * from peña where codPeña in (SELECT CodPeña FROM componente_peña WHERE CodigoJug = "+"'"+correoUsuario+"')");
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
                setupViewPager(vp);
                rellenaEspinersPeña();

            }else {
                AlertDialog.Builder builderc = new AlertDialog.Builder(EstadisticasActivity.this);
                builderc.setMessage(getResources().getString(R.string.usuario_no_equipo));
                builderc.setPositiveButton(getResources().getString(R.string.acept),
                        new DialogInterface.OnClickListener() {
                            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });
                builderc.create();
                builderc.show();
            }

        }

        @Override
        protected void onCancelled() {
            pDialog.dismiss();
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
