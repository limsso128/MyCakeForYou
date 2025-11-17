import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.ImageIcon;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

// public class main
public class main {

    // 1. 이미지를 배경으로 그리는 특수 패널(Panel)
    static class ImagePanel extends JPanel {

        // --- 1. 변수 선언 ---

        // ⭐↓↓↓ 여기서 크기 조절! (숫자를 바꾸세요) ↓↓↓⭐
        private static final int CREAM_WIDTH = 50;  // 생크림 조각 가로 크기
        private static final int CREAM_HEIGHT = 50; // 생크림 조각 세로 크기
        // ⭐↑↑↑ 여기서 크기 조절! ↑↑↑⭐

        // (배경 이미지)
        private Image startImage;
        private Image breadBasicImage, breadChocoImage, breadStrawberryImage;
        private Image breadBasicCreamImage, breadChocoCreamImage, breadStrawberryCreamImage;

        // (크림 '조각' 이미지)
        private Image creamChocoImg, creamStrawImg, creamWhiteImg;

        private String currentState;
        private String selectedTool = "none";

        private ArrayList<Placement> decorations = new ArrayList<>();

        // (Placement 클래스 - 동일)
        static class Placement {
            int x, y;
            Image image;
            public Placement(int x, int y, Image image) {
                this.x = x; this.y = y; this.image = image;
            }
        }

        // (생성자 - 동일)
        public ImagePanel() {
            loadImages();
            currentState = "start";

            // (마우스 클릭 리스너 - 이전과 동일)
            this.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    int x = e.getX();
                    int y = e.getY();

                    // [ 1단계: 시작 ]
                    if (currentState.equals("start")) {
                        currentState = "bread_basic";
                        repaint();

                        // [ 2단계: 빵 선택 ]
                    } else if (currentState.startsWith("bread_")) {

                        // "초코" 버튼
                        if (isClickInArea(x, y, 121, 271, 26, 126)) {
                            System.out.println("빵 선택: 초코");
                            currentState = "bread_choco";
                            repaint();
                        }
                        // "딸기" 버튼
                        else if (isClickInArea(x, y, 312, 462, 26, 126)) {
                            System.out.println("빵 선택: 딸기");
                            currentState = "bread_strawberry";
                            repaint();
                        }
                        // "기본" 버튼
                        else if (isClickInArea(x, y, 489, 639, 18, 118)) {
                            System.out.println("빵 선택: 기본");
                            currentState = "bread_basic";
                            repaint();
                        }
                        // "다음" 버튼
                        else if (isClickInArea(x, y, 601, 751, 441, 541)) {
                            System.out.println("--- '다음' 버튼 클릭 성공! ---");
                            if (currentState.equals("bread_basic")) currentState = "cream_basic";
                            else if (currentState.equals("bread_choco")) currentState = "cream_choco";
                            else if (currentState.equals("bread_strawberry")) currentState = "cream_strawberry";
                            decorations.clear();
                            selectedTool = "none";
                            repaint();
                        }
                        else {
                            System.out.println("빵 화면 클릭 (버튼 밖): x=" + x + ", y=" + y);
                        }

                        // [ 3단계: 크림 선택/배치 ]
                    } else if (currentState.startsWith("cream_")) {

                        // "초코크림" 선택
                        if (isClickInArea(x, y, 119, 269, 39, 139)) {
                            System.out.println("도구 선택: 초코 크림");
                            selectedTool = "cream_choco";
                        }
                        // "딸기크림" 선택
                        else if (isClickInArea(x, y, 314, 464, 42, 142)) {
                            System.out.println("도구 선택: 딸기 크림");
                            selectedTool = "cream_straw";
                        }
                        // "하얀크림" 선택
                        else if (isClickInArea(x, y, 496, 646, 38, 138)) {
                            System.out.println("도구 선택: 하얀 크림");
                            selectedTool = "cream_white";
                        }
                        // 케이크 영역 클릭 시
                        else {
                            Image imageToPlace = null;
                            if (selectedTool.equals("cream_choco")) imageToPlace = creamChocoImg;
                            else if (selectedTool.equals("cream_straw")) imageToPlace = creamStrawImg;
                            else if (selectedTool.equals("cream_white")) imageToPlace = creamWhiteImg;

                            if (imageToPlace != null) {
                                // ⭐ 크기 조절 덕분에 getWidth()는 50이 됨
                                int imgWidth = imageToPlace.getWidth(null);
                                int imgHeight = imageToPlace.getHeight(null);
                                int placeX = x - (imgWidth / 2);
                                int placeY = y - (imgHeight / 2);

                                decorations.add(new Placement(placeX, placeY, imageToPlace));
                                repaint();
                            }
                        }
                    }
                }
            });
        }

        // (클릭 영역 확인 - 동일)
        private boolean isClickInArea(int x, int y, int x1, int x2, int y1, int y2) {
            return (x >= x1 && x <= x2) && (y >= y1 && y <= y2);
        }

        // --- 4. 이미지 로딩 (⭐ 수정됨) ---
        private void loadImages() {
            try {
                // (배경 이미지들은 원본 크기로 로드)
                startImage = loadImage("background_start.jpg");
                breadBasicImage = loadImage("Bread_Basic.png");
                breadChocoImage = loadImage("Bread_Choco.png");
                breadStrawberryImage = loadImage("Bread_Strawberry.png");
                breadBasicCreamImage = loadImage("Bread_Basic_Cream.png");
                breadChocoCreamImage = loadImage("Bread_Choco_Cream.png");
                breadStrawberryCreamImage = loadImage("Bread_Strawberry_Cream.png");

                // ⭐ (크림 조각들은 '지정한 크기'로 로드)
                creamChocoImg = loadImage("Cream_Chocolate.png", 600, 600);
                creamStrawImg = loadImage("Cream_Strawberry.png", CREAM_WIDTH, CREAM_HEIGHT);
                creamWhiteImg = loadImage("Cream_White.png", CREAM_WIDTH, CREAM_HEIGHT);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // (원본 크기 로더 - 동일)
        private Image loadImage(String fileName) {
            ImageIcon icon = new ImageIcon(fileName);
            if (icon.getIconWidth() == -1) {
                System.err.println("오류: " + fileName + " 파일을 찾을 수 없습니다.");
                return null;
            }
            return icon.getImage();
        }

        // ⭐ (새로 추가됨: 크기 조절 로더)
        /**
         * 이미지를 불러와 지정한 크기(width, height)로 조절합니다.
         */
        // ⭐ (새로 추가됨: 크기 조절 로더)
        /**
         * 이미지를 불러와 지정한 크기(width, height)로 조절합니다.
         * (getScaledInstance의 문제를 해결하기 위해 ImageIcon으로 감싸서 로딩을 강제합니다)
         */
        private Image loadImage(String fileName, int width, int height) {
            Image originalImage = loadImage(fileName); // 1. 원본 로드
            if (originalImage != null) {

                // 2. 크기 조절 (아직 '준비'만 됨)
                Image scaledImage = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);

                // 3. ⭐(핵심)⭐ ImageIcon으로 감싸서 '즉시' 로딩/스케일링 실행
                ImageIcon scaledIcon = new ImageIcon(scaledImage);

                // 4. 완전히 로드된 이미지를 반환
                return scaledIcon.getImage();
            }
            return null;
        }

        // --- 5. 화면 그리기 (동일) ---
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Image backgroundToDraw = null;

            if (currentState.equals("start")) backgroundToDraw = startImage;
            else if (currentState.equals("bread_basic")) backgroundToDraw = breadBasicImage;
            else if (currentState.equals("bread_choco")) backgroundToDraw = breadChocoImage;
            else if (currentState.equals("bread_strawberry")) backgroundToDraw = breadStrawberryImage;
            else if (currentState.equals("cream_basic")) backgroundToDraw = breadBasicCreamImage;
            else if (currentState.equals("cream_choco")) backgroundToDraw = breadChocoCreamImage;
            else if (currentState.equals("cream_strawberry")) backgroundToDraw = breadStrawberryCreamImage;

            if (backgroundToDraw != null) {
                g.drawImage(backgroundToDraw, 0, 0, getWidth(), getHeight(), this);
            } else {
                g.drawString(currentState + " 이미지 로딩 실패.", 20, 20);
            }

            if (currentState.startsWith("cream_")) {
                for (Placement p : decorations) {
                    // p.image는 이미 50x50으로 조절된 이미지입니다.
                    g.drawImage(p.image, p.x, p.y, this);
                }
            }
        }
    }

    // 3. 프로그램 시작점 (main 메서드 - 동일)
    public static void main(String[] args) {
        JFrame frame = new JFrame("케이크 만들기 - 3단계 크림 배치");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        ImagePanel panel = new ImagePanel();
        frame.add(panel);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}