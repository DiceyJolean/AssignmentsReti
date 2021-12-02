import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

public class Client {
    private static int port = 12070;
    private static int iteration = 10;
    private static int buflen = 1024;
    private static int delay = 5000;

    public static void main ( String[] args ){
        MulticastSocket multicastSocket = null;
        InetAddress address = null;
        
        try{
            address = InetAddress.getByName(args[0]);
            if ( !address.isMulticastAddress() ){
                System.err.printf("CLIENT: L'indirizzo %s non è un Multicast Address\n", address);
                return;
            }
        }
        catch ( UnknownHostException e ){
            System.err.printf("CLIENT: %s", e.getMessage());
            return;
        }
        
        try{
            multicastSocket = new MulticastSocket(port);
            multicastSocket.joinGroup(address);
            multicastSocket.setSoTimeout(delay);
        }
        catch ( Exception e ){
            System.err.printf("CLIENT: Errore unendosi al gruppo multicast - %s\n", e.getMessage());
            return;
        }

        byte[] buf = new byte[buflen];
        DatagramPacket packet = new DatagramPacket(buf, buflen);

        for ( int i = 0; i < iteration; i++ ){
            try{
                multicastSocket.receive(packet);

                System.out.printf("CLIENT: Ricevo \"%s\"\n", new String(packet.getData() ) );
            }
            catch ( IOException e ){
                System.err.printf("CLIENT: Errore di IO nella ricezione - %s\n", e.getMessage());
                continue;
            }

        }

        multicastSocket.close();
    }

}
