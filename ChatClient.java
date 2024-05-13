import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class ChatClient extends UnicastRemoteObject implements ChatClientInterface {
    private ChatServerInterface server;
    private String name;
    private JTextArea chatArea;
    private JTextField messageField;
    private JComboBox<String> recipientList; // Moved here for global access
    private JButton chatHistoryButton;
    private String currentRecipient;

    protected ChatClient(String name) throws RemoteException {
        super();
        this.name = name;
    }

    public void connectToServer() {
        try {
            server = (ChatServerInterface) Naming.lookup("rmi://localhost/ChatServer");
            server.registerClient(this);
        } catch (Exception e) {
            System.err.println("Client exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void sendMessage(String recipient, String message) {
        try {
            String formattedMessage = "@" + recipient + ": " + message;
            server.sendMessageToClient(name, recipient, formattedMessage);
            DatabaseHandler.getInstance().saveMessage(name, recipient, formattedMessage);
        } catch (RemoteException e) {
            System.err.println("Error sending message: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void receiveMessage(String message) throws RemoteException {
        chatArea.append(message + "\n");
    }

    @Override
    public String getName() throws RemoteException {
        return name;
    }

    @Override
    public void updateAvailableUsers(List<String> users) throws RemoteException {
        // Update recipient list
        if (recipientList != null) { // Ensure recipientList is not null
            recipientList.removeAllItems();
            for (String user : users) {
                recipientList.addItem(user);
            }
        }
    }

    public void createAndShowGUI(List<String> availableUsers) {
        JFrame frame = new JFrame("Chat Application");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLayout(new BorderLayout());

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(chatArea);
        frame.add(scrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new BorderLayout());
        messageField = new JTextField();
        messageField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String recipient = (String) recipientList.getSelectedItem();
                if (recipient != null) {
                    sendMessage(recipient, messageField.getText());
                    messageField.setText("");
                }
            }
        });

        recipientList = new JComboBox<>(availableUsers.toArray(new String[0])); // Initialize recipientList here
        inputPanel.add(recipientList, BorderLayout.WEST);
        inputPanel.add(messageField, BorderLayout.CENTER);

        chatHistoryButton = new JButton("Chat History");
        chatHistoryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String recipient = (String) recipientList.getSelectedItem();
                if (recipient != null) {
                    currentRecipient = recipient;
                    fetchChatHistory(name, recipient);
                }
            }
        });
        inputPanel.add(chatHistoryButton, BorderLayout.EAST);

        frame.add(inputPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

   private void fetchChatHistory(String sender, String recipient) {
    List<String> chatHistory = DatabaseHandler.getInstance().getChatHistory(sender, recipient);
    chatArea.setText(""); // Clear chat area before displaying history
    for (String message : chatHistory) {
        chatArea.append(message + "\n");
    }
}


    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                ChatClient client;
                try {
                    String name = JOptionPane.showInputDialog("Enter your name:");
                    client = new ChatClient(name);
                    client.connectToServer();
                    List<String> availableUsers = client.server.getAvailableUsers();
                    client.createAndShowGUI(availableUsers);
                } catch (RemoteException e) {
                    System.err.println("Client exception: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }
}
