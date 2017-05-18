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
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.example.oscar.teammanager.Adaptadores.GestionListAdapter;
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
    protected Button bDialogAcept,bDialogCancel,aceptPref,cancelPref;
    public static SharedPreferences sp;
    public static SharedPreferences.Editor editor;
    private JSONArray jSONArray;
    private ClaseConexion devuelveJSON;
    private JSONObject jsonObject;
    private String url_consulta, url_insert;;
    private String IP_Server;
    private Jugadores jugador;
    private Peñas peña;
    private ArrayList<Peñas> arrayPeñas;
    private ArrayList<Jugadores> arrayListJugadores;
    protected String correoUsuario;
    protected int idPeña;
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
        IP_Server = "http://iesayala.ddns.net/19ramajo";
        url_consulta = IP_Server + "/consulta.php";
        url_insert = IP_Server + "/prueba.php";
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

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setEnabled(false);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MailJob(user, passwd).execute(
                        new MailJob.Mail("soporteteammanager@gmail.com", correo.toString(), "Sancion Team Manager", "El admninistrador de la peña " + nomPeña + " le ha impuesto una sancion por "+tipoMulta)
                );

                final AlertDialog.Builder builders = new AlertDialog.Builder(MultaActivity.this);
                builders.setMessage("Se ha notificado mediante email al usuario");
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
        dialog();

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
            //Creacion dialog de filtrado
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
                    GlobalParams.multaRetraso = Integer.valueOf(retraso.getText().toString());
                    GlobalParams.multaFaltaAsistencia =Integer.valueOf(asistencia.getText().toString());
                    dialog.dismiss();
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



    public void dialog() {
        //Creacion dialog de filtrado
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_list_equipo);
        dialog.setCancelable(false);

        spinner = (Spinner) dialog.findViewById(R.id.spinner);
        bDialogAcept = (Button)dialog.findViewById(R.id.bAceptar);


        //Accion de boton guardar filtrado
        dialog.findViewById(R.id.bAceptar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                idPeña = arrayPeñas.get(spinner.getSelectedItemPosition()).getId();
                nomPeña = arrayPeñas.get(spinner.getSelectedItemPosition()).getNombre();
                ConsulTask task = new ConsulTask();
                task.execute();
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

    class ConsulTask extends AsyncTask<String, String, JSONArray> {
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

    class MultaTask extends AsyncTask<String, String, JSONArray> {
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            correo = arrayListJugadores.get(spinner_jug.getSelectedItemPosition()).getCorreo();
            if(checkAsistencia.isChecked()){
                cantidadMulta = GlobalParams.multaFaltaAsistencia;
                tipoMulta = "Falta asistencia";
            }else{
                cantidadMulta = GlobalParams.multaRetraso;
                tipoMulta = "Retraso";
            }
            pDialog = new ProgressDialog(MultaActivity.this);
            pDialog.setMessage("Guardando...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected JSONArray doInBackground(String... args) {
            try {

                HashMap<String, String> parametrosPost = new HashMap<>();
                parametrosPost.put("ins_sql","INSERT INTO multas(TipoMulta,Cantidad,CodigoJug) VALUES ("+"'"+tipoMulta+"'"+","+cantidadMulta+","+"'"+correo+"')");
                jsonObject = devuelveJSON.sendInsert(url_insert, parametrosPost);

                if(jsonObject != null) {
                    try {
                        switch (jsonObject.getInt(("added"))){
                            case 1:
                                Snackbar.make(findViewById(android.R.id.content), "Multa guardada.", Snackbar.LENGTH_LONG).show();
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


}
