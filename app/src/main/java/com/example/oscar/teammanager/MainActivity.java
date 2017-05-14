package com.example.oscar.teammanager;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Base64;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.example.oscar.teammanager.Adaptadores.PeñaListAdapter;
import com.example.oscar.teammanager.Objects.Jugadores;
import com.example.oscar.teammanager.Objects.Peñas;
import com.example.oscar.teammanager.Utils.ClaseConexion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static SharedPreferences sp;
    public static SharedPreferences.Editor editor;
    private JSONArray jSONArray;
    private ClaseConexion devuelveJSON;
    private JSONObject jsonObject;
    private String url_consulta;
    private String IP_Server;
    private Jugadores jugadores;
    private Peñas peña;
    private ArrayList<Peñas> arrayPeñas;
    private ArrayList<Jugadores> arrayJugadores;
    protected String correoUsuario;
    protected TextView tvnombre, tvmail;
    protected Bitmap foto;
    protected ListView lv;
    protected DrawerLayout drawer;
    protected NavigationView navigationView;
    protected ImageView fotoMenu;
    protected LinearLayout empty_data;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        IP_Server = "http://iesayala.ddns.net/19ramajo";
        url_consulta = IP_Server + "/consulta.php";
        devuelveJSON = new ClaseConexion();
        arrayPeñas = new ArrayList<>();
        arrayJugadores = new ArrayList<>();
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        empty_data = (LinearLayout) findViewById(R.id.empty_data);

        sp = getSharedPreferences("preferencias", Context.MODE_PRIVATE);
        editor = sp.edit();

        correoUsuario = sp.getString("us_email", correoUsuario);

        lv = (ListView) findViewById(R.id.list);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView adapter, View view, int position, long arg) {
                Intent intent = new Intent(MainActivity.this, GestionEquipo.class);
                intent.putExtra("posicion",position);
                intent.putExtra("id",arrayPeñas.get(position).getId());
                startActivity(intent);
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, PartidoActivity.class);
                startActivity(i);
            }
        });


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();



        View headerView = navigationView.getHeaderView(0);
        tvnombre = (TextView) headerView.findViewById(R.id.NombreMenu);
        tvmail = (TextView) headerView.findViewById(R.id.CorreoMenu);
        fotoMenu = (CircleImageView) headerView.findViewById(R.id.FotoMenu);
        fotoMenu.setImageResource(R.mipmap.ic_launcher_round);



        headerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, PerfilActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        navigationView.setNavigationItemSelectedListener(this);

        MainTaskJug task = new MainTaskJug();
        task.execute();

        MainTaskPeña task2 = new MainTaskPeña();
        task2.execute();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_desconectar) {
            AlertDialog.Builder builderc = new AlertDialog.Builder(MainActivity.this);
            builderc.setMessage(R.string.msj_desconectar);
            builderc.setPositiveButton(getResources().getString(R.string.acept),
                    new DialogInterface.OnClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                            Intent i = new Intent(MainActivity.this, LoginActivity.class);
                            startActivity(i);
                        }
                    });
            builderc.setNegativeButton(getResources().getString(R.string.cancel), null);
            builderc.create();
            builderc.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_administrar) {

        }

        else if (id == R.id.nav_jugadores) {
            Intent i = new Intent(this, InvitarJugadorActivity.class);
            startActivity(i);
        }

        else if (id == R.id.nav_estadisticas) {

        }

        else if(id == R.id.nav_peñas){
            Intent i = new Intent(this, NuevoGrupoActivity.class);
            i.putExtra("correoUsuario",sp.getString("us_email", correoUsuario));
            startActivity(i);
        }

        else if (id == R.id.nav_multas) {

        } else if (id == R.id.nav_partidos) {

        } else if (id == R.id.nav_info) {

        } else if (id == R.id.nav_contacto) {

        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public static Bitmap decodeBase64(String input) {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }


    class MainTaskJug extends AsyncTask<String, String, JSONArray> {
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Cargando...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONArray doInBackground(String... args) {
            try {

                HashMap<String, String> parametrosPosteriores = new HashMap<>();
                parametrosPosteriores.put("ins_sql","select * from jugadores where Correo= "+"'"+correoUsuario+"'");
                jSONArray = devuelveJSON.sendRequest(url_consulta, parametrosPosteriores);

                if (jSONArray.length() > 0) {
                    return jSONArray;
                }else{
                    System.out.println("Error al obtener datos JSON");
                    Snackbar.make(findViewById(android.R.id.content), "Usuario o contraseña incorrectos  ", Snackbar.LENGTH_LONG).show();
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
                        jugadores = new Jugadores();
                        jugadores.setNombre(jsonObject.getString("Nombre"));
                        jugadores.setEdad(jsonObject.getString("Edad"));
                        jugadores.setTipoJug(jsonObject.getString("TipoJugador"));
                        jugadores.setCorreo(jsonObject.getString("Correo"));
                        jugadores.setPass(jsonObject.getString("Pass"));
                        jugadores.setRutaFoto(jsonObject.getString("Ruta_Foto"));
                        arrayJugadores.add(jugadores);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                tvnombre.setText(arrayJugadores.get(0).getNombre().toString());
                tvmail.setText(arrayJugadores.get(0).getCorreo().toString());

                if(arrayJugadores.get(0).getRutaFoto() != null) {
                    foto = decodeBase64(arrayJugadores.get(0).getRutaFoto());
                    fotoMenu.setImageBitmap(foto);
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

    class MainTaskPeña extends AsyncTask<String, String, JSONArray> {
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Cargando...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONArray doInBackground(String... args) {
            try {

                HashMap<String, String> parametrosPosteriores = new HashMap<>();
                parametrosPosteriores.put("ins_sql","select * from peña where codPeña in (SELECT codPeña FROM componente_peña WHERE CodigoJug = "+"'"+correoUsuario+"')");
                jSONArray = devuelveJSON.sendRequest(url_consulta, parametrosPosteriores);

                if (jSONArray.length() > 0) {
                    return jSONArray;
                }else{
                    System.out.println("Error al obtener datos JSON");
                    Snackbar.make(findViewById(android.R.id.content), "Usuario o contraseña incorrectos  ", Snackbar.LENGTH_LONG).show();
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
                        peña.setHoraPartido(jsonObject.getString("horaEvento"));
                        peña.setAdministrador(jsonObject.getString("CodAministrador"));
                        peña.setRutaFoto(jsonObject.getString("rutaFoto"));
                        arrayPeñas.add(peña);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }


                lv.setAdapter(new PeñaListAdapter(MainActivity.this, arrayPeñas));

            } else {
                empty_data.setVisibility(View.VISIBLE);
                Snackbar.make(findViewById(android.R.id.content), "Error de conexion", Snackbar.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onCancelled() {
            pDialog.dismiss();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    public void onBackPressed() {

        //Si el drawer esta abierto al pulsar en el botón de atras lo cierra:

        if (drawer.isDrawerOpen(GravityCompat.START)) {

            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                super.onBackPressed();
            }

            //De lo contrario muestra un mensaje al usuario:
        } else {
            AlertDialog.Builder builderc = new AlertDialog.Builder(MainActivity.this);
            builderc.setMessage(R.string.msj_cerrar);
            builderc.setPositiveButton(getResources().getString(R.string.acept),
                    new DialogInterface.OnClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                        public void onClick(DialogInterface dialog, int which) {
                            //Salimos de la app por completo
                            finishAffinity();
                        }
                    });
            builderc.setNegativeButton(getResources().getString(R.string.cancel), null);
            builderc.create();
            builderc.show();
        }
    }


}