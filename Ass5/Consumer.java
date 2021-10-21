import java.io.File;

public class Consumer implements Runnable {

    private static final boolean DEBUG = false;
    private Queue q;
    
    public Consumer(Queue q){
        this.q = q;
    }

    public void run(){

        Thread.currentThread().setName("Consumer");
        if ( DEBUG ) System.out.printf("%s - Inizio la routine di esecuzione\n", Thread.currentThread().getName());
        String dir = q.pop();
        while ( dir != null ){
            if ( DEBUG ) System.out.printf("%s - Prelevo %s dalla coda\n", Thread.currentThread().getName(), dir);
            File d = new File(dir);
            assert(d.isDirectory());
            File[] files = d.listFiles();
            // d è un path assoluto, quindi riesco a visitarla
            if ( files != null ){
                if ( DEBUG ) System.out.printf("%s - Esploro la directory %s\n", Thread.currentThread().getName(), d.getName());
                for ( File elem : files )
                    System.out.printf("%s - %s\n", Thread.currentThread().getName(), elem.getName());
                
            }
            dir = q.pop();
        }
    }

}
