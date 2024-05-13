import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class ChatServer extends UnicastRemoteObject implements ChatServerInterface {
    private ArrayList<ChatClientInterface> clients;

    public ChatServer() throws RemoteException {
        clients = new ArrayList<>();
    }

    
    public synchronized void registerClient(ChatClientInterface client) throws RemoteException {
        clients.add(client);
        updateAvailableUsers();
    }

    
    public synchronized void unregisterClient(ChatClientInterface client) throws RemoteException {
        clients.remove(client);
        updateAvailableUsers();
    }

    @Override
    public synchronized void sendMessageToClient(String sender, String recipient, String message) throws RemoteException {
        for (ChatClientInterface client : clients) {
            if (client.getName().equals(recipient)) {
                client.receiveMessage(message);
                return; // Exit loop after sending message to the recipient
            }
        }
    }

    @Override
    public synchronized void broadcastMessage(String message) throws RemoteException {
        for (ChatClientInterface client : clients) {
            client.receiveMessage(message);
        }
    }

    private synchronized void updateAvailableUsers() throws RemoteException {
        List<String> availableUsers = getAvailableUsers();
        for (ChatClientInterface client : clients) {
            client.updateAvailableUsers(availableUsers); // Notify each client about the updated list
        }
    }

    public static void main(String[] args) {
        try {
            ChatServer server = new ChatServer();
            Naming.rebind("rmi://192.168.10.9/ChatServer", server);
            System.out.println("Server running...");
        } catch (Exception e) {
            System.err.println("Server exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public synchronized List<String> getAvailableUsers() {
        List<String> userList = new ArrayList<>();
        for (ChatClientInterface client : clients) {
            try {
                userList.add(client.getName());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return userList;
    }
}
