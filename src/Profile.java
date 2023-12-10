import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Profile extends JFrame {
    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JPanel panel;
    private JButton marks;

    private boolean mark = false;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    Profile frame = new Profile("nickname");
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
    public Profile(String userName) {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 373, 675);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

        setContentPane(contentPane);
        contentPane.setLayout(null);

        panel = new JPanel();
        panel.setBackground(new Color(204, 220, 230));
        panel.setBounds(0, 0, 360, 640);
        contentPane.add(panel);
        panel.setLayout(null);

        setProfile(userName);
        buttonProfile();

    }

    //프로필 사진, 이름, 즐겨 찾기
    private void setProfile(String userNickname){
        //프로필 사진 출력 수정
        JPanel panel_1 = new JPanel();
        panel_1.setBounds(130, 320, 100, 100);
        panel.add(panel_1);

        //클릭된 프로필 이름 출력 수정
        JTextPane userName = new JTextPane();
        userName.setEditable(false);
        userName.setBackground(new Color(204, 220, 230));
        userName.setFont(new Font("Yu Gothic UI", Font.BOLD, 20));
        userName.setText(userNickname);
        userName.setBounds(152, 420, 60, 31);
        panel.add(userName);

        //프로필 즐겨 찾기 기능 버튼 생성
        marks = new JButton();
        marks.setFocusPainted(false);
        marks.setBorderPainted(false);
        marks.setBackground(new Color(235, 235, 235));
        marks.setBounds(12, 12, 25, 25);
        panel.add(marks);

        //즐겨 찾기 여부
        marks.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if(mark){
                    setMark();
                }
                else{
                    setMark();
                }
            }
        });

        JTextPane textPane_2 = new JTextPane();
        textPane_2.setForeground(new Color(87, 87, 87));
        textPane_2.setFont(new Font("Calibri", Font.PLAIN, 15));
        textPane_2.setText("_____________________________________________________");
        textPane_2.setBackground(new Color(204, 220, 230));
        textPane_2.setBounds(-12, 480, 372, 31);
        panel.add(textPane_2);
    }

    //프로필 이미지 하단 버튼 2개 ( 1:1채팅, 프로필 편집 )
    private void buttonProfile(){
        //1:1 채팅 버튼 이미지 변경
        JButton startChat = new JButton("N");
        startChat.setBorderPainted(false);
        startChat.setFocusPainted(false);
        startChat.setBounds(100, 535, 50, 50);
        panel.add(startChat);

        startChat.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JOptionPane.showMessageDialog(panel, "1:1 채팅", "Message",JOptionPane.PLAIN_MESSAGE );
            }
        });

        //프로필 편집 버튼 이미지 변경
        JButton profileChange = new JButton("N");
        profileChange.setFocusPainted(false);
        profileChange.setBorderPainted(false);
        profileChange.setBounds(210, 535, 50, 50);
        panel.add(profileChange);

        profileChange.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                modifyProfile newFrame = new modifyProfile();
                newFrame.setVisible(true);
            }
        });

        JTextPane textPane = new JTextPane();
        textPane.setBackground(new Color(204, 220, 230));
        textPane.setEditable(false);
        textPane.setText("1:1 채팅");
        textPane.setBounds(99, 590, 50, 21);
        panel.add(textPane);

        JTextPane textPane_1 = new JTextPane();
        textPane_1.setBackground(new Color(204, 220, 230));
        textPane_1.setEditable(false);
        textPane_1.setText("프로필 편집");
        textPane_1.setBounds(199, 590, 70, 21);
        panel.add(textPane_1);
    }

    //즐겨 찾기 표시
    private void setMark(){
        mark = !mark;
        if(mark)
            marks.setBackground(new Color(237, 199, 204));
        else
            marks.setBackground(new Color(235, 235, 235));

    }
}