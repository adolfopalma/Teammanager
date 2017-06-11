package com.example.oscar.teammanager;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.oscar.teammanager.Utils.GlobalParams;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class EditarEquipo extends AppCompatActivity {

    protected Bundle extras;
    protected EditText nombreEquipo, ededad;
    protected TextView diaPartido, horaPartido;
    protected ImageView fotoEquipo;
    private  int color;
    private Paint paint;
    private Rect rect;
    private RectF rectF;
    private Bitmap result;
    private Canvas canvas;
    private  float roundPx;
    private int mYear, mMonth, mDay;
    protected Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_equipo);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        nombreEquipo = (EditText)findViewById(R.id.nom_peña);
        diaPartido = (TextView)findViewById(R.id.peña_dia);
        horaPartido = (TextView)findViewById(R.id.peña_hora);
        fotoEquipo = (ImageView) findViewById(R.id.peña_img);
        spinner = (Spinner)findViewById(R.id.spinner4);


        extras = getIntent().getExtras();
        String nombre = extras.getString("nombre");
        String dia = extras.getString("dia");
        String hora = extras.getString("hora");
        Bitmap foto = (Bitmap) extras.get("foto");

        nombreEquipo.setText(nombre);
        diaPartido.setText(dia);
        horaPartido.setText(hora);
        fotoEquipo.setImageBitmap(getRoundedRectBitmap(foto, 12));
        rellenaSpinner();
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

            return true;
        }

        return super.onOptionsItemSelected(item);
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

}
