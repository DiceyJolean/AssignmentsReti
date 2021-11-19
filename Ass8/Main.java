import java.util.Scanner;

public class Main {

    // Libreria esterna importata: gson-2.8.9.jar

    static int nClienti;
    static int nThreads;
    static int nTrans;
    final static String file = "./contiCorrente.json";
    static int terminationDelay = 15000;
    
    public static void main ( String args[]){
        try{
            if ( args.length == 3 ){
                nClienti = Integer.parseInt(args[0]);
                nTrans = Integer.parseInt(args[1]);
                nThreads = Integer.parseInt(args[2]);
            }
            else{
                try( Scanner in = new Scanner(System.in) ){
                    System.out.printf("Inserire quanti clienti creare, quante transazioni assegnare a ognuno e quanti thread eseguire\n");
                    
                    nClienti = in.nextInt();
                    nTrans = in.nextInt();
                    nThreads = in.nextInt();
                    in.close();
                }
            }
        } catch ( Exception e ){
            nClienti = 100;
            nThreads = 8;
            nTrans = 30;
        }
        
        JSONCreator json = new JSONCreator(nClienti, nTrans);
        json.createJSON(file);
        Reader parser = new Reader(file, nThreads);
        parser.start();
        try{
            parser.join(terminationDelay);
        } catch ( InterruptedException e ){
            System.err.printf("Main - Interrotto, terminazione\n");
            parser.interrupt();
            System.exit(1);
        }
        if ( parser.isAlive() ){
            parser.interrupt();
        }
    }

}
