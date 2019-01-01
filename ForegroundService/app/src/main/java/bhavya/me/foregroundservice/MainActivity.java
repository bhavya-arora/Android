package bhavya.me.foregroundservice;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import bhavya.me.foregroundservice.services.ForegroundService;

public class MainActivity extends AppCompatActivity {
    
    public static final String TAG = "Bhavya";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void buttonClicked(View v) {
        Button button = (Button) v;
        Intent service = new Intent(MainActivity.this, ForegroundService.class);
        if (!ForegroundService.IS_SERVICE_RUNNING) {
            service.setAction(ForegroundService.STARTFOREGROUND_ACTION);
            ForegroundService.IS_SERVICE_RUNNING = true;
            button.setText("Stop Service");
            startService(service);
        } else {
            stopService(service);
            ForegroundService.IS_SERVICE_RUNNING = false;
            button.setText("Start Service");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart: ");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume: ");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause: ");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG, "onRestart: ");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop: ");
    }

    @Override
    protected void onDestroy() {
        Intent service = new Intent(MainActivity.this, ForegroundService.class);
        service.setAction(ForegroundService.STOPFOREGROUND_ACTION);
        startService(service);
        Log.i(TAG, "onDestroy: ");
        super.onDestroy();
        Log.i(TAG, "onDestroy1: ");
    }
}
