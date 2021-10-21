import java.io.File;
import java.io.IOException;

public class Producer implements Runnable {

    private static final boolean DEBUG = false;
    private Queue q;
    private String dir;

    public Producer(Queue q, String dir){
        this.q = q;
        this.dir = dir;
    }

    public static void visitDir(File dir, Queue q) throws IOException {
        File[] filelist = dir.listFiles();
        if ( filelist == null ){
            if ( DEBUG ) System.out.printf("%s - La directory %s era vuota\n", Thread.currentThread().getName(), dir.getName());
            return;
        }
        for (File file : filelist) {

            if ( DEBUG ) System.out.printf("%s - Trovo un file: %s\n", Thread.currentThread().getName(), file.getName());
            if ( file.isDirectory() ){

                if ( DEBUG ) System.out.printf("%s - Il file %s è una directory e la inserisco in coda\n", Thread.currentThread().getName(), file.getName());
                q.push(file.getAbsolutePath()); // Utilizzo il path assoluto, altrimenti i consumatori non riescono ad aprire la directory corretta
                visitDir(file, q);
            }
            // Altrimenti non faccio niente
        }
    }

    public void run(){
        Thread.currentThread().setName("Producer");
        File dir = new File(this.dir);
        if ( dir.exists() && dir.isDirectory() ){
            try {
                q.push(this.dir);
                visitDir(dir, this.q);
            } catch ( IOException e ){
                System.err.printf("%s - Eccezione\n", Thread.currentThread().getName());
                e.printStackTrace();
                q.push(null); // Avviso comunque i consumer
                return;
            }
        }
        else
            System.err.printf("%s - Errore nella directory %s\n", Thread.currentThread().getName(), this.dir);
        
        if ( DEBUG ) System.out.printf("%s - Visita terminata, inserisco null nella coda\n", Thread.currentThread().getName());
        q.push(null); // elemento che chiude la coda
    }
}
