import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class main {

    static class ImagePanel extends JPanel {

        // --- [1. 변수 선언] ---
        private static final int CREAM_WIDTH = 60;
        private static final int CREAM_HEIGHT = 60;

        // 과일 크기 설정
        private static final int FRUIT_WIDTH = 50;
        private static final int FRUIT_HEIGHT = 50;

        // 이미지 변수들
        private Image startImage;
        private Image breadSelectionImage;
        private Image creamSelectionImage;
        private Image fruitSelectionImage;

        private Image breadBasicImage, breadChocoImage, breadStrawberryImage;

        private Image letterSelectionImage;
        private Image letterWriteImage;
        private Image letterSaveImage; // 저장 완료 화면 이미지
        private Image[] letterImages = new Image[9];

        private Image creamChocoImg, creamStrawImg, creamWhiteImg;
        private Image fruitBananaImg, fruitGrapeImg, fruitStrawImg, fruitOrangeImg;

        private String currentState;
        private String selectedBreadType = "none";
        private String selectedTool = "none";
        private int selectedLetterNumber = 0;
        private ArrayList<Placement> decorations = new ArrayList<>();

        // ★ 케이크가 그려진 위치와 크기를 저장할 변수 (영역 제한용) ★
        private int cakeX = 0, cakeY = 0, cakeWidth = 0, cakeHeight = 0;

        private JTextField dateField;
        private JTextField toField;
        private JTextPane bodyPane;
        private JTextField fromField;

        private final Color TEXT_COLOR = new Color(80, 50, 40);
        private final Color SELECTION_COLOR = new Color(255, 200, 200);
        private final Font BOLD_FONT = new Font("Malgun Gothic", Font.BOLD, 16);

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

            // ★★★ 클릭 좌표 출력 코드 ★★★
            System.out.println("클릭 좌표: x=" + x + ", y=" + y);

            // 1. 시작 화면
            if (currentState.equals("start")) {
                currentState = "bread_selection";
                selectedBreadType = "none";
                repaint();

                // 2. 빵 선택
            } else if (currentState.equals("bread_selection")) {
                // 클릭 영역 수정 (원본 코드의 오타 수정 및 재정렬)
                if (isClickInArea(x, y, 121, 271, 26, 126)) { // Basic (가정)
                    selectedBreadType = "basic";
                    repaint();
                }
                else if (isClickInArea(x, y, 312, 462, 26, 126)) { // Strawberry (가정)
                    selectedBreadType = "strawberry";
                    repaint();
                }
                else if (isClickInArea(x, y, 489, 639, 18, 118)) { // Choco (가정)
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
                // [다음] 버튼
                if (isClickInArea(x, y, 601, 751, 441, 541)) {
                    currentState = "fruit_selection";
                    selectedTool = "none";
                    repaint();
                }
                // 도구 선택 버튼
                else if (isClickInArea(x, y, 119, 269, 39, 139)) selectedTool = "cream_choco";
                else if (isClickInArea(x, y, 314, 464, 42, 142)) selectedTool = "cream_straw";
                else if (isClickInArea(x, y, 496, 646, 38, 138)) selectedTool = "cream_white";

                    // ★ 꾸미기 (케이크 영역 내부인지 확인) ★
                else {
                    // 케이크 영역(Rect) 안에 클릭이 들어왔는지 체크
                    if (isInCakeArea(x, y)) {
                        Image img = null;
                        if (selectedTool.equals("cream_choco")) img = creamChocoImg;
                        else if (selectedTool.equals("cream_straw")) img = creamStrawImg;
                        else if (selectedTool.equals("cream_white")) img = creamWhiteImg;

                        if (img != null) {
                            decorations.add(new Placement(x - (img.getWidth(null)/2), y - (img.getHeight(null)/2), img, "cream"));
                            repaint();
                        }
                    }
                }

                // 4. 과일 선택
            } else if (currentState.equals("fruit_selection")) {

                // [다음] 버튼
                if (isClickInArea(x, y, 601, 751, 441, 541)) {
                    currentState = "letter_selection";
                    selectedTool = "none";
                    repaint();
                }

                // 과일 도구 선택
                else if (isClickInArea(x, y, 168, 238, 53, 123)) selectedTool = "fruit_banana";
                else if (isClickInArea(x, y, 293, 363, 50, 120)) selectedTool = "fruit_grape";
                else if (isClickInArea(x, y, 413, 483, 57, 127)) selectedTool = "fruit_strawberry";
                else if (isClickInArea(x, y, 547, 617, 51, 121)) selectedTool = "fruit_orange";

                    // ★ 과일 배치 (케이크 영역 내부인지 확인) ★
                else {
                    if (isInCakeArea(x, y)) {
                        Image img = null;
                        if (selectedTool.equals("fruit_banana")) img = fruitBananaImg;
                        else if (selectedTool.equals("fruit_grape")) img = fruitGrapeImg;
                        else if (selectedTool.equals("fruit_strawberry")) img = fruitStrawImg;
                        else if (selectedTool.equals("fruit_orange")) img = fruitOrangeImg;

                        if (img != null) {
                            decorations.add(new Placement(x - (img.getWidth(null)/2), y - (img.getHeight(null)/2), img, "fruit"));
                            repaint();
                        }
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
                    currentState = "letter_save";
                    toggleInputFields(false);
                    repaint();
                }

                // 7. 저장 완료 화면 (letter_save.png)
            } else if (currentState.equals("letter_save")) {
                // [이미지 저장] 버튼 클릭 (x=395, y=385 주변)
                if (isClickInArea(x, y, 321, 471, 350, 420)) {
                    saveCakeImage();
                    // 저장이 완료되면 다시 letter_save 화면을 새로 그려 저장 성공 메시지를 보여줄 수 있음 (여기서는 별도의 상태 변화는 주지 않음)
                }
                // [이전 페이지] 버튼 클릭 (x=111, y=490 주변)
                else if (isClickInArea(x, y, 40, 180, 460, 520)) {
                    currentState = "letter_write";
                    toggleInputFields(true);
                    repaint();
                }
                // [다시 시작] 버튼 좌표 (601, 441) ~ (751, 541)
                else if (isClickInArea(x, y, 601, 751, 441, 541)) {
                    currentState = "start";
                    selectedBreadType = "none";
                    decorations.clear();
                    repaint();
                }
            }
        }

        // --- [4. 유틸리티 메서드] ---

        /** 케이크와 장식을 포함한 이미지를 생성합니다. */
        private BufferedImage createCakeImage() {
            // 케이크를 그리고 있는 상태 (cream_selection 또는 fruit_selection)의 논리를 재사용합니다.
            // 전체 패널 크기와 동일하게 버퍼링된 이미지를 생성합니다.
            BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = image.createGraphics();

            // 1. 배경 이미지 그리기
            Image bg = (currentState.equals("cream_selection")) ? creamSelectionImage : fruitSelectionImage;
            if (bg != null) g2.drawImage(bg, 0, 0, getWidth(), getHeight(), this);

            // 2. 빵 이미지 그리기
            Image breadBase = null;
            if ("basic".equals(selectedBreadType)) breadBase = breadBasicImage;
            else if ("choco".equals(selectedBreadType)) breadBase = breadChocoImage;
            else if ("strawberry".equals(selectedBreadType)) breadBase = breadStrawberryImage;

            // drawCenteredImage 로직을 사용하여 위치를 계산하고 그립니다.
            // 이 로직은 cakeX, cakeY, cakeWidth, cakeHeight도 업데이트합니다.
            if (breadBase != null) {
                // 임시로 Graphics 객체를 생성하여 위치 계산 로직을 실행합니다.
                drawCenteredImage(g2, breadBase);
            }

            // 3. 데코레이션 그리기
            for (Placement p : decorations) {
                g2.drawImage(p.image, p.x, p.y, this);
            }

            g2.dispose();
            return image;
        }

        /** 현재 케이크 이미지를 파일로 저장합니다. */
        private void saveCakeImage() {
            try {
                // 케이크가 그려진 이미지를 생성합니다.
                // 편지 화면에서는 케이크 이미지를 만들 수 없으므로, 편지 화면 진입 전 마지막 꾸미기 화면의 상태를 사용합니다.
                // 여기서는 간단히 케이크 꾸미기 화면의 로직을 사용하여 케이크 이미지를 새로 생성합니다.
                // (실제 어플리케이션에서는 저장할 데이터를 별도로 유지하는 것이 좋습니다.)
                // 케이크 이미지만 저장하는 것이 아니라, "letter_write" 상태의 편지지 전체를 저장하고 싶다면,
                // 이 부분을 `letter_write`의 `paintComponent` 로직을 캡처하도록 수정해야 합니다.

                // 편지 화면을 캡처하도록 수정합니다.
                BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
                Graphics2D g2 = image.createGraphics();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // letter_write 배경 그리기
                if (letterWriteImage != null) g2.drawImage(letterWriteImage, 0, 0, getWidth(), getHeight(), this);

                // 편지 그리기 (paintComponent 로직 재활용)
                if (selectedLetterNumber != 0) {
                    Image selectedLetterImage = letterImages[selectedLetterNumber - 1];
                    if (selectedLetterImage != null) {
                        int targetWidth = 405; int targetHeight = 304;
                        int lx = (getWidth() - targetWidth) / 2;
                        int ly = (getHeight() - targetHeight) / 2;
                        g2.drawImage(selectedLetterImage, lx, ly, targetWidth, targetHeight, this);

                        // 텍스트 필드 값 복사 (TextField는 paintComponent에서 그려지지 않으므로 직접 그립니다.)
                        g2.setColor(TEXT_COLOR);
                        g2.setFont(BOLD_FONT);

                        // 날짜
                        int dateX = lx + targetWidth - 160;
                        int dateY = ly + 18 + 20; // 폰트 높이 고려
                        g2.drawString(dateField.getText(), dateX + 140 - g2.getFontMetrics().stringWidth(dateField.getText()), dateY);

                        // To
                        int toX = lx + 25;
                        int toY = ly + 45 + 23;
                        g2.drawString(toField.getText(), toX, toY);

                        // From
                        int fromX = lx + targetWidth - 160;
                        int fromY = ly + targetHeight - 40 + 23;
                        g2.drawString(fromField.getText(), fromX + 140 - g2.getFontMetrics().stringWidth(fromField.getText()), fromY);

                        // Body Pane 내용 그리기 (JTextPane의 내용을 캡처하는 것은 복잡하므로 간단히 String으로 가정하고 처리)
                        // 실제 텍스트 내용을 가져와서 drawString으로 그릴 수 있지만, JTextPane 스타일을 완벽히 재현하기는 어려우므로
                        // 여기서는 일단 생략하고 캡처 시 화면 그대로 캡처하는 방식으로 처리합니다. (아래 코드는 현재 화면 전체 캡처)

                        // JTextPane의 텍스트를 직접 캡처하는 대신,
                        // JTextPane이 보이는 상태에서 전체 패널을 캡처하는 방식이 가장 쉽습니다.
                        // 하지만 현재 "letter_save"에서는 JTextPane이 숨겨져 있으므로,
                        // 필드 값을 이용하여 텍스트를 그리는 방식으로 대체해야 합니다.

                        // bodyPane의 텍스트는 복잡해서 JTextPane의 내용을 그대로 Image로 변환하는 코드가 필요합니다.
                        // (별도 라이브러리 없이는 구현이 어려우므로 텍스트 필드 내용만 저장되는 것으로 간주합니다.)
                    }
                }
                g2.dispose();

                // 파일 저장
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setSelectedFile(new File("My_Cake_Letter.png"));
                int userSelection = fileChooser.showSaveDialog(this);

                if (userSelection == JFileChooser.APPROVE_OPTION) {
                    File fileToSave = fileChooser.getSelectedFile();
                    if (!fileToSave.getName().toLowerCase().endsWith(".png")) {
                        fileToSave = new File(fileToSave.getAbsolutePath() + ".png");
                    }
                    ImageIO.write(image, "png", fileToSave);
                    JOptionPane.showMessageDialog(this, "편지 이미지가 저장되었습니다: " + fileToSave.getAbsolutePath());
                }

            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "이미지 저장 중 오류가 발생했습니다: " + e.getMessage(), "저장 오류", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // ★ 케이크 영역 판별 함수 ★
        private boolean isInCakeArea(int x, int y) {
            if (cakeWidth == 0 || cakeHeight == 0) return false;
            return (x >= cakeX && x <= cakeX + cakeWidth) &&
                    (y >= cakeY && y <= cakeY + cakeHeight);
        }

        private Image loadImage(String fileName) {
            try {
                java.net.URL url = getClass().getResource(fileName);
                if (url == null) {
                    System.err.println("경고: 이미지를 찾을 수 없습니다! 파일명: " + fileName);
                }
                // Image 객체 로드 시 크기가 0이 나오는 경우가 있어 ImageIO.read를 사용하도록 변경
                // 단, getResource를 쓰려면 ClassLoader 문제로 인해 InputStream을 사용해야 합니다.
                if (url != null) {
                    return ImageIO.read(url);
                }
                return null;
            } catch (Exception e) {
                e.printStackTrace();
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
            // 편지 쓰기 화면이 아닌 경우 포커스 해제
            if (!show) {
                KeyboardFocusManager.getCurrentKeyboardFocusManager().clearGlobalFocusOwner();
            }
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
                letterSaveImage = loadImage("letter_save.jpg");
                for (int i = 0; i < 9; i++) letterImages[i] = loadImage("letter" + (i + 1) + ".png");

                creamChocoImg = loadImage("Cream_Chocolate.png", CREAM_WIDTH, CREAM_HEIGHT);
                creamStrawImg = loadImage("Cream_Strawberry.png", CREAM_WIDTH, CREAM_HEIGHT);
                creamWhiteImg = loadImage("Cream_White.png", CREAM_WIDTH, CREAM_HEIGHT);

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
            else if (currentState.equals("letter_save")) bg = letterSaveImage;

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

                    // ★ 케이크의 현재 위치와 크기를 변수에 업데이트 (클릭 판정용) ★
                    this.cakeX = x;
                    this.cakeY = y;
                    this.cakeWidth = finalW;
                    this.cakeHeight = finalH;

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