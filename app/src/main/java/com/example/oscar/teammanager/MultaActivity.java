package com.example.oscar.teammanager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import com.example.oscar.teammanager.Objects.Jugadores;
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

public class MultaActivity extends AppCompatActivity {

    protected CheckBox checkRetraso,checkAsistencia;
    protected Dialog dialog;
    protected Spinner spinner,spinner_jug;
    protected Button aceptPref,cancelPref;
    public static SharedPreferences sp;
    public static SharedPreferences.Editor editor;
    private JSONArray jSONArray;
    private ClaseConexion devuelveJSON;
    private JSONObject jsonObject;
    private Jugadores jugador;
    private Peñas peña;
    private ArrayList<Peñas> arrayPeñas;
    private ArrayList<Jugadores> arrayListJugadores;
    protected String correoUsuario;
    protected int idPeña;
    protected String idJug;
    protected int cantidadMulta = GlobalParams.multaRetraso;
    protected String tipoMulta = "Retraso";
    protected String correo;
    protected Button aceptar;
    protected String user,passwd;
    protected String nomPeña;
    protected EditText retraso,asistencia;
    protected FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multa);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        user = "soporteteammanager@gmail.com";
        passwd = "TM100517";
        aceptar = (Button)findViewById(R.id.bAceptar);
        devuelveJSON = new ClaseConexion();
        arrayPeñas = new ArrayList<>();
        arrayListJugadores = new ArrayList<>();
        checkAsistencia = (CheckBox)findViewById(R.id.checkBox);
        checkRetraso    = (CheckBox)findViewById(R.id.checkBox2);
        spinner_jug = (Spinner) findViewById(R.id.spinner_jug);

        sp = getSharedPreferences("preferencias", Context.MODE_PRIVATE);
        editor = sp.edit();

        correoUsuario = sp.getString("us_email", correoUsuario);

        //boton de notificar multa, esta inhabilitado hasta que no se impone una multa
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setEnabled(false);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //envio correo a traves de la clase MailJob al usuario informando de la multa establecida
                new MailJob(user, passwd).execute(
                        new MailJob.Mail(getResources().getString(R.string.soporte), correo.toString(), "Sancion Team Manager", "El admninistrador de la peña " + nomPeña + " le ha impuesto una sancion por "+tipoMulta)
                );

                final AlertDialog.Builder builders = new AlertDialog.Builder(MultaActivity.this);
                builders.setMessage(getResources().getString(R.string.notifi));
                builders.setPositiveButton(getResources().getString(R.string.acept),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                fab.setEnabled(false);
                                builders.setCancelable(true);
                            }
                        });
                builders.setCancelable(false);
                builders.create();
                builders.show();
            }
        });

        ConsultEquipTask task= new ConsultEquipTask();
        task.execute();

        checkAsistencia.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(checkAsistencia.isChecked()){
                    checkRetraso.setChecked(false);
                    cantidadMulta = GlobalParams.multaFaltaAsistencia;
                    tipoMulta = "Falta asistencia";
                }

            }
        });

        checkRetraso.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(checkRetraso.isChecked()){
                    checkAsistencia.setChecked(false);
                    cantidadMulta = GlobalParams.multaRetraso;
                    tipoMulta = "Retraso";
                }

            }
        });

        aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MultaTask task1 = new MultaTask();
                task1.execute();
                fab.setEnabled(true);
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_multa, menu);

        MenuItem item = menu.findItem(R.id.spinner);
        spinner = (Spinner) MenuItemCompat.getActionView(item);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int postion, long arg3) {

                //selecciono el equipo y ejecuto task de consulta de datos
                idPeña = arrayPeñas.get(spinner.getSelectedItemPosition()).getId();
                nomPeña = arrayPeñas.get(spinner.getSelectedItemPosition()).getNombre();
                arrayListJugadores.clear();
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
        if (id == R.id.action_cantidad) {
            //Creacion dialog en el que puedo modificar la cantida en cada tipo de multa, por defecto es 3 retraso y 5 no asistencia
            dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.layout_pref);
            dialog.setCancelable(false);

            retraso = (EditText)dialog.findViewById(R.id.edRetraso);
            asistencia = (EditText)dialog.findViewById(R.id.edAsistencia);
            aceptPref = (Button)dialog.findViewById(R.id.bAceptPref);
            cancelPref = (Button)dialog.findViewById(R.id.bCancelarPref);

            //Accion de boton guardar
            dialog.findViewById(R.id.bAceptPref).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(retraso.getText().toString().equals("") || asistencia.getText().toString().equals("")){
                        Snackbar.make(findViewById(android.R.id.content), "Escriba cantidad de dinero en cada campo o pulse cancelar.", Snackbar.LENGTH_LONG).show();
                    }else{
                    GlobalParams.multaRetraso = Integer.valueOf(retraso.getText().toString());
                    GlobalParams.multaFaltaAsistencia =Integer.valueOf(asistencia.getText().toString());
                    dialog.dismiss();
                    }
                }
            });

            //Accion de boton cancelar
            dialog.findViewById(R.id.bCancelarPref).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            dialog.show();
            return true;
        }


        return super.onOptionsItemSelected(item);
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

    //task que consultaEquipos administrados
    class ConsultEquipTask extends AsyncTask<String, String, JSONArray> {
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(MultaActivity.this);
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


    //task que consulta jugadores de equipo seleccionado
    class ConsulTask extends AsyncTask<String, String, JSONArray> {
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(MultaActivity.this);
            pDialog.setMessage(getResources().getString(R.string.load));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected JSONArray doInBackground(String... args) {
            try {

                HashMap<String, String> parametrosPosteriores = new HashMap<>();
                parametrosPosteriores.put("ins_sql","select Nombre, Ruta_Foto, Correo from jugadores where Correo in (SELECT CodigoJug FROM componente_peña WHERE CodPeña = "+idPeña+")");
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
            }
        }

        @Override
        protected void onCancelled() {
            pDialog.dismiss();
        }
    }

    //task que actualiza datos de multa
    class MultaTask extends AsyncTask<String, String, JSONArray> {
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {

            //compruebo tipo de multa impuesta
            correo = arrayListJugadores.get(spinner_jug.getSelectedItemPosition()).getCorreo();
            if(checkAsistencia.isChecked()){
                cantidadMulta = GlobalParams.multaFaltaAsistencia;
                tipoMulta = "Falta asistencia";
            }else{
                cantidadMulta = GlobalParams.multaRetraso;
                tipoMulta = "Retraso";
            }
            pDialog = new ProgressDialog(MultaActivity.this);
            pDialog.setMessage(getResources().getString(R.string.saved));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected JSONArray doInBackground(String... args) {
            try {

                HashMap<String, String> parametrosPost = new HashMap<>();
                parametrosPost.put("ins_sql","INSERT INTO multas(TipoMulta,Cantidad,CodigoJug,Estado,CodPeña) VALUES ("+"'"+tipoMulta+"'"+","+cantidadMulta+","+"'"+correo+"', 'No pagado', "+idPeña+")");
                jsonObject = devuelveJSON.sendInsert(GlobalParams.url_insert, parametrosPost);

                if(jsonObject != null) {
                    try {
                        switch (jsonObject.getInt(("added"))){
                            case 1:
                                Snackbar.make(findViewById(android.R.id.content), R.string.multa_guardada, Snackbar.LENGTH_LONG).show();
                                break;
                            default:
                                Snackbar.make(findViewById(android.R.id.content), "Error.", Snackbar.LENGTH_LONG).show();
                                break;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    System.out.println("OBJETO NULO.");
                    Snackbar.make(findViewById(android.R.id.content), "Error al guardar.", Snackbar.LENGTH_LONG).show();
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

        }

        @Override
        protected void onCancelled()
        {
            pDialog.dismiss();
        }
    }

   /* @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        Intent i = new Intent(MultaActivity.this, MainActivity.class);
        startActivity(i);
    }*/

}
