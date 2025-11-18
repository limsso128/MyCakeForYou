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
        private static final int CREAM_WIDTH = 50;  // 생크림 조각 가로 크기
        private static final int CREAM_HEIGHT = 50; // 생크림 조각 세로 크기

        // (배경 이미지)
        private Image startImage;
        private Image breadBasicImage, breadChocoImage, breadStrawberryImage;
        private Image breadBasicCreamImage, breadChocoCreamImage, breadStrawberryCreamImage;
        private Image letterSelectionImage; // (4단계)
        private Image letterWriteImage;     // (6단계 배경)
        private Image[] letterImages = new Image[9]; // (letter1~9 편지지)

        // (크림 '조각' 이미지)
        private Image creamChocoImg, creamStrawImg, creamWhiteImg;

        private String currentState;
        private String selectedTool = "none";
        private int selectedLetterNumber = 0; // (선택된 편지 번호 저장, 1~9)

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
            loadImages(); // 여기서 이미지 로드
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
                            currentState = "bread_choco";
                            repaint();
                        }
                        // "딸기" 버튼
                        else if (isClickInArea(x, y, 312, 462, 26, 126)) {
                            currentState = "bread_strawberry";
                            repaint();
                        }
                        // "기본" 버튼
                        else if (isClickInArea(x, y, 489, 639, 18, 118)) {
                            currentState = "bread_basic";
                            repaint();
                        }
                        // "다음" 버튼 (빵 -> 크림)
                        else if (isClickInArea(x, y, 601, 751, 441, 541)) {
                            if (currentState.equals("bread_basic")) currentState = "cream_basic";
                            else if (currentState.equals("bread_choco")) currentState = "cream_choco";
                            else if (currentState.equals("bread_strawberry")) currentState = "cream_strawberry";
                            decorations.clear();
                            selectedTool = "none";
                            repaint();
                        }

                        // [ 3단계: 크림 선택/배치 ]
                    } else if (currentState.startsWith("cream_")) {

                        // "다음" 버튼 (크림 -> 편지 선택)
                        if (isClickInArea(x, y, 601, 751, 441, 541)) {
                            currentState = "letter_selection";
                            decorations.clear(); // 크림 장식 지우기
                            selectedTool = "none";
                            repaint();
                        }
                        // "초코크림" 선택
                        else if (isClickInArea(x, y, 119, 269, 39, 139)) {
                            selectedTool = "cream_choco";
                        }
                        // "딸기크림" 선택
                        else if (isClickInArea(x, y, 314, 464, 42, 142)) {
                            selectedTool = "cream_straw";
                        }
                        // "하얀크림" 선택
                        else if (isClickInArea(x, y, 496, 646, 38, 138)) {
                            selectedTool = "cream_white";
                        }
                        // 케이크 영역 클릭 시 (크림 배치)
                        else {
                            Image imageToPlace = null;
                            if (selectedTool.equals("cream_choco")) imageToPlace = creamChocoImg;
                            else if (selectedTool.equals("cream_straw")) imageToPlace = creamStrawImg;
                            else if (selectedTool.equals("cream_white")) imageToPlace = creamWhiteImg;

                            if (imageToPlace != null) {
                                int imgWidth = imageToPlace.getWidth(null);
                                int imgHeight = imageToPlace.getHeight(null);
                                int placeX = x - (imgWidth / 2);
                                int placeY = y - (imgHeight / 2);
                                decorations.add(new Placement(placeX, placeY, imageToPlace));
                                repaint();
                            }
                        }

                        // [ 4단계: 편지 선택 ] -> 클릭하면 6단계(letter_write)로 넘어감
                    } else if (currentState.equals("letter_selection")) {
                        // "다음" 버튼
                        if (isClickInArea(x, y, 601, 751, 441, 541)) {
                            currentState = "start"; // 임시로 처음으로
                            selectedLetterNumber = 0;
                            repaint();
                            return;
                        }

                        // --- ⭐ 9개 편지지 좌표 적용 (주위 +/- 50픽셀) ---
                        int clickedLetter = 0; // 0은 클릭 안 함

                        // (192, 110) 주변
                        if (isClickInArea(x, y, 142, 242, 60, 160)) { clickedLetter = 1; }
                        // (386, 110) 주변
                        else if (isClickInArea(x, y, 336, 436, 60, 160)) { clickedLetter = 2; }
                        // (588, 111) 주변
                        else if (isClickInArea(x, y, 538, 638, 61, 161)) { clickedLetter = 3; }
                        // (191, 253) 주변
                        else if (isClickInArea(x, y, 141, 241, 203, 303)) { clickedLetter = 4; }
                        // (387, 256) 주변
                        else if (isClickInArea(x, y, 337, 437, 206, 306)) { clickedLetter = 5; }
                        // (586, 262) 주변
                        else if (isClickInArea(x, y, 536, 636, 212, 312)) { clickedLetter = 6; }
                        // (191, 403) 주변
                        else if (isClickInArea(x, y, 141, 241, 353, 453)) { clickedLetter = 7; }
                        // (390, 403) 주변
                        else if (isClickInArea(x, y, 340, 440, 353, 453)) { clickedLetter = 8; }
                        // (590, 400) 주변
                        else if (isClickInArea(x, y, 540, 640, 350, 450)) { clickedLetter = 9; }

                        // 편지 중 하나가 클릭되었다면
                        if (clickedLetter != 0) {
                            selectedLetterNumber = clickedLetter; // 선택된 편지 번호 저장 (1~9)
                            currentState = "letter_write";       // 6단계(편지 작성) 상태로 전환
                            System.out.println("편지 " + selectedLetterNumber + " 선택. 편지 작성 화면으로 이동.");
                            repaint();
                        }

                        // [ 6단계: 편지 작성 (letter_write) ]
                    } else if (currentState.equals("letter_write")) {

                        // "다음" 버튼
                        if (isClickInArea(x, y, 601, 751, 441, 541)) {
                            currentState = "start"; // 임시로 처음으로
                            selectedLetterNumber = 0; // 선택된 편지 초기화
                            repaint();
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

                // (4단계: 편지 선택 이미지 로드)
                letterSelectionImage = loadImage("letter_selection.png");

                // (6단계 배경 이미지 로드)
                letterWriteImage = loadImage("letter_write.png");

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

        // (원본 크기 로더 - ⭐ 'getResource' 방식으로 수정됨)
        /**
         * 이미지를 .class 파일 기준(리소스)으로 불러옵니다.
         * (new ImageIcon(fileName)보다 훨씬 안정적입니다)
         */
        private Image loadImage(String fileName) {
            try {
                // getClass().getResource()는 .class 파일 기준으로 리소스를 찾습니다.
                java.net.URL imgURL = getClass().getResource(fileName);

                if (imgURL == null) {
                    System.err.println("오류: " + fileName + " 파일을 찾을 수 없습니다. (리소스로딩 실패)");
                    System.err.println("팁: .class 파일과 .png 파일이 같은 폴더에 있는지 확인하세요.");
                    return null;
                }

                ImageIcon icon = new ImageIcon(imgURL);

                // 로딩이 실패했는지 한 번 더 확인 (getIconWidth() == -1)
                if (icon.getIconWidth() == -1) {
                    System.err.println("오류: " + fileName + "을 찾았지만, 이미지로 불러올 수 없습니다.");
                    return null;
                }

                return icon.getImage();

            } catch (Exception e) {
                System.err.println("오류: " + fileName + " 로딩 중 예외 발생: " + e.getMessage());
                e.printStackTrace();
                return null;
            }
        }

        // (크기 조절 로더 - 동일)
        private Image loadImage(String fileName, int width, int height) {
            Image originalImage = loadImage(fileName); // 1. 원본 로드 (수정된 리소스 로더 호출)
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

        // --- 5. 화면 그리기 (⭐ 디버깅 코드 포함) ---
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
            // (6단계 편지 작성 화면 그리기)
            else if (currentState.equals("letter_write")) {
                backgroundToDraw = letterWriteImage;
            }

            // (선택된 배경 그리기)
            if (backgroundToDraw != null) {
                g.drawImage(backgroundToDraw, 0, 0, getWidth(), getHeight(), this);
            } else {
                g.drawString(currentState + " 이미지 로딩 실패.", 20, 20);
            }

            // (크림 장식은 'cream_' 상태일 때만 그림)
            if (currentState.startsWith("cream_")) {
                for (Placement p : decorations) {
                    g.drawImage(p.image, p.x, p.y, this);
                }
            }

            // (letter_write 상태일 때 선택된 편지지를 겹쳐 그리기)
            // ... (위쪽 코드는 그대로) ...

            // (letter_write 상태일 때 선택된 편지지를 겹쳐 그리기)
            if (currentState.equals("letter_write") && selectedLetterNumber != 0) {
                // selectedLetterNumber는 1~9, 배열 인덱스는 0~8
                Image selectedLetterImage = letterImages[selectedLetterNumber - 1];

                if (selectedLetterImage != null) {

                    // --- ⭐ [수정된 부분 시작] ---

                    // 1. 원하는 크기 설정 (화면에 적당히 들어오도록 400~500 정도로 줄임)
                    // 편지지가 세로로 길다면 width=400, height=500 추천
                    // 편지지가 가로로 길다면 width=500, height=400 추천
                    int targetWidth = 405;
                    int targetHeight = 304;

                    // 2. 화면 중앙 좌표 계산 (줄어든 크기 기준)
                    int letterX = (getWidth() - targetWidth) / 2;   // 가로 중앙
                    int letterY = (getHeight() - targetHeight) / 2; // 세로 중앙

                    // 3. 지정한 크기(targetWidth, targetHeight)로 그리기
                    // g.drawImage(이미지, x, y, 가로크기, 세로크기, this);
                    g.drawImage(selectedLetterImage, letterX, letterY, targetWidth, targetHeight, this);

                    // --- ⭐ [수정된 부분 끝] ---

                } else {
                    // (실패!) 이미지가 null입니다.
                    String errorMsg = "오류: letter" + selectedLetterNumber + ".png 파일을 불러올 수 없습니다!";
                    System.err.println(errorMsg);
                    g.setColor(java.awt.Color.RED);
                    g.setFont(new java.awt.Font("SansSerif", java.awt.Font.BOLD, 20));
                    g.drawString(errorMsg, 50, 100);
                }
            }
// ... (메서드 끝)
        }
    }

    // 3. 프로그램 시작점 (main 메서드)
    public static void main(String[] args) {
        JFrame frame = new JFrame("케이크 만들기");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        ImagePanel panel = new ImagePanel();
        frame.add(panel);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}