package com.example.oscar.teammanager;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.example.oscar.teammanager.Utils.ClaseConexion;
import com.example.oscar.teammanager.Utils.GlobalParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


//Clase que edita la informacion de perfil
public class EditarPerfil extends AppCompatActivity {

    protected Bundle extras;
    private static int ACT_GALERIA = 1;
    private static Uri fotoGaleria;
    private static InputStream is;
    private static BufferedInputStream bis;
    private static Bitmap bm, foto;
    protected String encodedImageData;
    private ClaseConexion devuelveJSON;
    private JSONObject jsonObject;
    protected String fot;
    protected String correo;
    protected EditText nombreJug, edadJug, edNick;
    protected Spinner spinner;
    protected ImageView ivFoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        devuelveJSON = new ClaseConexion();
        nombreJug = (EditText)findViewById(R.id.edNombre);
        edNick = (EditText)findViewById(R.id.edNick);
        edadJug = (EditText)findViewById(R.id.edEdad);
        spinner = (Spinner)findViewById(R.id.spinner3);
        ivFoto =(CircleImageView) findViewById(R.id.ivFoto);

        //Obtengo datos de activiy anterior
        extras = getIntent().getExtras();
        correo = extras.getString("correo");
        String nombre = extras.getString("nombre");
        String nick = extras.getString("nick");
        String edad = extras.getString("edad");
        String tipo = extras.getString("tipo");
        fot =  extras.getString("foto");
        foto = GlobalParams.decodeBase64(fot);


        nombreJug.setText(nombre);
        edadJug.setText(edad);
        edNick.setText(nick);
        ivFoto.setImageResource(R.drawable.foto);
        if(foto == null){
            ivFoto.setImageResource(R.drawable.perfil);
            fot = null;
        }else {
            ivFoto.setImageBitmap(foto);
            encodedImageData = GlobalParams.getEncoded64ImageStringFromBitmap(foto);
            fot = encodedImageData;
        }
        rellenaSpinner();

        //swicth para seleccionar valor correspondiente del jugador
        switch (tipo) {
            case "Portero":
                spinner.setSelection(0);
                break;
            case "Defensa":
                spinner.setSelection(1);
                break;
            case "Lateral":
                spinner.setSelection(2);
                break;
            case "Delantero":
                spinner.setSelection(3);
                break;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_editar, menu);
        menu.findItem(R.id.action_borrar).setVisible(false);
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
            //Compruebo campo obligatoria y Ejecuto task para editar los datos
            if(nombreJug.getText().toString().equals("")){
                Snackbar.make(findViewById(android.R.id.content), "Debe rellenar el campo nombre obligatoriamente", Snackbar.LENGTH_LONG).show();
            }else{
            EditarTask task = new EditarTask();
            task.execute();
            }
            return true;
        }


        return super.onOptionsItemSelected(item);
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

    //Metodo para seleccionar foto desde galeria
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
                ivFoto.setImageBitmap(bm);

                encodedImageData = GlobalParams.getEncoded64ImageStringFromBitmap(bm);
                fot = encodedImageData;
            } catch (FileNotFoundException e) {
            }
        }
    }


    // task para actualizar datos de perfil
    class EditarTask extends AsyncTask<String, String, JSONArray> {
        private ProgressDialog pDialog;
        String  nombre = nombreJug.getText().toString();
        String  edad = edadJug.getText().toString();
        String  tipo = spinner.getSelectedItem().toString();
        String  nick = edNick.getText().toString();


        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(EditarPerfil.this);
            pDialog.setMessage(getResources().getString(R.string.act));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected JSONArray doInBackground(String... args) {
            try {

                HashMap<String, String> parametrosPost = new HashMap<>();
                parametrosPost.put("ins_sql", "UPDATE jugadores set Nombre = "+"'"+nombre+"',"+"  Edad = "+"'"+edad+"',"+"  TipoJugador = "+"'"+tipo+"',"+" Nick = "+"'"+nick+"',"+" Ruta_Foto = " +"'"+fot+"' where  Correo = '"+correo+"'");
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
                            AlertDialog.Builder builders = new AlertDialog.Builder(EditarPerfil.this);
                            builders.setMessage(getResources().getString(R.string.perfil_act));
                            builders.setPositiveButton(getResources().getString(R.string.acept),
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                            Intent i = new Intent(EditarPerfil.this, PerfilActivity.class);
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

}
