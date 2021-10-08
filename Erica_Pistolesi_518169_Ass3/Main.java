import java.util.Scanner;
import java.util.concurrent.*;
import java.util.InputMismatchException;

public class Main{
    final static int totalePosti = 20;
    static int maxDelay = 10000;
    
    public static void main( String args[] ){
        int k = 0;
        int nProf = 0;
        int nTes = 0;
        int nStud = 0;
        Scanner scan = new Scanner(System.in);

        try {
            System.out.printf("%s - Inserire un intero per indicare quante volte accede ogni utente\n", Thread.currentThread().getName());
            k = scan.nextInt();
            System.out.printf("%s - Inserire un intero per indicare quanti studenti far entrare\n", Thread.currentThread().getName());
            nStud = scan.nextInt();
            System.out.printf("%s - Inserire un intero per indicare quanti tesisti far entrare\n", Thread.currentThread().getName());
            nTes = scan.nextInt();
            System.out.printf("%s - Inserire un intero per indicare quanti professori far entrare\n", Thread.currentThread().getName());
            nProf = scan.nextInt();
            scan.close();
        } catch ( InputMismatchException e ){
            System.out.printf("%s - Formato parametri errato\n", Thread.currentThread().getName());
            return;
        }
        
        Laboratorio Lab = new Laboratorio(totalePosti);
        int n = ( nStud > nTes ) ? nStud : nTes;
        n = ( n > nProf ) ? n : nProf;
        int r = 0;
        ExecutorService pool = Executors.newCachedThreadPool();

        for ( int j = 0; j < k; j++ )
            // Avvio tutti i thread k volte
            for ( int i = 0; i < n; i++ ){
                // Li avvio insieme
                if ( r < nProf ){
                    Thread t = new Thread( new Utente(ThreadLocalRandom.current().nextInt(0, totalePosti), Lab, "Professore"));
                    t.setName("Professore "+ r);
                    pool.execute(t);
                }
                
                if ( r < nTes ){
                    Thread t = new Thread( new Utente(ThreadLocalRandom.current().nextInt(0, totalePosti), Lab, "Tesista"));
                    t.setName("Tesista "+ r);
                    pool.execute(t);
                }
                
                if ( r < nStud ){
                    Thread t = new Thread( new Utente(ThreadLocalRandom.current().nextInt(0, totalePosti), Lab, "Studente"));
                    t.setName("Studente "+ r);
                    pool.execute(new Thread(t));
                }
                
                r++;
            }
        
        pool.shutdown();
        
        try {
            pool.awaitTermination(maxDelay, TimeUnit.MILLISECONDS);
        } catch ( InterruptedException e ){
            System.out.printf("Main - Interrotto\n");
            return;
        }
        pool.shutdownNow();
        
    }
}