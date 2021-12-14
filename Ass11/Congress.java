import java.rmi.RemoteException;
import java.util.Arrays;

public class Congress implements CongressInterface {
    private int maxSessions;
    private int maxInterv;
    private int maxDays;
    private String[][][] congress;

    public Congress(int maxDays, int maxSessions, int maxInterv) 
    throws  RemoteException{
        this.maxSessions = maxSessions;
        this.maxDays = maxDays;
        this.maxInterv = maxInterv;
        this.congress = new String[maxDays][maxSessions][maxInterv];

        for ( String[][] day : congress ){
            for ( String[] session : day ){
                Arrays.fill(session, "<empty>");
            }
        }            
    }

    public void addSpeaker(int day, int session, String name) 
    throws  RemoteException, 
            FullSessionException, 
            IllegalArgumentException,
            IndexOutOfBoundsException{

        day--; session--;
        if ( day > maxDays || day < 0 || session > maxSessions || maxSessions < 0 )
            throw new IndexOutOfBoundsException();

        if ( name.equals("<empty") )
                throw new IllegalArgumentException();

        boolean ok = false;
        for ( int i = 0; i < this.maxInterv; i++ )
            if ( congress[day][session][i].equals("<empty>") ){
                congress[day][session][i] = name;
                ok = true;
                break;
            }

        if ( !ok ) throw new FullSessionException();

    }

    public String[][][] getCongress() 
    throws RemoteException{

        String[][][] clone = new String[maxDays][maxSessions][maxInterv];
        int i, j, k;
        i = j = k = 0;
        for ( String[][] day : congress ){
            for ( String[] session : day ){
                for ( String interv : session ){
                    clone[i][j][k] = new String(interv);
                    k++;
                }
                k = 0;
                j++;
            }
            j = 0;
            i++;
        }

        return clone;
    }

}
