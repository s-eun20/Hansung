import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class ImageViewer extends JFrame {
    //private JLabel bigImageLabel;
    private String selectedImagePath;
    private static ImageViewer instance; // 추가: 인스턴스를 저장할 필드
    private ChatClient chatPanelInstance; // 추가: ChatPanel에 대한 참조 필드

    public ImageViewer() {
        instance = this; // 추가: 현재 인스턴스로 초기화
        chatPanelInstance = ChatClient.getInstance(); 
        setTitle("Image Viewer");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // 이미지 파일 경로들
        String[] imagePaths = {
                "/image/상상부기 2.png",
                "/image/상상부기 3.png",
                "/image/상상부기 4.png",
                "/image/상상부기 5.png",
                "/image/상상부기 7.png",
                "/image/상상부기 8.png"
        };


        // 작은 이미지를 나타낼 라벨들이 있는 패널 (1x6 그리드 레이아웃)
        JPanel imagePanel = new JPanel(new GridLayout(2, imagePaths.length));

        // 작은 이미지들을 추가
        for (String imagePath : imagePaths) {
        	ImageIcon originalImageIcon = new ImageIcon(getClass().getResource(imagePath));
            Image originalImage = originalImageIcon.getImage();
            
            // 이미지를 50x50 픽셀로 조정
            Image scaledImage = originalImage.getScaledInstance(80, 80, Image.SCALE_SMOOTH);
            
            // 조정된 이미지로 ImageIcon 생성
            ImageIcon scaledImageIcon = new ImageIcon(scaledImage);
            
            JLabel smallImageLabel = new JLabel(scaledImageIcon);
            smallImageLabel.addMouseListener(new SmallImageClickListener(imagePath));
            imagePanel.add(smallImageLabel);
        }

        // 작은 이미지 패널을 프레임에 추가
        add(imagePanel, BorderLayout.SOUTH);

        // 프레임 크기 설정
        setSize(350, 210);
        setLocation(150, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    // 추가: 인스턴스 반환 메소드
    public static ImageViewer getInstance() {
        return instance;
    }

    public class SmallImageClickListener extends MouseAdapter {
        private String imagePath;
        private long lastClickTime;

        public SmallImageClickListener(String imagePath) {
            this.imagePath = imagePath;
            this.lastClickTime = 0;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            long clickTime = System.currentTimeMillis();

            if (clickTime - lastClickTime < 500) { // 더블 클릭 속도에 대한 임계값을 조절하세요 (밀리초 단위)
                // 더블 클릭 감지
                handleDoubleClick();
            }

            lastClickTime = clickTime;
        }

        private void handleDoubleClick() {
            // imagePath가 null이 아닌지 확인한 후 이미지를 로드합니다.
            if (imagePath != null) {
                // 이미지를 로드하고 bigImageLabel에 설정합니다.
                ImageIcon imageIcon = new ImageIcon(getClass().getResource(imagePath));

                // ChatPanel의 텍스트 업데이트
                chatPanelInstance.updateImage(imagePath);

                // 이미지 경로 설정
                ImageViewer.getInstance().selectedImagePath = imagePath;

                System.out.println("더블 클릭 감지. 선택된 이미지 경로: " + imagePath);
            } else {
                System.out.println("chatPanelInstance 또는 chatTextPane이 null입니다.");
            }
        }
    }

    public String getSelectedImagePath() {
        return selectedImagePath;
    }
}
