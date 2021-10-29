import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;
import java.util.Date;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class Worker implements Runnable{
    // gestisce richieste Http di tipo GET alla Request URL localhost:port/filename

    private Socket task;

    public Worker(Socket task){
        this.task = task;
    }

    @Override
    public void run(){
        // Leggo la richiesta del client
        try{
            // Leggo la richiesta del client
            BufferedReader read = new BufferedReader(new InputStreamReader(task.getInputStream()));
            // Assumo che sia una richiesta HTTP di tipo GET
            String header = read.readLine();
            assert(header.startsWith("GET"));
                
            // Posso gestire la richiesta
            // Il file sarà indicato dal secondo termine dell'header
            String[] token = header.split("\\s"); // see StringTokenizer docs
            String filename = token[1].substring(1); // Tolgo / dal nome del file

            // recupero il file dall'header
            // Non conosco il formato, quindi lo tratto come binario
            BufferedInputStream fileRequest = null;
            String text = null;
            OutputStream out = task.getOutputStream();

            try{
                fileRequest = new BufferedInputStream(new FileInputStream(new File(filename)));
                String type = filename.substring(filename.lastIndexOf('.')+1);

                byte[] content = fileRequest.readAllBytes();
                text = "HTTP/1.0 200 OK\n"
                + "Date: " + new Date() + "\n"
                + "Server: Localhost\n"
                + "Content-type: " + type + "\n"
                + "Content-length: " + content.length +"\n"
                + "\n"; // Doppio a capo prima del body

                task.getOutputStream().write(text.getBytes());
                task.getOutputStream().write(content);

                fileRequest.close();
                System.out.printf("Worker: Richiesta elaborata correttamente\n");

            }
            catch ( FileNotFoundException e ){

                text = "HTTP/1.0 404 Not found\n"
                + "Date: " + new Date() + "\n"
                + "Server: Localhost\n";

                out.write(text.getBytes());
                System.out.printf("Worker: Il file richiesto non è stato trovato\n");
            }
            catch ( Exception ex ){
                // Problema generico, rispondo comunque al client
                text = "HTTP/1.0 500 Some error has occured\n"
                + "Date: " + new Date() + "\n"
                + "Server: Localhost\n";

                out.write(text.getBytes());
                System.out.printf("Worker: Errore\n");
            }

            out.close();
        }
        catch ( Exception e ){
            e.printStackTrace();
        }
        finally {
            try{
                task.close(); // Connessione non persistente
            }
            catch ( IOException e ){
                e.printStackTrace();
                return;
            }
        }
    }
    
}
