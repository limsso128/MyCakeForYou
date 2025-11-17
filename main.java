import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.ImageIcon;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

// 하나의 파일로 모든 것을 해결하는 Main 클래스
public class main {

    // 1. 이미지를 배경으로 그리는 특수 패널(Panel)
    static class ImagePanel extends JPanel {

        // 4개의 화면 이미지를 저장할 변수
        private Image startImage;
        private Image breadBasicImage;
        private Image breadChocoImage;
        private Image breadStrawberryImage;

        private String currentState; // 현재 어떤 화면인지 저장

        public ImagePanel() {
            loadImages();
            currentState = "start"; // 시작 상태

            // ⭐↓↓↓ 여기가 핵심입니다! ↓↓↓⭐
            // ⭐ '버튼 클릭'용 마우스 리스너
            this.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {

                    int x = e.getX();
                    int y = e.getY();

                    if (currentState.equals("start")) {
                        // 1. 'start' 화면에서는 'bread_basic' (기본 빵) 화면으로 넘어감
                        currentState = "bread_basic";
                        repaint();

                    } else if (currentState.startsWith("bread_")) {
                        // 2. 'bread_' (빵) 화면일 때

                        // "초코" 버튼 영역 (196, 76) 근처
                        // (x: 121~271, y: 26~126) -> (가로 150, 세로 100 크기)
                        if ((x >= 121 && x <= 271) && (y >= 26 && y <= 126)) {
                            System.out.println("초코 빵 선택!");
                            currentState = "bread_choco";
                            repaint();
                        }

                        // "딸기" 버튼 영역 (387, 76) 근처
                        // (x: 312~462, y: 26~126)
                        else if ((x >= 312 && x <= 462) && (y >= 26 && y <= 126)) {
                            System.out.println("딸기 빵 선택!");
                            currentState = "bread_strawberry";
                            repaint();
                        }

                        // "기본" 버튼 영역 (564, 68) 근처
                        // (x: 489~639, y: 18~118)
                        else if ((x >= 489 && x <= 639) && (y >= 18 && y <= 118)) {
                            System.out.println("기본 빵 선택!");
                            currentState = "bread_basic";
                            repaint();
                        }

                        // (참고: 나중에 '다음' 버튼을 만들려면 여기에 else if로 추가하면 됩니다)
                    }
                }
            });
            // ⭐↑↑↑ 여기까지입니다! ↑↑↑⭐
        }

        // 이미지 로딩 전용 메서드 (수정됨)
        private void loadImages() {
            try {
                // 4개 이미지 모두 불러오기
                startImage = loadImage("background_start.jpg");
                breadBasicImage = loadImage("Bread_Basic.png");
                breadChocoImage = loadImage("Bread_Choco.png"); // (이 파일이 없으면 null이 됨)
                breadStrawberryImage = loadImage("Bread_Strawberry.png");

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // (이미지 로딩을 편하게 해주는 도우미 메서드)
        private Image loadImage(String fileName) {
            ImageIcon icon = new ImageIcon(fileName);
            if (icon.getIconWidth() == -1) {
                System.err.println("오류: " + fileName + " 파일을 찾을 수 없습니다.");
                return null;
            }
            return icon.getImage();
        }

        // 패널에 그림을 그리는 메서드 (수정됨)
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            // 현재 상태(currentState)에 맞는 이미지를 골라서 그림
            if (currentState.equals("start") && startImage != null) {
                g.drawImage(startImage, 0, 0, getWidth(), getHeight(), this);
            }
            else if (currentState.equals("bread_basic") && breadBasicImage != null) {
                g.drawImage(breadBasicImage, 0, 0, getWidth(), getHeight(), this);
            }
            else if (currentState.equals("bread_choco") && breadChocoImage != null) {
                g.drawImage(breadChocoImage, 0, 0, getWidth(), getHeight(), this);
            }
            else if (currentState.equals("bread_strawberry") && breadStrawberryImage != null) {
                g.drawImage(breadStrawberryImage, 0, 0, getWidth(), getHeight(), this);
            }
            else {
                // 해당 이미지가 로딩 실패했을 때
                g.drawString(currentState + " 이미지 로딩 실패.", 20, 20);
            }
        }
    }

    // 3. 프로그램 시작점 (main 메서드) (이전과 동일)
    public static void main(String[] args) {
        JFrame frame = new JFrame("케이크 만들기 - 2단계 빵 선택");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600); // 창 크기

        ImagePanel panel = new ImagePanel();
        frame.add(panel);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}