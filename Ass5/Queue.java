import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Queue{
    // LinkedList sincronizzata
    private ReentrantLock qlock;
    private final Condition qcond;
    private int length;
    private LinkedList<String> q;

    public Queue(){
        this.qlock = new ReentrantLock();
        this.qcond = qlock.newCondition();
        this.length = 0;
        q = new LinkedList<String>();
    }

    public void push(String e){
        try { 
            qlock.lock(); 
            q.add(e);
            length++;
            qcond.signalAll();
        }
        finally { qlock.unlock(); }
    }

    public String pop(){
        String e = null;
        try {
            qlock.lock();
            while ( length == 0 ){
                try { qcond.await(); }
                catch ( InterruptedException ex ){
                    System.err.printf("%s - Interrotto\n", Thread.currentThread().getName());
                    qlock.unlock();
                    return null;
                }
            }
            // C'è almeno un elemento in q
            e = q.getFirst();
            if ( e != null ){
                // null ce lo lascio per segnalare ai consumatori che la coda è vuota
                q.removeFirst();
                length--;
            }
        }
        finally { qlock.unlock(); }

        return e;    
    }

}