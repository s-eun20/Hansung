import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChatList extends JFrame {
    private static final long serialVersionUID = 1L;
    private JPanel contentPane;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    ChatList frame = new ChatList();
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
    public ChatList() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 373, 675);
        contentPane = new JPanel();
        contentPane.setLayout(null);

        setMenu();
        setTop();
        initListPanel();

        setContentPane(contentPane);
    }

    //채팅방 목록 출력 필요
    private void initListPanel() {
        JScrollPane chatListScrollPane = new JScrollPane();
        chatListScrollPane.setBorder(BorderFactory.createEmptyBorder());
        chatListScrollPane.setBounds(60, 60, 300, 580);
        contentPane.add(chatListScrollPane);

        JTextPane textPane = new JTextPane();
        textPane.setBorder(null);
        chatListScrollPane.setViewportView(textPane);
    }

    //친구 목록, 채팅 목록 이동 버튼 (좌측 버튼 2개)
    private void setMenu(){
        JPanel panel = new JPanel();
        panel.setBounds(0, 0, 60, 640);
        contentPane.add(panel);
        panel.setLayout(null);

        JButton mainButton = new JButton("-");
        mainButton.setFocusPainted(false);
        mainButton.setBorderPainted(false);
        mainButton.setBackground(new Color(255, 255, 255));
        mainButton.setBounds(10, 35, 40, 40);
        panel.add(mainButton);

        mainButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ChatPanel n = new ChatPanel();
                n.setVisible(true);
                ChatList.this.dispose();
            }
        });

        JButton chatListButton = new JButton("-");
        chatListButton.setBorderPainted(false);
        chatListButton.setFocusPainted(false);
        chatListButton.setBackground(Color.WHITE);
        chatListButton.setBounds(10, 85, 40, 40);
        panel.add(chatListButton);
    }

    //상단 "채팅" 출력, 채팅방 생성 버튼
    //버튼 수정 필요
    private void setTop(){
        JPanel panel_1 = new JPanel();
        panel_1.setBackground(new Color(255, 255, 255));
        panel_1.setBounds(60, 0, 300, 60);
        contentPane.add(panel_1);
        panel_1.setLayout(null);

        JTextPane textPane_1 = new JTextPane();
        textPane_1.setEditable(false);
        textPane_1.setBounds(10, 20, 62, 33);
        textPane_1.setFont(new Font("나눔고딕", Font.BOLD, 22));
        textPane_1.setText("채팅");
        panel_1.add(textPane_1);

        JButton newChatButton = new JButton("-");
        newChatButton.setFont(new Font("나눔고딕", Font.BOLD, 5));
        newChatButton.setFocusPainted(false);
        newChatButton.setBorderPainted(false);
        newChatButton.setBackground(new Color(239, 239, 239));
        newChatButton.setBounds(260, 25, 25, 25);
        panel_1.add(newChatButton);

        newChatButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(panel_1, "채팅방 생성", "친구 목록",JOptionPane.PLAIN_MESSAGE );
            }
        });
    }
}