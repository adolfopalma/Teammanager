package com.example.oscar.teammanager.Utils;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

//Servicio que hace que el tiempo siga contando aunque el movil este bloqueado en el partido
public class NotificationUpdateService extends Service {

    public static final String ACTION_START = "com.example.oscar.teammanager.START_NOTIFY_SERVICE";
    public static final String ACTION_STOP = "com.example.oscar.teammanager.STOP_NOTIFY_SERVICE";

    private long mStartTime;
    private boolean mThreadCanRun;

    private UpdateNotification mUpdateNotification;
    private Thread mUpdateThread;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(intent.getAction().equalsIgnoreCase(ACTION_START)) {
            mStartTime = intent.getLongExtra("START_TIME", System.currentTimeMillis());
            start();
            return START_REDELIVER_INTENT;
        } else {
            stop();
            stopSelf();
        }

        return START_NOT_STICKY;
    }



    private void start() {

        mThreadCanRun = true;

        if(mUpdateNotification == null) {
            mUpdateNotification = new UpdateNotification();
            mUpdateThread = new Thread(mUpdateNotification);
            mUpdateThread.start();
        } else {
            stop();
            start();
        }

    }

    private void stop() {

        mThreadCanRun = false;

        if(mUpdateThread != null) {
            mUpdateThread.interrupt();
        }

        mUpdateThread = null;
        mUpdateNotification = null;

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    class UpdateNotification implements Runnable {

        @Override
        public void run() {
            while(mThreadCanRun) {

                long since = System.currentTimeMillis() - mStartTime;

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }

        }
    }
}
