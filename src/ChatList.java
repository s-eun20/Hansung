import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChatList extends JFrame {
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private String loginemail;
	private String loginNickname;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ChatList frame = new ChatList("", "");
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
	public ChatList(String loginEmail, String loginNickname) {
		this.loginemail = loginEmail;
		this.loginNickname = loginNickname;
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 373, 675);
		contentPane = new JPanel();
		contentPane.setBackground(new Color(255, 255, 255));

		// GUI
		setMenu(loginEmail);
		setTop(loginNickname);

		// 데이터베이스에서 채팅 방 목록 불러오기
		loadChatRoomsFromDatabase(loginNickname);
		setContentPane(contentPane);
	}

	// 현재 사용자를 제외한 모든 사용자의 닉네임을 데이터베이스에서 불러오는 메소드
	private List<String> loadFriendList(String currentUserEmail) {
		// 사용자의 닉네임을 담을 리스트 생성
		List<String> nicknames = new ArrayList<>();
		String query = "SELECT nickname FROM users WHERE email != ?";
		try (Connection connection = connectToDatabase();
				PreparedStatement preparedStatement = connection.prepareStatement(query)) {
			preparedStatement.setString(1, currentUserEmail);
			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				nicknames.clear(); // Clear existing data and fetch fresh data
				while (resultSet.next()) {
					nicknames.add(resultSet.getString("nickname"));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return nicknames;
	}

	// 친구 목록, 채팅 목록 이동 버튼 (좌측 버튼 2개)
	private void setMenu(String loginEmail) {
		contentPane.setLayout(null);
		JPanel panel = new JPanel();
		panel.setBackground(new Color(224, 240, 254));
		panel.setBounds(0, 0, 60, 640);
		contentPane.add(panel);
		panel.setLayout(null);

		JButton mainButton = new JButton();
		mainButton.setIcon(new ImageIcon(ChatList.class.getResource("/image/free-icon-person-7542670.png")));
		mainButton.setFocusPainted(false);
		mainButton.setBorderPainted(false);
		mainButton.setBackground(new Color(224, 240, 254));
		mainButton.setBounds(10, 32, 40, 40);
		panel.add(mainButton);

		mainButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				FriendList n = new FriendList(loginEmail);
				n.setVisible(true);
				ChatList.this.dispose();
			}
		});

		JButton chatListButton = new JButton();
		chatListButton.setIcon(new ImageIcon(ChatList.class.getResource("/image/free-icon-chat-5962500.png")));
		chatListButton.setBorderPainted(false);
		chatListButton.setFocusPainted(false);
		chatListButton.setBackground(new Color(224, 240, 254));
		chatListButton.setBounds(10, 85, 40, 40);
		panel.add(chatListButton);
	}

	// 상단 "채팅" 출력, 채팅방 생성 버튼
	private void setTop(String loginNickName) {
		JPanel panel_1 = new JPanel();
		panel_1.setBounds(60, 0, 300, 60);
		panel_1.setBackground(new Color(255, 255, 255));
		contentPane.add(panel_1);
		panel_1.setLayout(null);

		JTextPane textPane_1 = new JTextPane();
		textPane_1.setEditable(false);
		textPane_1.setBounds(10, 25, 73, 33);
		textPane_1.setFont(new Font("나눔고딕", Font.BOLD, 22));
		textPane_1.setText("채팅");
		panel_1.add(textPane_1);

		JButton newChatButton = new JButton("채팅방 생성");
		newChatButton.setIcon(null);
		newChatButton.setFont(new Font("Dialog", Font.PLAIN, 12));
		newChatButton.setFocusPainted(false);
		newChatButton.setBorder(BorderFactory.createLineBorder(new Color(192, 192, 192)));
		newChatButton.setBackground(new Color(255, 255, 255));
		newChatButton.setBounds(190, 31, 93, 22);
		panel_1.add(newChatButton);

		// addChatRoom 메소드를 호출하여 새로운 채팅 방 추가
		newChatButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addChatRoom(loginNickName);
			}
		});
	}

	// 데이터베이스에서 모든 채팅 방 목록을 불러와 화면에 표시하는 메소드
	private void loadChatRoomsFromDatabase(String loginNickname) {
		int currentY = 63;
		
		//데이터베이스에서 모든 채팅 방 목록 불러오기
		List<String> chatRoomNames = getAllChatRoomNamesFromDatabase();
		
		for (String chatRoomName : chatRoomNames) {
			
			String[] nicknames = chatRoomName.split(",");

			System.out.println(Arrays.asList(nicknames));

			//현재 로그인한 사용자의 닉네임이 배열에 포함되어 있다면 채팅 패널을 로드하여 화면에 추가
			if (Arrays.asList(nicknames).contains(loginNickname)) {
				JPanel loadChatPanel2 = loadChatPanel(chatRoomName, loginNickname);
				loadChatPanel2.setBounds(62, currentY, 295, 46);
				loadChatPanel2.setVisible(true);
				contentPane.add(loadChatPanel2);
				currentY += 50;
			}
		}
	}

	//새로운 채팅 방을 데이터베이스에 저장 메소드
	private void saveChatRoomToDatabase(String chatRoomName, String loginNickname) {
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

	//데이터베이스에서 모든 채팅 방의 이름을 불러오는 메소드
	private List<String> getAllChatRoomNamesFromDatabase() {
		List<String> chatRoomNames = new ArrayList<>();
		String query = "SELECT chat_name FROM ChatRooms";
		try (Connection connection = connectToDatabase();
				PreparedStatement preparedStatement = connection.prepareStatement(query);
				ResultSet resultSet = preparedStatement.executeQuery()) {
			while (resultSet.next()) {
				chatRoomNames.add(resultSet.getString("chat_name"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return chatRoomNames;
	}

	//채팅 방 정보를 표시하는 패널을 생성하고 반환하는 메소드
	private JPanel loadChatPanel(String chatRoomName, String loginNickname) {
		System.out.println(loginNickname);
		JPanel chatPanel = new JPanel();
		chatPanel.setBackground(new Color(243, 248, 252));
		chatPanel.setLayout(null);

		JTextPane textPane = new JTextPane();
		textPane.setBounds(12, 10, 160, 27);
		textPane.setBackground(new Color(243, 248, 252));
		textPane.setEditable(false);
		textPane.setText(chatRoomName);

		chatPanel.add(textPane);

		JButton openChatButton = new JButton("Open Chat");
		openChatButton.setFont(new Font("굴림", Font.PLAIN, 10));
		openChatButton.setBackground(new Color(243, 248, 252));
		openChatButton.setBorder(BorderFactory.createLineBorder(new Color(192, 192, 192)));
		openChatButton.setBounds(200, 12, 75, 23);

		chatPanel.add(openChatButton);

		//버튼 클릭 시 지정된 정보로 ChatClient 열기
		openChatButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				openChatClient(chatRoomName, loginNickname);
			}
		});

		contentPane.revalidate();
		contentPane.repaint();

		return chatPanel;
	}

	//데이터베이스에 연결 메소드
	private Connection connectToDatabase() throws SQLException {
		String url = "jdbc:mysql://localhost:3306/Chat";
		String user = "root";
		String password = "0000";
		return DriverManager.getConnection(url, user, password);
	}

	// 채팅방 목록 생성
	private void addChatRoom(String loginNickname) {
		List<String> friends = loadFriendList(loginNickname);

		// 친구 선택을 위한 체크박스 배열 생성
		JCheckBox[] checkBoxes = new JCheckBox[friends.size()];
		for (int i = 0; i < friends.size(); i++) {
			checkBoxes[i] = new JCheckBox(friends.get(i));
		}

		// 패널에 체크박스 추가
		JPanel panel = new JPanel(new GridLayout(0, 1));
		for (JCheckBox checkBox : checkBoxes) {
			panel.add(checkBox);
		}

		// 스크롤 가능한 패널 생성
		JScrollPane scrollPane = new JScrollPane(panel);

		// 다이얼로그 표시
		int option = JOptionPane.showConfirmDialog(contentPane, scrollPane, "Select friends",
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

		if (option == JOptionPane.OK_OPTION) {
			// 선택된 친구들의 닉네임을 저장할 리스트
			List<String> selectedFriends = new ArrayList<>();

			// 선택된 친구만 콘솔에 출력 및 리스트에 추가
			System.out.println("Selected friends:");
			for (JCheckBox checkBox : checkBoxes) {
				if (checkBox.isSelected()) {
					String friendNickname = checkBox.getText();
					System.out.println(", " + friendNickname);
					selectedFriends.add(friendNickname);
				}
			}

			// 선택된 친구들로 채팅방 판넬 추가
			createChatPanel(selectedFriends, loginNickname);
		}
	}

	//선택된 친구 목록으로 채팅 방 정보를 표시하는 패널을 생성 및 처리 메소드
	private void createChatPanel(List<String> selectedFriends, String loginNickname) {
		JPanel friendPanel = new JPanel();
		friendPanel.setBackground(new Color(243, 248, 252));
		friendPanel.setLayout(null);

		StringBuilder textContent = new StringBuilder();
		for (String nickname : selectedFriends) {
			textContent.append(nickname).append(", ");
		}

		JTextPane textPane = new JTextPane();
		textPane.setBounds(12, 10, 160, 27);
		textPane.setBackground(new Color(243, 248, 252));
		textPane.setEditable(false);
		textPane.setText(textContent.toString());

		friendPanel.add(textPane);

		JButton openChatButton = new JButton("Open Chat");
		openChatButton.setFont(new Font("굴림", Font.PLAIN, 10));
		openChatButton.setBackground(new Color(243, 248, 252));
		openChatButton.setBorder(BorderFactory.createLineBorder(new Color(192, 192, 192)));
		openChatButton.setBounds(200, 12, 75, 23);

		friendPanel.add(openChatButton);

		openChatButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				openChatClient(textContent.toString(), loginNickname);
			}
		});

		//데이터베이스에 채팅 방 저장
		saveChatRoomToDatabase(textContent.toString(), loginNickname);
		//채팅 패널 생성 후 추가 처리 메소드 호출
		afterCreateChatPanel(textContent.toString(), loginNickname);

		contentPane.revalidate();
		contentPane.repaint();
	}

	//채팅 패널을 생성한 후 추가 처리 수행 메소드
	private void afterCreateChatPanel(String chatRoomName, String loginNickname) {
		JFrame chatList = new ChatList(loginemail, this.loginNickname);
		chatList.setVisible(true);
		dispose();
		contentPane.revalidate();
		contentPane.repaint();

	}

	// friendPanel 클릭 시 ChatClient 실행 및 textContent 전달
	private void openChatClient(String RoomName, String loginNickname) {
		// ChatClient 실행 및 필요한 정보 전달
		ChatClient chatClient = new ChatClient(loginNickname, RoomName);
		chatClient.setVisible(true);
	}
}