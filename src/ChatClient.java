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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatClient extends JFrame {

    private JPanel contentPane;
    private JScrollPane chatScrollPane;
    private JTextArea chatTextArea;
    private JScrollPane scrollPane;
    private JTextPane textPane;
    private JPanel panel;
    private JPanel topPanel;
    private JButton sendButton;
    private List<String> emojiList; 

    private Socket socket;
    private DataInputStream dis;
    private DataOutputStream dos;

    private String roomName;  // 추가: 현재 사용자가 속한 채팅방의 ID
    private String userName;  // 추가: 사용자 이름

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    ChatClient frame = new ChatClient("seungeun","채팅방1");
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public ChatClient(String userName,String roomName) {
    	setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.userName = userName;
        this.roomName=roomName;

        setBounds(100, 100, 373, 675);
        contentPane = new JPanel();

        TopPanel();
        initChatPanel();
        TextPanel();
        sendButton();
        
        emojiList = new ArrayList<>();
        emojiList.add("src/image/상상부기 1.png");
        emojiList.add("src/image/상상부기 2.png");
        emojiList.add("src/image/상상부기 3.png");

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
        
        loadChatHistory(roomName);
    }
    
    private void loadChatHistory(String roomName) {
    	
        List<ChatMessage> chatHistory = ChatDatabaseManager.loadChatHistory(roomName);

        for (ChatMessage chatMessage : chatHistory) {
        	String messageText = chatMessage.getMessageText();
        	Integer closingBracketIndex = messageText.indexOf("]");
        	String extractedText = messageText.substring(closingBracketIndex + 2); // +2 to skip "] "
            String formattedMessage = String.format("[%s] %s - %s\n", chatMessage.getFormattedTimestamp(), chatMessage.getSender(), extractedText);
            AppendText(formattedMessage);
        }
    }

    private void TopPanel() {
        contentPane.setLayout(null);
        topPanel = new JPanel();
        topPanel.setBounds(0, 0, 360, 70);
        topPanel.setBackground(new Color(197, 216, 226));
        contentPane.add(topPanel);
    }

    private void initChatPanel() {
        chatScrollPane = new JScrollPane();
        chatScrollPane.setBounds(0, 70, 360, 437);
        chatScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        chatScrollPane.setBorder(BorderFactory.createEmptyBorder());
        contentPane.add(chatScrollPane);

        chatTextArea = new JTextArea();
        chatTextArea.setFocusable(false);
        chatScrollPane.setViewportView(chatTextArea);
        chatTextArea.setBackground(new Color(204, 220, 230));

        chatScrollPane.setViewportView(chatTextArea);
        contentPane.add(chatScrollPane);
    }

    private void TextPanel() {
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
                    pressEnter();
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

    private void selectEmoji() {
        // 여기에 이모티콘 선택 다이얼로그 또는 패널을 구현합니다.
        // 선택한 이모티콘 정보를 반환받습니다.
    	
    	
        String selectedEmoji = showEmojiSelectionDialog();

         //이모티콘을 메시지로 전송
        if (selectedEmoji != null) {
            String msg = String.format("[%s] %s\n", userName, selectedEmoji);
            SendMessage(msg);
            ChatDatabaseManager.saveChatMessage(roomName, userName, msg);
            buttonState();
        }
    }

    // 추가: 다이얼로그를 사용하여 이모티콘 선택
    private String showEmojiSelectionDialog() {
        // 여기에 이모티콘 선택 다이얼로그를 구현하고 선택한 이모티콘을 반환합니다.
        // 실제 구현은 사용자 경험에 맞게 설계되어야 합니다.
        // 아래는 간단한 예시 코드입니다.
        Object[] options = {"😃", "😄", "😊", "😉", "😍", "😘", "😜", "😎", "😇", "😏", "😂", "😭", "😱", "😡"};
        int choice = JOptionPane.showOptionDialog(
                this,
                "이모티콘을 선택하세요.",
                "이모티콘 선택",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                options[0]
        );

        // 선택한 이모티콘을 반환합니다.
        if (choice >= 0 && choice < options.length) {
            return options[choice].toString();
        } else {
            return null; // 사용자가 선택하지 않은 경우
        }
    }

    // 추가: 이모티콘 전송 버튼 이벤트 핸들러
    private void sendButton() {
        panel = new JPanel();
        panel.setBounds(0, 595, 360, 45);
        panel.setBackground(new Color(255, 255, 255));
        contentPane.add(panel);
        panel.setLayout(null);

        JButton emojiButton = new JButton("😊"); // 이모티콘 선택 버튼
        emojiButton.setFocusPainted(false);
        emojiButton.setBorderPainted(false);
        emojiButton.setBackground(new Color(235, 230, 133));
        emojiButton.setFont(new Font("Dialog", Font.BOLD, 18));
        emojiButton.setBounds(10, 8, 55, 25);
        panel.add(emojiButton);

        emojiButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                selectEmoji();
            }
        });

        sendButton = new JButton("전송");
        sendButton.setFocusPainted(false);
        sendButton.setBorderPainted(false);
        sendButton.setBackground(new Color(235, 230, 133));
        sendButton.setFont(new Font("나눔고딕 ExtraBold", Font.BOLD, 11));
        sendButton.setBounds(277, 8, 68, 25);
        panel.add(sendButton);

        sendButton.setEnabled(false);

        // Send button click event to send a message
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                pressEnter();
            }
        });
    }

    private void pressEnter() {
        String enteredText = textPane.getText().trim();

        if (!enteredText.isEmpty()) {
            String msg = String.format("[%s] %s\n", userName, enteredText);
            SendMessage(msg);
            ChatDatabaseManager.saveChatMessage(roomName, userName, msg);
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

    // client to server 메시지 수신, 화면에 표시
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
