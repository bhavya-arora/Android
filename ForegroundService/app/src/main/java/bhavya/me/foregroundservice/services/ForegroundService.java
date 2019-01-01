package bhavya.me.foregroundservice.services;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat.Action;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import bhavya.me.foregroundservice.MainActivity;
import bhavya.me.foregroundservice.R;

import static bhavya.me.foregroundservice.MainActivity.TAG;

public class ForegroundService extends Service {

    public static String MAIN_ACTION = "foregroundservice.action.main";
    public static String INIT_ACTION = "foregroundservice.action.init";
    public static String PREV_ACTION = "foregroundservice.action.prev";
    public static String PLAY_ACTION = "foregroundservice.action.play";
    public static String NEXT_ACTION = "foregroundservice.action.next";
    public static String STARTFOREGROUND_ACTION = "foregroundservice.action.startforeground";
    public static String STOPFOREGROUND_ACTION = "foregroundservice.action.stopforeground";

    public static final int MAIN_NOTIFICATION_ID = 100;
    public static final int PREV_NOTIFICATION_ID = 101;
    public static final int PLAY_NOTIFICATION_ID = 102;
    public static final int NEXT_NOTIFICATION_ID = 103;

    public static boolean IS_SERVICE_RUNNING = false;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @SuppressLint("WrongConstant")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent.getAction().equals(STARTFOREGROUND_ACTION)){
            showNotification();
        }
        else if(intent.getAction().equals(PREV_ACTION)){
            Toast.makeText(this, "Previous Button", Toast.LENGTH_SHORT).show();
        }
        else if (intent.getAction().equals(PLAY_ACTION)){
            Toast.makeText(this, "Play Button", Toast.LENGTH_SHORT).show();
        }
        else if (intent.getAction().equals(NEXT_ACTION)){
            Toast.makeText(this, "Next Button Clicked", Toast.LENGTH_SHORT).show();
        }
        else if (intent.getAction().equals(STOPFOREGROUND_ACTION)){
            stopForeground(true);
            stopSelf();
        }
        return Service.START_NOT_STICKY;
    }

    public void showNotification(){

        Intent mainIntent = new Intent(this, MainActivity.class);
        mainIntent.setAction(MAIN_ACTION);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, MAIN_NOTIFICATION_ID,
                mainIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //Previous Intent and Action
        Intent preIntent = new Intent(this, ForegroundService.class);
        preIntent.setAction(PREV_ACTION);
        PendingIntent prePendingIntent = PendingIntent.getService(this, PREV_NOTIFICATION_ID,
                preIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Action prevAction = new Action(R.drawable.ic_skip_previous_black_24dp, "Previous",
                prePendingIntent);

        //Play Intent and Action
        Intent playIntent = new Intent(this, ForegroundService.class);
        playIntent.setAction(PLAY_ACTION);
        PendingIntent playPendingIntent = PendingIntent.getService(this, PLAY_NOTIFICATION_ID,
                playIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Action playAction = new Action(R.drawable.ic_play_arrow_black_24dp, "Play",
                playPendingIntent);

        //Next Intent and Action
        Intent nextIntent = new Intent(this, ForegroundService.class);
        nextIntent.setAction(NEXT_ACTION);
        PendingIntent nextPendingIntent = PendingIntent.getService(this, NEXT_NOTIFICATION_ID,
                nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Action nextAction = new Action(R.drawable.ic_skip_next_black_24dp, "Next",
                nextPendingIntent);



        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle("Music Player")
                .setTicker("Music Player Ticker")
                .setContentText("Song")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .addAction(prevAction)
                .addAction(playAction)
                .addAction(nextAction)
                .build();
        startForeground(201, notification);
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy: Service");
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
