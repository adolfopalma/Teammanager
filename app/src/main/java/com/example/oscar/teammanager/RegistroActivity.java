package com.example.oscar.teammanager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.example.oscar.teammanager.Objects.Jugadores;
import com.example.oscar.teammanager.Utils.ClaseConexion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegistroActivity extends AppCompatActivity {


    //Declaracion de variables
    protected Spinner spinner;
    protected EditText ednombre,edcorreo,edcontraseña,ededad;
    protected ImageView foto;
    protected String encodedImageData;
    private String IP_Server;
    private String url_consulta, url_insert;
    private JSONArray jSONArray;
    private ClaseConexion devuelveJSON;
    private JSONObject jsonObject;
    private static int ACT_GALERIA = 1;
    private static Uri fotoGaleria;
    private static InputStream is;
    private static BufferedInputStream bis;
    private static Bitmap bm;
    protected String hora,nombreFoto;
    private Jugadores jugadores;
    String nombre;
    String edad;
    String correo;
    String pass;
    String tipoJugador;
    String rutaFoto;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        //Inicializo variables

        spinner = (Spinner)findViewById(R.id.spinner3);
        edcontraseña = (EditText)findViewById(R.id.edContraseña);
        edcorreo = (EditText)findViewById(R.id.edCorreo);
        ededad = (EditText)findViewById(R.id.edEdad);
        ednombre = (EditText)findViewById(R.id.edNombre);
        foto =(CircleImageView) findViewById(R.id.ivFotoPeña);
        SimpleDateFormat sdf = new SimpleDateFormat("hh-mm-dd-MM-yyyy");
        hora = sdf.format(new Date());
        IP_Server = "http://iesayala.ddns.net/19ramajo";
        url_insert = IP_Server + "/prueba.php";
        url_consulta = IP_Server + "/consulta.php";
        devuelveJSON = new ClaseConexion();
        foto.setImageResource(R.mipmap.ic_launcher_round);

        //Boton flotante con accion, en el comprueblo que estan todos los campos rellenos y la conexion,
        //ejecuto asynctask y limpio campos.
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ededad.getText().toString().equals("") || edcontraseña.getText().toString().equals("") || ednombre.getText().toString().equals("") || edcorreo.getText().toString().equals("")) {
                    Snackbar.make(findViewById(android.R.id.content), "Rellene todos los campos", Snackbar.LENGTH_SHORT).show();
                }else if(!ClaseConexion.compruebaConexion(RegistroActivity.this)){
                    Snackbar.make(findViewById(android.R.id.content), "Conexion limitada o nula, comprueba tu conexion.", Snackbar.LENGTH_LONG).show();
                }else{
                    RegistroTask task = new RegistroTask();
                    task.execute();
                    limpiarCampos();
                }
            }
        });
        rellenaSpinner();
    }



    private void limpiarCampos() {
        ededad.setText("");
        edcontraseña.setText("");
        ednombre.setText("");
        edcorreo.setText("");
        spinner.setSelection(0);
        foto.setImageResource(R.mipmap.ic_launcher_round);
        nombreFoto = null;
        rutaFoto = null;
        encodedImageData = null;
    }


    //Seleccionar foto desde galeria
    public void seleccionFoto(View v){
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(intent, ACT_GALERIA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ACT_GALERIA && resultCode == RESULT_OK) {
            fotoGaleria = data.getData();
            try {
                is = getContentResolver().openInputStream(fotoGaleria);
                bis = new BufferedInputStream(is);
                bm = BitmapFactory.decodeStream(bis);
                foto.setImageBitmap(bm);
                encodedImageData =getEncoded64ImageStringFromBitmap(bm);
            } catch (FileNotFoundException e) {
            }
        }
    }


    public String getEncoded64ImageStringFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        byte[] byteFormat = stream.toByteArray();
        // get the base 64 string
        String imgString = Base64.encodeToString(byteFormat, Base64.NO_WRAP);
        return imgString;
    }


    //Relleno spinner de tipo de jugador
    public void rellenaSpinner(){
        List<String> tipoJugadores = new ArrayList();
        tipoJugadores.add("Portero");
        tipoJugadores.add("Defensa");
        tipoJugadores.add("Lateral");
        tipoJugadores.add("Delantero");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_plegado, tipoJugadores);
        adapter.setDropDownViewResource(R.layout.spinner_desplegado);
        spinner.setAdapter(adapter);
    }


    //Asynctask en la que compruebo si el usuario ya esta registrado, si no lo esta inserto, si no lo esta muestro mensaje
    class RegistroTask extends AsyncTask<String, String, JSONArray> {
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            nombre = ednombre.getText().toString();
            edad = ededad.getText().toString();
            correo = edcorreo.getText().toString();
            pass = edcontraseña.getText().toString();
            tipoJugador = spinner.getSelectedItem().toString();
            rutaFoto = encodedImageData;

            pDialog = new ProgressDialog(RegistroActivity.this);
            pDialog.setMessage("Registrando...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected JSONArray doInBackground(String... args) {
            try {

                HashMap<String, String> parametrosPosteriores = new HashMap<>();
                parametrosPosteriores.put("ins_sql","select Correo from jugadores");
                jSONArray = devuelveJSON.sendRequest(url_consulta, parametrosPosteriores);

                ArrayList listaCorreos = new ArrayList();

                for(int i = 0; i< jSONArray.length();i++){
                    try {
                        jsonObject = jSONArray.getJSONObject(i);
                        jugadores = new Jugadores();
                        jugadores.setCorreo(jsonObject.getString("Correo"));
                        listaCorreos.add(jugadores.getCorreo());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                if(listaCorreos.contains(correo)){
                    Snackbar.make(findViewById(android.R.id.content), "Ya existe un usuario registrado con este correo, Pon otro so subnormal.", Snackbar.LENGTH_LONG).show();
                }else{
                    HashMap<String, String> parametrosPost = new HashMap<>();
                    parametrosPost.put("ins_sql","INSERT INTO jugadores(Nombre,Edad,Correo,Pass,TipoJugador,Ruta_Foto) VALUES ("+"'"+nombre+"'"+","+"'"+edad+"'"+","+"'"+correo+"'"+","+"'"+pass+"'"+","+"'"+tipoJugador+"'"+","+"'"+rutaFoto+"')");
                    jsonObject = devuelveJSON.sendInsert(url_insert, parametrosPost);

                    if(jsonObject != null) {
                        switch (jsonObject.getInt("added")){
                            case 1:
                                Snackbar.make(findViewById(android.R.id.content), "El usuario se registró correctamente.", Snackbar.LENGTH_LONG).show();
                                break;
                            default:
                                Snackbar.make(findViewById(android.R.id.content), "Error en el registro del usuario.", Snackbar.LENGTH_LONG).show();
                                break;
                        }
                    }else{
                        System.out.println("OBJETO NULO.");
                        Snackbar.make(findViewById(android.R.id.content), "Error en el registro del usuario.", Snackbar.LENGTH_LONG).show();
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

        }

        @Override
        protected void onCancelled() {
            pDialog.dismiss();
        }
    }

}

