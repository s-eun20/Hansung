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
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
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

        int currentY = 119;
        

        panel_1 = createFriendPanel(loadLoggedInUserImagePath(), loadLoggedInUserNickname());
        panel_1.setBounds(62, 63, 295, 46);
        panel.add(panel_1);
        
        JPanel panel_2 = new JPanel();
        panel_2.setBackground(new Color(224, 240, 254));
        panel_2.setLayout(null);
        panel_2.setBounds(0, 0, 60, 640);
        panel.add(panel_2);
        
        JButton mainButton = new JButton();
        mainButton.setIcon(new ImageIcon(FriendList.class.getResource("/image/free-icon-person-7542670.png")));
        mainButton.setFocusPainted(false);
        mainButton.setBorderPainted(false);
        mainButton.setBackground(new Color(224, 240, 254));
        mainButton.setBounds(10, 32, 40, 40);
        panel_2.add(mainButton);
        
        JButton chatListButton = new JButton();
        chatListButton.setIcon(new ImageIcon(FriendList.class.getResource("/image/free-icon-chat-5962500.png")));
        chatListButton.setFocusPainted(false);
        chatListButton.setBorderPainted(false);
        chatListButton.setBackground(new Color(224, 240, 254));
        chatListButton.setBounds(10, 85, 40, 40);
        panel_2.add(chatListButton);
        
        JTextPane textPane_1 = new JTextPane();
        textPane_1.setText("나");
        textPane_1.setFont(new Font("Dialog", Font.BOLD, 21));
        textPane_1.setEditable(false);
        textPane_1.setBounds(72, 25, 39, 33);
        panel.add(textPane_1);
        
        JTextPane textPane_1_1 = new JTextPane();
        textPane_1_1.setText("친구");
        textPane_1_1.setFont(new Font("Dialog", Font.BOLD, 21));
        textPane_1_1.setEditable(false);
        textPane_1_1.setBounds(71, 125, 60, 33);
        panel.add(textPane_1_1);
        
        chatListButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	ChatList list = new ChatList(currentUserEmail,loadLoggedInUserNickname());
            	list.setVisible(true);
            	FriendList.this.dispose();
            }
        });
        currentY += 48;

        List<String> otherUserNicknames = loadOtherUserNicknames();
        for (int i = 0; i < otherUserNicknames.size(); i++) {
            String otherUserNickname = otherUserNicknames.get(i);

            JPanel friendPanel = createFriendPanel(loadUserImagePath(otherUserNickname), otherUserNickname);
            friendPanel.setBounds(62, currentY, 295, 46);
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
    private String loadLoggedInUserImagePath() {
        return loadImagePathFromDatabase(loadLoggedInUserNickname());
    }

    private String loadUserImagePath(String userNickname) {
        return loadImagePathFromDatabase(userNickname);
    }

    private String loadImagePathFromDatabase(String nickname) {
        String query = "SELECT image_path FROM users WHERE nickname = ?";
        try (Connection connection = connectToDatabase();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, nickname);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("image_path");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
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
    
    
    private void saveImagePathToDatabase(String imagePath, String nickname) {
    	
        String query = "UPDATE users SET image_path = ? WHERE nickname = ?";

        try (Connection connection = connectToDatabase();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, imagePath);
            preparedStatement.setString(2, nickname);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("이미지 경로가 업데이트되었습니다.");
            } else {
                System.out.println("이미지 경로 업데이트에 실패했습니다.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private JPanel createFriendPanel(String imagePath, String nickname) {
        JPanel friendPanel = new JPanel();
        friendPanel.setBackground(new Color(243, 248, 252));

        JTextPane textPane = new JTextPane();
        textPane.setBounds(56, 10, 72, 27);
        textPane.setBackground(new Color(243, 248, 252));
        textPane.setEditable(false);
        textPane.setText(nickname);

        JLabel iconLabel = new JLabel(new ImageIcon(new ImageIcon(imagePath).getImage().getScaledInstance(41, 36, Image.SCALE_DEFAULT)));
        iconLabel.setBounds(12, 10, 41, 30);

        JButton profileButton;
        
        // 프로필 수정 버튼 추가 및 이벤트 처리
       
            profileButton = new JButton("프로필");
            profileButton.setFont(new Font("굴림", Font.PLAIN, 10));
            profileButton.setBackground(new Color(243, 248, 252));
            profileButton.setBorder(BorderFactory.createLineBorder(new Color(192, 192, 192)));
            profileButton.setBounds(215, 12, 60, 23);
            profileButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    openProfile(nickname,imagePath,loadLoggedInUserNickname());
                }
            });
        
        
        friendPanel.setLayout(null);

        friendPanel.add(textPane);
        friendPanel.add(iconLabel);
        friendPanel.add(profileButton);

        return friendPanel;
    }

   


    private void openProfile(String userNickname,String imagePath,String login) {
        Profile profileWindow = new Profile(userNickname,imagePath,login);
        profileWindow.setVisible(true);
    }

    private Connection connectToDatabase() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/Chat";
        String user = "root";
        String password = "0000";
        return DriverManager.getConnection(url, user, password);
    }
    
}
