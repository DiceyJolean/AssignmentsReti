import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Date;

public class PingClient {
    static String serverName;
    static int port;
    static InetAddress serverAddress;
    static DatagramSocket socket;
    static DatagramPacket pack;
    static int delay = 2000;
    static int toSend = 10;

    public static void main ( String args[] ){
        Thread.currentThread().setName("PingClient");

        if (args.length == 2){
            try {
                serverName = args[0];
            } catch ( Exception e ){
                System.err.printf("%s - ERR -arg 1\n", Thread.currentThread().getName());
                return;
            }
            try {
                port = Integer.parseInt(args[1]);
            } catch ( Exception e ){
                System.err.printf("%s - ERR -arg 2\n", Thread.currentThread().getName());
                return;
            }
        }
        else{
            System.err.printf("Usage: %s hostname port\n", Thread.currentThread().getName());
            return;
        }
        
        try{ 
            serverAddress = InetAddress.getByName(serverName);
        } catch ( UnknownHostException e ){
            System.err.printf("%s - Impossibile raggiungere l'host %s\n", Thread.currentThread().getName(), serverName);
            return;
        }

        try{
            // Creo la socket
            socket = new DatagramSocket();
            socket.setSoTimeout(delay);
        } catch ( SocketException e ){
            System.err.printf("%s - ERRORE\n", Thread.currentThread().getName());
            return;
        }

        double RTTsum = 0;
        long RTTmin = Long.MAX_VALUE;
        long RTTmax = 0;
        int received = 0;

        for ( int i = 0; i < toSend; i++){

            long before = new Date().getTime();
            // Messaggio da inviare al server
            String text = new String("PING " + i + " " + before);
            byte[] mex = text.getBytes();
            // Creo il pacchetto da inviare
            pack = new DatagramPacket(mex, mex.length, serverAddress, port);
            
            // Invio il pacchetto
            try {
                socket.send(pack);
            } catch ( IOException e ){
                System.err.printf("%s - Errore di tipo I/O\n", Thread.currentThread().getName());
                socket.close();
                return;
            }
            try {
                // Attendo la risposta
                socket.receive(pack);
                received++;

                long after = new Date().getTime();
                long RTT = after - before;
                RTTsum += RTT;
                RTTmax = RTT > RTTmax ? RTT : RTTmax;
                RTTmin = RTT < RTTmin ? RTT : RTTmin;

                // String recv = new String(pack.getData());
                System.out.printf("%s RTT : %d ms\n", new String(pack.getData()), RTT);

            } catch ( SocketTimeoutException e ){
                System.out.printf("%s RTT : *\n", new String(pack.getData()));
                continue;
            } catch ( IOException e ){
                System.err.printf("%s - Errore di input/output\n", Thread.currentThread().getName());
                socket.close();
                return;
            }
        }
        double avg = (received*100)/toSend;
        System.out.printf("---- PING Statistics ----\n" +
        "%d packets transmitted, %d packets received, %2.0f%% packet loss\n" +
        "round-trip (ms) min/avg/max = %d/%.2f/%d\n",
        toSend, received, avg, RTTmin, RTTsum/received, RTTmax);
    }
    
}
