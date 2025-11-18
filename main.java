import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class main {

    // 1. 이미지를 배경으로 그리는 특수 패널 (게임 로직의 핵심)
    static class ImagePanel extends JPanel {

        // --- [변수 선언] ---
        private static final int CREAM_WIDTH = 50;  // 생크림 가로 크기
        private static final int CREAM_HEIGHT = 50; // 생크림 세로 크기

        // (배경 및 소스 이미지)
        private Image startImage;
        private Image breadBasicImage, breadChocoImage, breadStrawberryImage;
        private Image breadBasicCreamImage, breadChocoCreamImage, breadStrawberryCreamImage;
        private Image letterSelectionImage; // 4단계: 편지 고르기 화면
        private Image letterWriteImage;     // 5단계: 편지 쓰기 배경 화면
        private Image[] letterImages = new Image[9]; // 편지지 9장

        // (도구/아이콘 이미지)
        private Image creamChocoImg, creamStrawImg, creamWhiteImg;

        // (상태 관리 변수)
        private String currentState;          // 현재 화면 상태 (start, bread_..., cream_..., letter_...)
        private String selectedTool = "none"; // 현재 선택된 크림
        private int selectedLetterNumber = 0; // 선택된 편지 번호 (1~9)

        // (장식 저장 리스트)
        private ArrayList<Placement> decorations = new ArrayList<>();

        // ⭐ [핵심] 편지 작성을 위한 투명 텍스트 입력창
        private JTextArea textArea;

        // (위치 정보 저장용 내부 클래스)
        static class Placement {
            int x, y;
            Image image;
            public Placement(int x, int y, Image image) {
                this.x = x; this.y = y; this.image = image;
            }
        }

        // --- [생성자] ---
        public ImagePanel() {
            // 1. 레이아웃을 null로 설정 (텍스트창 위치를 내 마음대로 조절하기 위함)
            this.setLayout(null);

            loadImages(); // 이미지 파일 불러오기
            currentState = "start";

            // 2. 텍스트 입력창(JTextArea) 설정
            textArea = new JTextArea();
            textArea.setOpaque(false); // 배경 투명하게 (편지지가 비쳐 보이게)
            textArea.setForeground(Color.BLACK); // 글자색 검정
            textArea.setFont(new Font("SansSerif", Font.BOLD, 18)); // 폰트 설정
            textArea.setLineWrap(true);     // 자동 줄 바꿈
            textArea.setWrapStyleWord(true); // 단어 단위 줄 바꿈
            textArea.setVisible(false);     // 처음엔 숨김
            this.add(textArea);             // 패널에 추가

            // 3. 마우스 이벤트 리스너
            this.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    int x = e.getX();
                    int y = e.getY();

                    // [1단계: 시작 화면]
                    if (currentState.equals("start")) {
                        currentState = "bread_basic";
                        repaint();

                        // [2단계: 빵 선택 화면]
                    } else if (currentState.startsWith("bread_")) {
                        // 초코빵
                        if (isClickInArea(x, y, 121, 271, 26, 126)) {
                            currentState = "bread_choco";
                            repaint();
                        }
                        // 딸기빵
                        else if (isClickInArea(x, y, 312, 462, 26, 126)) {
                            currentState = "bread_strawberry";
                            repaint();
                        }
                        // 기본빵
                        else if (isClickInArea(x, y, 489, 639, 18, 118)) {
                            currentState = "bread_basic";
                            repaint();
                        }
                        // 다음 버튼 (빵 -> 크림)
                        else if (isClickInArea(x, y, 601, 751, 441, 541)) {
                            if (currentState.equals("bread_basic")) currentState = "cream_basic";
                            else if (currentState.equals("bread_choco")) currentState = "cream_choco";
                            else if (currentState.equals("bread_strawberry")) currentState = "cream_strawberry";

                            decorations.clear(); // 이전 장식 초기화
                            selectedTool = "none";
                            repaint();
                        }

                        // [3단계: 크림 꾸미기 화면]
                    } else if (currentState.startsWith("cream_")) {
                        // 다음 버튼 (크림 -> 편지 선택)
                        if (isClickInArea(x, y, 601, 751, 441, 541)) {
                            currentState = "letter_selection";
                            decorations.clear();
                            selectedTool = "none";
                            repaint();
                        }
                        // 크림 도구 선택
                        else if (isClickInArea(x, y, 119, 269, 39, 139)) selectedTool = "cream_choco";
                        else if (isClickInArea(x, y, 314, 464, 42, 142)) selectedTool = "cream_straw";
                        else if (isClickInArea(x, y, 496, 646, 38, 138)) selectedTool = "cream_white";

                            // 케이크 위에 크림 찍기
                        else {
                            Image imageToPlace = null;
                            if (selectedTool.equals("cream_choco")) imageToPlace = creamChocoImg;
                            else if (selectedTool.equals("cream_straw")) imageToPlace = creamStrawImg;
                            else if (selectedTool.equals("cream_white")) imageToPlace = creamWhiteImg;

                            if (imageToPlace != null) {
                                int imgWidth = imageToPlace.getWidth(null);
                                int imgHeight = imageToPlace.getHeight(null);
                                decorations.add(new Placement(x - (imgWidth / 2), y - (imgHeight / 2), imageToPlace));
                                repaint();
                            }
                        }

                        // [4단계: 편지봉투 선택 화면]
                    } else if (currentState.equals("letter_selection")) {
                        int clickedLetter = 0;

                        // 9개의 편지 좌표 체크 (위쪽 줄)
                        if (isClickInArea(x, y, 142, 242, 60, 160)) clickedLetter = 1;
                        else if (isClickInArea(x, y, 336, 436, 60, 160)) clickedLetter = 2;
                        else if (isClickInArea(x, y, 538, 638, 61, 161)) clickedLetter = 3;
                            // (가운데 줄)
                        else if (isClickInArea(x, y, 141, 241, 203, 303)) clickedLetter = 4;
                        else if (isClickInArea(x, y, 337, 437, 206, 306)) clickedLetter = 5;
                        else if (isClickInArea(x, y, 536, 636, 212, 312)) clickedLetter = 6;
                            // (아랫 줄)
                        else if (isClickInArea(x, y, 141, 241, 353, 453)) clickedLetter = 7;
                        else if (isClickInArea(x, y, 340, 440, 353, 453)) clickedLetter = 8;
                        else if (isClickInArea(x, y, 540, 640, 350, 450)) clickedLetter = 9;

                        // 편지를 클릭했다면 -> 작성 화면으로 이동
                        if (clickedLetter != 0) {
                            selectedLetterNumber = clickedLetter;
                            currentState = "letter_write";

                            // ⭐ 텍스트창 활성화 로직
                            textArea.setText("");       // 내용 비우기
                            textArea.setVisible(true);  // 보이게 하기
                            textArea.requestFocus();    // 커서 깜빡이기

                            repaint();
                        }

                        // [5단계: 편지 작성 화면 (완료)]
                    } else if (currentState.equals("letter_write")) {
                        // 다음(완료) 버튼 -> 처음으로 돌아가기
                        if (isClickInArea(x, y, 601, 751, 441, 541)) {
                            currentState = "start";
                            selectedLetterNumber = 0;

                            // ⭐ 텍스트창 숨기기
                            textArea.setVisible(false);

                            repaint();
                        }
                    }
                }
            });
        }

        // 좌표 클릭 확인 도우미 메서드
        private boolean isClickInArea(int x, int y, int x1, int x2, int y1, int y2) {
            return (x >= x1 && x <= x2) && (y >= y1 && y <= y2);
        }

        // --- [이미지 로드 메서드] ---
        private void loadImages() {
            try {
                // 배경류
                startImage = loadImage("background_start.jpg");
                breadBasicImage = loadImage("Bread_Basic.png");
                breadChocoImage = loadImage("Bread_Choco.png");
                breadStrawberryImage = loadImage("Bread_Strawberry.png");
                breadBasicCreamImage = loadImage("Bread_Basic_Cream.png");
                breadChocoCreamImage = loadImage("Bread_Choco_Cream.png");
                breadStrawberryCreamImage = loadImage("Bread_Strawberry_Cream.png");

                letterSelectionImage = loadImage("letter_selection.png");
                letterWriteImage = loadImage("letter_write.png");

                // 편지지 9개
                for (int i = 0; i < 9; i++) {
                    letterImages[i] = loadImage("letter" + (i + 1) + ".png");
                }

                // 크림 아이콘 (사이즈 조절)
                creamChocoImg = loadImage("Cream_Chocolate.png", CREAM_WIDTH, CREAM_HEIGHT);
                creamStrawImg = loadImage("Cream_Strawberry.png", CREAM_WIDTH, CREAM_HEIGHT);
                creamWhiteImg = loadImage("Cream_White.png", CREAM_WIDTH, CREAM_HEIGHT);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // 기본 이미지 로더
        private Image loadImage(String fileName) {
            try {
                java.net.URL imgURL = getClass().getResource(fileName);
                if (imgURL == null) {
                    System.err.println("이미지 없음: " + fileName);
                    return null;
                }
                return new ImageIcon(imgURL).getImage();
            } catch (Exception e) { return null; }
        }

        // 크기 조절 이미지 로더
        private Image loadImage(String fileName, int width, int height) {
            Image original = loadImage(fileName);
            if (original != null) {
                return new ImageIcon(original.getScaledInstance(width, height, Image.SCALE_SMOOTH)).getImage();
            }
            return null;
        }

        // --- [화면 그리기 (Paint)] ---
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            // 1. 배경 그리기
            Image backgroundToDraw = null;

            if (currentState.equals("start")) backgroundToDraw = startImage;
            else if (currentState.equals("bread_basic")) backgroundToDraw = breadBasicImage;
            else if (currentState.equals("bread_choco")) backgroundToDraw = breadChocoImage;
            else if (currentState.equals("bread_strawberry")) backgroundToDraw = breadStrawberryImage;
            else if (currentState.equals("cream_basic")) backgroundToDraw = breadBasicCreamImage;
            else if (currentState.equals("cream_choco")) backgroundToDraw = breadChocoCreamImage;
            else if (currentState.equals("cream_strawberry")) backgroundToDraw = breadStrawberryCreamImage;
            else if (currentState.equals("letter_selection")) backgroundToDraw = letterSelectionImage;
            else if (currentState.equals("letter_write")) backgroundToDraw = letterWriteImage;

            if (backgroundToDraw != null) {
                g.drawImage(backgroundToDraw, 0, 0, getWidth(), getHeight(), this);
            }

            // 2. 크림 장식 그리기 (케이크 꾸미기 단계일 때)
            if (currentState.startsWith("cream_")) {
                for (Placement p : decorations) {
                    g.drawImage(p.image, p.x, p.y, this);
                }
            }

            // 3. 편지지 및 텍스트창 위치 잡기 (편지 쓰기 단계일 때)
            if (currentState.equals("letter_write") && selectedLetterNumber != 0) {
                Image selectedLetterImage = letterImages[selectedLetterNumber - 1];

                if (selectedLetterImage != null) {
                    // (1) 편지지 크기 설정 (809:607 비율의 50% -> 405:304)
                    int targetWidth = 405;
                    int targetHeight = 304;

                    // (2) 화면 정중앙 좌표 계산
                    int letterX = (getWidth() - targetWidth) / 2;
                    int letterY = (getHeight() - targetHeight) / 2;

                    // (3) 편지지 이미지 그리기
                    g.drawImage(selectedLetterImage, letterX, letterY, targetWidth, targetHeight, this);

                    // (4) ⭐ 텍스트 입력창 위치를 편지지 위에 정확히 맞추기
                    int padding = 25; // 편지지 테두리 안쪽 여백

                    // 화면 크기가 바뀔 때를 대비해 여기서 setBounds를 계속 업데이트
                    textArea.setBounds(
                            letterX + padding,
                            letterY + padding,
                            targetWidth - (padding * 2),
                            targetHeight - (padding * 2)
                    );

                    // 혹시라도 숨겨져 있다면 보이게 설정
                    if (!textArea.isVisible()) textArea.setVisible(true);
                }
            } else {
                // 편지 쓰기 단계가 아니면 텍스트창 숨김
                if (textArea.isVisible()) textArea.setVisible(false);
            }
        }
    }

    // --- [메인 실행] ---
    public static void main(String[] args) {
        JFrame frame = new JFrame("나만의 케이크 만들기");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600); // 창 크기 설정

        ImagePanel panel = new ImagePanel(); // 패널 생성
        frame.add(panel);

        frame.setResizable(false); // 창 크기 고정 (좌표 밀림 방지)
        frame.setLocationRelativeTo(null); // 화면 중앙에서 시작
        frame.setVisible(true);
    }
}