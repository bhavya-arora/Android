package bhavya.me.threadlooper;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

public class SimpleWorker extends Thread {

    private static final String TAG = "SimpleWorker";

    Handler mHandler;
    /*When we call start() method from any activity then Thread is started and run()
    * method will call.*/
    @Override
    public void run() {
        super.run();

        /*This Looper.prepare() method Prepare the Looper of this current Thread and also
        * make a MessageQueue for this Thread which this Looper can loop through. and Now this Looper
        * is Associated with Current Thread.
        *
        * NOTE: 1. one thing to note that Thread should be in running state while Looper is being
        * prepared.
        *
        *       2. Preparing looper may take some time, and Handler is only prepared after the Looper
        *       being Prepared and ready to use.*/
        Looper.prepare();

        /*Handler only made when Looper is ready, Current Looper object will implicitly passed to
        * this Handler constructor so that this Handler is Associated with the Looper
        *
        * We can also pass the Looper Object explicitly like:
        *              mHandler = new Handler(Looper.getMainLooper());
        * Now this Handler is Associated with the Looper of Main Thread and this handler now can
        * only handle the Main Thread Messages.*/
        mHandler = new Handler(){

            /*Now this Message execute the Message Object using this Current Thread, when Looper
            * dequeue the Message from MessageQueue it will return back the Mesasge Object to the
            * Handler which enqueue the Message in MessageQueue*/
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Log.d(TAG,"In Handler, Msg = "+msg.arg1);
            }
        };
        /*loop() method will keep the Thread running until quit method is not called.*/
        Looper.loop();
    }

}
