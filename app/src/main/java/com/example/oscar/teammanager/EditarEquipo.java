package com.example.oscar.teammanager;

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
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class EditarEquipo extends AppCompatActivity {

    protected Bundle extras;
    protected EditText nombreEquipo;
    protected TextView diaPartido, horaPartido;
    protected ImageView fotoEquipo;
    private  int color;
    private Paint paint;
    private Rect rect;
    private RectF rectF;
    private Bitmap result;
    private Canvas canvas;
    private  float roundPx;

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


        extras = getIntent().getExtras();
        String nombre = extras.getString("nombre");
        String dia = extras.getString("dia");
        String hora = extras.getString("hora");
        Bitmap foto = (Bitmap) extras.get("foto");

        nombreEquipo.setText(nombre);
        diaPartido.setText(dia);
        horaPartido.setText(hora);
        fotoEquipo.setImageBitmap(getRoundedRectBitmap(foto, 12));
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

}
