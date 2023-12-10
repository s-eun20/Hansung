import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FriendList extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private String currentUserEmail;
    private JPanel panel;
    private JPanel panel_1;

    public FriendList(String currentUserEmail) {
        this.currentUserEmail = currentUserEmail;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 373, 675);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        panel = new JPanel();
        panel.setBackground(new Color(255, 255, 255));
        panel.setBounds(0, 0, 360, 640);
        contentPane.add(panel);
        panel.setLayout(null);
        
        panel.removeAll();

        int currentY = 63;

        panel_1 = createFriendPanel("src/image/상상부기 8.png", loadLoggedInUserNickname());
        panel_1.setBounds(84, currentY, 267, 46);
        panel.add(panel_1);
        
        JTextPane textPane = new JTextPane();
        textPane.setEditable(false);
        textPane.setFont(new Font("굴림", Font.BOLD, 31));
        textPane.setText("친구목록");
        textPane.setBounds(81, 10, 146, 46);
        panel.add(textPane);
        currentY += 50;

        List<String> otherUserNicknames = loadOtherUserNicknames();
        for (int i = 0; i < otherUserNicknames.size(); i++) {
            String imagePath = "src/image/상상부기 " + (i + 2) + ".png";
            String otherUserNickname = otherUserNicknames.get(i);

            JPanel friendPanel = createFriendPanel(imagePath, otherUserNickname);
            friendPanel.setBounds(84, currentY, 267, 46);
            panel.add(friendPanel);
            currentY += 50;
        }
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                panel.revalidate();
                panel.repaint();
            }
        });
    }

    private String loadLoggedInUserNickname() {
        String query = "SELECT nickname FROM users WHERE email = ?";
        try (Connection connection = connectToDatabase();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, currentUserEmail);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("nickname");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }

    private List<String> loadOtherUserNicknames() {
        List<String> nicknames = new ArrayList<>();
        String query = "SELECT nickname FROM users WHERE email != ?";
        try (Connection connection = connectToDatabase();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, currentUserEmail);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                nicknames.clear();  // 기존 데이터를 비우고 최신 데이터를 새로 가져오도록 함
                while (resultSet.next()) {
                    nicknames.add(resultSet.getString("nickname"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return nicknames;
    }

    private JPanel createFriendPanel(String imagePath, String nickname) {
        JPanel friendPanel = new JPanel();
        friendPanel.setBackground(new Color(221, 244, 255));

        JTextPane textPane = new JTextPane();
        textPane.setBounds(56, 0, 72, 27);
        textPane.setBackground(new Color(0, 0, 0, 0));
        textPane.setEditable(false);
        textPane.setText(nickname);

        JLabel iconLabel = new JLabel(new ImageIcon(new ImageIcon(imagePath).getImage().getScaledInstance(41, 36, Image.SCALE_DEFAULT)));
        iconLabel.setBounds(12, 10, 41, 30);

        JButton profileButton = new JButton("프로필");
        profileButton.setBounds(164, 10, 91, 23);
        profileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openProfile(nickname);
            }
        });
        friendPanel.setLayout(null);

        friendPanel.add(textPane);
        friendPanel.add(iconLabel);
        friendPanel.add(profileButton);

        return friendPanel;
    }

    private void openProfile(String userNickname) {
        Profile profileWindow = new Profile(userNickname);
        profileWindow.setVisible(true);
    }

    private Connection connectToDatabase() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/Chat";
        String user = "root";
        String password = "7981";
        return DriverManager.getConnection(url, user, password);
    }
    

    public static void main(String[] args) {
    	
    }
}
