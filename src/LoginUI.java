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
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.JLabel;

public class LoginUI extends JFrame { 
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField textField;
    private JPasswordField passwordField;

    // MySQL Connection 정보
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/Chat";
    private static final String USER = "root";
    private static final String PASSWORD = "0000";

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    LoginUI frame = new LoginUI();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public LoginUI() {
        setTitle("Hansung Talk");
        setSize(373, 675);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 373, 675);
        JPanel contentPane = new JPanel() {
            /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon icon = new ImageIcon("src/image/상상부기 1.png");
                Image image = icon.getImage();

                // 이미지 크기 조절
                int newWidth = 200;  // 적절한 너비
                int newHeight = 300; // 적절한 높이
                Image scaledImage = image.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
                ImageIcon scaledIcon = new ImageIcon(scaledImage);

                int x = (getWidth() - newWidth) / 2;
                int y = 50; // 이미지를 위로 조절
                g.drawImage(scaledIcon.getImage(), x, y, this);

                // "Hansung Talk" 텍스트를 이미지 중앙에 추가
                g.setFont(new Font("Arial", Font.BOLD, 18));
                g.setColor(Color.WHITE);
                String text = "Hansung Talk";
                int textWidth = g.getFontMetrics().stringWidth(text);
                int textX = (getWidth() - textWidth) / 2;
                int textY = y + newHeight + 30;
                g.drawString(text, textX, textY);
            }
        };
        contentPane.setBackground(new Color(213, 239, 255));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        textField = new JTextField();
        textField.setBounds(114, 400, 133, 28); 
        contentPane.add(textField);
        textField.setColumns(10);

        passwordField = new JPasswordField();
        passwordField.setBounds(114, 440, 132, 28); 
        contentPane.add(passwordField);

        JButton loginButton = new JButton("로그인");
        loginButton.setBackground(new Color(255, 255, 255));
        loginButton.setBorderPainted(false);
        loginButton.setFocusPainted(false);
        loginButton.setBounds(114, 478, 132, 28); 
        contentPane.add(loginButton);

        JButton registerButton = new JButton("회원가입");
        registerButton.setBackground(new Color(255, 255, 255));
        registerButton.setBorderPainted(false);
        registerButton.setFocusPainted(false);
        registerButton.setBounds(114, 510, 132, 28); 
        contentPane.add(registerButton);
        
        JLabel emailLabel = new JLabel("이메일");
        emailLabel.setForeground(Color.BLACK);
        emailLabel.setBounds(58, 403, 44, 21);
        contentPane.add(emailLabel);
        
        JLabel emailLabel_1 = new JLabel("비밀번호");
        emailLabel_1.setForeground(Color.BLACK);
        emailLabel_1.setBounds(57, 443, 50, 21);
        contentPane.add(emailLabel_1);

        loginButton.addActionListener(new ActionListener() { // 로그인 버튼을 눌렀을 때
            @Override
            public void actionPerformed(ActionEvent e) {
                login(); // 로그인 함수 실행
            }
        });

        registerButton.addActionListener(new ActionListener() { // 가입 버튼 눌렀을 때
            @Override
            public void actionPerformed(ActionEvent e) {
                openJoinUI(); // 가입 함수 실행
            }
        });
    }

    private void login() { 
        String email = textField.getText();
        char[] passwordChars = passwordField.getPassword();
        String password = new String(passwordChars);

        if (validateUser(email, password)) {
            System.out.println("로그인 성공! - " + email);
            EventQueue.invokeLater(new Runnable() {
                public void run() {
                    try {
                        FriendList frame = new FriendList(email); // 친구목록을 띄워줌
                        frame.setVisible(true);
                        setVisible(false);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            System.out.println("로그인 실패. 이메일 또는 비밀번호를 확인하세요.");
        }
    }

    private void openJoinUI() {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    JoinUI joinFrame = new JoinUI();
                    joinFrame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private boolean validateUser(String email, String password) { // 데이터 베이스에 저장된 정보와 일치하는지 확인
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD)) {
            String query = "SELECT * FROM users WHERE email = ? AND password = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, email);
                preparedStatement.setString(2, password);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    return resultSet.next(); // 결과가 있으면 true, 없으면 false
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
