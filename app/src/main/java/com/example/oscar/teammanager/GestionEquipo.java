package com.example.oscar.teammanager;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.example.oscar.teammanager.Adaptadores.GestionListAdapter;
import com.example.oscar.teammanager.Objects.Jugadores;
import com.example.oscar.teammanager.Objects.Peñas;
import com.example.oscar.teammanager.Utils.ClaseConexion;
import com.example.oscar.teammanager.Utils.GlobalParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;

//clase que gestiona los equipos , puedo añadir componentes y editar el equipo
public class GestionEquipo extends AppCompatActivity {

    public static SharedPreferences sp;
    public static SharedPreferences.Editor editor;
    private Bundle datos;
    private Peñas peña;
    private Jugadores jugador;
    private  int color;
    private Paint paint;
    private Rect rect;
    private RectF rectF;
    private Bitmap result;
    private Canvas canvas;
    private  float roundPx;
    private JSONArray jSONArray;
    private ClaseConexion devuelveJSON;
    private JSONObject jsonObject;
    protected String correoUsuario, correo;
    protected int id;
    protected ListView lv;
    protected EditText correoAdd;
    protected Button bDialogAcept,bDialogCancel;
    protected TextView nombrePeña, creacionPeña, componentesPeña, administrador, diaPartido, horaPartido;
    protected ImageView fotoPeña;
    protected Bitmap foto;
    protected Dialog dialog;
    boolean usuario_repetido,usuario_registrado;
    protected GestionListAdapter ga;
    protected Menu menuBar;


    //Listas
    private ArrayList<Peñas> arrayPeñas;
    private ArrayList<Jugadores> arrayListaJugadores;
    private ArrayList<Jugadores> arraJugadores;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_equipo);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        datos = getIntent().getExtras();
        devuelveJSON = new ClaseConexion();
        arrayPeñas = new ArrayList<>();
        arrayListaJugadores = new ArrayList<>();
        arraJugadores = new ArrayList<>();
        lv = (ListView) findViewById(R.id.lv_gestion);
        nombrePeña = (TextView)findViewById(R.id.nom_peña);
        creacionPeña = (TextView)findViewById(R.id.peña_creacion);
        componentesPeña = (TextView)findViewById(R.id.peña_component);
        administrador = (TextView)findViewById(R.id.peña_administrador);
        diaPartido = (TextView)findViewById(R.id.peña_dia);
        horaPartido = (TextView)findViewById(R.id.peña_hora);
        fotoPeña = (ImageView)findViewById(R.id.peña_img);
        fotoPeña.setImageResource(R.drawable.penia);

        sp = getSharedPreferences("preferencias", Context.MODE_PRIVATE);
        editor = sp.edit();

        correoUsuario = sp.getString("us_email", correoUsuario);

        inicializarTask();
        inicializarTask2();

    }

    public void inicializarTask(){
        GestionTask task = new GestionTask();
        task.execute();
    }

    public void inicializarTask2(){
        GestionJugTask task2 = new GestionJugTask();
        task2.execute();
    }

    //muestra un dialog em el que puedes introducir el correo de un usuario ya registrado y añadirlo al equipo
    public void dialogAdd() {
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layour_add_jug);
        dialog.setCancelable(false);

        correoAdd = (EditText)dialog.findViewById(R.id.correoAdd);
        bDialogAcept = (Button)dialog.findViewById(R.id.bGuardar);
        bDialogCancel = (Button)dialog.findViewById(R.id.bCancelarFiltro);

        dialog.findViewById(R.id.bGuardar).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                //compruebo que el usuario no este ya en el equipo si no lo esta ejecuto task para añadir datos a la base de datos
                correo = correoAdd.getText().toString();
                for(int i=0; i< arrayListaJugadores.size(); i++){
                    if(arrayListaJugadores.get(i).getCorreo().equals(correo)) {
                        usuario_repetido = true;
                    }
                }

                if(usuario_repetido){
                    Snackbar.make(findViewById(android.R.id.content), R.string.usu_repe, Snackbar.LENGTH_LONG).show();
                    dialog.dismiss();
                }else {
                    AddTask task = new AddTask();
                    task.execute();
                    dialog.dismiss();
                }

            }
        });

        dialog.findViewById(R.id.bCancelarFiltro).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // menu que muestra iconos de gestion en toolbar si eres adminstrado, si no lo eres no aparecen
        getMenuInflater().inflate(R.menu.menu_gestion, menu);
        menuBar = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_modificar) {

            //Inicio nueva actividad de modificacion pasandole los datos necesarios
            Intent i = new Intent(GestionEquipo.this, EditarEquipo.class);
            i.putExtra("id", arrayPeñas.get(0).getId());
            i.putExtra("nombre", nombrePeña.getText().toString());
            i.putExtra("administrador", arrayPeñas.get(0).getAdministrador());
            i.putExtra("foto", foto);
            i.putExtra("dia", diaPartido.getText().toString());
            i.putExtra("hora", horaPartido.getText().toString());
            startActivity(i);
            return true;
        }

        if (id == R.id.action_añadir_componente) {
            usuario_repetido = false;
            correo = "";
            dialogAdd();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //task que consulta datos de equipo
    class GestionTask extends AsyncTask<String, String, JSONArray> {
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            id = datos.getInt("id");
            pDialog = new ProgressDialog(GestionEquipo.this);
            pDialog.setMessage(getResources().getString(R.string.load));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected JSONArray doInBackground(String... args) {
            try {

                HashMap<String, String> parametrosPosteriores = new HashMap<>();
                parametrosPosteriores.put("ins_sql","select p.*, j.Nombre from peña p, jugadores j where codPeña in (SELECT codPeña FROM componente_peña WHERE CodigoJug = "+"'"+correoUsuario+"') and p.CodAministrador = j.Correo and codPeña = "+id);
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
                        peña.setDiaPartido(jsonObject.getString("diaEvento"));
                        peña.setFechaCreacion(jsonObject.getString("fechaCreacion"));
                        peña.setHoraPartido(jsonObject.getString("horaEvento"));
                        peña.setAdministrador(jsonObject.getString("CodAministrador"));
                        peña.setNomAdministrador(jsonObject.getString("Nombre"));
                        peña.setRutaFoto(jsonObject.getString("rutaFoto"));
                        arrayPeñas.add(peña);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                nombrePeña.setText(arrayPeñas.get(0).getNombre().toString());
                creacionPeña.setText(arrayPeñas.get(0).getFechaCreacion().toString());
                administrador.setText(arrayPeñas.get(0).getNomAdministrador().toString());
                diaPartido.setText(arrayPeñas.get(0).getDiaPartido().toString());
                horaPartido.setText(arrayPeñas.get(0).getHoraPartido().toString());

                if(GlobalParams.administradores != null || arrayPeñas.get(0).getAdministrador().toString() != null){
                    if (arrayPeñas.get(0).getAdministrador().toString().equals(correoUsuario.toString())) {
                        menuBar.findItem(R.id.action_modificar).setVisible(true);
                        menuBar.findItem(R.id.action_añadir_componente).setVisible(true);
                    } else {
                        menuBar.findItem(R.id.action_modificar).setVisible(false);
                        menuBar.findItem(R.id.action_añadir_componente).setVisible(false);
                    }

                }else {
                     menuBar.findItem(R.id.action_modificar).setVisible(false);
                     menuBar.findItem(R.id.action_añadir_componente).setVisible(false);
        }

                if(!arrayPeñas.get(0).getRutaFoto().equals("null")) {
                    foto = GlobalParams.decodeBase64(arrayPeñas.get(0).getRutaFoto());
                    fotoPeña.setImageBitmap(GlobalParams.getRoundedRectBitmap(foto, 12));
                }


            } else {
                Snackbar.make(findViewById(android.R.id.content), "Error de conexion", Snackbar.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onCancelled() {
            pDialog.dismiss();
        }
    }

    //task que consulta los componentes de ese equipo
    class GestionJugTask extends AsyncTask<String, String, JSONArray> {
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            id = datos.getInt("id");
            pDialog = new ProgressDialog(GestionEquipo.this);
            pDialog.setMessage(getResources().getString(R.string.load));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected JSONArray doInBackground(String... args) {
            try {

                HashMap<String, String> parametrosPosteriores = new HashMap<>();
                parametrosPosteriores.put("ins_sql","select Nombre, Ruta_Foto, Correo, TipoJugador from jugadores where Correo in (SELECT CodigoJug FROM componente_peña WHERE CodPeña = "+id+")");
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
                        jugador.setTipoJug(jsonObject.getString("TipoJugador"));
                        arrayListaJugadores.add(jugador);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                GlobalParams.listaJugadores = new ArrayList<Jugadores>();
                GlobalParams.listaJugadores = arrayListaJugadores;

                ga = new GestionListAdapter(GestionEquipo.this,arrayListaJugadores);
                lv.setAdapter(ga);

                int tam = arrayListaJugadores.size();
                if(tam > 1) {
                    componentesPeña.setText(tam + " componentes");
                }else{
                    componentesPeña.setText(tam + " componente");
                }

            } else {
                Snackbar.make(findViewById(android.R.id.content), "Error de conexion", Snackbar.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onCancelled() {
            pDialog.dismiss();
        }
    }

    //task para añadir componentes al equipo seleccionado
    class AddTask extends AsyncTask<String, String, JSONArray> {
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            id = datos.getInt("id");
            usuario_repetido = false;
            usuario_registrado = false;
            pDialog = new ProgressDialog(GestionEquipo.this);
            pDialog.setMessage(getResources().getString(R.string.add));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected JSONArray doInBackground(String... args) {

            try {
                HashMap<String, String> parametrosPosteriores = new HashMap<>();
                parametrosPosteriores.put("ins_sql","select Correo from jugadores");
                jSONArray = devuelveJSON.sendRequest(GlobalParams.url_consulta, parametrosPosteriores);

                if (jSONArray != null) {
                    for (int i = 0; i < jSONArray.length(); i++) {
                        try {
                            jsonObject = jSONArray.getJSONObject(i);
                            jugador = new Jugadores();
                            jugador.setCorreo(jsonObject.getString("Correo"));
                            arraJugadores.add(jugador);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                for(int i=0; i<arraJugadores.size(); i++){
                    if(arraJugadores.get(i).getCorreo().equals(correo)){
                        usuario_registrado = true;
                    }
                }

                if(usuario_registrado) {

                    HashMap<String, String> parametrosPost = new HashMap<>();
                    parametrosPost.put("ins_sql", "INSERT INTO componente_peña(CodigoJug, CodPeña) VALUES (" + "'" + correo + "'" + "," + "'" + id + "')");
                    jsonObject = devuelveJSON.sendInsert(GlobalParams.url_insert, parametrosPost);

                    HashMap<String, String> parametrosPost2 = new HashMap<>();
                    parametrosPost2.put("ins_sql", "INSERT INTO estadisticas(CodigoJug, Goles, TarjetaAmarilla, TarjetaRoja, CodPeña, PartidosJugados, PartidosGanados, PartidosPerdidos, PartidosEmpatados, Puntos) VALUES (" + "'" + correo + "'" + ",0,0,0," +id+",0,0,0,0,0);");
                    devuelveJSON.sendInsert(GlobalParams.url_insert, parametrosPost2);

                    if(jsonObject != null) {
                        switch (jsonObject.getInt("added")) {
                            case 1:
                                Snackbar.make(findViewById(android.R.id.content), "Usuario "+correo+" se añadio correctamente al equipo "+arrayPeñas.get(0).getNombre(), Snackbar.LENGTH_LONG).show();
                                break;
                            default:
                                Snackbar.make(findViewById(android.R.id.content), "Error de conexion.", Snackbar.LENGTH_LONG).show();
                                break;
                        }
                    }

                }else{
                    usuario_registrado = false;
                    Snackbar.make(findViewById(android.R.id.content), R.string.usu_no_reg, Snackbar.LENGTH_LONG).show();

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

            ga.notifyDataSetChanged();
            inicializarTask2();
            arrayListaJugadores.clear();
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
        Intent i = new Intent(GestionEquipo.this, MainActivity.class);
        startActivity(i);
    }

    @Override
    public void onRestart() {
        super.onRestart();
        inicializarTask();
    }
}
