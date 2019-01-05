package bhavya.me.threadlooper;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

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
        * NOTE: If we didn't pass the getMainLooper() even then it will produce same result
        * because we are calling and Instantiating the Handle in the MainActivity, so that it
        * will automatically pass the Current Activity Thread(Main/UI) to the Handler, and Handler
        * will associated with Main Thread.
        *
        * */
        //This is used to Post/Enqueue the Runnable task to the Main Thread.
        new Handler(getMainLooper()).postDelayed(new Runnable() {
            public void run() {
                Message m = worker.mHandler.obtainMessage();
                m.arg1 = 10;
                worker.mHandler.sendMessage(m);
            }
        }, 300);

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //This is used to Post/Enqueue the Runnable task to the SimpleWorker, because as we can see we are giving the Looper of SimpleWorker
        // So this is Associated with SimpleWorker Thread.
        new Handler(worker.mHandler.getLooper()).post(new Runnable() {
            @Override
            public void run() {
                Log.d(SimpleWorker.TAG, "Runnale Example ");
            }
        });
    }
}

/*There is two Types of Messages that passed to the MessageQueue:
*    1. Message(Object/Payload/Data only)
*    2. Runnable(Task/ Code to execute)
*
* So, when we sendMessage(msg) through the Handler in the Particular Thread we have to make
* sure that we have the handleMessage() Method which can then execute the code in the handleMessage()
* method on the basis of the Payload we provide.
*
 * -> And when we give Runnable/task, so we have to provide/give Runnable through the Handler
  * and Handler which is Associated with the Looper of particular Thread.
  *
  * NOTE: In the Above Example I defined both Runnable and Message, how they are executing.*/
