import java.lang.Thread;
import java.lang.Math;

public class Pi implements Runnable{
    private double accuracy;
    private double pi;

    /**
     * 
     * @param accuracy
     */
    public Pi(double accuracy){
        this.accuracy = accuracy;
        pi = 0.0;
    }

    @Override
    public void run(){
        int i = 0;
        double dispari = 1;
        // Finché non ho raggiunto la giusta precisione o finché non vengo interrotto (dal timer)
        while ( Math.abs(Math.PI - pi) >= accuracy ){
            if ( Thread.currentThread().isInterrupted() ){
                System.out.println(Thread.currentThread().getName()+": Sono stato interrotto durante il calcolo\n");
                break;
            }

            if ( ( i % 2 ) == 0 ){
                pi = pi + (4/dispari);
            }
            else{
                pi = pi - (4/dispari);
            }
            
            i++;
            dispari = dispari + 2;
        }
        System.out.println("PI calcolato con la serie di Gregory-Leibniz con grado di accuratezza "+accuracy+" ha come valore "+ pi+" (Math.PI = "+Math.PI+")"+"\n");
    }

}