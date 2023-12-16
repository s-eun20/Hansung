import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class ChatServer extends JFrame {
    private JPanel contentPane;
    private JTextPane textPane_1;
    private JButton btnConnect;
    private JScrollPane serverListScrollPane;
    private JTextPane textPane;

    private static final long serialVersionUID = 1L;

    private ServerSocket serverSocket;
    private Socket clientSocket;
    private Vector<UserThread> UserVec = new Vector<UserThread>();

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    ChatServer frame = new ChatServer();
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
    public ChatServer() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 373, 675);
        contentPane = new JPanel();

        ServerPanel();

        btnConnect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    serverSocket = new ServerSocket(9999);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                AppendText("Chat Server Running..." + "\n");
                btnConnect.setText("Server Running.." + "\n");
                btnConnect.setEnabled(false);
                AcceptServer acceptServer = new AcceptServer();
                acceptServer.start();
            }
        });

    }

    //thread 생성
    class AcceptServer extends Thread {
        public void run(){
            AppendText("Waiting clients ..." + "\n");
            while (true) {
                try {
                    clientSocket = serverSocket.accept();
                    // User 당 하나씩 Thread 생성
                    ChatServer.UserThread newUser = new ChatServer.UserThread(clientSocket);
                    UserVec.add(newUser);
                    //AppendText("사용자 입장. 현재 참가자 수 " + UserVec.size() + "\n");
                    newUser.start();
                } catch (IOException e) {
                    AppendText("accept error" + "\n");
                }
            }
        }
    }

    //Server 역할
    class UserThread extends Thread{
        private Socket clientSocket;
        private DataInputStream dis;
        private DataOutputStream dos;
        private Vector<UserThread> user_vc;

        public UserThread(Socket clientSocket) {
            this.clientSocket = clientSocket;
            this.user_vc = UserVec;
            try{
                dis = new DataInputStream(clientSocket.getInputStream());
                dos = new DataOutputStream(clientSocket.getOutputStream());
            } catch (IOException e) {
                AppendText("userService error" + "\n");
            }
        }

        //client - 메시지 전송
        public void WriteOne(String msg) {
            try {
                dos.writeUTF(msg);
            } catch (IOException e) {
                AppendText("dos.write() error" + "\n");
                try {
                    dos.close();
                    dis.close();
                    clientSocket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }


        //모든 client - 모든 메세지 전송
        public void WriteAll(String str) {
            for (int i = 0; i < user_vc.size(); i++) {
                ChatServer.UserThread user = user_vc.get(i);
                user.WriteOne(str);
            }
        }

        public void run(){
            while(true){
                try{
                    String msg = dis.readUTF();
                    WriteAll(msg + "\n");
                } catch (Exception e) {
                    UserVec.removeElement(this);
                    AppendText("UserName " + "퇴장. 현재 참가자 수 " + UserVec.size()+ "\n");
                    try {
                        dos.close();
                        dis.close();
                        clientSocket.close();
                        break;
                    } catch (Exception ee) {
                        break;
                    }
                }
            }
        }
    }

    //UI 구현
    private void ServerPanel(){
        setContentPane(contentPane);
        contentPane.setLayout(null);
        JPanel panel_1 = new JPanel();
        panel_1.setBackground(new Color(197, 216, 226));
        panel_1.setBounds(0, 0, 360, 60);
        contentPane.add(panel_1);
        panel_1.setLayout(null);

        textPane_1 = new JTextPane();
        textPane_1.setBackground(new Color(197, 216, 226));
        textPane_1.setEditable(false);
        textPane_1.setBounds(12, 15, 100, 33);
        textPane_1.setFont(new Font("나눔고딕", Font.BOLD, 22));
        textPane_1.setText("서버 화면");
        panel_1.add(textPane_1);

        btnConnect = new JButton("Server Connect");
        btnConnect.setFont(new Font("나눔고딕", Font.BOLD, 11));
        btnConnect.setFocusPainted(false);
        btnConnect.setBorderPainted(false);
        btnConnect.setBackground(new Color(239, 239, 239));
        btnConnect.setBounds(234, 23, 114, 25);
        panel_1.add(btnConnect);

        serverListScrollPane = new JScrollPane();
        serverListScrollPane.setBorder(BorderFactory.createEmptyBorder());
        serverListScrollPane.setBounds(0, 60, 360, 580);
        contentPane.add(serverListScrollPane);

        textPane = new JTextPane();
        textPane.setEditable(false);
        textPane.setBackground(new Color(204, 220, 230));
        textPane.setBorder(null);
        serverListScrollPane.setViewportView(textPane);
    }

    //Message 출력
    public void AppendText(String str) {
        textPane.setText(textPane.getText() + str); // 현재 텍스트 뒤에 새로운 텍스트 추가
        textPane.setCaretPosition(textPane.getText().length()); // 커서(캐럿) 위치를 텍스트 영역의 마지막으로 이동
    }


}