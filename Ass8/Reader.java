import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.google.gson.Gson;

public class Reader extends Thread {
    private String file;
    private int nThreads;
    private static int terminationDelay = 15000;
    
    public Reader(String file, int nThreads){
        this.file = file;
        this.nThreads = nThreads;
    }

    @Override
    public void run(){
        // Creo il pool dei thread che contano le occorrenze delle causali
        ExecutorService pool = Executors.newFixedThreadPool(nThreads);
        
        Gson gson = new Gson();
        StringBuilder tmp = new StringBuilder();

        // Utilizzo NIO per ricreare il file json in formato stringa
        try(
            FileInputStream fin = new FileInputStream(file);
            FileChannel in = fin.getChannel();
        ){
            ByteBuffer buf = ByteBuffer.allocate(file.length());
            
            while( in.read(buf) > 0 ){
                buf.flip();
                while ( buf.hasRemaining() )
                    tmp = tmp.append( (char) buf.get() );

                buf.clear();
            }
        } catch ( IOException e ){

        }
        // Deserializzo con gson
        Cliente[] cliente = gson.fromJson(tmp.toString(), Cliente[].class);
        Counter counter = new Counter();
        // Per ogni cliente trovato creo un nuovo thread che lo elabora
        for ( Cliente c : cliente ){
            pool.execute( new CounterThread(c, counter) );
        }
        try{
            pool.shutdown();
            pool.awaitTermination(terminationDelay, TimeUnit.MILLISECONDS);
        } catch ( InterruptedException e ){
            System.err.printf("Reader: Interrotto, terminazione\n");
            pool.shutdownNow();
            System.exit(1);
        }

        pool.shutdown();
        System.out.println("Bonifici: \t" + counter.getBon() + "\n" +
            "Bollettini: \t"+ counter.getBol() + "\n" +
            "Accrediti: \t" + counter.getAcc() + "\n" +
            "F24: \t\t" + counter.getF24() + "\n" +
            "PagoBancomat: \t" + counter.getPag() + "\n");

    }
    
}
