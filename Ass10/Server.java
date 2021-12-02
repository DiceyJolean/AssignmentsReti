import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Server{
    private static int port = 6789;
    private static int toSleep = 500;

    public static void main( String[] args ){
        InetAddress address;

        try{
            // Genero l'indirizzo IP di multicast
            address = InetAddress.getByName(args[0]);
            if ( !address.isMulticastAddress() ){
                System.err.printf("SERVER: L'indirizzo non è un Multicast Address\n");
                return;
            }
        } catch ( Exception e ){
            System.err.printf("SERVER: Errore nella creazione dell'indirizzo IP - %s\n", e.getMessage());
            return;
        }
        else
            System.out.printf("SERVER: L'indirizzo %s è un Multicast Address\n", address);
        
        try(
            // Instauro la connessione UDP
            DatagramSocket socket = new DatagramSocket();
        ){
            while ( Thread.currentThread().isInterrupted() ){

                // Preparo il messaggio da spedire
                DateFormat format = new SimpleDateFormat("dd/MM/yyy HH:mm:ss");
                Date date = Calendar.getInstance().getTime();
                byte[] buf = format.format(date).getBytes();

                // Preparo il datagramma UDP
                DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);

                // Invio il pacchetto
                System.out.printf("SERVER: Invio %s\n", format.format(date) );
                socket.send(packet);

                // Attendo un intervallo di tempo periodico tra un invio e l'altro
                Thread.sleep(toSleep); // InterruptedException gestita dal catch generico per Exception

            }
        } catch ( Exception e ){
            System.err.printf("SERVER: Errore - %s\n", e.getMessage());
            return;
        }
    }

}
