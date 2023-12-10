import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class JoinUI extends JFrame {

    private JTextField emailField;
    private JTextField nicknameField;
    private JPasswordField passwordField;

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;

    // MySQL Connection 정보
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/Chat";
    private static final String USER = "root";
    private static final String PASSWORD = "7981";
    
    private JLabel emailLabel;
    private JLabel nicknameLabel;
    private JLabel passwordLabel;

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    JoinUI frame = new JoinUI();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public JoinUI() {
        setTitle("회원가입");
        setSize(373, 675);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 373, 675);
        contentPane = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon icon = new ImageIcon("src/image/상상부기 1.png");
                Image image = icon.getImage();

                int newWidth = 200;
                int newHeight = 300;
                Image scaledImage = image.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
                ImageIcon scaledIcon = new ImageIcon(scaledImage);

                int x = (getWidth() - newWidth) / 2;
                int y = 50;
                g.drawImage(scaledIcon.getImage(), x, y, this);

                g.setFont(new Font("Arial", Font.BOLD, 18));
                g.setColor(Color.WHITE);
                String text = "Join";
                int textWidth = g.getFontMetrics().stringWidth(text);
                int textX = (getWidth() - textWidth) / 2;
                int textY = y + newHeight + 30;
                g.drawString(text, textX, textY);
            }
        };
        contentPane.setBackground(new Color(213, 239, 255));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        emailField = new JTextField();
        emailField.setBounds(114, 400, 133, 28);
        contentPane.add(emailField);
        emailField.setColumns(10);

        nicknameField = new JTextField();
        nicknameField.setBounds(114, 440, 132, 28);
        contentPane.add(nicknameField);
        nicknameField.setColumns(10);

        passwordField = new JPasswordField();
        passwordField.setBounds(114, 478, 132, 28);
        contentPane.add(passwordField);

        JButton joinButton = new JButton("회원가입");
        joinButton.setBackground(new Color(255, 255, 255));
        joinButton.setBorderPainted(false);
        joinButton.setFocusPainted(false);
        joinButton.setBounds(114, 520, 132, 28);
        contentPane.add(joinButton);
        
        emailLabel = new JLabel("이메일");
        emailLabel.setBounds(58, 403, 44, 21);
        emailLabel.setForeground(new Color(0, 0, 0));
        contentPane.add(emailLabel);
        
        nicknameLabel = new JLabel("닉네임");
        nicknameLabel.setBounds(58, 443, 44, 21);
        nicknameLabel.setForeground(new Color(0, 0, 0));
        contentPane.add(nicknameLabel);
        
        passwordLabel = new JLabel("비밀번호");
        passwordLabel.setBounds(50, 481, 52, 21);
        passwordLabel.setForeground(new Color(0, 0, 0));
        contentPane.add(passwordLabel);

        joinButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                register();
            }
        });
        
        // 텍스트 필드 배경 제거
        emailField.setOpaque(false);
        nicknameField.setOpaque(false);
        passwordField.setOpaque(false);
    }

    private void register() {
        String email = emailField.getText();
        String nickname = nicknameField.getText();
        char[] passwordChars = passwordField.getPassword();
        String password = new String(passwordChars);

        if (!userExists(email)) {
            saveUserToDatabase(email, nickname, password);
            System.out.println("회원가입 성공!");
            // 회원가입 성공 후 다른 동작을 수행하거나 창을 닫을 수 있습니다.
        } else {
            System.out.println("이미 존재하는 이메일입니다.");
        }
    }

    private boolean userExists(String email) {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD)) {
            String query = "SELECT * FROM users WHERE email = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, email);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    return resultSet.next();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void saveUserToDatabase(String email, String nickname, String password) {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD)) {
            String query = "INSERT INTO users (email, nickname, password) VALUES (?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, email);
                preparedStatement.setString(2, nickname);
                preparedStatement.setString(3, password);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
