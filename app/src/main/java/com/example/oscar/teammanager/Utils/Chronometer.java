package com.example.oscar.teammanager.Utils;

import android.content.Context;
import com.example.oscar.teammanager.PartidoActivity;

public class Chronometer implements Runnable {

    public static final long MILLIS_TO_MINUTES = 60000;
    public static final long MILLS_TO_HOURS = 3600000;
    Context mContext;
    long mStartTime;
    boolean mIsRunning;


    public Chronometer(Context context) {
        mContext = context;
    }

    public Chronometer(Context context, long startTime) {
        this(context);
        mStartTime = startTime;
    }


    public void start() {
        if(mStartTime == 0) { //if the start time was not set before! e.g. by second constructor
            mStartTime = System.currentTimeMillis();
        }
        mIsRunning = true;
    }


    public void stop() {
        mIsRunning = false;
    }


    public boolean isRunning() {
        return mIsRunning;
    }


    public long getStartTime() {
        return mStartTime;
    }


    @Override
    public void run() {
        while(mIsRunning) {
            //We do not call ConvertTimeToString here because it will add some overhead
            //therefore we do the calculation without any function calls!

            //Here we calculate the difference of starting time and current time
            long since = System.currentTimeMillis() - mStartTime;

            //convert the resulted time difference into hours, minutes, seconds and milliseconds
            int seconds = (int) (since / 1000) % 60;
            int minutes = (int) ((since / (MILLIS_TO_MINUTES)) % 60);
            //int hours = (int) ((since / (MILLS_TO_HOURS)) % 24); //this resets to  0 after 24 hour!
            int hours = (int) ((since / (MILLS_TO_HOURS))); //this does not reset to 0!

            ((PartidoActivity) mContext).updateTimerText(String.format("%02d:%02d:%02d", hours, minutes, seconds));

            //Sleep the thread for a short amount, to prevent high CPU usage!
            try {
                Thread.sleep(15);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
