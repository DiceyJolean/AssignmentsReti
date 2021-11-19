public class Transazione{
    private String Data;
    private String Causale;

    public Transazione(String data, String causale){
        this.Data = data;
        this.Causale = causale;
    }

    public String getCausale(){
        return this.Causale.toString();
    }
}