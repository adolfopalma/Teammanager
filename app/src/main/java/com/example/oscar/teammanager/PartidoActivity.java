package com.example.oscar.teammanager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import com.example.oscar.teammanager.Adaptadores.Adapter_list_claros;
import com.example.oscar.teammanager.Adaptadores.Adapter_list_oscuros;
import com.example.oscar.teammanager.Adaptadores.GestionListAdapter;
import com.example.oscar.teammanager.Objects.Jugadores;
import com.example.oscar.teammanager.Objects.Peñas;
import com.example.oscar.teammanager.Utils.Chronometer;
import com.example.oscar.teammanager.Utils.ClaseConexion;
import com.example.oscar.teammanager.Utils.GlobalParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class PartidoActivity extends AppCompatActivity {


    public static final String START_TIME = "START_TIME";
    public static final String CHRONO_WAS_RUNNING = "CHRONO_WAS_RUNNING";
    public static final String TV_TIMER_TEXT = "TV_TIMER_TEXT";
    public static final String ET_LAPST_TEXT = "ET_LAPST_TEXT";
    public static final String LAP_COUNTER  = "LAP_COUNTER";

    protected Button BtnStart, BtnStop, BtnSortear,bDialogAcept;
    protected int mLapCounter = 1;
    protected int idPeña;
    protected Chronometer mChrono;
    protected Thread mThreadChrono;
    protected Context mContext;
    private JSONArray jSONArray;
    private ClaseConexion devuelveJSON;
    private JSONObject jsonObject;
    private String url_consulta, url_insert,IP_Server;
    private Jugadores jugador;
    protected ListView lvOscuro, lvClaro, lv;
    protected TextView marcadorClaro, marcadorOscuro,tv1;
    public static SharedPreferences sp;
    public static SharedPreferences.Editor editor;
    protected Dialog dialog;
    protected Spinner spinner;
    protected String correoUsuario,ganador,fecha,resultado;
    private Peñas peña;
    private ArrayList<Peñas> arrayPeñas;
    protected LinearLayout lista_jug;
    protected LinearLayout empty_equipo;
    protected Adapter_list_claros alc;
    protected Adapter_list_oscuros alo;
    String compuestoGanadores = "";
    String compuestoPerdedores = "";
    String compuestoEmpate = "";
    String compuestoJugados = "";


    //Listas
    private ArrayList<Jugadores> arrayListaJugadores;
    private ArrayList<Jugadores> arrayListaPorteros;
    private ArrayList<Jugadores> tempList;
    private ArrayList<Jugadores> jugadoresOscuros;
    private ArrayList<Jugadores> jugadoresClaros;
    private ArrayList<String> listaIdsGanadores;
    private ArrayList<String> listaIdsPerdedores;
    private ArrayList<String> listaIdsEmpate;
    private ArrayList<String> listaIdsJugPartido;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partido);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mContext = this;
        lista_jug = (LinearLayout)findViewById(R.id.lista_jugadores);
        empty_equipo = (LinearLayout)findViewById(R.id.empty_equipo);
        arrayListaJugadores = new ArrayList<>();
        arrayPeñas = new ArrayList<>();
        sp = getSharedPreferences("preferencias", Context.MODE_PRIVATE);
        editor = sp.edit();
        correoUsuario = sp.getString("us_email", correoUsuario);
        IP_Server = "http://iesayala.ddns.net/19ramajo";
        url_consulta = IP_Server + "/consulta.php";
        url_insert = IP_Server + "/prueba.php";
        devuelveJSON = new ClaseConexion();
        tempList = new ArrayList<Jugadores>();
        tv1 = (TextView) findViewById(R.id.tv1);
        mContext = this;
        BtnSortear = (Button) findViewById(R.id.bSortear);
        BtnStart = (Button) findViewById(R.id.bComenzar);
        BtnStart.setEnabled(false);
        BtnStop = (Button) findViewById(R.id.bFinalizar);
        BtnStop.setEnabled(false);
        tv1 = (TextView) findViewById(R.id.tv1);
        lvClaro = (ListView) findViewById(R.id.listviewClaro);
        lvOscuro = (ListView) findViewById(R.id.listviewOscuro);
        lv = (ListView) findViewById(R.id.lv_gestion);
        marcadorClaro = (TextView) findViewById(R.id.MarcadorClaro);
        marcadorOscuro = (TextView) findViewById(R.id.MarcadorOscuro);
        jugadoresOscuros = new ArrayList<>();
        jugadoresClaros = new ArrayList<>();
        arrayListaPorteros = new ArrayList<>();


            //btn_start click handler
            BtnStart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    BtnSortear.setEnabled(false);

                    //if the chronometer has not been instantiated before...
                    if (mChrono == null) {
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
            BtnStop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //if the chronometer had been instantiated before...
                    if (mChrono != null) {
                        //stop the chronometer
                        mChrono.stop();
                        //stop the thread

                        mThreadChrono.interrupt();
                        mThreadChrono = null;
                        //kill the chrono class
                        mChrono = null;
                        tv1.setText("00:00:00");
                    }

                    compruebaGanador();


                    if (listaIdsGanadores != null) {
                        for (int i = 0; i < listaIdsGanadores.size(); i++) {
                            compuestoGanadores = compuestoGanadores + " " + listaIdsGanadores.get(i);
                        }
                    }

                    if (listaIdsPerdedores != null) {
                        for (int i = 0; i < listaIdsPerdedores.size(); i++) {
                            compuestoPerdedores = compuestoPerdedores + " " + listaIdsPerdedores.get(i);
                        }
                    }

                    if (listaIdsEmpate != null) {
                        for (int i = 0; i < listaIdsEmpate.size(); i++) {
                           compuestoEmpate = compuestoEmpate + " " + listaIdsEmpate.get(i);
                        }
                    }

                    if (listaIdsJugPartido != null) {
                        for (int i = 0; i < listaIdsJugPartido.size(); i++) {
                            compuestoJugados = compuestoJugados + " " + listaIdsJugPartido.get(i);
                        }
                    }


                    Calendar cal = new GregorianCalendar();
                    Date date = cal.getTime();
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                    fecha = df.format(date);

                    resultado = "Claro: " + marcadorClaro.getText().toString() + " - " + marcadorOscuro.getText().toString() + " :Oscuro";

                    PartidoTask task = new PartidoTask();
                    task.execute();

                    UpdateTask task1 = new UpdateTask();
                    task1.execute();

                    GlobalParams.MarcadorClaro = "0";
                    GlobalParams.MarcadorOscuro = "0";

                    final AlertDialog.Builder builders = new AlertDialog.Builder(PartidoActivity.this);
                    if (ganador.equals("Empate")) {
                        builders.setMessage("Ha habido un empate entre el equipo oscuro y el equipo claro");
                    } else
                        builders.setMessage(getResources().getString(R.string.ganador_partido) + ganador);
                    builders.setPositiveButton(getResources().getString(R.string.acept),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                    //Intent i = new Intent(PartidoActivity.this, MainActivity.class);
                                    //startActivity(i);
                                }
                            });
                    builders.setCancelable(false);
                    builders.create();
                    builders.show();
                }
            });


            BtnSortear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GlobalParams.equipo1 = new ArrayList<Jugadores>();
                    GlobalParams.equipo2 = new ArrayList<Jugadores>();

                    lv.setVisibility(View.GONE);
                    lista_jug.setVisibility(View.VISIBLE);
                    GlobalParams.MarcadorClaro = "0";
                    GlobalParams.MarcadorOscuro = "0";

                    if (tempList.size() > 0) {
                        jugadoresClaros.clear();
                        jugadoresOscuros.clear();
                        process();
                        alc = (new Adapter_list_claros(PartidoActivity.this, jugadoresClaros));
                        lvClaro.setAdapter(alc);
                        alo = (new Adapter_list_oscuros(PartidoActivity.this, jugadoresOscuros));
                        lvOscuro.setAdapter(alo);
                        GlobalParams.equipo1 = jugadoresClaros;
                        GlobalParams.equipo2 = jugadoresOscuros;
                        BtnStart.setEnabled(true);
                        BtnStop.setEnabled(true);
                    } else {
                        Snackbar.make(findViewById(android.R.id.content), "No hay suficientes jugadores seleccionados para jugar", Snackbar.LENGTH_LONG).show();

                    }
                }
            });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_partido, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_añadir) {
            dialog();
            return true;
        }

        if (id == R.id.action_invitado) {

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


        dialog.findViewById(R.id.bAceptar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GlobalParams.codPeña = arrayPeñas.get(spinner.getSelectedItemPosition()).getId();
                lv.setVisibility(View.VISIBLE);
                ConsultaTask task2 = new ConsultaTask();
                task2.execute();
                dialog.dismiss();

                final AlertDialog.Builder builders = new AlertDialog.Builder(PartidoActivity.this);
                builders.setMessage(getResources().getString(R.string.lista_partido));
                builders.setPositiveButton(getResources().getString(R.string.acept),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                builders.setCancelable(true);
                            }
                        });
                builders.setCancelable(false);
                builders.create();
                builders.show();

            }
        });
        EquipoTask task = new EquipoTask();
        task.execute();
        dialog.show();
    }

    public void compruebaGanador(){
        int m1 = Integer.valueOf(marcadorClaro.getText().toString());
        int m2 = Integer.valueOf(marcadorOscuro.getText().toString());
        listaIdsGanadores = new ArrayList<>();
        listaIdsPerdedores = new ArrayList<>();
        listaIdsEmpate = new ArrayList<>();
        listaIdsJugPartido  = new ArrayList<>();

        for(int i=0; i<jugadoresClaros.size(); i++){
                listaIdsJugPartido.add("CodigoJug like " +"'"+jugadoresClaros.get(i).getCorreo()+"'"+ " ||");
        }

        for(int i=0; i<jugadoresOscuros.size(); i++){
            if(i == jugadoresOscuros.size()-1){
                listaIdsJugPartido.add("CodigoJug like "+"'"+jugadoresOscuros.get(i).getCorreo()+"'");
            }else {
                listaIdsJugPartido.add("CodigoJug like " +"'"+jugadoresOscuros.get(i).getCorreo()+"'"+ " ||");
            }
        }

        if(m1 < m2){
            ganador = "Equipo Oscuro";
        }else if(m1 > m2){
            ganador = "Equipo Claro";
        }else{
            ganador = "Empate";
        }

        switch(ganador){

            case "Equipo Oscuro":
                for(int i=0; i<jugadoresOscuros.size(); i++){
                    if(i == jugadoresOscuros.size()-1){
                        listaIdsGanadores.add("CodigoJug like "+"'"+jugadoresOscuros.get(i).getCorreo()+"'");
                    }else {
                        listaIdsGanadores.add("CodigoJug like " +"'"+jugadoresOscuros.get(i).getCorreo()+"'"+ " ||");
                    }
                }

                for(int i=0; i<jugadoresClaros.size(); i++){
                    if(i == jugadoresClaros.size()-1){
                        listaIdsPerdedores.add("CodigoJug like "+"'"+jugadoresClaros.get(i).getCorreo()+"'");
                    }else {
                        listaIdsPerdedores.add("CodigoJug like " +"'"+jugadoresClaros.get(i).getCorreo()+"'"+ " ||");
                    }
                }
                break;

            case "Equipo Claro":
                for(int i=0; i<jugadoresClaros.size(); i++){
                    if(i == jugadoresClaros.size()-1){
                        listaIdsGanadores.add("CodigoJug like "+"'"+jugadoresClaros.get(i).getCorreo()+"'");
                    }else {
                        listaIdsGanadores.add("CodigoJug like " +"'"+jugadoresClaros.get(i).getCorreo()+"'"+ " ||");
                    }
                }

                for(int i=0; i<jugadoresOscuros.size(); i++){
                    if(i == jugadoresOscuros.size()-1){
                        listaIdsPerdedores.add("CodigoJug like "+"'"+jugadoresOscuros.get(i).getCorreo()+"'");
                    }else {
                        listaIdsPerdedores.add("CodigoJug like " +"'"+jugadoresOscuros.get(i).getCorreo()+"'"+ " ||");
                    }
                }
                break;

            case "Empate":
                for(int i=0; i<jugadoresClaros.size(); i++){
                        listaIdsEmpate.add("CodigoJug like " +"'"+jugadoresClaros.get(i).getCorreo()+"'"+ " ||");
                }

                for(int i=0; i<jugadoresOscuros.size(); i++){
                    if(i == jugadoresOscuros.size()-1){
                        listaIdsEmpate.add("CodigoJug like "+"'"+jugadoresOscuros.get(i).getCorreo()+"'");
                    }else {
                        listaIdsEmpate.add("CodigoJug like " +"'"+jugadoresOscuros.get(i).getCorreo()+"'"+ " ||");
                    }
                }
                break;
        }
    }

    private void suffleList(ArrayList<Jugadores> list) {
        final long seed = System.nanoTime();
        Collections.shuffle(list, new Random(seed));
    }

    public void process() {
        int numAle = (int) (Math.random() * 2);
        suffleList(tempList);
        suffleList(arrayListaPorteros);


            for (int i = 0; i < tempList.size(); i++) {

                if (i <= tempList.size() / 2 - 1) {
                    jugadoresOscuros.add(tempList.get(i));

                } else if (i > tempList.size() / 2 - 1) {
                    jugadoresClaros.add(tempList.get(i));
                }
            }

            if(arrayListaPorteros.size() == 1) {
                if (jugadoresOscuros.size() < jugadoresClaros.size()) {
                    jugadoresOscuros.add(arrayListaPorteros.get(0));
                } else if (jugadoresOscuros.size() > jugadoresClaros.size()) {
                    jugadoresClaros.add(arrayListaPorteros.get(0));
                }else{
                    if(numAle == 1) {
                        jugadoresOscuros.add(arrayListaPorteros.get(0));
                    }else{
                        jugadoresClaros.add(arrayListaPorteros.get(0));
                    }
                }
            }

        if(arrayListaPorteros.size() > 1){

            for (int i = 0; i < arrayListaPorteros.size(); i++) {

                if (i <= arrayListaPorteros.size() / 2-1) {
                    jugadoresOscuros.add(arrayListaPorteros.get(i));

                } else if (i > arrayListaPorteros.size() / 2-1) {
                    jugadoresClaros.add(arrayListaPorteros.get(i));
                }
            }

        }

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
        loadInstance();

        //stop background services and notifications
        /*ChronometerApplication ch = new ChronometerApplication();
        ch.stopBackgroundServices();
        ch.cancelNotification();*/
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveInstance();

        if(mChrono != null && mChrono.isRunning()) {
            //start background notification and timer
/*            ((ChronometerApplication)getApplication())
                    .startBackgroundServices(mChrono.getStartTime());*/
        }
    }

    @Override
    protected void onDestroy() {
        saveInstance();

        //When back button is pressed, app will be destoyed by OS. We do not want this to stop us
        //from showing the notification if the chronometer is running!
        if(mChrono == null || !mChrono.isRunning()) {
            //stop background services and notifications
            /*((ChronometerApplication) getApplication()).stopBackgroundServices();
            ((ChronometerApplication) getApplication()).cancelNotification();*/
        }

        super.onDestroy();
    }

    private void loadInstance() {

        SharedPreferences pref = getPreferences(MODE_PRIVATE);

        //if chronometer was running
        if(pref.getBoolean(CHRONO_WAS_RUNNING, false)) {
            //get the last start time from the bundle
            long lastStartTime = pref.getLong(START_TIME, 0);
            //if the last start time is not 0
            if(lastStartTime != 0) { //because 0 means value was not saved correctly!

                if(mChrono == null) { //make sure we dont create new instance and thread!

                    if(mThreadChrono != null) { //if thread exists...first interrupt and nullify it!
                        mThreadChrono.interrupt();
                        mThreadChrono = null;
                    }

                    //start chronometer with old saved time
                    mChrono = new Chronometer(mContext, lastStartTime);
                    mThreadChrono = new Thread(mChrono);
                    mThreadChrono.start();
                    mChrono.start();
                }
            }
        }

        //we will load the lap text anyway in any case!
        //set the old value of lap counter
        mLapCounter = pref.getInt(LAP_COUNTER, 1);

        String oldEtLapsText = pref.getString(ET_LAPST_TEXT, "");
        if(!oldEtLapsText.isEmpty()) { //if old timer was saved correctly
            //mEtLaps.setText(oldEtLapsText);
        }

        String oldTvTimerText = pref.getString(TV_TIMER_TEXT, "");
        if(!oldTvTimerText.isEmpty()){
            //mTvTimer.setText(oldTvTimerText);
        }
    }

    private void saveInstance() {
        SharedPreferences pref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        //TODO move tags to a static class
        if(mChrono != null && mChrono.isRunning()) {
            editor.putBoolean(CHRONO_WAS_RUNNING, mChrono.isRunning());
            editor.putLong(START_TIME, mChrono.getStartTime());
            editor.putInt(LAP_COUNTER, mLapCounter);
        } else {
            editor.putBoolean(CHRONO_WAS_RUNNING, false);
            editor.putLong(START_TIME, 0); //0 means chronometer was not active! a redundant check!
            editor.putInt(LAP_COUNTER, 1);
        }

        //We save the lap text in any case. only a new click on start button should clear this text!
       // editor.putString(ET_LAPST_TEXT, mEtLaps.getText().toString());

        //Same story for timer text
       // editor.putString(TV_TIMER_TEXT, mTvTimer.getText().toString());

        editor.commit();
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

    class ConsultaTask extends AsyncTask<String, String, JSONArray> {
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
                parametrosPosteriores.put("ins_sql","select * from jugadores where Correo in (SELECT CodigoJug FROM componente_peña WHERE CodPeña = "+GlobalParams.codPeña+")");
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
                        jugador.setCorreo(jsonObject.getString("Correo"));
                        jugador.setRutaFoto(jsonObject.getString("Ruta_Foto"));
                        jugador.setTipoJug(jsonObject.getString("TipoJugador"));
                        arrayListaJugadores.add(jugador);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                lv.setAdapter(new GestionListAdapter(PartidoActivity.this, arrayListaJugadores));
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView adapter, View view, int position, long arg) {
                        if(arrayListaJugadores.get(position).getTipoJug().equals("Portero")){
                            arrayListaPorteros.add(arrayListaJugadores.get(position));
                            Snackbar.make(findViewById(android.R.id.content), "Jugador : " + arrayListaJugadores.get(position).getNombre() + " añadido", Snackbar.LENGTH_LONG).show();
                            arrayListaJugadores.remove(position);
                            lv.setAdapter(new GestionListAdapter(PartidoActivity.this, arrayListaJugadores));
                        }else {
                            tempList.add(arrayListaJugadores.get(position));
                            Snackbar.make(findViewById(android.R.id.content), "Jugador : " + arrayListaJugadores.get(position).getNombre() + " añadido", Snackbar.LENGTH_LONG).show();
                            arrayListaJugadores.remove(position);
                            lv.setAdapter(new GestionListAdapter(PartidoActivity.this, arrayListaJugadores));
                        }
                    }
                });

            } else {

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
                jSONArray = devuelveJSON.sendRequest(url_consulta, parametrosPosteriores);

                if (jSONArray.length() > 0) {
                    return jSONArray;
                }else{
                    System.out.println("Error al obtener datos JSON");
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
                        peña.setId(jsonObject.getInt("codPeña"));
                        peña.setNombre(jsonObject.getString("nomPeña"));
                        arrayPeñas.add(peña);
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

    class PartidoTask extends AsyncTask<String, String, JSONArray> {


        @Override
        protected void onPreExecute() {

        }

        @Override
        protected JSONArray doInBackground(String... args) {
            try {

                HashMap<String, String> parametrosPost = new HashMap<>();
                parametrosPost.put("ins_sql","INSERT INTO partido(CodPeña,fechaPartido,resultado,ganador,nomPeña) VALUES ("+GlobalParams.codPeña+","+"'"+fecha+"'"+","+"'"+resultado+"'"+","+"'"+ganador+"',"+GlobalParams.codPeña+")");
                jsonObject = devuelveJSON.sendInsert(url_insert, parametrosPost);

                if(jsonObject != null) {
                    switch (jsonObject.getInt("added")){
                        case 1:
                           System.out.println("Añadido");
                            break;
                        default:
                            System.out.println("No añadido");
                            break;
                    }
                }else{
                    System.out.println("OBJETO NULO.");

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(JSONArray json) {

        }

        @Override
        protected void onCancelled() {
        }
    }

    class UpdateTask extends AsyncTask<String, String, JSONArray> {

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected JSONArray doInBackground(String... args) {
            try {

                HashMap<String, String> parametrosPostJugados = new HashMap<>();
                parametrosPostJugados.put("ins_sql", "UPDATE estadisticas SET PartidosJugados = PartidosJugados+1  WHERE (codPeña = "+GlobalParams.codPeña+") and ("+ compuestoJugados + ")");
                devuelveJSON.sendRequest(url_insert, parametrosPostJugados);

                if(listaIdsGanadores != null) {
                    HashMap<String, String> parametrosPostVict = new HashMap<>();
                    parametrosPostVict.put("ins_sql", "UPDATE estadisticas SET PartidosGanados = PartidosGanados+1, Puntos = Puntos+3  WHERE (codPeña = "+GlobalParams.codPeña+") and ("+ compuestoGanadores + ")");
                    devuelveJSON.sendRequest(url_insert, parametrosPostVict);
                }

                if(listaIdsPerdedores != null) {
                    HashMap<String, String> parametrosPostPerd = new HashMap<>();
                    parametrosPostPerd.put("ins_sql","UPDATE estadisticas SET PartidosPerdidos = PartidosPerdidos+1  WHERE (codPeña = "+GlobalParams.codPeña+") and ("+ compuestoPerdedores + ")");
                    devuelveJSON.sendRequest(url_insert, parametrosPostPerd);
                }

                if(listaIdsEmpate != null) {

                    HashMap<String, String> parametrosPostEmpt = new HashMap<>();
                    parametrosPostEmpt.put("ins_sql", "UPDATE estadisticas SET PartidosEmpatados = PartidosEmpatados+1, Puntos = Puntos+1  WHERE (codPeña = "+GlobalParams.codPeña+") and ("+ compuestoEmpate + ")");
                    devuelveJSON.sendRequest(url_insert, parametrosPostEmpt);
                }


            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(JSONArray json) {

        }

        @Override
        protected void onCancelled() {

        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        arrayPeñas.clear();
        arrayListaJugadores.clear();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        lv.setVisibility(View.GONE);
        lista_jug.setVisibility(View.VISIBLE);
        jugadoresClaros = GlobalParams.equipo1;
        jugadoresOscuros = GlobalParams.equipo2;
        alc = (new Adapter_list_claros(PartidoActivity.this, jugadoresClaros));
        alc.notifyDataSetChanged();
        lvClaro.setAdapter(alc);
        alo = (new Adapter_list_oscuros(PartidoActivity.this, jugadoresOscuros));
        alo.notifyDataSetChanged();
        lvOscuro.setAdapter(alo);
        BtnSortear.setEnabled(false);
        BtnStart.setEnabled(false);
        BtnStop.setEnabled(true);
    }
}
