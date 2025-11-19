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
        private static final int CREAM_WIDTH = 50;
        private static final int CREAM_HEIGHT = 50;
        private static final int FRUIT_WIDTH = 40;
        private static final int FRUIT_HEIGHT = 40;

        private Image startImage;
        private Image breadSelectionImage;
        private Image breadBasicImage, breadChocoImage, breadStrawberryImage;
        private Image breadBasicCreamImage, breadChocoCreamImage, breadStrawberryCreamImage;
        private Image letterSelectionImage;
        private Image letterWriteImage;
        private Image[] letterImages = new Image[9];
        private Image creamChocoImg, creamStrawImg, creamWhiteImg;

        private Image fruitSelectionImage;
        private Image fruitStrawberryImg, fruitCherryImg, fruitOrangeImg;

        private String selectedBreadType = "none";
        private String currentState;
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
            if (currentState.equals("start")) {
                currentState = "bread_selection";
                selectedBreadType = "none";
                repaint();

            } else if (currentState.equals("bread_selection")) {
                // 1. 초코빵 영역
                if (isClickInArea(x, y, 121, 271, 26, 126)) {
                    selectedBreadType = "choco";
                    repaint();
                }
                // 2. 딸기빵 영역
                else if (isClickInArea(x, y, 312, 462, 26, 126)) {
                    selectedBreadType = "strawberry";
                    repaint();
                }
                // 3. 기본빵 영역 -> ⭐ [수정됨] 여기를 눌러도 'choco'로 설정
                else if (isClickInArea(x, y, 489, 639, 18, 118)) {
                    selectedBreadType = "choco"; // basic 대신 choco 대입
                    repaint();
                }
                // 4. [다음] 버튼 클릭
                else if (isClickInArea(x, y, 601, 751, 441, 541)) {
                    if (selectedBreadType.equals("none")) {
                        JOptionPane.showMessageDialog(this, "빵을 먼저 선택해주세요!");
                        return;
                    }
                    // 선택된 빵에 따라 다음 단계 결정
                    if (selectedBreadType.equals("basic")) currentState = "cream_basic";
                    else if (selectedBreadType.equals("choco")) currentState = "cream_choco";
                    else if (selectedBreadType.equals("strawberry")) currentState = "cream_strawberry";

                    decorations.clear();
                    selectedTool = "none";
                    repaint();
                }

            } else if (currentState.startsWith("cream_")) {
                if (isClickInArea(x, y, 601, 751, 441, 541)) {
                    currentState = "fruit_selection";
                    selectedTool = "none";
                    repaint();
                }
                else if (isClickInArea(x, y, 119, 269, 39, 139)) selectedTool = "cream_choco";
                else if (isClickInArea(x, y, 314, 464, 42, 142)) selectedTool = "cream_straw";
                else if (isClickInArea(x, y, 496, 646, 38, 138)) selectedTool = "cream_white";
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

            } else if (currentState.equals("fruit_selection")) {
                if (isClickInArea(x, y, 601, 751, 441, 541)) {
                    currentState = "letter_selection";
                    selectedTool = "none";
                    repaint();
                }
                else if (isClickInArea(x, y, 119, 269, 39, 139)) selectedTool = "fruit_strawberry";
                else if (isClickInArea(x, y, 314, 464, 42, 142)) selectedTool = "fruit_cherry";
                else if (isClickInArea(x, y, 496, 646, 38, 138)) selectedTool = "fruit_orange";
                else {
                    Image img = null;
                    if (selectedTool.equals("fruit_strawberry")) img = fruitStrawberryImg;
                    else if (selectedTool.equals("fruit_cherry")) img = fruitCherryImg;
                    else if (selectedTool.equals("fruit_orange")) img = fruitOrangeImg;
                    if (img != null) {
                        decorations.add(new Placement(x - (img.getWidth(null)/2), y - (img.getHeight(null)/2), img, "fruit"));
                        repaint();
                    }
                }

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

            } else if (currentState.equals("letter_write")) {
                if (isClickInArea(x, y, 601, 751, 441, 541)) {
                    finalCakeAndLetterImage = createLetterOnlyImage();
                    saveImageToFile(finalCakeAndLetterImage);

                    currentState = "final_cake";
                    toggleInputFields(false);
                    repaint();
                }
            }
            else if (currentState.equals("final_cake")) {
                currentState = "start";
                finalCakeAndLetterImage = null;
                selectedBreadType = "none";
                repaint();
            }
        }

        // --- [4. 이미지 기능] ---
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
            } catch (Exception e) { return null; }
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
                breadBasicImage = loadImage("Bread_Basic.png");
                breadChocoImage = loadImage("Bread_Choco.png");
                breadStrawberryImage = loadImage("Bread_Strawberry.png");
                breadBasicCreamImage = loadImage("Bread_Basic_Cream.png");
                breadChocoCreamImage = loadImage("Bread_Choco_Cream.png");
                breadStrawberryCreamImage = loadImage("Bread_Strawberry_Cream.png");
                letterSelectionImage = loadImage("letter_selection.png");
                letterWriteImage = loadImage("letter_write.png");
                for (int i = 0; i < 9; i++) letterImages[i] = loadImage("letter" + (i + 1) + ".png");
                creamChocoImg = loadImage("Cream_Chocolate.png", CREAM_WIDTH, CREAM_HEIGHT);
                creamStrawImg = loadImage("Cream_Strawberry.png", CREAM_WIDTH, CREAM_HEIGHT);
                creamWhiteImg = loadImage("Cream_White.png", CREAM_WIDTH, CREAM_HEIGHT);
                fruitSelectionImage = loadImage("fruit_selection.png");
                fruitStrawberryImg = loadImage("Fruit_Strawberry.png", FRUIT_WIDTH, FRUIT_HEIGHT);
                fruitCherryImg = loadImage("Fruit_Cherry.png", FRUIT_WIDTH, FRUIT_HEIGHT);
                fruitOrangeImg = loadImage("Fruit_Orange.png", FRUIT_WIDTH, FRUIT_HEIGHT);
            } catch (Exception e) { e.printStackTrace(); }
        }

        // --- [5. 화면 그리기 (비율 유지 기능 적용됨)] ---
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            if (currentState.equals("final_cake")) {
                // 필요시 구현
            }

            // ⭐ 빵 선택 화면: 원본 비율 유지하여 중앙 정렬
            if (currentState.equals("bread_selection")) {
                if (breadSelectionImage != null) {
                    g.drawImage(breadSelectionImage, 0, 0, getWidth(), getHeight(), this);
                }

                Image overlayImg = null;
                if ("basic".equals(selectedBreadType)) overlayImg = breadBasicImage;
                else if ("choco".equals(selectedBreadType)) overlayImg = breadChocoImage;
                else if ("strawberry".equals(selectedBreadType)) overlayImg = breadStrawberryImage;

                if (overlayImg != null) {
                    int imgW = overlayImg.getWidth(this);
                    int imgH = overlayImg.getHeight(this);

                    if (imgW > 0 && imgH > 0) {
                        // 화면 비율에 맞춰 축소/확대 (비율 깨짐 없음)
                        int maxW = 520;
                        int maxH = 370;

                        double widthRatio = (double) maxW / imgW;
                        double heightRatio = (double) maxH / imgH;
                        double scale = Math.min(widthRatio, heightRatio);

                        int finalW = (int) (imgW * scale);
                        int finalH = (int) (imgH * scale);

                        int x = (getWidth() - finalW) / 2;
                        int y = (getHeight() - finalH) / 2 + 90;

                        g.drawImage(overlayImg, x, y, finalW, finalH, this);
                    }
                }
                return;
            }

            // --- 기존 로직 동일 ---
            if (currentState.startsWith("cream_") || currentState.equals("fruit_selection")) {
                Image cakeBaseBg = null;
                if (selectedBreadType.equals("basic")) cakeBaseBg = breadBasicCreamImage;
                else if (selectedBreadType.equals("choco")) cakeBaseBg = breadChocoCreamImage;
                else if (selectedBreadType.equals("strawberry")) cakeBaseBg = breadStrawberryCreamImage;

                if (cakeBaseBg != null) g.drawImage(cakeBaseBg, 0, 0, getWidth(), getHeight(), this);

                for (Placement p : decorations) g.drawImage(p.image, p.x, p.y, this);

                if (currentState.equals("fruit_selection")) {
                    if (fruitSelectionImage != null) g.drawImage(fruitSelectionImage, 0, 0, getWidth(), getHeight(), this);
                }
                return;
            }

            Image bg = null;
            if (currentState.equals("start")) bg = startImage;
            else if (currentState.equals("letter_selection")) bg = letterSelectionImage;
            else if (currentState.equals("letter_write")) bg = letterWriteImage;

            if (bg != null) g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);

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