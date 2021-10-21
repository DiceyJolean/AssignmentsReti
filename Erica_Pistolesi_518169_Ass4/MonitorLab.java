import java.util.ArrayList;

public class MonitorLab {

    /*  Implementa le funzioni per la sincronizzazione
        Rappresento i PC con un array di "totalePosti" booleani: 
        true PC libero, false PC occupato
    */

    private ArrayList<Boolean> Lab; // ArrayList che rappresenta i PC
    private int busySet; // Intero che rappresenta quanti posti sono occupati
    private int profWaiting;
    private final int totalePosti;

    // Inizializzo il laboratorio con tutti i PC liberi
    public MonitorLab(int totalePosti){
        this.totalePosti = totalePosti;
        busySet = 0;
        profWaiting = 0;
        Lab = new ArrayList<Boolean>(totalePosti);
        for ( int i = 0; i < this.totalePosti; i++ ){
            Lab.add(i, true);
        }
    }

    // Assegno il PC i
    public synchronized void setPC(int i) throws InterruptedException{
        // Finché ci sono professori in attesa
        while ( Lab.get(i) == false || profWaiting > 0 ){
            try { this.wait(); }
            catch ( InterruptedException e ){
                throw new InterruptedException();
            }
        }
        // Il posto i è libero
        busySet++;
        assert(busySet<=this.totalePosti);
        Lab.set(i, false);
        
    }

    // Controllo se c'è almeno un PC disponibile e lo assegno
    public synchronized int isOneFreePC() throws InterruptedException{
        int i = -1;
        // Finché ci sono professori in attesa
        while ( busySet == this.totalePosti || profWaiting > 0 )
            try { this.wait(); }
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
        return i;
    }

    // Libero il PC i
    public synchronized void freePC(int i){
        if ( Lab.get(i) == false ){
            Lab.set(i, true);
            busySet--;
        }
        assert(busySet>=0);
        this.notifyAll();
    }

    public synchronized void lockEntireLab() throws InterruptedException{
        while ( busySet > 0 )
            try { 
                profWaiting++;
                this.wait(); 
                profWaiting--;
            }
            catch ( InterruptedException e ){
                throw new InterruptedException();
            }
        for ( int i = 0; i < this.totalePosti; i++ )
            Lab.set(i, false);
        busySet = this.totalePosti;
    }

    public synchronized void unlockEntireLab(){
        for ( int i = 0; i < this.totalePosti; i++ )
            Lab.set(i, true);
        busySet = 0;
        this.notifyAll();
    }
    
}