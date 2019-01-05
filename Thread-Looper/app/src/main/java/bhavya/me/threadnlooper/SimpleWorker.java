package bhavya.me.threadnlooper;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class SimpleWorker extends Thread {

    /*This is our custom implementation of looper with While loop and Queue.
    * Running Thread may be over head because a simple Thread only keep running while
    * run() method of run class executing something, when run() when will stop executing
    * then Thread also be stopped.
    *
    * And creating new Thread everytime we want to execute something off the Thread is not
    * efficient, so what we can do is that run a infinite loop in the run() method the
    * Thread and also give Thread class a Queue, loop(we used while) will keep the Thread running
    * and we pass tasks(Runnables) from the Queue, basically looper(in our case while loop)
    * will dequeue the tasks from the queue and give to the Thread one by one.*/

    private static final String TAG = "SimpleWorker";

    private AtomicBoolean isAlive = new AtomicBoolean(true);
    private ConcurrentLinkedQueue<Runnable> queue = new ConcurrentLinkedQueue<>();

    public SimpleWorker() {
        super(TAG);
        /*This will start the current Thread, so we don't have to make instance of
        * ths class like: SimpleWorker worker = new SimpleWorker();
        *                  worker.start();
        * We don't have to do this and now after start() being called, so now run() method
        * of this Thread class will be called.*/
        start();
    }

    @Override
    public void run() {
        while (isAlive.get()){
            Runnable task = queue.poll();
            if(task!=null){
                task.run();
            }
        }
    }

    //This method is used by any class to give the Runnables(tasks) to the Thread or for adding the tasks.
    public SimpleWorker execute(Runnable task){
        queue.add(task);
        return this;
    }

    //This will stop the current Looper(While loop) so then run() method of the Thread will stop
    //and also Thread will stop running.
    public void quit(){
        isAlive.set(false);
    }
}
