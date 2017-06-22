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
import com.example.oscar.teammanager.Objects.Peñas;
import com.example.oscar.teammanager.R;
import com.example.oscar.teammanager.Utils.GlobalParams;

/**
 * Created by oscar on 12/12/2016.
 */

public class Item extends LinearLayout {
    TextView nomPeña,diaPeña, horaPeña, componentePeña, administradorPeña;
    ImageView imagePeña;
    private  int color;
    private Paint paint;
    private Rect rect;
    private RectF rectF;
    private Bitmap result;
    private Canvas canvas;
    private  float roundPx;

    public Item(Context context) {
        super(context);
        inflate(context, R.layout.item_main, this);

        nomPeña = (TextView) findViewById(R.id.peña_denominacion);
        diaPeña = (TextView) findViewById(R.id.peña_dia);
        horaPeña = (TextView) findViewById(R.id.peña_hora);
        componentePeña = (TextView) findViewById(R.id.peña_component);
        administradorPeña = (TextView) findViewById(R.id.peña_administrador);
        imagePeña = (ImageView) findViewById(R.id.peña_img);
    }



    public void setPeña(Peñas p) {
        nomPeña.setText(""+p.getNombre());
        diaPeña.setText(""+p.getDiaPartido());
        horaPeña.setText(""+p.getHoraPartido());
        administradorPeña.setText(""+p.getNomAdministrador());
        result = GlobalParams.decodeBase64(p.getRutaFoto());

        if(result != null) {
            imagePeña.setImageBitmap(GlobalParams.getRoundedRectBitmap(result, 12));
        }

    }

}
