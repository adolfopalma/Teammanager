package com.example.oscar.teammanager;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import com.example.oscar.teammanager.Adaptadores.GestionListAdapter;
import com.example.oscar.teammanager.Objects.Jugadores;
import com.example.oscar.teammanager.Utils.ClaseConexion;
import com.example.oscar.teammanager.Utils.GlobalParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

//Clase para editar los equipo creados y eliminar componentes de el equipo
public class EditarEquipo extends AppCompatActivity {

    protected Bundle extras;
    protected EditText nombreEquipo;
    protected Button gestionar;
    protected ListView lv;
    protected TextView horaPartido,tvInfo;
    protected ImageView fotoEquipo;
    private static int ACT_GALERIA = 1;
    private static Uri fotoGaleria;
    private static InputStream is;
    private static BufferedInputStream bis;
    private static Bitmap bm, foto;
    protected String encodedImageData;
    protected Spinner spinner;
    protected FloatingActionButton fab;
    private ClaseConexion devuelveJSON;
    private JSONObject jsonObject;
    protected String fot;
    protected int codPeña;
    protected ArrayList<Jugadores> jugadores;
    protected String CodUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_equipo);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        devuelveJSON = new ClaseConexion();
        jugadores = new ArrayList<>();
        nombreEquipo = (EditText)findViewById(R.id.nom_peña);
        horaPartido = (TextView)findViewById(R.id.peña_hora);
        fotoEquipo = (ImageView) findViewById(R.id.peña_img);
        spinner = (Spinner)findViewById(R.id.spinner4);
        gestionar = (Button)findViewById(R.id.bGestionar);
        lv = (ListView)findViewById(R.id.lv_gestion);
        tvInfo = (TextView)findViewById(R.id.tvInfo);


        //Obtengo datos necesarios de la activiy anterior
        extras = getIntent().getExtras();
        codPeña = extras.getInt("id");
        String nombre = extras.getString("nombre");
        String dia = extras.getString("dia");
        String hora = extras.getString("hora");
        final String administrador = extras.getString("administrador");
        foto = (Bitmap) extras.get("foto");

        nombreEquipo.setText(nombre);
        horaPartido.setText(hora);
        if(foto == null){
            fotoEquipo.setImageResource(R.drawable.penia);
            fot = null;
        }else {
            fotoEquipo.setImageBitmap(GlobalParams.getRoundedRectBitmap(foto, 12));
            encodedImageData = GlobalParams.getEncoded64ImageStringFromBitmap(foto);
            fot = encodedImageData;
        }
        rellenaSpinner();

        //swicth para seleccionar el dato correcto de el equipo seleccionado
        switch (dia) {
            case "Lunes":
                spinner.setSelection(0);
                break;
            case "Martes":
                spinner.setSelection(1);
                break;
            case "Miercoles":
                spinner.setSelection(2);
                break;
            case "Jueves":
                spinner.setSelection(3);
                break;
            case "Viernes":
                spinner.setSelection(4);
                break;
            case "Sabado":
                spinner.setSelection(5);
                break;
            case "Domingo":
                spinner.setSelection(6);
                break;
        }

        //obtengo los datos globales previamente pasados desde la activity anterior y se lo paso al adaptador
        jugadores = GlobalParams.listaJugadores;
        lv.setAdapter(new GestionListAdapter(EditarEquipo.this, jugadores));

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.GONE);

        //Pulsacion prolongada sobre un item para eliminarlo
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int pos, long id) {
                CodUsuario = jugadores.get(pos).getCorreo().toString();

                //Compruebo si es administrador, si lo es no puede ser eliminado del equipo, si no lo es lo elimino de la lista
                //y ejecuto la task que borra los datos
                if(jugadores.get(pos).getCorreo().equals(administrador)){
                    final AlertDialog.Builder builderc = new AlertDialog.Builder(EditarEquipo.this);
                    builderc.setMessage(R.string.jugador_administrador);
                    builderc.setPositiveButton(getResources().getString(R.string.acept),
                            new DialogInterface.OnClickListener() {
                                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                                public void onClick(DialogInterface dialog, int which) {
                                    builderc.setCancelable(true);
                                }
                            });
                    builderc.create();
                    builderc.show();
                }else {
                    jugadores.remove(pos);
                    lv.setAdapter(new GestionListAdapter(EditarEquipo.this, jugadores));
                    Snackbar.make(findViewById(android.R.id.content), "Jugador eliminado", Snackbar.LENGTH_LONG).show();
                    BorrarComponentTask task = new BorrarComponentTask();
                    task.execute();
                }

                //Si no hay componente oculto lista y muestro mensaje
                if(jugadores.size() < 0){
                    lv.setVisibility(View.GONE);
                    tvInfo.setText(R.string.no_comp);
                }
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_editar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_editar) {

            //Ejecuto task que actualiza los datos
            EditarTask task = new EditarTask();
            task.execute();
            return true;
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_borrar) {

            //Muestro mensaje de alerta para quw confirme usuario o cancele y si acepta ejecuto task que borra equipo
            AlertDialog.Builder builderc = new AlertDialog.Builder(EditarEquipo.this);
            builderc.setMessage(getResources().getString(R.string.equipo_delete));
            builderc.setPositiveButton(getResources().getString(R.string.acept),
                    new DialogInterface.OnClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                        public void onClick(DialogInterface dialog, int which) {
                            BorrarTask task = new BorrarTask();
                            task.execute();
                            finish();
                            Intent i = new Intent(EditarEquipo.this, MainActivity.class);
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

    //Metodo onclick que le paso al boton gestionar, si lo pulso oculto boton y muestro lista de componentes
    public void onClick(View v){
        gestionar.setVisibility(View.GONE);
        tvInfo.setText(R.string.componentes);
        lv.setVisibility(View.VISIBLE);
    }

    //Metodo para seleccionar foto desde galeria
    public void seleccionFoto(View v){
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(intent, ACT_GALERIA);
    }

    //Metodo que obtiene la foto tomado la convierte y la muestra en imageView.
    //tambie codifico la imagen para pasarla a la base de datos
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ACT_GALERIA && resultCode == RESULT_OK) {
            fotoGaleria = data.getData();
            try {
                is = getContentResolver().openInputStream(fotoGaleria);
                bis = new BufferedInputStream(is);
                bm = BitmapFactory.decodeStream(bis);
                bm = bm.createScaledBitmap(bm, 150, 150, true);
                fotoEquipo.setImageBitmap(GlobalParams.getRoundedRectBitmap(bm,12));

                encodedImageData = GlobalParams.getEncoded64ImageStringFromBitmap(bm);
                fot = encodedImageData;
            } catch (FileNotFoundException e) {
            }
        }
    }

    //Metodo para deplegar dialog con reloj para seleccionar hora
    public void insertahora(View v){
        // Get Current time
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);


        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        String minuto;
                        if(minute <= 10) minuto = "0" + minute;
                        else minuto = ""+ minute;

                        horaPartido.setText(hourOfDay + ":" + minuto);
                    }
                }, hour, minute, false);

        timePickerDialog.show();
    }

    //Relleno spinner de tipo de jugador
    public void rellenaSpinner(){
        List<String> tipoJugadores = new ArrayList();
        tipoJugadores.add("Lunes");
        tipoJugadores.add("Martes");
        tipoJugadores.add("Miercoles");
        tipoJugadores.add("Jueves");
        tipoJugadores.add("Viernes");
        tipoJugadores.add("Sabado");
        tipoJugadores.add("Domingo");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_plegado, tipoJugadores);
        adapter.setDropDownViewResource(R.layout.spinner_desplegado);
        spinner.setAdapter(adapter);
    }


    //task para actualizar datos de equipo
    class EditarTask extends AsyncTask<String, String, JSONArray> {
        private ProgressDialog pDialog;
        String nombre;
        String dia;
        String hora;


        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(EditarEquipo.this);
            pDialog.setMessage(getResources().getString(R.string.act));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();

            nombre = nombreEquipo.getText().toString();
            dia = spinner.getSelectedItem().toString();
            hora = horaPartido.getText().toString();
        }

        @Override
        protected JSONArray doInBackground(String... args) {
            try {

                HashMap<String, String> parametrosPost = new HashMap<>();
                parametrosPost.put("ins_sql", "UPDATE peña set nomPeña = "+"'"+nombre+"',"+"  diaEvento = "+"'"+dia+"',"+"  horaEvento = "+"'"+hora+"',"+ " rutaFoto = " +"'"+fot+"' where  codPeña = "+codPeña);
                jsonObject = devuelveJSON.sendInsert(GlobalParams.url_insert, parametrosPost);

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(JSONArray json) {
            if (pDialog != null && pDialog.isShowing()) {
                pDialog.dismiss();
            }

            if(jsonObject != null) {
                try {
                    switch (jsonObject.getInt("added")){
                        case 1:
                            pDialog.dismiss();
                            AlertDialog.Builder builders = new AlertDialog.Builder(EditarEquipo.this);
                            builders.setMessage(getResources().getString(R.string.equip_act));
                            builders.setPositiveButton(getResources().getString(R.string.acept),
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                            Intent i = new Intent(EditarEquipo.this, MainActivity.class);
                                            startActivity(i);
                                        }
                                    });
                            builders.setCancelable(false);
                            builders.create();
                            builders.show();
                            break;
                        default:
                            pDialog.dismiss();
                            Snackbar.make(findViewById(android.R.id.content), "Error.", Snackbar.LENGTH_LONG).show();
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else{
                pDialog.dismiss();
                System.out.println("OBJETO NULO.");
                Snackbar.make(findViewById(android.R.id.content), "Error al conectar.", Snackbar.LENGTH_LONG).show();
            }

        }

        @Override
        protected void onCancelled() {
            Snackbar.make(findViewById(android.R.id.content), "Error actualizando datos.", Snackbar.LENGTH_LONG).show();

        }
    }

    //task que borra equipo
    class BorrarTask extends AsyncTask<String, String, JSONArray> {
        private ProgressDialog pDialog;



        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(EditarEquipo.this);
            pDialog.setMessage(getResources().getString(R.string.borra));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected JSONArray doInBackground(String... args) {
            try {

                HashMap<String, String> parametrosPost = new HashMap<>();
                parametrosPost.put("ins_sql"," delete from peña where codPeña = "+codPeña);
                devuelveJSON.sendInsert(GlobalParams.url_insert, parametrosPost);

                HashMap<String, String> parametrosPost2 = new HashMap<>();
                parametrosPost2.put("ins_sql"," delete from administrador where codPeña = "+codPeña);
                devuelveJSON.sendInsert(GlobalParams.url_insert, parametrosPost2);

                HashMap<String, String> parametrosPost3 = new HashMap<>();
                parametrosPost3.put("ins_sql"," delete from componente_peña where codPeña = "+codPeña);
                devuelveJSON.sendInsert(GlobalParams.url_insert, parametrosPost3);

                HashMap<String, String> parametrosPost4 = new HashMap<>();
                parametrosPost4.put("ins_sql"," delete from estadisticas where codPeña = "+codPeña);
                devuelveJSON.sendInsert(GlobalParams.url_insert, parametrosPost4);

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
        protected void onCancelled() {
            Snackbar.make(findViewById(android.R.id.content), "Error.", Snackbar.LENGTH_LONG).show();

        }
    }

    //task que borra componente del equipo
    class BorrarComponentTask extends AsyncTask<String, String, JSONArray> {
        private ProgressDialog pDialog;



        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(EditarEquipo.this);
            pDialog.setMessage(getResources().getString(R.string.borra));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected JSONArray doInBackground(String... args) {
            try {

                HashMap<String, String> parametrosPost = new HashMap<>();
                parametrosPost.put("ins_sql"," delete from componente_peña where CodigoJug = "+"'"+CodUsuario+"' and CodPeña = "+codPeña);
                devuelveJSON.sendInsert(GlobalParams.url_insert, parametrosPost);

                HashMap<String, String> parametrosPost2 = new HashMap<>();
                parametrosPost2.put("ins_sql"," delete from estadisticas where CodigoJug = "+"'"+CodUsuario+"' and codPeña = "+codPeña);
                devuelveJSON.sendInsert(GlobalParams.url_insert, parametrosPost2);

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
        protected void onCancelled() {
            Snackbar.make(findViewById(android.R.id.content), "Error.", Snackbar.LENGTH_LONG).show();

        }
    }

}
