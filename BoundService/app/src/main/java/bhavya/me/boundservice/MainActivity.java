package bhavya.me.boundservice;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import bhavya.me.boundservice.service.BoundService;

public class MainActivity extends AppCompatActivity {

    //Source(Full Explaination) : https://www.truiton.com/2014/11/bound-service-example-android/
    //Source(Theory) : http://codetheory.in/understanding-android-started-bound-services/
    //Source(Theory) : https://developer.android.com/guide/components/services

    /*In this Example i'm using Chronometer class for running the timer in the background
    * with the help of service. Basically Services are of 3 Types:
    *   1. Service(Intent Service/ Simple extending from Service class)
    *   2. ForeGround Service(Head Notification will be used.)
    *   3. Bound Service(Any component of the application can interact or request something
    *   from the service. that means any component(Activity, BR) can bind to service and
    *   control the service from the Activity itself)
    *
    * Services are either StartedService( Service/IntentService ) or BoundService but in case of Music Player, we can
    * use both ForegroundService( for Notification that tell user that is something is happening
    * behind the scene ) AND BoundService( for Controlling the Music like: getting music name
    * it's like client - server architecure ).
    *
    * So I used both startedService and BoundService in this example, Because BoundService is
    * bound to the component which is started that, like in this case if MainActivity(Component) is
    * Minimize so onStop() will call and Activity( MainActivity ) is unbind() from the service
    * and service will stop, so timer will stop.
    *
    * But I'm also using startedService with BoundService, in startedService on binding will occur
    * and after running the startedService even the component( MainAcitivty ) which started the
    * service will get minimized( onStop() ) even after that service will not stop and timer will
    * continue to run even the app is minimized.
    *
    * @@Callbacks:
    * >>StartedService -> startService() > onCreate() > onStartCommand() > [Service is Running] > onDestroy() > [Service is ShutDown]
    *
    * >>BoundService -> bindService() > onCreate() > onBind() > [Client is bind to service using IBinder object which is returned from the onbind() in the ServiceConnection ] > onUnbind() > onDestroy() > [Service is Shutdown]
    *
    * NOTE: 1. IntentService is the subclass of Service class because if we inherit or use Service
    *       class then we have to implement Thread by ourself but with the help of IntentService, Thread
    *       is automatically created when we extend from IntentService( in onHandleIntent() ).
    *
    *       2. In StartedService( Service/ IntentService ), if we want to stop the service, we have
    *       two Options. First by calling stopService() method by passing Intent object and Second,
    *       if Service itself will stop by stopSelf() method.
    *
    *       3. In BoundService, if we want to stop the boundService we have to unBind() all the
    *       components which is connected or bind with the service so that service will automatically
    *       will shutdown.
    *
    *
    * ___________________                                        _______________________
    *|                   |                                      |                       |
    *|    App Component  |    -------------------------->       |     Started Service   |
    *| [Activity/Service]|    <-------------X------------       |    [Intent Service]   |
    *|___________________|                                      |_______________________|
    *
    * If the Calling Component is Destroyed -> Started Service Continue to run Normally.
    *                                       -> Until stopService or stopSelf is called.
    *                                       -> Stops Automatically if IntentService.
    *
    * ___________________                                        _______________________
    *|                   |                                      |                       |
    *|    App Component  |    -------------------------->       |     Bound Service     |
    *| [Activity/Service]|    <-------------------------        |                       |
    *|___________________|                                      |_______________________|
    *
    * If the calling Component is Destroyed -> Bound Service is also Destroyed.
    *
    *   >>       STARTED SERVICE/ INTENT SERVICE VS BOUND SERVICE
    *
    * # Just to Accomplish Task[ Long Task ]     |  # For long standing connection
    * # Invoked by startService()                |  # Invoked By bindService()
    * # onBind() returns null                    |  # onBind() returns IBinder
    *
    * NOTE: 1. Generally we use onStart() and onStop()
    *         - To bind or unbind the service
    *           - So that the Bound Service is only active when Activity is Visible.
    *
    *       2. If you want your Activity to Bind to service even when it is not visible
    *         - i.e Acitivty is in Paused state
    *           - call bindService in onCreate()
    *           - call unBind in onDestroy()
    *
    *       IMPORTANT POINT:
    *         --- Since we are using started Service as well and started service is not bind to
    *         the component which started the service, even the App is Destroyed service
    *         won't stop. if we want to stop the service we want to override the onDestroy()
    *         method of MainActivity by calling stopService() method
    *
    * */

    BoundService mBoundService;
    boolean mServiceBound = false;
    
    public static final String TAG = "Bhavya";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.i(TAG, "onCreate: ");
        
        final TextView timestampText = (TextView) findViewById(R.id.timestamp_text);
        Button printTimestampButton = (Button) findViewById(R.id.print_timestamp);
        Button stopServiceButon = (Button) findViewById(R.id.stop_service);

        printTimestampButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.i(TAG, "onClick: printTimeStamp");
                if(mServiceBound)
                    timestampText.setText(mBoundService.getTimestamp());
            }
        });

        stopServiceButon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mServiceBound){
                    unbindService(mServiceConnection);
                    mServiceBound = false;
                }
                Intent intent = new Intent(MainActivity.this, BoundService.class);
                stopService(intent);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(MainActivity.this, BoundService.class);
        //Started Service
        startService(intent);
        //Bound Service
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);

        Log.i(TAG, "onStart: ");
    }

    @Override
    protected void onStop() {
        super.onStop();

        Log.i(TAG, "onStop: ");
        
        if (mServiceBound) {
            unbindService(mServiceConnection);
            mServiceBound = false;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause: ");
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "onDestroy: ");
        super.onDestroy();

        //only uncomment if we want to destroy the service as well with the component which started
        //the service. Otherwise if will continue to run even if app is destroyed.
        /*Intent intent = new Intent(this, BoundService.class);
        stopService(intent);*/

        Log.i(TAG, "onDestroy: 1");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume: ");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG, "onRestart: ");
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            BoundService.MyBinder myBinder = (BoundService.MyBinder) iBinder;
            mBoundService = myBinder.getService();
            mServiceBound = true;

            Log.i(TAG, "onServiceConnected: ");
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mServiceBound = false;
        }
    };
}
