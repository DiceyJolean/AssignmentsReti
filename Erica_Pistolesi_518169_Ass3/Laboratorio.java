import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.ArrayList;

public class Laboratorio {

    /*  Implementa le funzioni per la sincronizzazione
        Rappresento i PC con un array di "totalePosti" booleani: 
        true PC libero, false PC occupato
    */

    private ReentrantLock lock; // Mutua esclusione su tutto il laboratorio
    final Condition labFree; // true se tutti i PC sono liberi
    final Condition PCFree; // true se uno specifico PC è libero
    final Condition oneSetFree; // true se c'è almeno un PC libero
    private ArrayList<Boolean> Lab; // ArrayList che rappresenta i PC
    private int busySet; // Intero che rappresenta quanti posti sono occupati
    private int tesistiWaiting; // Intero che rappresenta il numero di tesisti in attesa
    private final int totalePosti;

    // Inizializzo il laboratorio con tutti i PC liberi
    public Laboratorio(int totalePosti){
        lock = new ReentrantLock();
        labFree = lock.newCondition();
        PCFree = lock.newCondition();
        oneSetFree = lock.newCondition();
        this.totalePosti = totalePosti;
        busySet = 0;
        tesistiWaiting = 0;
        Lab = new ArrayList<Boolean>(totalePosti);
        for ( int i = 0; i < this.totalePosti; i++ ){
            Lab.add(i, true);
        }
    }

    // Assegno il PC i
    public boolean setPC(int i) throws InterruptedException{
        boolean ok = false;
        try { 
            lock.lock();
            while ( !Lab.get(i) ){
                try {
                    tesistiWaiting++;
                    PCFree.await();
                    tesistiWaiting--;
                }
                catch ( InterruptedException e ){
                    throw new InterruptedException();
                }
            }
            // Il posto i è libero
            ok = true;
            busySet++;
            assert(busySet<=this.totalePosti);
            Lab.set(i, false);
     
        }
        finally { lock.unlock(); }
        
        return ok;
    }

    // Controllo se c'è almeno un PC disponibile e lo assegno
    public int isOneFreePC() throws InterruptedException{
        int i = -1;
        try { 
            lock.lock();
            while ( busySet == this.totalePosti )
                try { oneSetFree.await(); }
                catch ( InterruptedException e ){
                    throw new InterruptedException();
                }
        
            // C'è un posto libero, cerco quale
            i = Lab.indexOf(true);
            if ( i != -1 ){
                busySet++;
                assert(busySet<=this.totalePosti);
                Lab.set(i, false);
            }
            // if ( i == -1 ) FATAL ERROR
        }
        finally { lock.unlock(); }
        
        return i;
    }

    // Libero il PC i
    public void freePC(int i){
        try {
            lock.lock();
            if ( !Lab.get(i) ){
                Lab.set(i, true);
                busySet--;
            }
            assert(busySet>=0);
            // Sveglio un professore
            if ( busySet == 0 )
                labFree.signal();
            else{
                // Prima sveglio i tesisti
                PCFree.signalAll();
                // Se ci sono abbastanza PC sveglio anche gli studenti
                for ( int j = tesistiWaiting; j <= this.totalePosti; j++ )
                    oneSetFree.signal();
            }
        }
        finally { lock.unlock(); }
    }

    public void lockEntireLab() throws InterruptedException{
        try {
            lock.lock();
            while ( busySet > 0 )
                try { labFree.await(); }
                catch ( InterruptedException e ){
                    throw new InterruptedException();
                }
            for ( int i = 0; i < this.totalePosti; i++ )
                Lab.set(i, false);
            busySet = this.totalePosti;
        }
        finally { lock.unlock(); }
    }

    public void unlockEntireLab(){
        try {
            lock.lock();
            for ( int i = 0; i < this.totalePosti; i++ )
                Lab.set(i, true);
            busySet = 0;
            // Prima sveglio altri professori
            labFree.signal();
            // Poi sveglio i tesisti
            PCFree.signalAll();
            // Se ci sono abbastanza PC sveglio anche gli studenti
            for ( int i = tesistiWaiting; i <= this.totalePosti; i++ )
                oneSetFree.signal();
        }
        finally { lock.unlock(); }

    }
    
}
