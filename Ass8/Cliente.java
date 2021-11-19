import java.util.ArrayList;

public class Cliente{
    private String ID;
    private ArrayList<Transazione> Transazioni;

    public Cliente(String id, ArrayList<Transazione> trans){
        this.ID = id;
        this.Transazioni = trans;
    }

    public ArrayList<Transazione> getTrans(){
        return this.Transazioni;
    }
}
