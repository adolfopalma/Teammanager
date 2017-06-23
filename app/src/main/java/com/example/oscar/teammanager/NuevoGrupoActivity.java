package com.example.oscar.teammanager;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import com.example.oscar.teammanager.Objects.Peñas;
import com.example.oscar.teammanager.Utils.ClaseConexion;
import com.example.oscar.teammanager.Utils.GlobalParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

//clase que crea un nuevo equipo
public class NuevoGrupoActivity extends AppCompatActivity{

    //Declaracion de variables
    public static SharedPreferences sp;
    public static SharedPreferences.Editor editor;
    protected TextView horaPartido,horaPeña;
    private static int ACT_GALERIA = 1;
    private static Uri fotoGaleria;
    private static InputStream is;
    private static BufferedInputStream bis;
    private static Bitmap bm;
    private String horaRuta;
    private JSONArray jSONArray;
    private ClaseConexion devuelveJSON;
    private JSONObject jsonObject;
    private String correoUsuario;
    private Peñas peña;
    private ArrayList<Peñas> listaPeñas;
    private ProgressDialog pDialog;
    protected ImageView foto;
    protected EditText nombrePeña;
    protected String encodedImageData;
    protected Spinner spinner;
    protected LinearLayout ln;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_equipo);

        //Inicializo variables
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        sp = getSharedPreferences("preferencias", Context.MODE_PRIVATE);
        editor = sp.edit();
        devuelveJSON = new ClaseConexion();
        foto =(ImageView) findViewById(R.id.peña_img);
        foto.setImageResource(R.drawable.camera);
        SimpleDateFormat sdf = new SimpleDateFormat("hh-mm-dd-MM-yyyy");
        horaRuta = sdf.format(new Date());
        horaPartido = (TextView)findViewById(R.id.peña_hora);
        nombrePeña = (EditText)findViewById(R.id.nom_peña);
        horaPeña = (TextView)findViewById(R.id.peña_hora);
        listaPeñas = new ArrayList<>();
        ln= (LinearLayout)findViewById(R.id.linear);
        ln.setVisibility(View.GONE);
        spinner = (Spinner)findViewById(R.id.spinner4);
        rellenaSpinner();

        //Obtengo correo del usuario de las preferencias
        correoUsuario = sp.getString("us_email", correoUsuario);


        //boton flotante con la accion, en el compruebo que los campos obligatorios esten rellenos y si tenemos conexion
        //y inicio la consulta
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(nombrePeña.getText().toString().equals("")){
                    Snackbar.make(findViewById(android.R.id.content), R.string.campos_oblig, Snackbar.LENGTH_SHORT).show();
                    nombrePeña.setHintTextColor(Color.RED);
                    nombrePeña.setHint(R.string.oblig);
                }else if(!ClaseConexion.compruebaConexion(NuevoGrupoActivity.this)){
                    Snackbar.make(findViewById(android.R.id.content), R.string.conexion_limi, Snackbar.LENGTH_LONG).show();
                }else{
                    //Ejecuto Asyntask para consultar
                    CompruebaTask task = new CompruebaTask();
                    task.execute();
                }
            }
        });
    }


    //Metodo para deplegar dialog con reloj para seleccionar hora
    public void insertahora(View v){
        // Get Current time
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);


        TimePickerDialog timePickerDialog = new TimePickerDialog(NuevoGrupoActivity.this,
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

    //Metodo para seleccionar foto desde galeria
    public void seleccionFoto(View v){
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(intent, ACT_GALERIA);
    }

    //Relleno spinner de tipo de jugador
    public void rellenaSpinner(){
        List<String> tipoJugadores = new ArrayList();
        tipoJugadores.add(getResources().getString(R.string.lunes));
        tipoJugadores.add(getResources().getString(R.string.martes));
        tipoJugadores.add(getResources().getString(R.string.miercoles));
        tipoJugadores.add(getResources().getString(R.string.jueves));
        tipoJugadores.add(getResources().getString(R.string.viernes));
        tipoJugadores.add(getResources().getString(R.string.sabado));
        tipoJugadores.add(getResources().getString(R.string.domingo));

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_plegado, tipoJugadores);
        adapter.setDropDownViewResource(R.layout.spinner_desplegado);
        spinner.setAdapter(adapter);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ACT_GALERIA && resultCode == RESULT_OK) {
            fotoGaleria = data.getData();
            try {
                is = getContentResolver().openInputStream(fotoGaleria);
                bis = new BufferedInputStream(is);
                bm = BitmapFactory.decodeStream(bis);
                bm = bm.createScaledBitmap(bm, 150, 150, true);
                foto.setImageBitmap(GlobalParams.getRoundedRectBitmap(bm,12));

                encodedImageData =GlobalParams.getEncoded64ImageStringFromBitmap(bm);
            } catch (FileNotFoundException e) {
            }
        }
    }


    //Asynctask que comprueba si el la peña esta registrada con el nombre introducido y administrador que la crea
    //Si no esta creada  ejecuto asyntask para registrar peña, si no muestro un mensaje
    class CompruebaTask extends AsyncTask<String, String, JSONArray> {
        String nombre = nombrePeña.getText().toString();
        boolean datos = false;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(NuevoGrupoActivity.this);
            pDialog.setMessage(getResources().getString(R.string.creando));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected JSONArray doInBackground(String... args) {
            try {
                HashMap<String, String> parametrosPosteriores = new HashMap<>();
                parametrosPosteriores.put("ins_sql","select CodAministrador, nomPeña from peña where nomPeña= "+"'"+nombre+"'"+" and "+"CodAministrador= "+"'"+correoUsuario+"'");
                jSONArray = devuelveJSON.sendRequest(GlobalParams.url_consulta, parametrosPosteriores);


                if(jSONArray.length() == 0)
                    datos = false;
                else datos = true;

            } catch (Exception e) {
                datos = true;
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {

            if(datos){
                pDialog.dismiss();
                AlertDialog.Builder builders = new AlertDialog.Builder(NuevoGrupoActivity.this);
                builders.setMessage(getResources().getString(R.string.equipo_repe));
                builders.setPositiveButton(getResources().getString(R.string.cambiar),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                pDialog.dismiss();
                            }
                        });
                builders.setCancelable(false);
                builders.create();
                builders.show();
            }else{
                RegistroPeñaTask task = new RegistroPeñaTask();
                task.execute();
            }
        }
    }


    //Asynctask para insertar tabla en la base de datos
    class RegistroPeñaTask extends AsyncTask<String, String, JSONArray> {
        String nombre = nombrePeña.getText().toString();
        String dia = spinner.getSelectedItem().toString();
        String hora = horaPeña.getText().toString();
        String rutaFoto = encodedImageData;


        @Override
        protected JSONArray doInBackground(String... args) {
            try {
                HashMap<String, String> parametrosPost = new HashMap<>();
                parametrosPost.put("ins_sql", "INSERT INTO peña(nomPeña,CodAministrador,fechaCreacion,diaEvento,horaEvento,rutaFoto) VALUES (" + "'" + nombre + "'" + "," + "'" + correoUsuario + "'" + "," + "'" + horaRuta + "'" + "," + "'" + dia + "'" + "," + "'" + hora + "'" + "," + "'" + rutaFoto + "')");
                jsonObject = devuelveJSON.sendInsert(GlobalParams.url_insert, parametrosPost);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(JSONArray json) {
            if(jsonObject != null) {
                try {
                    switch (jsonObject.getInt("added")){
                        case 1:
                            pDialog.dismiss();
                            AlertDialog.Builder builders = new AlertDialog.Builder(NuevoGrupoActivity.this);
                            builders.setMessage(getResources().getString(R.string.equipo_creado));
                            builders.setPositiveButton(getResources().getString(R.string.acept),
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                            Intent i = new Intent(NuevoGrupoActivity.this, MainActivity.class);
                                            startActivity(i);
                                        }
                                    });
                            builders.setCancelable(false);
                            builders.create();
                            builders.show();
                            break;
                        default:
                            pDialog.dismiss();
                            Snackbar.make(findViewById(android.R.id.content), R.string.error_equipo, Snackbar.LENGTH_LONG).show();
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else{
                pDialog.dismiss();
                System.out.println("OBJETO NULO.");
                Snackbar.make(findViewById(android.R.id.content), R.string.error_conect, Snackbar.LENGTH_LONG).show();
            }
            AsignarPeñaTask task = new AsignarPeñaTask();
            task.execute();
        }

        @Override
        protected void onCancelled() {
            pDialog.dismiss();
        }
    }


    //Asynctask que consulta datos de la peña seleccionada y los guardo en la base de datos en tablas requeridas
    class AsignarPeñaTask extends AsyncTask<String, String, JSONArray> {
        String nombre = nombrePeña.getText().toString();

        @Override
        protected JSONArray doInBackground(String... args) {
            try {

                HashMap<String, String> parametrosPost2 = new HashMap<>();
                parametrosPost2.put("ins_sql","select * from peña where nomPeña= "+"'"+nombre+"'"+" and "+"CodAministrador= "+"'"+correoUsuario+"'");
                jSONArray = devuelveJSON.sendRequest(GlobalParams.url_consulta, parametrosPost2);

                if (jSONArray != null) {

                    try {

                        for (int i = 0; i < jSONArray.length(); i++) {
                            try {
                                jsonObject = jSONArray.getJSONObject(i);
                                peña = new Peñas();
                                peña.setId(jsonObject.getInt("codPeña"));
                                peña.setAdministrador(jsonObject.getString("CodAministrador"));
                                peña.setNombre(jsonObject.getString("nomPeña"));
                                listaPeñas.add(peña);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        int cod = listaPeñas.get(0).getId();


                        //inserto en tabla componentes el usuario y la peña
                        HashMap<String, String> parametrosPost = new HashMap<>();
                        parametrosPost.put("ins_sql","INSERT INTO componente_peña(CodigoJug, CodPeña) VALUES (" + "'" + correoUsuario + "'" + "," + cod + ")");
                        devuelveJSON.sendInsert(GlobalParams.url_insert, parametrosPost);

                        //inserto en la tabla administrador el administrador y peña
                        HashMap<String, String> parametrosPost3 = new HashMap<>();
                        parametrosPost3.put("ins_sql", "INSERT INTO administrador(CodAdministrador, CodPeña) VALUES (" + "'" + correoUsuario + "'" + "," + cod + ")");
                        devuelveJSON.sendInsert(GlobalParams.url_insert, parametrosPost3);

                        //inserto en la tabla administrador el administrador y peña
                        HashMap<String, String> parametrosPost4 = new HashMap<>();
                        parametrosPost4.put("ins_sql", "INSERT INTO estadisticas(CodigoJug, Goles, TarjetaAmarilla, TarjetaRoja, CodPeña, PartidosJugados, PartidosGanados, PartidosPerdidos, PartidosEmpatados, Puntos) VALUES (" + "'"+correoUsuario+"'"+ ",0,0,0," +cod+",0,0,0,0,0);");
                        devuelveJSON.sendInsert(GlobalParams.url_insert, parametrosPost4);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
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
