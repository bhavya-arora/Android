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

    /*As we know when we run our app Main/UI Thread is made and start for Execution like: events,
    * drawing the views etc. So Main Thread also have Handler and Looper both Associated with
    * the Main Thread.
    *
    *   First: Looper be initialize and will Associated with Current Thread.
    *   Second: After Looper created, now we want create Handler and pass the Looper which has
    *   already been created to the Handler, So its the Handler Responsibility to pass
    *   the Received tasks(Runnable)/Messages to the MessageQueue for execution by Main Thread which
    *   is continously running with the help of Looper.
    *
    *   Note: If we want to Communicate with any Thread from any other Thread, we have to Pass
    *   the Message or Runnable to Handler of that Thread which further give it to the
    *   MessageQueue then Looper one by one dequeue the Runnable or Message and Thread will
    *   then execute them one by one.
    *
    * In This Case: We want to communicate or send Messages from the Runnables to the Main/UI
    * Thread. So we created the (Handler handler) in which we passed the Main Thread Looper
    * so that now this Handler is Now Associated with the Main Thread because we passed the
    * Main Thread Looper(Looper.getMainLooper()) which is already been associated with
    * main thread. Now we can use handler(Object) to pass the Message to the MessageQueue
    * of the main Thread for execution then Main Thread will Execute the Message/task
    * one by one
    *
    * NOTE: A Thread only has one Looper(which is associated with the particular Thread)
    * but can have Multiple Handlers we can create as many as Handlers by giving the Handler
    * (Object) the Looper(Which is Associated with the Thread) like:
    *
    *     Handler handler1 = new Handler(Looper.getMainLooper())
    *     Handler handler2 = new Handler(Looper.getMainLooper())
    *     .
    *     .
    *     .
    *     .
    *     */
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

                /*Now as we can see we want to Access the Main Thread from this Background Task
                * Now we can do this by Calling the Main/UI thread Handler, so that Main Thread
                * Handler get this Message Object and then Pass to the MessageQueue of the Main
                * Thead for Execution.*/
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
