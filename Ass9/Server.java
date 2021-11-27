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
    public static int buflen = 1024;

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

        while(true){
            try{
                System.out.printf("Server: In attesa di nuove richieste sulla porta %d\n", port);
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
                        System.out.println("Server: Connessione accettata con il client " + client);
                        client.configureBlocking(false);
                        
                        // Nuovo client, l'operazione che voglio associare è la lettura
                        SelectionKey key2 = client.register(selector, SelectionKey.OP_READ);
                    }
                    else if ( key.isReadable() ){
                        // Equivale a key.readyOps( )& SelectionKey.OP_READ) != 0
                        // Un channel contiene dati laggere
                        SocketChannel client = (SocketChannel) key.channel();
                        // client.configureBlocking(false);
                        /* 
                        Il metodo attachment è necessario perchè le operazioni di lettura 
                        o scrittura non bloccanti non possono essere considerate atomiche: 
                        caso in cui la lettura/scrittura precedente non ha letto/scritto tutti i bytes richiesti.
                        Utile quando si vuole accedere ad informazioni relative al canale (associato ad
                        una chiave) che riguardano il suo stato pregresso.
                        Consente di tenere traccia di quanto è stato fatto in una operazione
                        precedente
                        */
                        StringBuilder reply = new StringBuilder("Echoed by Server: ");
                        String att = (String) key.attachment();
                        reply.append(att);

                        ByteBuffer buf = ByteBuffer.allocate(buflen);
                        buf.clear();
                        // Leggo quello che invia il client sulla socket
                        int n = client.read(buf);

                        if ( n < 0 ){
                            // Il client ha chiuso la connessione
                            key.cancel();
                            key.channel().close();
                            System.out.printf("Server: Connessione chiusa dal client %s\n", client.getRemoteAddress().toString() );
                            continue;
                        }
                        
                        while ( n > 0 ){
                            // Ho letto i dati disponibili, o una parte di essi (se erano più di buflen)
                            buf.flip();
                            while ( buf.hasRemaining() )
                                // Aggiungo i byte letti a quelli già precedentemente salvati se ce n'erano
                                // Altrimenti li scrivo in append a "Echoed by Server"
                                reply.append((char) buf.get() );
                            
                            n = client.read(buf);
                        }
                        key.attach(reply);
                        key.interestOps(SelectionKey.OP_WRITE);
                    }
                    else if ( key.isWritable() ){
                        // Un channel contiene dati da scrivere
                        SocketChannel client = (SocketChannel) key.channel();

                        StringBuilder string = new StringBuilder();
                        String att = (String) key.attachment();
                        string.append(att);

                        ByteBuffer buf = ByteBuffer.allocate(buflen);
                        
                        int n = client.write(buf);

                        if ( n < 0 ){
                            // Il client ha chiuso la connessione
                            key.cancel();
                            key.channel().close();
                            System.out.printf("Server: Connessione chiusa dal client %s\n", client.getRemoteAddress().toString() );
                            continue;
                        }
                        
                        while ( n > 0 ){
                            // Ho letto i dati disponibili, o una parte di essi (se erano più di buflen)
                            while ( buf.hasRemaining() )
                                // Aggiungo i byte letti a quelli già precedentemente salvati se ce n'erano
                                // Altrimenti li scrivo in append a "Echoed by Server"
                                string.append((char) buf.get() );
                            
                            n = client.write(buf);
                        }
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
