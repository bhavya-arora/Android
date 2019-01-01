package bhavya.me.boundservice.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Chronometer;

import bhavya.me.boundservice.MainActivity;

import static bhavya.me.boundservice.MainActivity.TAG;

public class BoundService extends Service {

    private IBinder mBinder = new MyBinder();
    private Chronometer mChronometer;
    final Handler h = new Handler();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate: Service is created");
        mChronometer = new Chronometer(BoundService.this);
        mChronometer.setBase(SystemClock.elapsedRealtime());
        mChronometer.start();


    }

    //onStartCommand() method will call only when startService() method will call but in this case both onStartCommand() and onBind() methods will call.
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    //OnBind() method will call only when bindService() method will call
    //NOTE: only one IBinder object will make, only when first time this method will call, after that if any activity wants to bind to this service this method won't call again and again, but instead this class will send same IBinder object to anyone which want to bind to this service.
    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "onBind: ");
        return mBinder;
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy: service");
        super.onDestroy();
        mChronometer.stop();
    }

    //This method will call only when BoundService will use because in simple startedService(Service/IntentService)
    // no response from the service can sent back to the calling component( MainActivity ), but in
    // BoundService we can call this method from the Component( MainActivity ).
    public String getTimestamp() {

        Log.i(TAG, "getTimestamp: ");
        long elapsedMillis = SystemClock.elapsedRealtime()
                - mChronometer.getBase();
        int hours = (int) (elapsedMillis / 3600000);
        int minutes = (int) (elapsedMillis - hours * 3600000) / 60000;
        int seconds = (int) (elapsedMillis - hours * 3600000 - minutes * 60000) / 1000;
        int millis = (int) (elapsedMillis - hours * 3600000 - minutes * 60000 - seconds * 1000);

        Log.i(TAG, "getTimestamp: " + hours + ":" + minutes + ":" + seconds + ":" + millis);
        return hours + ":" + minutes + ":" + seconds + ":" + millis;
    }

    public class MyBinder extends Binder{
        public BoundService getService(){
            return BoundService.this;
        }
    }
}
