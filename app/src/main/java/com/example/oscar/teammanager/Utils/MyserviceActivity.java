package com.example.oscar.teammanager.Utils;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;

import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.example.oscar.teammanager.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * Created by oscar on 19/06/2017.
 */

public class MyserviceActivity extends Service {

    private NotificationManager notificationManager;
    private static final int id_notification =1;

    @Override
    public void onCreate() {
        super.onCreate();

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        int currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        Date d = new Date();
        String dayOfTheWeek = sdf.format(d);
        String dia = Character.toUpperCase(dayOfTheWeek.charAt(0)) + dayOfTheWeek.substring(1,dayOfTheWeek.length());


        if(dia.equals(GlobalParams.diaPartido.get(1).getDiaPartido().toString())) {
            if(currentHour+2 == 20) {
                NotificationCompat.Builder builder = new NotificationCompat.Builder(
                        getBaseContext())
                        .setSmallIcon(R.drawable.balon)
                        .setContentTitle(GlobalParams.diaPartido.get(1).getNombre())
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_icono))
                        .setContentText("Hoy tienes partido a las " + GlobalParams.diaPartido.get(1).getHoraPartido())
                        .setVibrate(new long[]{100, 250, 100, 500})
                        .setSound(alarmSound)
                        .setAutoCancel(true);


                notificationManager.notify(id_notification, builder.build());
            }
        }


        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "Servicio parado", Toast.LENGTH_LONG).show();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
