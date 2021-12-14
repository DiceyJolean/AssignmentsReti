import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Client {

    /*
        Il client può richiedere operazioni per:
        • registrare uno speaker ad una sessione; 
        • ottenere il programma del congresso;

        Il client inoltra le richieste al server tramite il meccanismo di RMI. 
        Prevedere, per ogni possibile operazione una gestione di eventuali 
        condizioni  anomale (ad esempio la richiesta di registrazione ad una 
        giornata e/o sessione inesistente oppure per la quale sono già stati 
        coperti tutti gli spazi d’intervento).

        Il client è implementato come un processo ciclico che continua a fare 
        richieste sincrone fino ad esaurire tutte le esigenze utente.
        Stabilire una opportuna condizione di terminazione del processo di richiesta.
    */

    public static int port = 12120;
    public static String serviceName = "RMICongress";


    public static void printCongress(String[][][] congress){
        
        try {
            int i = 1, j = 1;
            for ( String[][] day : congress ){
                System.out.printf("\n\n \t\t Giornata %d\n\n", i);
                i++;
                for ( String[] session : day ){
                    System.out.printf("\t\t -------------------------------------------------------------------------------- \n" +
                    "Sessione %d\t |", j);
                    j++;
                    for ( String interv : session )
                        System.out.printf("\t%s\t|", interv);

                    System.out.printf("\n");
                }
                System.out.printf("\t\t -------------------------------------------------------------------------------- \n");
                j = 1;
            }

        } catch ( Exception e ){
            e.printStackTrace();
        }
        return;
    }

    public static void main ( String[] args ){
        Registry r;
        CongressInterface congresso = null;

        if ( args.length == 1){
            try{
                port = Integer.parseInt(args[0]);
            } catch (  Exception e ){
                System.out.printf("CLIENT: Parametri errati, chiamare il programma come Client [port] [serviceName]\n" +
                "Impostati parametri di default\n");
            }
        }
        else if ( args.length == 2){
            try {
                port = Integer.parseInt(args[0]);
                serviceName = args[1];
            } catch ( Exception e ){
                System.out.printf("CLIENT: Parametri errati, chiamare il programma come Client [port] [serviceName]\n" +
                "Impostati parametri di default\n");
            }
        }

        try{
            r = LocateRegistry.getRegistry(port);
            congresso = ( CongressInterface ) r.lookup(serviceName);
        } catch ( Exception e ){
            System.err.printf("CLIENT: Errore: %s\n", e.getMessage());
            System.exit(1);
        }

        try (
            Scanner in = new Scanner(System.in);
        ){
            String op = in.next();
            // Il client termina quando legge il carattere q
            while ( !op.equals("q") ){
                switch(op){
                    case "addSpeaker":{
                        try {
                            int day = in.nextInt();
                            int session = in.nextInt();
                            String name = in.next();

                            congresso.addSpeaker(day, session, name);
                        } catch ( InputMismatchException e ){
                            System.err.printf("CLIENT: Errore nei parametri della funzione addSpeaker\n");
                        } catch ( FullSessionException e ){
                            System.out.printf("CLIENT: La sessione richiesta è al completo\n");
                        } catch ( IllegalArgumentException e ){
                            System.out.printf("CLIENT: Il nome dello speaker indicato non è valido\n");
                        } catch ( IndexOutOfBoundsException e ){
                            System.out.printf("CLIENT: La sessione indicata non è disponibile\n");
                        } catch ( RemoteException e ){
                            System.err.printf("CLIENT: Errore - %s\n", e.getMessage());
                            System.exit(1);
                        }
                    } break;
                    case "getCongress":{

                        try {
                            String[][][] matrix = congresso.getCongress();
                            printCongress(matrix);

                        } catch ( RemoteException e ){
                            System.err.printf("CLIENT: Errore - %s\n", e.getMessage());
                            System.exit(1);
                        }
                    } break;
                    case "help":
                    case "-h":
                    case "--help":{
                        System.out.printf("Usare:\n" +
                        "\taddSpeaker <day> <session> <name> : Registra lo speaker name alla sessione session nel giorno day \n" +
                        "\tgetCongress : Ottiene il programma del congresso \n" +
                        "\thelp | -h | --help : Visualizza questo messaggio\n" +
                        "\tq : Termina il programma\n");
                    } break;
                    default:{
                        System.out.printf("CLIENT: Operazione non consentita\n");
                    } break;
                }
                op = in.next();
            }
            System.out.printf("CLIENT: Terminazione\n");

        } catch ( Exception e ){
            System.err.printf("CLIENT: Errore - %s\n", e.getMessage());
            System.exit(1);
        }

    }

}
