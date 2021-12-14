import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.*;

public class Server {
    public static int port = 12120;
    public static String serviceName = "RMICongress";
    public static final int nSession = 12;
    public static final int nDays = 3;
    public static final int nInterv = 5;
    
    public static void main ( String args[] ){
        if ( args.length == 1){
            try{
                port = Integer.parseInt(args[0]);
            } catch (  Exception e ){
                System.out.printf("SERVER: Parametri errati, chiamare il programma come Server [port] [serviceName]\n" +
                "Impostati parametri di default\n");
            }
        }
        else if ( args.length == 2){
            try {
                port = Integer.parseInt(args[0]);
                serviceName = args[1];
            } catch ( Exception e ){
                System.out.printf("SERVER: Parametri errati, chiamare il programma come Server [port] [serviceName]\n" +
                "Impostati parametri di default\n");
            }
        }

        try{
            Congress congresso = new Congress(nDays, nSession, nInterv);

            CongressInterface stub = ( CongressInterface ) UnicastRemoteObject.exportObject(congresso, 0);
            LocateRegistry.createRegistry(port);
            Registry r = LocateRegistry.getRegistry(port);
            r.rebind(serviceName, stub);

            System.out.printf("SERVER: Pronto ( %s - %d ) \n", serviceName, port);

        } catch ( RemoteException e ){
            System.err.printf("SERVER: Errore: %s\n", e.getMessage());
            return;
        }
        
    }

}
