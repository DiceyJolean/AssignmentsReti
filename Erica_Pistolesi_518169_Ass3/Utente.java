import java.util.concurrent.ThreadLocalRandom;

public class Utente implements Runnable {
    private String ruolo;
    private int index;
    private Laboratorio Lab;
    private final int minDelay = 200;
    private final int maxDelay = 1000;

    public Utente(int i, Laboratorio Lab, String ruolo){
        this.index = i;
        this.Lab = Lab;
        this.ruolo = ruolo;
    }

    @Override
    public void run(){
        if ( this.ruolo.compareTo("Professore") == 0 ){
            // Necessita di tutto il laboratorio libero
            try { 
                Lab.lockEntireLab();
                System.out.printf("Professore - Entro nel laboratorio\n");
                Thread.sleep(ThreadLocalRandom.current().nextInt(minDelay, maxDelay));
                System.out.printf("Professore - Esco nel laboratorio\n");
                Lab.unlockEntireLab();
            }
            catch ( InterruptedException e ){
                System.out.printf("Professore - Interrotto\n");
                return;
            }
        }
        if ( this.ruolo.compareTo("Tesista") == 0 ){
            // Necessita del suo PC libero (indicato da index)
            try { 
                Lab.setPC(this.index);
                System.out.printf("Tesista - Occupo il PC %d\n", this.index);
                Thread.sleep(ThreadLocalRandom.current().nextInt(minDelay, maxDelay));
                System.out.printf("Tesista - Esco nel laboratorio\n");
                Lab.freePC(this.index);
            }
            catch ( InterruptedException e ){
                System.out.printf("Tesista - Interrotto\n");
                return;
            }
        }
        if ( this.ruolo.compareTo("Studente") == 0 ){
            // Necessita di un PC qualsiasi libero
            try { 
                int pc = Lab.isOneFreePC();
                System.out.printf("Studente - Occupo il PC %d\n", this.index);
                Thread.sleep(ThreadLocalRandom.current().nextInt(minDelay, maxDelay));
                System.out.printf("Studente - Esco nel laboratorio\n");
                Lab.freePC(pc);
            }
            catch ( InterruptedException e ){
                System.out.printf("Studente - Interrotto\n");
                return;
            }
        }
    }
    
}
