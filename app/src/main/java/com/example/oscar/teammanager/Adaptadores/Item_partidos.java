package com.example.oscar.teammanager.Adaptadores;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.oscar.teammanager.Objects.Partidos;
import com.example.oscar.teammanager.Objects.Peñas;
import com.example.oscar.teammanager.R;

/**
 * Created by oscar on 19/05/2017.
 */

public class Item_partidos extends LinearLayout {
    TextView nomPeña,diaPartido, ganador, resultado;
    ImageView imagenEquipo;
    private  int color;
    private Paint paint;
    private Rect rect;
    private RectF rectF;
    private Bitmap result;
    private Canvas canvas;
    private  float roundPx;

    public Item_partidos(Context context) {
        super(context);
        inflate(context, R.layout.item_list_partidos, this);

        nomPeña = (TextView) findViewById(R.id.tvNomEquipo);
        diaPartido= (TextView) findViewById(R.id.tvFecha);
        ganador = (TextView) findViewById(R.id.tvGanador);
        resultado = (TextView) findViewById(R.id.tvResultado);
        imagenEquipo = (ImageView) findViewById(R.id.peña_img);
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

    public void setPartido(Partidos pa) {
        nomPeña.setText(""+pa.getNomPeña());
        diaPartido.setText(""+pa.getFechaPartido());
        ganador.setText(""+pa.getGanador());
        resultado.setText(""+pa.getResultado());
        result = decodeBase64(pa.getRutaFoto());

        if(result != null) {
            imagenEquipo.setImageBitmap(getRoundedRectBitmap(result, 12));
        }else{
            imagenEquipo.setImageResource(R.mipmap.ic_launcher);
        }
    }

}
