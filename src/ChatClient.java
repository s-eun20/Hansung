import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ChatClient extends JFrame {

    private JPanel contentPane;
    private JScrollPane chatScrollPane;
    private JTextArea chatTextArea;
    private JScrollPane scrollPane;
    private JTextPane textPane;
    private JPanel panel;
    private JPanel topPanel;
    private JButton sendButton;

    private Socket socket;
    private DataInputStream dis;
    private DataOutputStream dos;

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    ChatClient frame = new ChatClient("seungeun");
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public ChatClient(String userName) {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 373, 675);
        contentPane = new JPanel();
        contentPane.setLayout(null);

        TopPanel();
        initChatPanel();
        TextPanel(userName);
        sendButton(userName);

        setContentPane(contentPane);

        try {
            socket = new Socket("localhost", 9999);
            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());

            // Start a thread to listen for messages from the server
            Thread listenThread = new Thread(new ListenNetwork());
            listenThread.start();
            AppendText("connect" + "\n");

        } catch (IOException e) {
            e.printStackTrace();
            AppendText("connect error");
        }
    }

    private void TopPanel() {
        topPanel = new JPanel();
        topPanel.setBackground(new Color(197, 216, 226));
        topPanel.setBounds(0, 0, 360, 70);
        contentPane.add(topPanel);
    }

    private void initChatPanel() {
        chatScrollPane = new JScrollPane();
        chatScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        chatScrollPane.setBounds(0, 70, 360, 437);
        chatScrollPane.setBorder(BorderFactory.createEmptyBorder());
        contentPane.add(chatScrollPane);

        chatTextArea = new JTextArea();
        chatTextArea.setFocusable(false);
        chatScrollPane.setViewportView(chatTextArea);
        chatTextArea.setBackground(new Color(204, 220, 230));

        chatScrollPane.setViewportView(chatTextArea);
        contentPane.add(chatScrollPane);
    }

    private void TextPanel(String userName) {
        scrollPane = new JScrollPane();
        scrollPane.setBounds(0, 506, 360, 88);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        contentPane.add(scrollPane);

        textPane = new JTextPane();
        scrollPane.setViewportView(textPane);
        scrollPane.setBackground(new Color(255, 255, 255));

        // Enter key input to send a message
        textPane.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (isEnter(e)) {
                    pressEnter(userName);
                }
            }
        });

        textPane.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                buttonState();
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
                buttonState();
            }
            @Override
            public void changedUpdate(DocumentEvent e) {
                buttonState();
            }
        });
    }

    private void sendButton(String userName) {
        panel = new JPanel();
        panel.setBackground(new Color(255, 255, 255));
        panel.setBounds(0, 595, 360, 45);
        contentPane.add(panel);
        panel.setLayout(null);

        sendButton = new JButton("전송");
        sendButton.setFocusPainted(false);
        sendButton.setBorderPainted(false);
        sendButton.setBackground(new Color(235, 230, 133));
        sendButton.setFont(new Font("나눔고딕 ExtraBold", Font.BOLD, 11));
        sendButton.setBounds(290, 8, 55, 25);
        panel.add(sendButton);

        sendButton.setEnabled(false);

        // Send button click event to send a message
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                pressEnter(userName);
            }
        });
    }

    private void pressEnter(String userName) {
        String enteredText = textPane.getText().trim();

        if (!enteredText.isEmpty()) {
            String msg = String.format("[%s] %s\n", userName, enteredText);
            SendMessage(msg);
            textPane.setText(""); // Clear the message input field after sending
            textPane.requestFocus();
            if (msg.contains("/exit")) // Exit handling
                System.exit(0);
            buttonState();
        }
    }

    private void buttonState() {
        sendButton.setEnabled(!textPane.getText().trim().isEmpty());
    }

    private void AppendText(String msg) {
        chatTextArea.append(msg);
        chatTextArea.setCaretPosition(chatTextArea.getText().length());
    }

    private void SendMessage(String msg) {
        try {
            dos.writeUTF(msg);
        } catch (IOException e) {
            AppendText("Error sending message");
            try {
                dos.close();
                dis.close();
                socket.close();
            } catch (IOException e1) {
                e1.printStackTrace();
                System.exit(0);
            }
        }
    }

    private boolean isEnter(KeyEvent e) {
        return e.getKeyCode() == KeyEvent.VK_ENTER;
    }

    //client to server 메시지 수신, 화면에 표시
    class ListenNetwork extends Thread {
        public void run() {
            while (true) {
                try {
                    // Use readUTF to read messages
                    String msg = dis.readUTF();
                    AppendText(msg);
                } catch (IOException e) {
                    AppendText("Error reading from server");
                    try {
                        dis.close();
                        socket.close();
                        break;
                    } catch (Exception ee) {
                        break;
                    }
                }
            }
        }
    }
}