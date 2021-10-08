import java.util.concurrent.ThreadLocalRandom;

public class Utente implements Runnable{
    private int id;
    private int minDelay = 1;
    private int maxDelay = 2000;

    public Utente(int id){
        this.id = id;
    }

    public int getId(){
        return this.id;
    }
    
    @Override
    public void run(){
        System.out.println("Utente {"+this.id+"}: Vengo servito da uno sportello\n");
        long toSleep = ThreadLocalRandom.current().nextLong(minDelay, maxDelay +1);
        try {
            Thread.sleep(toSleep);
        } catch ( InterruptedException e ){
            System.err.println("Utente {"+this.id+"}: sono stato interrotto\n");
            return;
        }
        System.out.println("Utente {"+this.id+"}: ho terminato le mie operazioni\n");
    }
    
}
