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
 * Created by oscar on 27/05/2017.
 */

public class Item_equipos extends LinearLayout {
    TextView nombreEquip;
    ImageView imageEquip;
    private  int color;
    private Paint paint;
    private Rect rect;
    private RectF rectF;
    private Bitmap result;
    private Canvas canvas;
    private  float roundPx;



    public Item_equipos(Context context) {
        super(context);
        inflate(context, R.layout.item_list_perfil, this);

        nombreEquip = (TextView) findViewById(R.id.tvNomEquip);
        imageEquip = (ImageView) findViewById(R.id.peña_img);
    }


    public void setEquipo(Peñas p) {
        nombreEquip.setText(""+p.getNombre());
        result = GlobalParams.decodeBase64(p.getRutaFoto());

        if(result != null) {
            imageEquip.setImageBitmap(GlobalParams.getRoundedRectBitmap(result, 12));
        }else{
            imageEquip.setImageResource(R.drawable.penia);
        }

    }
}

