package com.example.oscar.teammanager;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;

public class GestionEquipo extends AppCompatActivity {

    public static SharedPreferences sp;
    public static SharedPreferences.Editor editor;
    private Bundle datos;
    private String url_consulta, url_insert;
    private String IP_Server;
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
    boolean usuario_repetido;


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
        IP_Server = "http://iesayala.ddns.net/19ramajo";
        url_consulta = IP_Server + "/consulta.php";
        url_insert = IP_Server + "/prueba.php";
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

    public void dialogAdd() {
        //Creacion dialog de filtrado
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layour_add_jug);
        dialog.setCancelable(false);

        correoAdd = (EditText)dialog.findViewById(R.id.correoAdd);
        bDialogAcept = (Button)dialog.findViewById(R.id.bGuardar);
        bDialogCancel = (Button)dialog.findViewById(R.id.bCancelarFiltro);

        //Accion de boton guardar filtrado
        dialog.findViewById(R.id.bGuardar).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                correo = correoAdd.getText().toString();
                System.out.println("----------correo"+ correo);
                AddTask task = new AddTask();
                task.execute();
                dialog.dismiss();
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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_gestion, menu);
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
            return true;
        }

        if (id == R.id.action_añadir_componente) {
            dialogAdd();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static Bitmap decodeBase64(String input) {

        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    public Bitmap getRoundedRectBitmap(Bitmap bitmap, int pixels) {
        result = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        canvas = new Canvas(result);

        color = 0xff424242;
        paint = new Paint();
        rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        rectF = new RectF(rect);
        roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return result;
    }

    class GestionTask extends AsyncTask<String, String, JSONArray> {
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            id = datos.getInt("id");
            pDialog = new ProgressDialog(GestionEquipo.this);
            pDialog.setMessage("Cargando...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected JSONArray doInBackground(String... args) {
            try {

                HashMap<String, String> parametrosPosteriores = new HashMap<>();
                parametrosPosteriores.put("ins_sql","select * from peña where codPeña = "+id);
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
                        peña.setId(jsonObject.getInt("codPeña"));
                        peña.setNombre(jsonObject.getString("nomPeña"));
                        peña.setDiaPartido(jsonObject.getString("diaEvento"));
                        peña.setFechaCreacion(jsonObject.getString("fechaCreacion"));
                        peña.setHoraPartido(jsonObject.getString("horaEvento"));
                        peña.setAdministrador(jsonObject.getString("CodAministrador"));
                        peña.setRutaFoto(jsonObject.getString("rutaFoto"));
                        arrayPeñas.add(peña);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                nombrePeña.setText(arrayPeñas.get(0).getNombre().toString());
                creacionPeña.setText(arrayPeñas.get(0).getFechaCreacion().toString());
                administrador.setText(arrayPeñas.get(0).getAdministrador().toString());
                diaPartido.setText(arrayPeñas.get(0).getDiaPartido().toString());
                horaPartido.setText(arrayPeñas.get(0).getHoraPartido().toString());

                if(arrayPeñas.get(0).getRutaFoto() != null) {
                    foto = decodeBase64(arrayPeñas.get(0).getRutaFoto());
                    fotoPeña.setImageBitmap(getRoundedRectBitmap(foto, 12));
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

    class GestionJugTask extends AsyncTask<String, String, JSONArray> {
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            id = datos.getInt("id");
            pDialog = new ProgressDialog(GestionEquipo.this);
            pDialog.setMessage("Cargando...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected JSONArray doInBackground(String... args) {
            try {

                HashMap<String, String> parametrosPosteriores = new HashMap<>();
                parametrosPosteriores.put("ins_sql","select Nombre, Ruta_Foto, Correo from jugadores where Correo in (SELECT CodigoJug FROM componente_peña WHERE CodPeña = "+id+")");
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
                        arrayListaJugadores.add(jugador);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                lv.setAdapter(new GestionListAdapter(GestionEquipo.this, arrayListaJugadores));
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

    class AddTask extends AsyncTask<String, String, JSONArray> {
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            id = datos.getInt("id");
            usuario_repetido = false;
            pDialog = new ProgressDialog(GestionEquipo.this);
            pDialog.setMessage("Añadiendo...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected JSONArray doInBackground(String... args) {

            try {
                for(int i = 0; i <= arrayListaJugadores.size()-1; i++){
                    if (arrayListaJugadores.get(i).getCorreo().toString().equals(correo)){
                        usuario_repetido = true;
                    }
                }
                if(usuario_repetido){
                    Snackbar.make(findViewById(android.R.id.content), "Este usuario ya es componente de este equipo", Snackbar.LENGTH_LONG).show();
                } else {

                    HashMap<String, String> parametrosPosteriores = new HashMap<>();
                    parametrosPosteriores.put("ins_sql", "select * from jugadores where Correo = " + "'" + correo + "'");
                    jSONArray = devuelveJSON.sendRequest(url_consulta, parametrosPosteriores);

                    if (jSONArray.length() > 0) {

                        for (int i = 0; i < jSONArray.length(); i++) {
                            try {
                                jsonObject = jSONArray.getJSONObject(i);
                                jugador = new Jugadores();
                                jugador.setId(jsonObject.getInt("CodigoJug"));
                                jugador.setNombre(jsonObject.getString("Nombre"));
                                jugador.setCorreo(jsonObject.getString("Correo"));
                                arraJugadores.add(jugador);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        HashMap<String, String> parametrosPost = new HashMap<>();
                        parametrosPost.put("ins_sql", "INSERT INTO componente_peña(CodigoJug, CodPeña) VALUES (" + "'" + arraJugadores.get(0).getCorreo().toString() + "'" + "," + "'" + id + "')");
                        devuelveJSON.sendInsert(url_insert, parametrosPost);

                        HashMap<String, String> parametrosPost2 = new HashMap<>();
                        parametrosPost2.put("ins_sql", "INSERT INTO estadisticas(CodigoJug, Goles, TarjetaAmarilla, TarjetaRoja, CodPeña) VALUES (" + "'"+correo+"'"+ ",0,0,0," +id+");");
                        System.out.println("-------------------"+url_insert+parametrosPost2);
                        devuelveJSON.sendInsert(url_insert, parametrosPost2);

                    } else {
                        System.out.println("Error al obtener datos JSON");
                        Snackbar.make(findViewById(android.R.id.content), "Este usuario no está aún resgistrado en la aplicación", Snackbar.LENGTH_LONG).show();
                    }
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
            arrayListaJugadores.clear();
            inicializarTask2();
        }

        @Override
        protected void onCancelled() {
            pDialog.dismiss();
        }
    }

}
