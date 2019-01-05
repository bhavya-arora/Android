package bhavya.me.threadnlooper;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView textView;
    private SimpleWorker worker;

    private Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            textView.setText((String)msg.obj);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.textView);

        //Basically this is the Thread class which has Looper(While loop) and Queue.
        worker = new SimpleWorker();

        /*now we are giving/enqueue the Runnable Tasks to the SimpleWorker Queue, so that Looper
        * of the SimpleWorker can loop through the Queue and give the task to the Thread for
        * execution one by one.
        *
        * Here: I'm providing 3 Tasks to perform.*/
        worker.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Message message = Message.obtain();
                message.obj = "This is Runnable 1";
                handler.sendMessage(message);
            }
        })
                .execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Message message = Message.obtain();
                        message.obj = "This is Runnable 2";
                        handler.sendMessage(message);
                    }
                })
                .execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(4000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Message message = Message.obtain();
                        message.obj = "This is Runnable 3";
                        handler.sendMessage(message);
                    }
                });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        worker.quit();
    }
}
