import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class modifyProfile extends JFrame {
	
	
    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextField userName;
    private JPanel imagePanel;
    private JButton okButton;
    private JButton cancelButton;
    private String originalUserName; // 추가: 기존 사용자 이름 저장
    private JLabel imageLabel;  // 추가: 이미지를 나타낼 라벨
    private String newImagePath;

    /**
     * Create the frame.
     */
    public modifyProfile(String userName, String imagePath) {
    	
    	setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(150, 150, 310, 470);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

        setContentPane(contentPane);
        contentPane.setLayout(null);

        this.originalUserName = userName; // 추가: 기존 사용자 이름 저장
        panel(userName, imagePath);
    }

    private void panel(String loginUserName, String imagePath) {
    	this.newImagePath=imagePath;
        JPanel panel = new JPanel();
        panel.setBackground(new Color(255, 255, 255));
        panel.setBounds(0, 0, 296, 443);
        contentPane.add(panel);
        panel.setLayout(null);

        JTextPane textPane = new JTextPane();
        textPane.setEditable(false);
        textPane.setFocusTraversalKeysEnabled(false);
        textPane.setFont(new Font("나눔고딕", Font.PLAIN, 16));
        textPane.setText("기본프로필 편집");
        textPane.setBounds(25, 40, 131, 25);
        panel.add(textPane);

        imagePanel = new JPanel();
        imagePanel.setBackground(new Color(255, 255, 255));
        imagePanel.setBounds(103, 95, 90, 90);

        // imagePath를 이용하여 이미지를 설정
        ImageIcon imageIcon = new ImageIcon(new ImageIcon(imagePath).getImage().getScaledInstance(100, 100, Image.SCALE_DEFAULT));
        imageLabel = new JLabel(imageIcon);
        imagePanel.add(imageLabel);

        // 이미지를 클릭하면 파일 선택 창이 나타나도록 설정
        imagePanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                chooseImage();
            }
        });

        panel.add(imagePanel);

        userName = new JTextField();
        userName.setText(loginUserName);
        userName.setBounds(63, 210, 170, 25);
        panel.add(userName);
        userName.setColumns(1);

        okButton = new JButton("확인");
        okButton.setFont(new Font("나눔고딕", Font.PLAIN, 12));
        okButton.setFocusPainted(false);
        okButton.setBackground(new Color(255, 255, 255));
        okButton.setBounds(155, 393, 60, 23);
        panel.add(okButton);
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                // 변경된 사용자 이름 가져오기
                String newUserName = userName.getText();

                // 데이터베이스 업데이트
                updateUserNameInDatabase(originalUserName, newUserName);
                
                JFrame Profile = new Profile(newUserName,newImagePath,loginUserName);
                Profile.setVisible(true);
        
                
                

                // 수정창 닫기
                dispose();
            }
        });

        cancelButton = new JButton("취소");
        cancelButton.setFont(new Font("나눔고딕", Font.PLAIN, 12));
        cancelButton.setFocusPainted(false);
        cancelButton.setBackground(new Color(255, 255, 255));
        cancelButton.setBounds(225, 393, 60, 23);
        panel.add(cancelButton);

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                // 수정창 닫기
                dispose();
            }
        });
    }

    // 데이터베이스에서 사용자 이름 업데이트
    private void updateUserNameInDatabase(String originalName, String newName) {
        String query = "UPDATE users SET nickname = ? WHERE nickname = ?";

        try (Connection connection = connectToDatabase();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, newName);
            preparedStatement.setString(2, originalName);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("사용자 이름이 업데이트되었습니다.");
           
                
            } else {
                System.out.println("사용자 이름 업데이트에 실패했습니다.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 파일 선택 창을 띄우고 선택된 이미지를 라벨에 적용
    private void chooseImage() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            String imagePath = selectedFile.getAbsolutePath();

            // 선택된 이미지를 라벨에 적용
            ImageIcon newImageIcon = new ImageIcon(new ImageIcon(imagePath).getImage().getScaledInstance(100, 100, Image.SCALE_DEFAULT));
            imageLabel.setIcon(newImageIcon);

            // 데이터베이스 업데이트
            updateImagePathInDatabase(originalUserName, imagePath);
        }
    }

    // 데이터베이스에서 이미지 경로 업데이트
    private void updateImagePathInDatabase(String username, String imagePath) {
        String query = "UPDATE users SET image_path = ? WHERE nickname = ?";
        
        this.newImagePath=imagePath;
        System.out.println(newImagePath);
        try (Connection connection = connectToDatabase();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, imagePath);
            preparedStatement.setString(2, username);

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

    // 데이터베이스 연결
    private Connection connectToDatabase() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/Chat";
        String user = "root";
        String password = "7981";
        return DriverManager.getConnection(url, user, password);
    }
}
