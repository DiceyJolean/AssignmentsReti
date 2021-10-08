import java.util.InputMismatchException;
import java.util.Scanner;

public class Main{
    public static void main(String[] args) throws InputMismatchException, IllegalArgumentException {
        Scanner scan = new Scanner(System.in);
        double accuracy = 0.0;
        long timeout = 0;

        try{
            accuracy = scan.nextDouble();
            timeout = scan.nextLong();
            scan.close();
        } catch( InputMismatchException e ){
            e.printStackTrace();
            System.out.println(Thread.currentThread().getName()+": Invalid argument, accuracy must be double and timeout must be long\n");
            return;
        }

        if ( timeout < 0 ) throw new IllegalArgumentException("Timeout must be a positive value\n");
        if ( accuracy < 0 ) throw new IllegalArgumentException("Accuracy must be a positive value\n");

        Pi pi = new Pi(accuracy);
        Thread t = new Thread(pi);
        t.start();
        try {
            t.join(timeout);
        } catch ( InterruptedException e ){
            e.printStackTrace();
            System.out.println(Thread.currentThread().getName()+": Interrupted\n");
            return;
        }

        if ( t.isAlive() )
            t.interrupt();
                   
    }
}