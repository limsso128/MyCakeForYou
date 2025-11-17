import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.ImageIcon;
import java.awt.Graphics;
import java.awt.Image;

// 하나의 파일로 모든 것을 해결하는 Main 클래스
public class main {

    // 1. 이미지를 배경으로 그리는 특수 패널(Panel)
    // (Main 클래스 안에 작은 클래스를 하나 더 만듦)
    static class ImagePanel extends JPanel {

        private Image backgroundImage;

        public ImagePanel() {
            try {
                // Main.java와 같은 폴더에 있는 "background_start.jpg"를 불러옵니다.
                ImageIcon icon = new ImageIcon("background_start.jpg");
                backgroundImage = icon.getImage();

                // ImageIcon은 파일이 없어도 에러가 안 나서,
                // 이미지가 잘 로드되었는지 확인하는 작업이 필요합니다.
                if (backgroundImage == null || icon.getIconWidth() == -1) {
                    System.err.println("---!!! 오류 !!!---");
                    System.err.println(" background_start.jpg 이미지를 찾을 수 없습니다.");
                    System.err.println(" Main.java와 같은 폴더에 파일이 있는지 확인하세요!");
                    System.err.println("-------------------");
                    backgroundImage = null; // 실패 시 확실히 null로
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // 2. 패널에 그림을 그리는 메서드
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                // 이미지를 패널 크기(getWidth(), getHeight())에 꽉 차게 그립니다.
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            } else {
                // 이미지 로딩 실패 시 글자 표시
                g.drawString("이미지 로딩 실패. Main.java와 같은 폴더에 background_start.jpg가 있는지 확인하세요.", 20, 20);
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