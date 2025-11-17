import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.ImageIcon;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

// public class main (클래스 이름이 소문자 'main'인 것 유지)
public class main {

    // 1. 이미지를 배경으로 그리는 특수 패널(Panel)
    static class ImagePanel extends JPanel {

        // 총 7개의 화면 이미지를 저장할 변수
        private Image startImage;
        private Image breadBasicImage;
        private Image breadChocoImage;
        private Image breadStrawberryImage;
        private Image breadBasicCreamImage;
        private Image breadChocoCreamImage;
        private Image breadStrawberryCreamImage;

        // 현재 어떤 화면인지 저장
        // "start", "bread_basic", "bread_choco", "bread_strawberry",
        // "cream_basic", "cream_choco", "cream_strawberry"
        private String currentState;

        public ImagePanel() {
            loadImages();
            currentState = "start"; // 시작 상태

            // ⭐↓↓↓ 여기가 최종 클릭 리스너입니다! ↓↓↓⭐
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
                        // 2. 'bread_' (빵 선택) 화면일 때

                        // "초코" 버튼 영역 (196, 76) 근처
                        if ((x >= 121 && x <= 271) && (y >= 26 && y <= 126)) {
                            System.out.println("초코 빵 선택!");
                            currentState = "bread_choco";
                            repaint();
                        }

                        // "딸기" 버튼 영역 (387, 76) 근처
                        else if ((x >= 312 && x <= 462) && (y >= 26 && y <= 126)) {
                            System.out.println("딸기 빵 선택!");
                            // 'S' 대소문자 오류 방지를 위해 소문자로 통일
                            currentState = "bread_strawberry";
                            repaint();
                        }

                        // "기본" 버튼 영역 (564, 68) 근처
                        else if ((x >= 489 && x <= 639) && (y >= 18 && y <= 118)) {
                            System.out.println("기본 빵 선택!");
                            currentState = "bread_basic";
                            repaint();
                        }

                        // ⭐ "다음" 버튼 영역 (676, 491) 근처
                        // (x: 601~751, y: 441~541) -> (가로 150, 세로 100 크기)
                        else if ((x >= 601 && x <= 751) && (y >= 441 && y <= 541)) {
                            System.out.println("'다음' 버튼 클릭! 현재 빵: " + currentState);

                            // 현재 빵 상태에 따라 다음 크림 화면으로 이동
                            if (currentState.equals("bread_basic")) {
                                currentState = "cream_basic";
                            }
                            else if (currentState.equals("bread_choco")) {
                                currentState = "cream_choco";
                            }
                            else if (currentState.equals("bread_strawberry")) {
                                currentState = "cream_strawberry";
                            }
                            repaint();
                        }
                    }
                    // (추가) 3단계 크림 화면에서 클릭 이벤트가 필요하면
                    // else if (currentState.startsWith("cream_")) { ... }
                }
            });
            // ⭐↑↑↑ 여기까지입니다! ↑↑↑⭐
        }

        // 이미지 로딩 전용 메서드 (수정됨)
        private void loadImages() {
            try {
                // 7개 이미지 모두 불러오기
                startImage = loadImage("background_start.jpg");
                breadBasicImage = loadImage("Bread_Basic.png");
                breadChocoImage = loadImage("Bread_Choco.png");
                breadStrawberryImage = loadImage("Bread_Strawberry.png"); // 대문자 S

                breadBasicCreamImage = loadImage("Bread_Basic_Cream.png");
                breadChocoCreamImage = loadImage("Bread_Choco_Cream.png");
                breadStrawberryCreamImage = loadImage("Bread_Strawberry_Cream.png");

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

            Image imageToDraw = null; // 이번에 그릴 이미지를 담을 변수

            // 현재 상태(currentState)에 맞는 이미지를 골라서 변수에 담음
            if (currentState.equals("start")) {
                imageToDraw = startImage;
            }
            else if (currentState.equals("bread_basic")) {
                imageToDraw = breadBasicImage;
            }
            else if (currentState.equals("bread_choco")) {
                imageToDraw = breadChocoImage;
            }
            else if (currentState.equals("bread_strawberry")) {
                imageToDraw = breadStrawberryImage;
            }
            // 3단계 크림 화면 추가
            else if (currentState.equals("cream_basic")) {
                imageToDraw = breadBasicCreamImage;
            }
            else if (currentState.equals("cream_choco")) {
                imageToDraw = breadChocoCreamImage;
            }
            else if (currentState.equals("cream_strawberry")) {
                imageToDraw = breadStrawberryCreamImage;
            }

            // 그릴 이미지가 정해졌다면(null이 아니라면) 화면에 그림
            if (imageToDraw != null) {
                g.drawImage(imageToDraw, 0, 0, getWidth(), getHeight(), this);
            } else {
                // 해당 이미지가 로딩 실패했을 때
                g.drawString(currentState + " 이미지 로딩 실패.", 20, 20);
            }
        }
    }

    // 3. 프로그램 시작점 (main 메서드) (이전과 동일)
    public static void main(String[] args) {
        JFrame frame = new JFrame("케이크 만들기 - 3단계 크림");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600); // 창 크기

        ImagePanel panel = new ImagePanel();
        frame.add(panel);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}