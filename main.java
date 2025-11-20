import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.util.ArrayList;

public class main {

    static class ImagePanel extends JPanel {

        // --- [1. 변수 선언] ---
        private static final int CREAM_WIDTH = 55;
        private static final int CREAM_HEIGHT = 55;
        // 과일 크기 (필요시 조절하세요)
        private static final int FRUIT_WIDTH = 65;
        private static final int FRUIT_HEIGHT = 65;

        // 이미지 변수들
        private Image startImage;
        private Image breadSelectionImage;
        private Image creamSelectionImage;
        private Image fruitSelectionImage;

        private Image breadBasicImage, breadChocoImage, breadStrawberryImage;

        private Image letterSelectionImage;
        private Image letterWriteImage;
        private Image[] letterImages = new Image[9];

        // 꾸미기 재료: 크림
        private Image creamChocoImg, creamStrawImg, creamWhiteImg;
        // 꾸미기 재료: 과일 (요청하신 4종류)
        private Image fruitBananaImg, fruitGrapeImg, fruitStrawImg, fruitOrangeImg;

        private String currentState;
        private String selectedBreadType = "none";
        private String selectedTool = "none";
        private int selectedLetterNumber = 0;
        private ArrayList<Placement> decorations = new ArrayList<>();

        private JTextField dateField;
        private JTextField toField;
        private JTextPane bodyPane;
        private JTextField fromField;

        private final Color TEXT_COLOR = new Color(80, 50, 40);
        private final Color SELECTION_COLOR = new Color(255, 200, 200);
        private final Font BOLD_FONT = new Font("Malgun Gothic", Font.BOLD, 16);

        private BufferedImage finalCakeAndLetterImage;

        static class Placement {
            int x, y;
            Image image;
            String type;
            public Placement(int x, int y, Image image, String type) {
                this.x = x; this.y = y; this.image = image; this.type = type;
            }
        }

        // --- [2. 생성자] ---
        public ImagePanel() {
            this.setLayout(null);
            loadImages();
            currentState = "start";

            dateField = createStyledTextField(JTextField.RIGHT, "2024. 12. 25");
            toField = createStyledTextField(JTextField.LEFT, "To. ");
            toField.addActionListener(e -> bodyPane.requestFocus());
            fromField = createStyledTextField(JTextField.RIGHT, "From. ");

            bodyPane = new JTextPane();
            bodyPane.setOpaque(false);
            bodyPane.setVisible(false);
            bodyPane.setSelectionColor(SELECTION_COLOR);

            StyledDocument doc = bodyPane.getStyledDocument();
            SimpleAttributeSet style = new SimpleAttributeSet();
            StyleConstants.setFontFamily(style, "Malgun Gothic");
            StyleConstants.setFontSize(style, 17);
            StyleConstants.setForeground(style, TEXT_COLOR);
            StyleConstants.setLineSpacing(style, 0.5f);
            bodyPane.setParagraphAttributes(style, true);

            this.add(dateField);
            this.add(toField);
            this.add(bodyPane);
            this.add(fromField);

            this.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    handleMouseClick(e.getX(), e.getY());
                }
            });
        }

        // --- [3. 마우스 클릭 로직] ---
        private void handleMouseClick(int x, int y) {

            // 좌표 확인용 로그
            System.out.println("클릭 좌표 -> x: " + x + ", y: " + y);

            // 1. 시작 화면
            if (currentState.equals("start")) {
                currentState = "bread_selection";
                selectedBreadType = "none";
                repaint();

                // 2. 빵 선택
            } else if (currentState.equals("bread_selection")) {
                if (isClickInArea(x, y, 121, 271, 26, 126)) {
                    selectedBreadType = "choco";
                    repaint();
                }
                else if (isClickInArea(x, y, 312, 462, 26, 126)) {
                    selectedBreadType = "strawberry";
                    repaint();
                }
                else if (isClickInArea(x, y, 489, 639, 18, 118)) {
                    selectedBreadType = "choco";
                    repaint();
                }
                else if (isClickInArea(x, y, 601, 751, 441, 541)) {
                    if (selectedBreadType.equals("none")) {
                        JOptionPane.showMessageDialog(this, "빵을 먼저 선택해주세요!");
                        return;
                    }
                    currentState = "cream_selection";
                    decorations.clear();
                    selectedTool = "none";
                    repaint();
                }

                // 3. 크림 선택
            } else if (currentState.equals("cream_selection")) {
                if (isClickInArea(x, y, 601, 751, 441, 541)) { // 다음 버튼
                    currentState = "fruit_selection";
                    selectedTool = "none";
                    repaint();
                }
                // 크림 도구 선택
                else if (isClickInArea(x, y, 119, 269, 39, 139)) selectedTool = "cream_choco";
                else if (isClickInArea(x, y, 314, 464, 42, 142)) selectedTool = "cream_straw";
                else if (isClickInArea(x, y, 496, 646, 38, 138)) selectedTool = "cream_white";

                    // 꾸미기
                else {
                    Image img = null;
                    if (selectedTool.equals("cream_choco")) img = creamChocoImg;
                    else if (selectedTool.equals("cream_straw")) img = creamStrawImg;
                    else if (selectedTool.equals("cream_white")) img = creamWhiteImg;

                    if (img != null) {
                        decorations.add(new Placement(x - (img.getWidth(null)/2), y - (img.getHeight(null)/2), img, "cream"));
                        repaint();
                    }
                }

                // 4. 과일 선택 (★수정된 부분★)
            } else if (currentState.equals("fruit_selection")) {

                // [다음] 버튼 (좌표는 기존 유지)
                if (isClickInArea(x, y, 601, 751, 441, 541)) {
                    currentState = "letter_selection";
                    selectedTool = "none";
                    repaint();
                }

                // ★ 과일 도구 선택 (알려주신 좌표 중심으로 ±35px 정도 범위를 잡음)

                // 1. 바나나 (중심: 203, 88)
                else if (isClickInArea(x, y, 168, 238, 53, 123)) {
                    selectedTool = "fruit_banana";
                    System.out.println("도구 선택: 바나나");
                }
                // 2. 포도 (중심: 328, 85)
                else if (isClickInArea(x, y, 293, 363, 50, 120)) {
                    selectedTool = "fruit_grape";
                    System.out.println("도구 선택: 포도");
                }
                // 3. 딸기 (중심: 448, 92)
                else if (isClickInArea(x, y, 413, 483, 57, 127)) {
                    selectedTool = "fruit_strawberry";
                    System.out.println("도구 선택: 딸기");
                }
                // 4. 오렌지 (중심: 582, 86)
                else if (isClickInArea(x, y, 547, 617, 51, 121)) {
                    selectedTool = "fruit_orange";
                    System.out.println("도구 선택: 오렌지");
                }

                // ★ 케이크 위에 과일 올리기
                else {
                    Image img = null;
                    if (selectedTool.equals("fruit_banana")) img = fruitBananaImg;
                    else if (selectedTool.equals("fruit_grape")) img = fruitGrapeImg;
                    else if (selectedTool.equals("fruit_strawberry")) img = fruitStrawImg;
                    else if (selectedTool.equals("fruit_orange")) img = fruitOrangeImg;

                    if (img != null) {
                        // 클릭 위치에 이미지 추가
                        decorations.add(new Placement(
                                x - (img.getWidth(null)/2),
                                y - (img.getHeight(null)/2),
                                img,
                                "fruit"
                        ));
                        repaint();
                    }
                }

                // 5. 편지지 선택
            } else if (currentState.equals("letter_selection")) {
                int clickedLetter = 0;
                if (isClickInArea(x, y, 142, 242, 60, 160)) clickedLetter = 1;
                else if (isClickInArea(x, y, 336, 436, 60, 160)) clickedLetter = 2;
                else if (isClickInArea(x, y, 538, 638, 61, 161)) clickedLetter = 3;
                else if (isClickInArea(x, y, 141, 241, 203, 303)) clickedLetter = 4;
                else if (isClickInArea(x, y, 337, 437, 206, 306)) clickedLetter = 5;
                else if (isClickInArea(x, y, 536, 636, 212, 312)) clickedLetter = 6;
                else if (isClickInArea(x, y, 141, 241, 353, 453)) clickedLetter = 7;
                else if (isClickInArea(x, y, 340, 440, 353, 453)) clickedLetter = 8;
                else if (isClickInArea(x, y, 540, 640, 350, 450)) clickedLetter = 9;

                if (clickedLetter != 0) {
                    selectedLetterNumber = clickedLetter;
                    currentState = "letter_write";
                    toggleInputFields(true);
                    toField.requestFocus();
                    repaint();
                }

                // 6. 편지 쓰기
            } else if (currentState.equals("letter_write")) {
                if (isClickInArea(x, y, 601, 751, 441, 541)) {
                    finalCakeAndLetterImage = createLetterOnlyImage();
                    saveImageToFile(finalCakeAndLetterImage);
                    currentState = "final_cake";
                    toggleInputFields(false);
                    repaint();
                }

                // 7. 완료
            } else if (currentState.equals("final_cake")) {
                currentState = "start";
                finalCakeAndLetterImage = null;
                selectedBreadType = "none";
                decorations.clear();
                repaint();
            }
        }

        // --- [4. 유틸리티 메서드] ---
        private void saveImageToFile(BufferedImage image) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("편지 저장");
            fileChooser.setSelectedFile(new File("MyLetter.png"));
            int userSelection = fileChooser.showSaveDialog(this);
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                try {
                    File fileToSave = fileChooser.getSelectedFile();
                    if (!fileToSave.getAbsolutePath().endsWith(".png")) {
                        fileToSave = new File(fileToSave.getAbsolutePath() + ".png");
                    }
                    ImageIO.write(image, "png", fileToSave);
                    JOptionPane.showMessageDialog(this, "저장되었습니다!");
                } catch (Exception ex) { ex.printStackTrace(); }
            }
        }

        private BufferedImage createLetterOnlyImage() {
            BufferedImage fullScreen = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = fullScreen.createGraphics();
            this.printAll(g2);
            g2.dispose();
            try {
                int targetWidth = 405; int targetHeight = 304;
                int lx = (getWidth() - targetWidth) / 2;
                int ly = (getHeight() - targetHeight) / 2;
                return fullScreen.getSubimage(lx, ly, targetWidth, targetHeight);
            } catch(Exception e) { return fullScreen; }
        }

        private Image loadImage(String fileName) {
            try {
                java.net.URL url = getClass().getResource(fileName);
                return (url != null) ? new ImageIcon(url).getImage() : null;
            } catch (Exception e) {
                System.out.println("이미지 로드 실패: " + fileName);
                return null;
            }
        }

        private Image loadImage(String fileName, int w, int h) {
            Image img = loadImage(fileName);
            return (img != null) ? new ImageIcon(img.getScaledInstance(w, h, Image.SCALE_SMOOTH)).getImage() : null;
        }

        private JTextField createStyledTextField(int alignment, String defaultText) {
            JTextField field = new JTextField(defaultText);
            field.setOpaque(false); field.setBorder(null);
            field.setForeground(TEXT_COLOR); field.setFont(BOLD_FONT);
            field.setSelectionColor(SELECTION_COLOR); field.setHorizontalAlignment(alignment);
            field.setVisible(false); return field;
        }

        private void toggleInputFields(boolean show) {
            dateField.setVisible(show); toField.setVisible(show);
            bodyPane.setVisible(show); fromField.setVisible(show);
        }

        private boolean isClickInArea(int x, int y, int x1, int x2, int y1, int y2) {
            return (x >= x1 && x <= x2) && (y >= y1 && y <= y2);
        }

        private void loadImages() {
            try {
                startImage = loadImage("background_start.jpg");
                breadSelectionImage = loadImage("bread_selection.png");
                creamSelectionImage = loadImage("cream_selection.png");
                fruitSelectionImage = loadImage("fruit_selection.png");

                breadBasicImage = loadImage("Bread_Basic.png");
                breadChocoImage = loadImage("Bread_Choco.png");
                breadStrawberryImage = loadImage("Bread_Strawberry.png");

                letterSelectionImage = loadImage("letter_selection.png");
                letterWriteImage = loadImage("letter_write.png");
                for (int i = 0; i < 9; i++) letterImages[i] = loadImage("letter" + (i + 1) + ".png");

                creamChocoImg = loadImage("Cream_Chocolate.png", CREAM_WIDTH, CREAM_HEIGHT);
                creamStrawImg = loadImage("Cream_Strawberry.png", CREAM_WIDTH, CREAM_HEIGHT);
                creamWhiteImg = loadImage("Cream_White.png", CREAM_WIDTH, CREAM_HEIGHT);

                // ★ 과일 이미지 로드 (파일명 변경됨)
                fruitBananaImg = loadImage("fruit_banana.png", FRUIT_WIDTH, FRUIT_HEIGHT);
                fruitGrapeImg = loadImage("fruit_grapes.png", FRUIT_WIDTH, FRUIT_HEIGHT);
                fruitStrawImg = loadImage("fruit_strawberry.png", FRUIT_WIDTH, FRUIT_HEIGHT);
                fruitOrangeImg = loadImage("fruit_orange.png", FRUIT_WIDTH, FRUIT_HEIGHT);

            } catch (Exception e) { e.printStackTrace(); }
        }

        // --- [5. 화면 그리기] ---
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            // 1. 빵 선택
            if (currentState.equals("bread_selection")) {
                if (breadSelectionImage != null) g.drawImage(breadSelectionImage, 0, 0, getWidth(), getHeight(), this);
                Image overlayImg = null;
                if ("basic".equals(selectedBreadType)) overlayImg = breadBasicImage;
                else if ("choco".equals(selectedBreadType)) overlayImg = breadChocoImage;
                else if ("strawberry".equals(selectedBreadType)) overlayImg = breadStrawberryImage;
                drawCenteredImage(g, overlayImg);
                toggleInputFields(false);
                return;
            }

            // 2. 크림 & 과일 선택
            if (currentState.equals("cream_selection") || currentState.equals("fruit_selection")) {
                if (currentState.equals("cream_selection")) {
                    if (creamSelectionImage != null) g.drawImage(creamSelectionImage, 0, 0, getWidth(), getHeight(), this);
                } else {
                    if (fruitSelectionImage != null) g.drawImage(fruitSelectionImage, 0, 0, getWidth(), getHeight(), this);
                }

                Image breadBase = null;
                if ("basic".equals(selectedBreadType)) breadBase = breadBasicImage;
                else if ("choco".equals(selectedBreadType)) breadBase = breadChocoImage;
                else if ("strawberry".equals(selectedBreadType)) breadBase = breadStrawberryImage;
                drawCenteredImage(g, breadBase);

                for (Placement p : decorations) {
                    g.drawImage(p.image, p.x, p.y, this);
                }
                toggleInputFields(false);
                return;
            }

            // 3. 나머지
            Image bg = null;
            if (currentState.equals("start")) bg = startImage;
            else if (currentState.equals("letter_selection")) bg = letterSelectionImage;
            else if (currentState.equals("letter_write")) bg = letterWriteImage;

            if (bg != null) g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);

            // 4. 편지 UI
            if (currentState.equals("letter_write") && selectedLetterNumber != 0) {
                Image selectedLetterImage = letterImages[selectedLetterNumber - 1];
                if (selectedLetterImage != null) {
                    int targetWidth = 405; int targetHeight = 304;
                    int lx = (getWidth() - targetWidth) / 2;
                    int ly = (getHeight() - targetHeight) / 2;
                    g.drawImage(selectedLetterImage, lx, ly, targetWidth, targetHeight, this);

                    dateField.setBounds(lx + targetWidth - 160, ly + 18, 140, 25);
                    toField.setBounds(lx + 25, ly + 45, 200, 30);
                    bodyPane.setBounds(lx + 25, ly + 85, targetWidth - 50, targetHeight - 130);
                    fromField.setBounds(lx + targetWidth - 160, ly + targetHeight - 40, 140, 30);
                }
            } else {
                if (dateField.isVisible()) toggleInputFields(false);
            }
        }

        private void drawCenteredImage(Graphics g, Image img) {
            if (img != null) {
                int imgW = img.getWidth(this);
                int imgH = img.getHeight(this);
                if (imgW > 0 && imgH > 0) {
                    int maxW = 520; int maxH = 370;
                    double widthRatio = (double) maxW / imgW;
                    double heightRatio = (double) maxH / imgH;
                    double scale = Math.min(widthRatio, heightRatio);
                    int finalW = (int) (imgW * scale);
                    int finalH = (int) (imgH * scale);
                    int x = (getWidth() - finalW) / 2;
                    int y = (getHeight() - finalH) / 2 + 90;
                    g.drawImage(img, x, y, finalW, finalH, this);
                }
            }
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("나만의 케이크 만들기");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.add(new ImagePanel());
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}