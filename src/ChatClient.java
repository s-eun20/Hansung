import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class ChatClient extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JScrollPane chatScrollPane;
	private JTextPane chatTextArea;
	private JScrollPane scrollPane;
	private JTextPane textPane;
	private JPanel panel;
	private JPanel topPanel;
	private JButton sendButton;

	// 네트워크 통신을 위한 소켓 및 입출력 스트림
	private Socket socket;
	private DataInputStream dis;
	private DataOutputStream dos;

	private String roomName; // 현재 사용자가 속한 채팅방의 ID
	private String userName; // 사용자 이름

	// 이모티콘 이미지 파일 경로
	String[] imagePaths = { "/image/상상부기 2.png", "/image/상상부기 3.png", "/image/상상부기 4.png", "/image/상상부기 5.png",
			"/image/상상부기 7.png", "/image/상상부기 8.png" };

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ChatClient frame = new ChatClient("", "");
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public ChatClient(String userName, String roomName) {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.userName = userName;
		this.roomName = roomName;

		setBounds(100, 100, 373, 675);
		contentPane = new JPanel();

		//GUI 초기화
		TopPanel();
		initChatPanel();
		TextPanel();
		sendButton();

		setContentPane(contentPane);

		//서버 연결
		try {
			//소켓을 생성과 입출력 스트림 설정
			socket = new Socket("localhost", 9999);
			dis = new DataInputStream(socket.getInputStream());
			dos = new DataOutputStream(socket.getOutputStream());

			//스레드 실행
			Thread listenThread = new Thread(new ListenNetwork());
			listenThread.start();

		} catch (IOException e) {
			e.printStackTrace();
			AppendText("connect error");
		}
		//채팅 기록 로드
		loadChatHistory(roomName, userName);
	}

	//채팅 기록 로드 및 텍스트 창에 출력 메서드
	private void loadChatHistory(String roomName, String username) {
		//데이터베이스에서 채팅 기록 가져오기
		List<ChatMessage> chatHistory = ChatDatabaseManager.loadChatHistory(roomName);

		for (ChatMessage chatMessage : chatHistory) {
			//발신자와 채팅 내용 가져오기
			String Sender = chatMessage.getSender();
			String messageText = chatMessage.getMessageText();

			//메세지 내용에서 발신자 정보를 제외한 텍스트 추출
			Integer closingBracketIndex = messageText.indexOf("]");
			String extractedText = messageText.substring(closingBracketIndex + 2); // +2 to skip "] "

			//만약 메시지에 이미지 경로가 포함되어 있다면 이미지 로드
			if (messageText.contains("/")) {
				loadImageToChatTextArea(Sender, messageText);
			} else {
				//이미지가 아닌 경우 메세지 텍스트 창에 출력
				String formattedMessage = String.format("(%s)\n[%s] - %s\n", chatMessage.getFormattedTimestamp(),
						chatMessage.getSender(), extractedText);
				AppendText(formattedMessage);
			}
		}
		
		//접속 메시지 생성 후 텍스트 창에 추가
		String connect = String.format("                               - %s님이 접속하였습니다. -%n", userName);
		AppendText(connect);
	}

	//채팅 방 정보 데이터베이스 저장하는 메서드
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

	//데이터베이스 연결 메서드
	private Connection connectToDatabase() throws SQLException {
		String url = "jdbc:mysql://localhost:3306/Chat";
		String user = "root";
		String password = "0000";
		return DriverManager.getConnection(url, user, password);
	}

	//채팅 화면 GUI
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

		chatTextArea = new JTextPane();
		chatTextArea.setFocusable(false);
		chatScrollPane.setViewportView(chatTextArea);
		chatTextArea.setBackground(new Color(224, 240, 254));

		chatScrollPane.setViewportView(chatTextArea);
		contentPane.add(chatScrollPane);
	}

	//텍스트 패널 초기화 및 설정 메서드
	private void TextPanel() {
		scrollPane = new JScrollPane();
		scrollPane.setBounds(0, 506, 360, 88);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		contentPane.add(scrollPane);

		textPane = new JTextPane();
		scrollPane.setViewportView(textPane);
		scrollPane.setBackground(new Color(255, 255, 255));

		//Enter 키 입력을 감지하여 메시지 전송
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

	//전송 버튼 설정 메서드
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

		// 이모티콘 버튼 정의 - 클릭시 이모티콘 선택 창 호출
		emojiButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ImFrame(ChatClient.this);
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

		//전송 버튼 정의
		sendButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				pressEnter();
			}
		});
	}

	//이모티콘 선택 창을 생성 메서드
	private void ImFrame(JFrame parentFrame) {
		JDialog dialog = new JDialog();
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		
		// 부모 프레임을 기준으로 위치 계산
	    int parentX = parentFrame.getLocation().x;
	    int parentY = parentFrame.getLocation().y;
	    int dialogX = parentX + 50;
	    int dialogY = parentY + 400;

	    dialog.setBounds(dialogX, dialogY, 350, 210);
		JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		dialog.setContentPane(contentPane);
		contentPane.setLayout(new GridLayout(2, 3, 0, 0));

		for (String path : imagePaths) {
			ImageIcon originalIcon = new ImageIcon(getClass().getResource(path));
			ImageIcon scaledIcon = new ImageIcon(originalIcon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH));

			JLabel label = new JLabel(scaledIcon);

			//이미지를 클릭하면 해당 이미지의 경로를 출력하고 이벤트를 발생
			label.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if (e.getClickCount() == 2) {

						System.out.println("Image Path: " + path);

						pressEnter2(path);
						dialog.setVisible(false);
					}
				}
			});

			contentPane.add(label);
		}

		dialog.setVisible(true);
	}

	//이미지를 채팅 텍스트 영역에 삽입 메서드
	private void loadImageToChatTextArea(String nickname, String imagePath) {
		StyledDocument doc = chatTextArea.getStyledDocument();
		SimpleAttributeSet attributes = new SimpleAttributeSet();

		StyleConstants.setForeground(attributes, Color.BLACK);
		StyleConstants.setBold(attributes, false);

		try {

			doc.insertString(doc.getLength(), "[" + nickname + "]\n", attributes);
			chatTextArea.setCaretPosition(doc.getLength());

			ImageIcon originalIcon = new ImageIcon(getClass().getResource(imagePath));
			Image scaledImage = originalIcon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
			ImageIcon scaledIcon = new ImageIcon(scaledImage);

			doc.insertString(doc.getLength(), "\n", attributes);
			chatTextArea.setCaretPosition(doc.getLength());
			chatTextArea.insertIcon(scaledIcon);
			chatTextArea.setCaretPosition(doc.getLength());

			doc.insertString(doc.getLength(), "\n", attributes);
			chatTextArea.setCaretPosition(doc.getLength());
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	//Enter 키가 눌렸을 때 호출되는 메서드 - 텍스트 메시지를 전송하고 채팅 기록을 저장
	private void pressEnter() {
		String enteredText = textPane.getText().trim();

		if (!enteredText.isEmpty()) {
			String msg = String.format("[%s]\n%s\n", userName, enteredText);
			SendMessage(msg);
			ChatDatabaseManager.saveChatMessage(roomName, userName, msg);
			textPane.setText("");
			textPane.requestFocus();
			if (msg.contains("/exit"))
				System.exit(0);
			buttonState();
		}
	}

	//이미지를 선택했을 때 호출되는 메서드 - 이미지 메시지를 전송하고 채팅 기록을 저장
	private void pressEnter2(String imagePath) {
		String msg = String.format("[%s]\n%s\n", userName, imagePath);
		SendMessage(msg);
		ChatDatabaseManager.saveChatMessage(roomName, userName, imagePath);
		textPane.setText("");
		textPane.requestFocus();
		if (msg.contains("/exit"))
			System.exit(0);
	}

	//전송 버튼 상태 설정 메서드 - 텍스트 패널이 비어있지 않으면 활성화
	private void buttonState() {
		sendButton.setEnabled(!textPane.getText().trim().isEmpty());
	}

	//채팅 텍스트 영역에 메시지 추가 메서드
	private void AppendText(String msg) {
		StyledDocument doc = chatTextArea.getStyledDocument();
		SimpleAttributeSet attributes = new SimpleAttributeSet();

		try {
			doc.insertString(doc.getLength(), msg, attributes);
			chatTextArea.setCaretPosition(doc.getLength());
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	//서버로 메시지 전송 메서드
	private void SendMessage(String msg) {
		try {
			//DataOutputStream을 통해 메시지 전송
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

	//키 이벤트가 Enter 키인지 확인하는 메서드
	private boolean isEnter(KeyEvent e) {
		return e.getKeyCode() == KeyEvent.VK_ENTER;
	}

	//서버로부터 메시지를 수신하는 쓰레드 클래스
	class ListenNetwork extends Thread {
		public void run() {
			while (true) {
				try {
					//서버로부터 메시지 읽기
					String msg = dis.readUTF();
					//메시지가 이미지 메시지인 경우 이미지 표시, 그렇지 않으면 텍스트 추가
					if (msg.contains("/image/"))
						displayImage(msg);
					else
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

		//이미지를 채팅 텍스트 영역에 표시 메서드
		private void displayImage(String msg) {
			StyledDocument doc = chatTextArea.getStyledDocument();
			SimpleAttributeSet attributes = new SimpleAttributeSet();

			StyleConstants.setForeground(attributes, Color.BLACK);
			StyleConstants.setBold(attributes, false);

			try {
				//닉네임 추출
				int nicknameEndIndex = msg.indexOf("]");
				if (nicknameEndIndex != -1) {
					String nickname = msg.substring(1, nicknameEndIndex);

					doc.insertString(doc.getLength(), "[" + nickname + "]\n", attributes);
					chatTextArea.setCaretPosition(doc.getLength());

					//이미지 경로 추출
					String imagePath = msg.substring(nicknameEndIndex + 1).trim();

					ImageIcon originalIcon = new ImageIcon(getClass().getResource(imagePath));
					Image scaledImage = originalIcon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
					ImageIcon scaledIcon = new ImageIcon(scaledImage);

					doc.insertString(doc.getLength(), "\n", attributes);
					chatTextArea.setCaretPosition(doc.getLength());
					chatTextArea.insertIcon(scaledIcon);
					chatTextArea.setCaretPosition(doc.getLength());

					doc.insertString(doc.getLength(), "\n", attributes);
					chatTextArea.setCaretPosition(doc.getLength());
				}
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		}
	}

}