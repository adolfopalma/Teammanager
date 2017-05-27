package com.example.oscar.teammanager;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.oscar.teammanager.Adaptadores.Adapter_List_perfil;
import com.example.oscar.teammanager.Adaptadores.PeñaListAdapter;
import com.example.oscar.teammanager.Objects.Estadisticas;
import com.example.oscar.teammanager.Objects.Jugadores;
import com.example.oscar.teammanager.Objects.Peñas;
import com.example.oscar.teammanager.Utils.ClaseConexion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by ptmarketing05 on 09/05/2017.
 */

public class PerfilActivity extends Activity {

    public static SharedPreferences sp;
    public static SharedPreferences.Editor editor;
    String correoUsuario;
    private JSONArray jSONArray;
    private ClaseConexion devuelveJSON;
    private JSONObject jsonObject;
    private String url_consulta;
    private String IP_Server;
    protected Jugadores jugador;
    protected Peñas peña;
    protected Estadisticas estadistica;
    private ArrayList<Jugadores> arrayJugadores;
    private ArrayList<Estadisticas> arrayEstadisticas;
    private ArrayList<Peñas> arrayPeñas;
    protected TextView goles,rojas,amarillas,nombre,edad,correo,tipo;
    protected ImageView foto;
    private Bitmap result;
    protected ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        IP_Server = "http://iesayala.ddns.net/19ramajo";
        url_consulta = IP_Server + "/consulta.php";
        devuelveJSON = new ClaseConexion();
        arrayJugadores = new ArrayList<>();
        arrayEstadisticas = new ArrayList<>();
        arrayPeñas = new ArrayList<>();
        goles = (TextView)findViewById(R.id.tvGoles);
        rojas = (TextView)findViewById(R.id.tvRojas);
        amarillas = (TextView)findViewById(R.id.tvAmarillas);
        nombre = (TextView)findViewById(R.id.tvNomPerfil);
        edad = (TextView)findViewById(R.id.tvEdadPerfil);
        correo = (TextView)findViewById(R.id.tvCorreoPerfil);
        tipo = (TextView)findViewById(R.id.tvTipoPerfil);
        foto = (CircleImageView) findViewById(R.id.ivFotoPerfil);
        lv = (ListView) findViewById(R.id.lvPerfil);

        sp = getSharedPreferences("preferencias", Context.MODE_PRIVATE);
        editor = sp.edit();

        correoUsuario = sp.getString("us_email", correoUsuario);

        PerfilTask task = new PerfilTask();
        task.execute();

        TotalTask task1 = new TotalTask();
        task1.execute();

        EquipoTask task2 = new EquipoTask();
        task2.execute();
    }

    public static Bitmap decodeBase64(String input) {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    class PerfilTask extends AsyncTask<String, String, JSONArray> {
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(PerfilActivity.this);
            pDialog.setMessage("Cargando...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONArray doInBackground(String... args) {
            try {

                HashMap<String, String> parametrosPosteriores = new HashMap<>();
                parametrosPosteriores.put("ins_sql","select * from jugadores where Correo = "+"'"+correoUsuario+"'");
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
                        jugador = new Jugadores();
                        jugador.setNombre(jsonObject.getString("Nombre"));
                        jugador.setEdad(jsonObject.getString("Edad"));
                        jugador.setCorreo(jsonObject.getString("Correo"));
                        jugador.setTipoJug(jsonObject.getString("TipoJugador"));
                        jugador.setRutaFoto(jsonObject.getString("Ruta_Foto"));
                        arrayJugadores.add(jugador);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                nombre.setText(arrayJugadores.get(0).getNombre());
                edad.setText(arrayJugadores.get(0).getEdad());
                correo.setText(arrayJugadores.get(0).getCorreo());
                tipo.setText(arrayJugadores.get(0).getTipoJug());
                if(arrayJugadores.get(0).getRutaFoto() != null) {
                    result = decodeBase64(arrayJugadores.get(0).getRutaFoto());
                    foto.setImageBitmap(result);
                }


            } else {
                Snackbar.make(findViewById(android.R.id.content), "Error de conexion ", Snackbar.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onCancelled() {
            pDialog.dismiss();
        }
    }

    class TotalTask extends AsyncTask<String, String, JSONArray> {
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected JSONArray doInBackground(String... args) {
            try {

                HashMap<String, String> parametrosPosteriores = new HashMap<>();
                parametrosPosteriores.put("ins_sql","select sum(Goles),sum(TarjetaAmarilla),sum(TarjetaRoja) from estadisticas where CodigoJug = "+"'"+correoUsuario+"'");
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

            if (json != null) {
                for (int i = 0; i < json.length(); i++) {
                    try {
                        jsonObject = json.getJSONObject(i);
                        estadistica = new Estadisticas();
                        estadistica.setGoles(jsonObject.getInt("sum(Goles)"));
                        estadistica.setTarjetaAmarilla(jsonObject.getInt("sum(TarjetaAmarilla)"));
                        estadistica.setTarjetaRoja(jsonObject.getInt("sum(TarjetaRoja)"));
                        arrayEstadisticas.add(estadistica);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                goles.setText(String.valueOf(arrayEstadisticas.get(0).getGoles()));
                amarillas.setText(String.valueOf(arrayEstadisticas.get(0).getTarjetaAmarilla()));
                rojas.setText(String.valueOf(arrayEstadisticas.get(0).getTarjetaRoja()));
            } else {
                Snackbar.make(findViewById(android.R.id.content), "Error de conexion ", Snackbar.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onCancelled() {
        }
    }

    class EquipoTask extends AsyncTask<String, String, JSONArray> {
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected JSONArray doInBackground(String... args) {
            try {

                HashMap<String, String> parametrosPosteriores = new HashMap<>();
                parametrosPosteriores.put("ins_sql","SELECT c.CodPeña , p.nomPeña, p.rutaFoto FROM peña p, componente_peña c WHERE c.CodigoJug = "+"'"+correoUsuario+"' and c.CodPeña = p.codPeña");
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
                        peña.setNombre(jsonObject.getString("nomPeña"));
                        peña.setRutaFoto(jsonObject.getString("rutaFoto"));
                        arrayPeñas.add(peña);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                lv.setAdapter(new Adapter_List_perfil(PerfilActivity.this, arrayPeñas));

            } else {
                Snackbar.make(findViewById(android.R.id.content), "Error de conexion ", Snackbar.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onCancelled() {
        }
    }
}
