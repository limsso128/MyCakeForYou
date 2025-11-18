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
        private Image letterSelectionImage; // (4단계)
        private Image[] letterImages = new Image[9]; // (5단계: letter1~9)

        // (크림 '조각' 이미지)
        private Image creamChocoImg, creamStrawImg, creamWhiteImg;

        private String currentState;
        private String selectedTool = "none";

        private ArrayList<Placement> decorations = new ArrayList<>();

        // (크림/장식 위치 저장을 위한 내부 클래스)
        static class Placement {
            int x, y;
            Image image;
            public Placement(int x, int y, Image image) {
                this.x = x; this.y = y; this.image = image;
            }
        }

        // (생성자)
        public ImagePanel() {
            loadImages();
            currentState = "start";

            // (마우스 클릭 리스너)
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
                        // "다음" 버튼 (빵 -> 크림)
                        else if (isClickInArea(x, y, 601, 751, 441, 541)) {
                            System.out.println("--- '다음' 버튼 클릭 (빵 -> 크림) ---");
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

                        // "다음" 버튼 (크림 -> 편지)
                        if (isClickInArea(x, y, 601, 751, 441, 541)) {
                            System.out.println("--- '다음' 버튼 클릭 (크림 -> 편지) ---");
                            currentState = "letter_selection";
                            decorations.clear(); // 크림 장식 지우기
                            selectedTool = "none";
                            repaint();
                        }
                        // "초코크림" 선택
                        else if (isClickInArea(x, y, 119, 269, 39, 139)) {
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
                        // 케이크 영역 클릭 시 (크림 배치)
                        else {
                            Image imageToPlace = null;
                            if (selectedTool.equals("cream_choco")) imageToPlace = creamChocoImg;
                            else if (selectedTool.equals("cream_straw")) imageToPlace = creamStrawImg;
                            else if (selectedTool.equals("cream_white")) imageToPlace = creamWhiteImg;

                            if (imageToPlace != null) {
                                // 크기 조절된 이미지의 크기를 가져옴
                                int imgWidth = imageToPlace.getWidth(null);
                                int imgHeight = imageToPlace.getHeight(null);
                                int placeX = x - (imgWidth / 2);
                                int placeY = y - (imgHeight / 2);

                                decorations.add(new Placement(placeX, placeY, imageToPlace));
                                repaint();
                            }
                        }

                        // [ 4단계: 편지 선택 ]
                    } else if (currentState.equals("letter_selection")) {

                        // "다음" 버튼 (여기서는 우선 비활성화. 필요하면 좌표 넣고 기능 추가)
                        // if (isClickInArea(x, y, 601, 751, 441, 541)) { ... }

                        // --- ⭐ 여기에 편지 1~9번 클릭 좌표를 넣으세요 (지금은 0,0,0,0 입니다) ---
                        if (isClickInArea(x, y, 0, 0, 0, 0)) { // 1번 편지 (좌표 수정 필요)
                            currentState = "letter1";
                            repaint();
                        } else if (isClickInArea(x, y, 0, 0, 0, 0)) { // 2번 편지 (좌표 수정 필요)
                            currentState = "letter2";
                            repaint();
                        } else if (isClickInArea(x, y, 0, 0, 0, 0)) { // 3번 편지 (좌표 수정 필요)
                            currentState = "letter3";
                            repaint();
                        } else if (isClickInArea(x, y, 0, 0, 0, 0)) { // 4번 편지 (좌표 수정 필요)
                            currentState = "letter4";
                            repaint();
                        } else if (isClickInArea(x, y, 0, 0, 0, 0)) { // 5번 편지 (좌표 수정 필요)
                            currentState = "letter5";
                            repaint();
                        } else if (isClickInArea(x, y, 0, 0, 0, 0)) { // 6번 편지 (좌표 수정 필요)
                            currentState = "letter6";
                            repaint();
                        } else if (isClickInArea(x, y, 0, 0, 0, 0)) { // 7번 편지 (좌표 수정 필요)
                            currentState = "letter7";
                            repaint();
                        } else if (isClickInArea(x, y, 0, 0, 0, 0)) { // 8번 편지 (좌표 수정 필요)
                            currentState = "letter8";
                            repaint();
                        } else if (isClickInArea(x, y, 0, 0, 0, 0)) { // 9번 편지 (좌표 수정 필요)
                            currentState = "letter9";
                            repaint();
                        } else {
                            System.out.println("편지 선택 화면 클릭 (버튼 밖): x=" + x + ", y=" + y);
                        }

                        // [ 5단계: 개별 편지 확인 ]
                    } else if (currentState.startsWith("letter")) {

                        // "다음" 버튼 (편지 -> 다음 단계? 또는 처음?)
                        if (isClickInArea(x, y, 601, 751, 441, 541)) {
                            System.out.println("--- '다음' 버튼 클릭 (편지 -> ???) ---");
                            // TODO: 이 다음 단계로 넘어가거나, 처음으로 돌아갑니다.
                            currentState = "start"; // (임시로 처음으로)
                            decorations.clear();
                            selectedTool = "none";
                            repaint();
                        }
                    }

                }
            });
        }

        // (클릭 영역 확인)
        private boolean isClickInArea(int x, int y, int x1, int x2, int y1, int y2) {
            return (x >= x1 && x <= x2) && (y >= y1 && y <= y2);
        }

        // --- 4. 이미지 로딩 ---
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

                // (4단계: 편지 선택 이미지 로드)
                letterSelectionImage = loadImage("letter_selection.png");

                // (5단계: 개별 편지 1~9 로드)
                for (int i = 0; i < 9; i++) {
                    letterImages[i] = loadImage("letter" + (i + 1) + ".png");
                }

                // (크림 조각들은 '지정한 크기'로 로드)
                creamChocoImg = loadImage("Cream_Chocolate.png", CREAM_WIDTH, CREAM_HEIGHT);
                creamStrawImg = loadImage("Cream_Strawberry.png", CREAM_WIDTH, CREAM_HEIGHT);
                creamWhiteImg = loadImage("Cream_White.png", CREAM_WIDTH, CREAM_HEIGHT);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // (원본 크기 로더)
        private Image loadImage(String fileName) {
            ImageIcon icon = new ImageIcon(fileName);
            if (icon.getIconWidth() == -1) {
                System.err.println("오류: " + fileName + " 파일을 찾을 수 없습니다.");
                return null;
            }
            return icon.getImage();
        }

        // (크기 조절 로더)
        private Image loadImage(String fileName, int width, int height) {
            Image originalImage = loadImage(fileName); // 1. 원본 로드
            if (originalImage != null) {
                // 2. 크기 조절
                Image scaledImage = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
                // 3. ImageIcon으로 감싸서 '즉시' 로딩 실행
                ImageIcon scaledIcon = new ImageIcon(scaledImage);
                // 4. 완전히 로드된 이미지 반환
                return scaledIcon.getImage();
            }
            return null;
        }

        // --- 5. 화면 그리기 ---
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Image backgroundToDraw = null;

            // (1~3단계 그리기)
            if (currentState.equals("start")) backgroundToDraw = startImage;
            else if (currentState.equals("bread_basic")) backgroundToDraw = breadBasicImage;
            else if (currentState.equals("bread_choco")) backgroundToDraw = breadChocoImage;
            else if (currentState.equals("bread_strawberry")) backgroundToDraw = breadStrawberryImage;
            else if (currentState.equals("cream_basic")) backgroundToDraw = breadBasicCreamImage;
            else if (currentState.equals("cream_choco")) backgroundToDraw = breadChocoCreamImage;
            else if (currentState.equals("cream_strawberry")) backgroundToDraw = breadStrawberryCreamImage;

                // (4단계: 편지 선택 그리기)
            else if (currentState.equals("letter_selection")) {
                backgroundToDraw = letterSelectionImage;
            }
            // (5단계: 개별 편지 그리기)
            else if (currentState.startsWith("letter")) {
                try {
                    // "letter" 다음의 숫자를 추출 (e.g., "letter1" -> 1)
                    String numberStr = currentState.substring("letter".length());
                    int index = Integer.parseInt(numberStr) - 1; // 1~9 -> 0~8 (배열 인덱스)

                    if (index >= 0 && index < 9 && letterImages[index] != null) {
                        backgroundToDraw = letterImages[index];
                    } else {
                        g.drawString(currentState + " 이미지 로딩 실패.", 20, 20);
                    }
                } catch (Exception e) {
                    g.drawString("잘못된 letter 상태: " + currentState, 20, 40);
                }
            }

            // (선택된 배경 그리기)
            if (backgroundToDraw != null) {
                g.drawImage(backgroundToDraw, 0, 0, getWidth(), getHeight(), this);
            } else {
                // 5단계에서 letterImages[index]가 null일 경우 이 메시지가 뜸
                g.drawString(currentState + " 이미지 로딩 실패.", 20, 20);
            }

            // (크림 장식은 'cream_' 상태일 때만 그림)
            if (currentState.startsWith("cream_")) {
                for (Placement p : decorations) {
                    g.drawImage(p.image, p.x, p.y, this);
                }
            }
        }
    }

    // 3. 프로그램 시작점 (main 메서드)
    public static void main(String[] args) {
        JFrame frame = new JFrame("케이크 만들기"); // 제목 단순화
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        ImagePanel panel = new ImagePanel();
        frame.add(panel);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}