public class Counter {
    private int nBon; // Numero di bonifici trovati
    private int nBol; // Numero di bollettini trovati
    private int nF24; // Numero di F24 trovati
    private int nAcc; // Numero di accrediti trovati
    private int nPag; // Numero di pagoBancomat trovati

    public Counter(){
        nBol = nBon = nAcc = nF24 = nPag = 0;
    }

    // Incrementa di uno il contatore dei bonifici
    synchronized public void incBon(){
        nBon++;
    }

    // Incrementa di uno il contatore dei bollettini
    synchronized public void incBol(){
        nBol++;
    }

    // Incrementa di uno il contatore degli accrediti
    synchronized public void incAcc(){
        nAcc++;
    }

    // Incrementa di uno il contatore degli F24
    synchronized public void incF24(){
        nF24++;
    }

    // Incrementa di uno il contatore dei pagoBancomat
    synchronized public void incPag(){
        nPag++;
    }

    // Restituisce il numero dei bonifici trovati
    synchronized public int getBon(){
        return nBon;
    }

    // Restituisce il numero dei bollettini trovati
    synchronized public int getBol(){
        return nBol;
    }

    // Restituisce il numero degli accrediti trovati
    synchronized public int getAcc(){
        return nAcc;
    }

    // Restituisce il numero degli F24 trovati
    synchronized public int getF24(){
        return nF24;
    }

    // Restituisce il numero dei pagoBancomat trovati
    synchronized public int getPag(){
        return nPag;
    }

}
