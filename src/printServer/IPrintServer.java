package printServer;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.UUID;

public interface IPrintServer extends Remote {

    void print(String filename, String printer, UUID token) throws RemoteException; // prints file filename on the specified printer
    String queue(String printer, UUID token) throws RemoteException; // lists the print queue for a given printer on the user's display in lines of the form <job number> <file name>
    void topQueue(String printer, int job, UUID token) throws RemoteException; // moves job to the top of the queue
    UUID start(String user) throws RemoteException; // starts the print server
    void stop(UUID token) throws RemoteException; // stops the print server
    void restart(UUID token) throws RemoteException; // stops the print server, clears the print queue and starts the print server again
    boolean status(String printer, UUID token)  throws RemoteException; // prints status of printer on the user's display
    String readConfig(String parameter) throws RemoteException; // prints the value of the parameter on the user's display
    void setConfig(String parameter, String value) throws RemoteException; // sets the parameter to value
    boolean isAuthorized(String username, String password) throws RemoteException, SQLException, NoSuchAlgorithmException; // validates user credentials
    boolean isStarted() throws RemoteException; //checks if the printer is started
}



