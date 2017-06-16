package com.example.oscar.teammanager;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
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
import android.widget.TextView;
import android.widget.TimePicker;
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
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class EditarEquipo extends AppCompatActivity {

    protected Bundle extras;
    protected EditText nombreEquipo;
    protected TextView horaPartido;
    protected ImageView fotoEquipo;
    private  int color;
    private Paint paint;
    private Rect rect;
    private RectF rectF;
    private Bitmap result;
    private Canvas canvas;
    private  float roundPx;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_equipo);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        devuelveJSON = new ClaseConexion();
        nombreEquipo = (EditText)findViewById(R.id.nom_peña);
        horaPartido = (TextView)findViewById(R.id.peña_hora);
        fotoEquipo = (ImageView) findViewById(R.id.peña_img);
        spinner = (Spinner)findViewById(R.id.spinner4);


        extras = getIntent().getExtras();
        codPeña = extras.getInt("id");
        String nombre = extras.getString("nombre");
        String dia = extras.getString("dia");
        String hora = extras.getString("hora");
        foto = (Bitmap) extras.get("foto");

        nombreEquipo.setText(nombre);
        horaPartido.setText(hora);
        if(foto == null){
            fotoEquipo.setImageResource(R.drawable.penia);
            fot = null;
        }else {
            fotoEquipo.setImageBitmap(getRoundedRectBitmap(foto, 12));
            encodedImageData = getEncoded64ImageStringFromBitmap(foto);
            fot = encodedImageData;
        }
        rellenaSpinner();

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

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.GONE);
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
            EditarTask task = new EditarTask();
            task.execute();
            return true;
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_borrar) {
            AlertDialog.Builder builderc = new AlertDialog.Builder(EditarEquipo.this);
            builderc.setMessage("¿Esta seguro que desea eliminar permanentemente este equipo?");
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
                bm = bm.createScaledBitmap(bm, 150, 150, true);
                fotoEquipo.setImageBitmap(getRoundedRectBitmap(bm,12));

                encodedImageData = getEncoded64ImageStringFromBitmap(bm);
                fot = encodedImageData;
            } catch (FileNotFoundException e) {
            }
        }
    }


    public String getEncoded64ImageStringFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);
        byte[] byteFormat = stream.toByteArray();
        // get the base 64 string
        String imgString = Base64.encodeToString(byteFormat, Base64.NO_WRAP);
        return imgString;
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

    class EditarTask extends AsyncTask<String, String, JSONArray> {
        private ProgressDialog pDialog;
        String nombre;
        String dia;
        String hora;


        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(EditarEquipo.this);
            pDialog.setMessage("Actualizando...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();

            nombre = nombreEquipo.getText().toString();
            dia = spinner.getSelectedItem().toString();
            hora = horaPartido.getText().toString();
            /*if(fot == null){
                fot = null;
            }else {
                fot = encodedImageData;
            }*/
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
                            builders.setMessage("Equipo actualizado correctamente");
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

    class BorrarTask extends AsyncTask<String, String, JSONArray> {
        private ProgressDialog pDialog;



        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(EditarEquipo.this);
            pDialog.setMessage("Borrando...");
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
