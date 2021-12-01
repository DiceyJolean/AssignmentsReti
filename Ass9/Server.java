import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class Server {
    
    public static int port = 6789;
    private static int buflen = 1024;
    public static int terminationDelay = 10000;

    public static void main( String[] args ){

        ServerSocketChannel channel;
        ServerSocket socket;
        Selector selector = null;

        try{
            channel = ServerSocketChannel.open();
            channel.configureBlocking(false);

            socket = channel.socket();
            InetSocketAddress address = new InetSocketAddress(port);
            socket.bind(address);

            selector = Selector.open();

            // Prossima volta che mi sveglio dalla select dovrà essere una richiesta di connesione
            channel.register(selector, SelectionKey.OP_ACCEPT);
        } catch ( IOException e ){
            System.err.printf("%s\n", e.getMessage());
            System.exit(1);
        }

        while(true){ // Termina con interruzione da terminale
            try{
                System.out.printf("SERVER: In attesa di nuove richieste sulla porta %d\n", port);
                selector.select();
                // Tra i canali registrati sul selettore selector, seleziona quelli 
                // pronti per almeno una delle operazioni di I/O dell'interest set.
            } catch ( IOException e ){
                System.err.printf("%s\n", e.getMessage());
                break;
            }
            Set <SelectionKey> readyKeys = selector.selectedKeys();
            Iterator <SelectionKey> iterator = readyKeys.iterator();
            while ( iterator.hasNext() ){
                SelectionKey key = iterator.next();
                iterator.remove();
                // Rimuove la chiave dal Selected Set, ma non dal Registered Set
                try{
                    if ( key.isAcceptable() ){
                        // Nuova connessione accettata dal channel
                        // Connessione implicita lato Server
                        ServerSocketChannel server = (ServerSocketChannel) key.channel();
                        SocketChannel client = server.accept();
                        System.out.println("SERVER: Connessione accettata");
                        client.configureBlocking(false);
                        
                        // Nuovo client, l'operazione che voglio associare è la lettura
                        SelectionKey key2 = client.register(selector, SelectionKey.OP_READ);
                    }
                    else if ( key.isReadable() ){
                        // Equivale a key.readyOps( )& SelectionKey.OP_READ) != 0
                        // Un channel contiene dati laggere

                        System.out.printf("SERVER: Un canale è pronto in lettura\n");
                        SocketChannel client = (SocketChannel) key.channel();
                        client.configureBlocking(false);

                        String reply = new String("Echoed by Server: ");
                        String att = (String) key.attachment();
                        if ( att != null )
                            reply = reply + att;

                        ByteBuffer buf = ByteBuffer.allocate(buflen);
                        buf.clear();
                        // Leggo quello che invia il client sulla socket
                        int n = client.read(buf);

                        if ( n < 0 ){
                            // Il client ha chiuso la connessione
                            key.cancel();
                            key.channel().close();
                            System.out.printf("SERVER: Connessione chiusa dal client %s\n", client.getRemoteAddress().toString() );
                            continue;
                        }
                        
                        StringBuilder msg = new StringBuilder();
                        while ( n > 0 ){
                            // Ho letto i dati disponibili, o una parte di essi (se erano più di buflen)
                            buf.flip();
                            while ( buf.hasRemaining() )
                                // Aggiungo i byte letti a quelli già precedentemente salvati se ce n'erano
                                // Altrimenti li scrivo in append a "Echoed by Server"
                                msg.append((char) buf.get() );
                            
                            n = client.read(buf);
                        }

                        System.out.printf("SERVER: Ricevo dal client il messaggio %s\n", msg);
                        
                        // La prossima operazione da parte di questo client sarà una lettura
                        // Quindi invio i dati da scrivere e imposto la chiave in scrittura
                        reply = reply + msg;
                        key.attach(reply);
                        key.interestOps(SelectionKey.OP_WRITE);
                    }
                    else if ( key.isWritable() ){
                        // Un channel contiene dati da scrivere
                        System.out.printf("SERVER: Un canale è pronto in scrittura\n");
                        SocketChannel client = (SocketChannel) key.channel();
                        client.configureBlocking(false);

                        String string = (String) key.attachment();
                        if ( string == null )
                            string = "Echoed by server";

                        ByteBuffer buf = ByteBuffer.wrap(string.getBytes("UTF-8"));
                        int n = client.write(buf);

                        if ( n < 0 ){
                            // Il client ha chiuso la connessione
                            key.cancel();
                            key.channel().close();
                            System.out.printf("Server: Connessione chiusa dal client %s\n", client.getRemoteAddress().toString() );
                            continue;
                        }

                        System.out.printf("SERVER: Scrivo al client %s\n", string);
                        key.attach(null);
                        key.interestOps(SelectionKey.OP_READ);
                    }
                } catch ( IOException e ){
                    key.cancel();
                    try{
                        key.channel().close();
                    } catch ( IOException ex ){
                        System.err.printf("%s\n", ex.getMessage());
                        System.exit(1);
                    }
                }
            }
        }
    }

}
