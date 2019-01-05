package bhavya.me.threadlooper;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final SimpleWorker worker = new SimpleWorker();
        /*It will start the SimpleWorker Thread and run() method is called.*/
        worker.start();

        /*-> As we know Looper take some time to Prepare and after preparing looper only then we
        * can Prepare the Handler and if we try to call handler just after we start the Thread
        * it may be possible that looper is not prepared and also Handler not prepared, and
        * if we try to Access the mHandler just after start called it will throw the
        * NullPointerException.
        *
        * -> So that's why I'm giving the 300 milliSeconds delay so that looper can be Prepare.
        *
        * -> And as you can see i passed the getMainLooper() to the handler, now this Handler
        * is Associated with the Looper of the Main/UI thread or we can say that now this
        * Handler can Enqueue the Message Or Runnables to the MessageQueue of the Main Thread
        * after certain delay. And when Main Thread MessageQueue get this Runnable task then
        * Main/UI thread will immediately Execute this Runnable and call the run() method of this
        * Runnable.
        *
        * */
        new Handler(getMainLooper()).postDelayed(new Runnable() {
            public void run() {
                Message m = worker.mHandler.obtainMessage();
                m.arg1 = 10;
                worker.mHandler.sendMessage(m);
            }
        }, 300);
    }
}
