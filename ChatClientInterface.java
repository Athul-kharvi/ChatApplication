import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ChatClientInterface extends Remote {
    void receiveMessage(String message) throws RemoteException;
    String getName() throws RemoteException;
    void updateAvailableUsers(List<String> users) throws RemoteException;
}
