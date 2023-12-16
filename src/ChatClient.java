import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

public class ChatClient extends JFrame {
	
	private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JScrollPane chatScrollPane;
    private JTextArea chatTextArea;
    private JScrollPane scrollPane;
    private JTextPane textPane;
    private JPanel panel;
    private JPanel topPanel;
    private JButton sendButton;
    private List<String> emojiList; 
    private static ChatClient instance;

    private Socket socket;
    private DataInputStream dis;
    private DataOutputStream dos;

    private String roomName;  // 추가: 현재 사용자가 속한 채팅방의 ID
    private String userName;  // 추가: 사용자 이름
    
    public static ChatClient getInstance() {
        return instance;
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    ChatClient frame = new ChatClient("","");
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public ChatClient(String userName,String roomName) {
    	setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    	instance = this;
        this.userName = userName;
        this.roomName=roomName;

        setBounds(100, 100, 373, 675);
        contentPane = new JPanel();

        TopPanel();
        initChatPanel();
        TextPanel();
        sendButton();
        
        
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
            String formattedMessage = String.format("(%s)\n [%s] - %s\n", chatMessage.getFormattedTimestamp(), chatMessage.getSender(), extractedText);
            //String formattedMessage = String.format("[%s] %s - %s", chatMessage.getSender(), extractedText, chatMessage.getFormattedTimestamp());
            AppendText(formattedMessage);
        }
    }
    
    
    
    public void saveChatRoomToDatabase(String chatRoomName, String loginNickname) {
        String query = "INSERT INTO ChatRooms (chat_name, user_nickname) VALUES (?, ?)";
        try (Connection connection = connectToDatabase();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, chatRoomName);
            preparedStatement.setString(2, loginNickname);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private Connection connectToDatabase() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/Chat";
        String user = "root";
        String password = "0000";
        return DriverManager.getConnection(url, user, password);
    }


    private void TopPanel() {
        contentPane.setLayout(null);
        topPanel = new JPanel();
        topPanel.setBounds(0, 0, 360, 70);
        topPanel.setBackground(new Color(216, 236, 254));
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
        chatTextArea.setBackground(new Color(224, 240, 254));

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
        emojiButton.setBackground(new Color(243, 239, 180));
        emojiButton.setFont(new Font("Dialog", Font.BOLD, 18));
        emojiButton.setBounds(10, 8, 55, 25);
        panel.add(emojiButton);

        emojiButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ImageViewer imageViewer = new ImageViewer();
                imageViewer.setVisible(true);
            }
        });

        sendButton = new JButton("전송");
        sendButton.setFocusPainted(false);
        sendButton.setBorderPainted(false);
        sendButton.setBackground(new Color(243, 239, 180));
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
    
    public void updateImage(String imagePath) {
        System.out.println("ChatPanel에서 이미지 업데이트: " + imagePath);
        String imgTag = String.format("<img src=\"%s\" width=\"30\" height=\"30\">", imagePath);
        String msg = String.format("[%s]  %s\n", userName, "Image: " + imgTag);
        AppendText("이미지 출력: " + imagePath + "\n");
        SendMessage(msg);
        ChatDatabaseManager.saveChatMessage(roomName, userName, msg);
    }


    private void pressEnter() {
        String enteredText = textPane.getText().trim();

        if (!enteredText.isEmpty()) {
            String msg = String.format("[%s]\n %s\n", userName, enteredText);
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
