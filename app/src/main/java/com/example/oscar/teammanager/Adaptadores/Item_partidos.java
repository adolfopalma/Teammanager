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
import com.example.oscar.teammanager.R;
import com.example.oscar.teammanager.Utils.GlobalParams;

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


    public void setPartido(Partidos pa) {
        nomPeña.setText(""+pa.getNomPeña());
        diaPartido.setText(""+pa.getFechaPartido());
        ganador.setText(""+pa.getGanador());
        resultado.setText(""+pa.getResultado());
        result = GlobalParams.decodeBase64(pa.getRutaFoto());

        if(result != null) {
            imagenEquipo.setImageBitmap(GlobalParams.getRoundedRectBitmap(result, 12));
        }else{
            imagenEquipo.setImageResource(R.mipmap.ic_launcher);
        }
    }

}
