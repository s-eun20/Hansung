import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import javax.swing.JTextPane;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JTextField;
import javax.swing.JButton;

public class modifyProfile extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextField userName;
    private JPanel imagePanel;
    private JButton okButton;
    private JButton cancelButton;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    modifyProfile frame = new modifyProfile();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the frame.
     */
    public modifyProfile() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(150, 150, 310, 470);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

        setContentPane(contentPane);
        contentPane.setLayout(null);

        panel();
    }

    private void panel(){
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
        imagePanel.setBounds(103, 95, 90, 90);
        panel.add(imagePanel);

        userName = new JTextField();
        userName.setText("UserName");
        userName.setBounds(63, 210, 170, 25);
        panel.add(userName);
        userName.setColumns(1);

        okButton = new JButton("확인");
        okButton.setFont(new Font("나눔고딕", Font.PLAIN, 12));
        okButton.setFocusPainted(false);
        okButton.setBackground(new Color(255, 255, 255));
        okButton.setBounds(155, 393, 60, 23);
        panel.add(okButton);

        okButton.setEnabled(false);
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                //변화 여부 확인 후 실행 수정
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
                dispose();
            }
        });
    }
}