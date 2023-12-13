import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;
public class ChatDatabaseManager {

    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/Chat";
    private static final String USER = "root";  // 사용자 이름으로 수정
    private static final String PASSWORD = "7981";  // 비밀번호로 수정

    public static void saveChatMessage(String roomID, String sender, String message) {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "INSERT INTO chatmessages (RoomID, Sender, MessageText, Timestamp) VALUES (?, ?, ?, ?)")) {

            preparedStatement.setString(1, roomID);
            preparedStatement.setString(2, sender);
            preparedStatement.setString(3, message);
            preparedStatement.setString(4, getCurrentTimestamp());

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static String getCurrentTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date());
    }
    
    public static List<ChatMessage> loadChatHistory(String roomName) {
        List<ChatMessage> chatHistory = new ArrayList<>();

        String query = "SELECT Sender, MessageText, timestamp FROM ChatMessages WHERE RoomId = ? ORDER BY timestamp ASC";

        try (Connection connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, roomName);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    String sender = resultSet.getString("Sender");
                    String messageText = resultSet.getString("MessageText");
                    long timestamp = resultSet.getTimestamp("timestamp").getTime();

                    ChatMessage chatMessage = new ChatMessage(sender, messageText, timestamp);
                    chatHistory.add(chatMessage);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return chatHistory;
    }

}
