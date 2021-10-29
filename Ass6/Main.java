import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main{
    static final int port = 6789;
    static int nWorkers = 10;
    static long timeout = 15000;
    static boolean DEBUG = true;

    public static void main ( String[] args ){
        try{
            ServerSocket server = new ServerSocket(port);

            Thread timer = new Timeout(timeout);
            timer.start();
            if ( DEBUG ) System.out.printf("%s - Avvio il timeout\n", Thread.currentThread().getName());
            
            ExecutorService pool = Executors.newFixedThreadPool(nWorkers);
            while ( timer.isAlive() ){
                if ( DEBUG ) System.out.printf("%s - Sono in attesa di una nuova connessione...\n", Thread.currentThread().getName());
                
                Socket client = server.accept();
                Worker w = new Worker(client);
                if ( DEBUG ) System.out.printf("%s - Nuova connessione accettata\n", Thread.currentThread().getName());
                
                pool.execute(w);
            }
            pool.shutdownNow();
            server.close();

        } catch ( IOException e ){
            System.err.printf("%s - ", Thread.currentThread().getName());
            e.printStackTrace();
            return;
        }
    }
    
}