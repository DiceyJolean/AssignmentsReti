import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class UfficioPostale{
    private ArrayBlockingQueue<Runnable> q;
    private ThreadPoolExecutor pool;

    public UfficioPostale(int numSportelli, int k){
        this.q = new ArrayBlockingQueue<Runnable>(k);
        this.pool = new ThreadPoolExecutor(numSportelli, numSportelli, 0, TimeUnit.MILLISECONDS, this.q, new ThreadPoolExecutor.AbortPolicy());
    }

    public void newUtente(Utente utente) throws RejectedExecutionException{
        try {
            pool.execute(utente);
        } catch ( RejectedExecutionException e ){
            // Faccio galleggiare l'eccezione
            throw new RejectedExecutionException();
        }
    }

    public void closeOffice(){
        pool.shutdown();
    }

    public void bruteClose(){
        if ( !this.pool.isTerminated() )
            pool.shutdownNow();
    }
}