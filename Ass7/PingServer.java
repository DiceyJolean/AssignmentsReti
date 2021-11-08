import java.io.IOException;
import java.net.BindException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Random;

public class PingServer {
    static int port = 10023;
    static long seed = 123;
    static int terminationDelay = 15000;
    static int bufSize = 1024;
    static DatagramSocket socket;
    static DatagramPacket pack;

    public static void main ( String args[] ){
        Thread.currentThread().setName("PingServer");
/*        
        if ( args.length > 0 ){
            try {
                port = Integer.parseInt(args[0]);
            } catch ( Exception e ){
                System.err.printf("%s - ERR -arg 1\n", Thread.currentThread().getName());
                return;
            }
            if (args.length == 2){
                try {
                    seed = Long.parseLong(args[1]); // Sovrascrivo
                } catch ( Exception e ){
                    System.err.printf("%s - ERR -arg 2\n", Thread.currentThread().getName());
                    return;
                }
            }
        }
        else{
            System.err.printf("Usage: %s port [seed]\n", Thread.currentThread().getName());
            return;
        }
*/
        try {
            socket = new DatagramSocket(port);
            socket.setSoTimeout(terminationDelay);

        } catch ( BindException e ){
            System.err.printf("%s - Porta già in uso\n", Thread.currentThread().getName());
            return;
        } catch ( SocketException e ){
            System.err.printf("%s - ERRORE\n", Thread.currentThread().getName());
            return;
        }

        byte[] bytebuf = new byte[bufSize];
        pack = new DatagramPacket(bytebuf, bytebuf.length);
        Random generator = new Random(seed);

        try{
            while ( true ){
                socket.receive(pack);

                long num = Math.abs(generator.nextLong()) % 1000; // Attesa tra 0 e 999 millisecondi
                // Simulo la perdita (25% di probabilità)
                if ( (num % 4) == 0 )
                    System.out.printf("%s:%d %s ACTION: not sent\n", pack.getAddress().getHostAddress(), pack.getPort(), new String(pack.getData()));
                else{
                    // Simulo il ritardo
                    try {
                        Thread.sleep(num);
                        System.out.printf("%s:%d %s ACTION: delayed %d ms\n", pack.getAddress().getHostAddress(), pack.getPort(), new String(pack.getData()), num);
                    } catch ( InterruptedException e ){
                        System.err.printf("%s - Interrotto, in chiusura\n", Thread.currentThread().getName());
                        socket.close();
                        return;
                    }

                    socket.send(pack);
                }
            }
        } catch ( SocketTimeoutException e ){
            System.out.printf("%s - Timeout scaduto, in chiusura\n", Thread.currentThread().getName());
            socket.close();
            return;
        } catch ( IOException e ){
            System.err.printf("%s - ERRORE\n", Thread.currentThread().getName());
            return;
        }

    }

}
