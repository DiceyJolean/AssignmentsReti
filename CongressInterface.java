import java.rmi.Remote;
import java.rmi.RemoteException;

public interface CongressInterface extends Remote{
    
    public void addSpeaker(int day, int session, String name) 
    throws  RemoteException, 
            FullSessionException, 
            IllegalArgumentException,
            IndexOutOfBoundsException;

    public String[][][] getCongress() 
    throws RemoteException;

}
