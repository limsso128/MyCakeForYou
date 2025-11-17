import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.ImageIcon;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseAdapter; // <-- 클릭용 import
import java.awt.event.MouseEvent;  // <-- 클릭용 import

// 하나의 파일로 모든 것을 해결하는 Main 클래스
public class main {

    // 1. 이미지를 배경으로 그리는 특수 패널(Panel)
    static class ImagePanel extends JPanel {

        private Image startImage;  // 'background_start.jpg'
        private Image breadImage;  // 'Bread_Basic.png'

        private String currentState; // 현재 상태 (start / bread)

        public ImagePanel() {
            // 1. 이미지 불러오기
            loadImages();

            // 2. 현재 상태를 'start'로 설정
            currentState = "start";

            // 3. ⭐ 마우스 클릭 이벤트 추가
            this.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    // 'start' 상태일 때 (시작 화면일 때) 클릭하면
                    if (currentState.equals("start")) {

                        // 상태를 'bread'로 변경
                        currentState = "bread";

                        // 패널을 다시 그리도록 요청 (paintComponent가 호출됨)
                        repaint();
                    }
                }
            });
        }

        // 이미지 로딩 전용 메서드
        private void loadImages() {
            try {
                // Main.java와 같은 폴더에 있는 파일들을 불러옵니다.
                ImageIcon startIcon = new ImageIcon("background_start.jpg");
                ImageIcon breadIcon = new ImageIcon("Bread_Basic.png");

                startImage = startIcon.getImage();
                breadImage = breadIcon.getImage();

                // 시작 이미지 로딩 확인
                if (startImage == null || startIcon.getIconWidth() == -1) {
                    System.err.println("---!!! 오류 !!!---");
                    System.err.println(" background_start.jpg 이미지를 찾을 수 없습니다.");
                    startImage = null;
                }

                // 빵 이미지 로딩 확인
                if (breadImage == null || breadIcon.getIconWidth() == -1) {
                    System.err.println("---!!! 오류 !!!---");
                    System.err.println(" Bread_Basic.png 이미지를 찾을 수 없습니다.");
                    breadImage = null;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // 2. 패널에 그림을 그리는 메서드 (수정됨)
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            // ⭐ 현재 상태에 따라 다른 이미지를 그림
            if (currentState.equals("start") && startImage != null) {
                // "start" 상태면 시작 배경을 그림
                g.drawImage(startImage, 0, 0, getWidth(), getHeight(), this);

            } else if (currentState.equals("bread") && breadImage != null) {
                // "bread" 상태면 빵 이미지를 그림
                g.drawImage(breadImage, 0, 0, getWidth(), getHeight(), this);

            } else {
                // 이미지 로딩 실패 시 글자 표시
                g.drawString("이미지 로딩 실패. 파일이 Main.java와 같은 폴더에 있는지 확인하세요.", 20, 20);
            }
        }
    }

    // 3. 프로그램 시작점 (main 메서드)
    public static void main(String[] args) {
        // 메인 윈도우(창) 생성
        JFrame frame = new JFrame("케이크 만들기 (간단 버전)");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600); // 창 크기

        // 1번에서 만든 우리만의 이미지 패널을 생성
        ImagePanel panel = new ImagePanel();

        // 창에 이미지 패널을 추가
        frame.add(panel);

        // 창을 화면 중앙에 배치
        frame.setLocationRelativeTo(null);

        // 창을 보여줍니다
        frame.setVisible(true);
    }
}