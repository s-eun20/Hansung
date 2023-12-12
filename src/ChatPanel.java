import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChatPanel extends JFrame {
    private static final long serialVersionUID = 1L;
    private JPanel contentPane;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                	ChatPanel frame = new ChatPanel();
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

    public ChatPanel(){
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 373, 675);
        contentPane = new JPanel();
        contentPane.setLayout(null);

        setMenu();

        setContentPane(contentPane);
    }

    private void setMenu(){
        JPanel panel = new JPanel();
        panel.setBounds(0, 0, 60, 640);
        contentPane.add(panel);
        panel.setLayout(null);

        JButton btnNewButton = new JButton("-");
        btnNewButton.setFocusPainted(false);
        btnNewButton.setBorderPainted(false);
        btnNewButton.setBackground(new Color(255, 255, 255));
        btnNewButton.setBounds(10, 35, 40, 40);
        panel.add(btnNewButton);

        JButton chatListButton = new JButton("-");
        chatListButton.setBorderPainted(false);
        chatListButton.setFocusPainted(false);
        chatListButton.setBackground(Color.WHITE);
        chatListButton.setBounds(10, 85, 40, 40);
        panel.add(chatListButton);

        chatListButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ChatList n = new ChatList();
                n.setVisible(true);
                ChatPanel.this.dispose();
            }
        });
    }
}