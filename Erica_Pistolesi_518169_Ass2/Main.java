import java.util.InputMismatchException;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.concurrent.RejectedExecutionException;

public class Main {

    public static void main (String args[]){
        Thread.currentThread().setName("Ufficio Postale");

        Scanner scan = new Scanner(System.in);
        long tempoApertura = 0;
        int k = 0;
        try {
            System.out.printf("%s - Inserire un intero per indicare la capienza della seconda sala\n", Thread.currentThread().getName());
            k = scan.nextInt();
            System.out.printf("%s - Inserire un long per indicare il tempo di apertura dell'ufficio\n", Thread.currentThread().getName());
            tempoApertura = scan.nextLong();
            scan.close();
        } catch ( InputMismatchException e ){
            System.out.printf("%s - Formato parametri errato\n", Thread.currentThread().getName());
            return;
        }
        
        UfficioPostale up = new UfficioPostale(4, k);
        LinkedList<Utente> primaSala = new LinkedList<Utente>();

        // Avvio un timer per la terminazione (Evito while(true) )
        Timeout timer = new Timeout(tempoApertura);
        timer.start();
        for ( int i = 1; timer.isAlive(); i++){ // while ( timer.isAlive() ){
            // Nuovo cliente nella prima sala d'attesa
            Utente utente = new Utente(i);
            primaSala.add(utente);
            
            // Prendo il primo utente in coda in sala d'attesa
            Utente daServire = primaSala.getFirst();
            // Aggiungo il nuovo utente al pool (passa nella seconda sala)
            try {
                System.out.printf("%s - L'utente %d cerca di entrare nella seconda sala\n", Thread.currentThread().getName(), daServire.getId());
                up.newUtente(daServire);
                primaSala.remove(daServire);
            } catch ( RejectedExecutionException e ){
                // Non c'è spazio nella seconda sala
                // Ritorna nella prima sala
                if ( primaSala.contains(daServire) )
                    primaSala.add(utente);
                System.out.printf("%s - Non c'è abbastanza spazio nella seconda sala\n", Thread.currentThread().getName());
            }
            // Prima che entri un nuovo utente passa qualche millisecondo
            try {
                Thread.sleep(200);
            } catch ( InterruptedException e ){
                System.err.printf("%s - Sono stato interrotto\n", Thread.currentThread().getName());
                return;
            }
        }
        // Scaduto il tempo di apertura
        up.closeOffice();
        System.out.printf(("%s - In chiusura, tutti gli utenti rimasti nella prima sala d'attesa usciranno\n"), Thread.currentThread().getName());
        primaSala.clear();
        // Se c'è ancora qualcuno agli sportelli lo interrompo
        up.bruteClose();
    }
}
