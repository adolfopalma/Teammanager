package com.example.oscar.teammanager;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
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
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.oscar.teammanager.Adaptadores.Adapter_list_claros;
import com.example.oscar.teammanager.Adaptadores.Adapter_list_oscuros;
import com.example.oscar.teammanager.Objects.Jugadores;
import com.example.oscar.teammanager.Objects.Peñas;
import com.example.oscar.teammanager.Utils.Chronometer;
import com.example.oscar.teammanager.Utils.ClaseConexion;
import com.example.oscar.teammanager.Utils.GlobalParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class PartidoActivity extends AppCompatActivity {

    protected TextView tv1;
    protected Button mBtnStart, mBtnStop;
    protected int mLapCounter = 1;
    protected Chronometer mChrono;
    protected Thread mThreadChrono;
    protected Context mContext;
    private JSONArray jSONArray;
    private ClaseConexion devuelveJSON;
    private JSONObject jsonObject;
    private String url_consulta, url_insert;
    private String IP_Server;
    private Jugadores jugador;
    protected ListView lvOscuro, lvClaro;
    protected TextView marcadorClaro, marcadorOscuro;
    public static SharedPreferences sp;
    public static SharedPreferences.Editor editor;
    protected Dialog dialog;
    protected Spinner spinner;
    protected String correoUsuario;
    protected Button bDialogAcept,bDialogCancel;
    private Peñas peña;
    private ArrayList<Peñas> arrayPeñas;

    private ArrayList<Jugadores> arrayListaJugadores;
    ArrayList<Jugadores> tempList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partido);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        sp = getSharedPreferences("preferencias", Context.MODE_PRIVATE);
        editor = sp.edit();
        correoUsuario = sp.getString("us_email", correoUsuario);
        GlobalParams.MarcadorClaro = "0";
        GlobalParams.MarcadorOscuro = "0";
        IP_Server = "http://iesayala.ddns.net/19ramajo";
        url_consulta = IP_Server + "/consulta.php";
        url_insert = IP_Server + "/prueba.php";
        devuelveJSON = new ClaseConexion();
        EquipoTask task = new EquipoTask();
        task.execute();
        dialog();
        tv1 = (TextView) findViewById(R.id.tv1);
        mContext = this;
        mBtnStart = (Button) findViewById(R.id.bIniciar);
        mBtnStop = (Button) findViewById(R.id.bParar);
        tv1 = (TextView) findViewById(R.id.tv1);
        arrayListaJugadores = new ArrayList<>();
        arrayPeñas = new ArrayList<>();
        lvClaro = (ListView) findViewById(R.id.listviewClaro);
        lvOscuro = (ListView) findViewById(R.id.listviewOscuro);
        marcadorClaro = (TextView)findViewById(R.id.MarcadorClaro);
        marcadorOscuro = (TextView)findViewById(R.id.MarcadorOscuro);



        //btn_start click handler
        mBtnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if the chronometer has not been instantiated before...
                if(mChrono == null) {
                    //instantiate the chronometer
                    mChrono = new Chronometer(mContext);
                    //run the chronometer on a separate thread
                    mThreadChrono = new Thread(mChrono);
                    mThreadChrono.start();

                    //start the chronometer!
                    mChrono.start();

                    //reset the lap counter
                    mLapCounter = 1;
                }
            }
        });

        //btn_stop click handler
        mBtnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if the chronometer had been instantiated before...
                if(mChrono != null) {
                    //stop the chronometer
                    mChrono.stop();
                    //stop the thread

                    mThreadChrono.interrupt();
                    mThreadChrono = null;
                    //kill the chrono class
                    mChrono = null;
                }
            }
        });

        PartidoTask task2 = new PartidoTask();
        task2.execute();

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

    private void suffleList(ArrayList<Jugadores> list) {
        final long seed = System.nanoTime();
        Collections.shuffle(list, new Random(seed));
    }

    public void process() {
         // lista temporal
        tempList = arrayListaJugadores;
        suffleList(tempList); // mezclar
    }


    /**
     * Update the text of tv_timer
     * @param timeAsText the text to update tv_timer with
     */
    public void updateTimerText(final String timeAsText) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv1.setText(timeAsText);
                marcadorClaro.setText(GlobalParams.MarcadorClaro);
                marcadorOscuro.setText(GlobalParams.MarcadorOscuro);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    //relleno los datos de los spinner de el filtro
    public void rellenaEspiners(){
        List<String> list = new ArrayList<String>();
        for (int i = 0; i <arrayPeñas.size() ; i++) {
            list.add(arrayPeñas.get(i).getNombre().toString());
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(PartidoActivity.this, R.layout.spinner_plegado, list);
        dataAdapter.setDropDownViewResource(R.layout.spinner_desplegado);
        spinner.setAdapter(dataAdapter);
    }

    class PartidoTask extends AsyncTask<String, String, JSONArray> {
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(PartidoActivity.this);
            pDialog.setMessage("Cargando...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected JSONArray doInBackground(String... args) {
            try {

                HashMap<String, String> parametrosPosteriores = new HashMap<>();
                parametrosPosteriores.put("ins_sql","select Nombre, Ruta_Foto, Correo from jugadores where Correo in (SELECT CodigoJug FROM componente_peña WHERE CodPeña = 1)");
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
                        arrayListaJugadores.add(jugador);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                process();
                lvClaro.setAdapter(new Adapter_list_claros(PartidoActivity.this, tempList));

                lvOscuro.setAdapter(new Adapter_list_oscuros(PartidoActivity.this, tempList));

            } else {
                Snackbar.make(findViewById(android.R.id.content), "Error de conexion", Snackbar.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onCancelled() {
            pDialog.dismiss();
        }
    }

    class EquipoTask extends AsyncTask<String, String, JSONArray> {


        @Override
        protected void onPreExecute() {

        }

        @Override
        protected JSONArray doInBackground(String... args) {
            try {

                HashMap<String, String> parametrosPosteriores = new HashMap<>();
                parametrosPosteriores.put("ins_sql","select * from peña where CodAministrador = "+"'"+correoUsuario+"'");
                System.out.println("///////////////"+url_consulta+parametrosPosteriores);
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

            if (json != null) {
                for (int i = 0; i < json.length(); i++) {
                    try {
                        jsonObject = json.getJSONObject(i);
                        peña = new Peñas();
                        peña.setNombre(jsonObject.getString("nomPeña"));
                        arrayPeñas.add(peña);
                        System.out.println("********"+arrayPeñas.get(i).getNombre());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                rellenaEspiners();

            } else {
                Snackbar.make(findViewById(android.R.id.content), "Error de conexion", Snackbar.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onCancelled() {
        }
    }

}
