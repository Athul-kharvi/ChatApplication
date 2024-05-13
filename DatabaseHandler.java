import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler {
    private Connection connection;
    private static final String URL = "jdbc:mysql://127.0.0.1:3307/athul";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";

    private static DatabaseHandler instance;

    private DatabaseHandler() {
        try {
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static synchronized DatabaseHandler getInstance() {
        if (instance == null) {
            instance = new DatabaseHandler();
        }
        return instance;
    }

    public void saveMessage(String sender, String recipient, String message) {
        String query = "INSERT INTO communication (senderName, recipientName, message) VALUES (?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, sender);
            preparedStatement.setString(2, recipient);
            preparedStatement.setString(3, message);
            preparedStatement.executeUpdate();
            //System.out.println("hello wrol");
        } catch (SQLException e) {
             System.out.println("int the insert command");
            e.printStackTrace();
        }
    }

    public List<String> getChatHistory(String sender, String recipient) {
        List<String> history = new ArrayList<>();
        String query = "SELECT * FROM communication WHERE senderName = ? AND recipientName = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, sender);
            preparedStatement.setString(2, recipient);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String time = resultSet.getString("time");
                String message = resultSet.getString("message");
                history.add(time + " - " + message);
            }
        } catch (SQLException e) {
            System.out.println("int the databasehandler");
            e.printStackTrace();
        }
        return history;
    }
}
