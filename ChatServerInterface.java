import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ChatServerInterface extends Remote {
    void registerClient(ChatClientInterface client) throws RemoteException;
    void broadcastMessage(String message) throws RemoteException;
    void sendMessageToClient(String sender, String recipient, String message) throws RemoteException;
    List<String> getAvailableUsers() throws RemoteException;
}
