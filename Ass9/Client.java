import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class Client{

    private static int port = 6789;

    public static void main ( String[] args ){
        SocketAddress socket = new InetSocketAddress(port);
        SocketChannel channel;
        try {
            // Creazione esplicita lato client
            channel = SocketChannel.open();
            channel.connect(socket);

            // Leggo da console il messaggio da inviare al server
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            String msg = null;
            // Come terminazione mi aspetto un "q" da console
            while ( !( msg = in.readLine() ).equals("q") ){
                // Invio il messaggio scrivendo sul canale
                ByteBuffer buf = ByteBuffer.wrap(msg.getBytes());

                channel.write(buf);

                System.out.printf("CLIENT: Ho inviato il messaggio %s sul canale\n", msg);
                
                // Adesso leggo la risposta
                buf.clear();                
                StringBuilder string = new StringBuilder();
                // In modalità bloccante si blocca finché il Server non scrive
                int n = channel.read(buf);
                // Condizione per uscire dal while sulla read
                boolean readAll = false;

                while ( !readAll  ){
                    buf.flip();

                    while ( buf.hasRemaining() )
                        string.append( (char) buf.get());

                    buf.clear();

                    n = channel.read(buf);
                    if ( n < buf.capacity() )
                        // Ho letto tutto con quest'ultima read
                        readAll = true;
                }
                // Mi rimangono da scrivere i bytes dell'ultima read
                buf.flip();

                while ( buf.hasRemaining() )
                    string.append( (char) buf.get());

                buf.clear();

                System.out.printf("CLIENT: Leggo %s dal canale\n", string);
            }
            in.close();
        } catch( IOException e ){
            System.err.printf("%s\n", e.getMessage());
            System.exit(1);
        }

    }

}
