import java.util.ArrayList;

public class CounterThread implements Runnable{
    public Cliente c;
    public Counter counter;

    public CounterThread(Cliente c, Counter counter){
        this.c = c;
        this.counter = counter;
    }

    @Override
    public void run(){
        // Aggiorno il contatore globale con le transazioni di questo cliente
        ArrayList<Transazione> transArr = c.getTrans();
        for ( Transazione t : transArr ){
            switch( t.getCausale() ){
                case "Bonifico":{
                    counter.incBon();
                    break;
                }
                case "Bollettino":{
                    counter.incBol();
                    break;
                }
                case "Accredito":{
                    counter.incAcc();
                    break;
                }
                case "F24":{
                    counter.incF24();
                    break;
                }
                case "PagoBancomat":{
                    counter.incPag();
                    break;
                }
                default:{
                    break;
                }
            }
        }
    }

}
