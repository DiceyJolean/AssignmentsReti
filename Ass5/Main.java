import java.io.File;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.*;

public class Main {

    final static int k = 7;
    static int maxDelay = 15000;
    
    public static void main ( String args[] ){
        Thread.currentThread().setName("Main");
        String path; // = "/etc/";

        try {
            if (args.length == 1){
                path = args[0];
            }
            else{
                Scanner in = new Scanner(System.in);
                System.out.printf("%s - Inserire il path\n", Thread.currentThread().getName());
                path = in.next();
                in.close();
            }
        } catch ( Exception e ){
            System.err.printf("%s - Formato del path errato\n", Thread.currentThread().getName());
            return;
        }

        File dir = new File(path);
        if ( !dir.exists() ){
            System.err.printf("%s - %s non trovato\n", Thread.currentThread().getName(), path);
            return;
        }
        if ( !dir.isDirectory() ){
            System.err.printf("%s - %s non è una directory\n", Thread.currentThread().getName(), path);
            return;
        }
        
        Queue q = new Queue();
        ExecutorService pool = Executors.newCachedThreadPool();
        for ( int i = 0; i < k; i++ ){
            Thread c = new Thread(new Consumer(q));
            c.setName("Consumer "+i);
            pool.execute(c);
        }
        Thread p = new Thread(new Producer(q, path));

        p.setName("Producer");
        pool.execute(p);

        pool.shutdown();
        try {
            pool.awaitTermination(maxDelay, TimeUnit.MILLISECONDS);
        } catch ( InterruptedException e ){
            System.out.printf("Main - Interrotto\n");
            return;
        }
        pool.shutdownNow();

        System.out.printf("%s - Terminato con successo\n", Thread.currentThread().getName());
    }
}
