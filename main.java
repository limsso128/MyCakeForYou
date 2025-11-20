import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

// JDBC 관련 import
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

// --- [ DatabaseUtil 클래스 (SQLite용) ] ---
class DatabaseUtil {
    // ⚠️ SQLite는 파일 경로를 사용합니다. (프로젝트 폴더에 'MyCakeForYou.db' 파일 생성됨)
    private static final String URL = "jdbc:sqlite:MyCakeForYou.db";
    // SQLite는 사용자 이름/비밀번호가 필요 없습니다.
    private static final String USER = null;
    private static final String PASSWORD = null;

    public static Connection getConnection() throws SQLException {
        // 드라이버 로드 대신 URL만 사용하여 연결 시도
        return DriverManager.getConnection(URL);
    }
}
// --- [ DatabaseUtil 끝 ] ---


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
        private Image loginBackgroundImage;
        private Image signupBackgroundImage;
        private Image breadSelectionImage;
        private Image creamSelectionImage;
        private Image fruitSelectionImage;

        private Image breadBasicImage, breadChocoImage, breadStrawberryImage;

        private Image letterSelectionImage;
        private Image letterWriteImage;
        private Image letterSaveImage;
        private Image[] letterImages = new Image[9];

        private Image creamChocoImg, creamStrawImg, creamWhiteImg;
        private Image fruitBananaImg, fruitGrapeImg, fruitStrawImg, fruitOrangeImg;

        private String currentState;
        private String selectedBreadType = "none";
        private String selectedTool = "none";
        private int selectedLetterNumber = 0;
        private ArrayList<Placement> decorations = new ArrayList<>();

        private int cakeX = 0, cakeY = 0, cakeWidth = 0, cakeHeight = 0;

        // 로그인/회원가입 필드 추가
        private JTextField loginIdField;
        private JPasswordField loginPwField;
        private JTextField signupIdField;
        private JPasswordField signupPwField;

        private JTextField dateField;
        private JTextField toField;
        private JTextPane bodyPane;
        private JTextField fromField;

        private final Color TEXT_COLOR = new Color(80, 50, 40);
        private final Color SELECTION_COLOR = new Color(255, 200, 200);
        private final Font BOLD_FONT = new Font("Malgun Gothic", Font.BOLD, 16);
        private final Font FIELD_FONT = new Font("Malgun Gothic", Font.PLAIN, 18);

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

            // 로그인 필드 설정
            loginIdField = createStyledInputField("아이디");
            loginPwField = createStyledPasswordInput();

            // 회원가입 필드 설정
            signupIdField = createStyledInputField("새 아이디");
            signupPwField = createStyledPasswordInput();

            this.add(loginIdField);
            this.add(loginPwField);
            this.add(signupIdField);
            this.add(signupPwField);

            // 편지 필드 설정 (기존 코드 유지)
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
            System.out.println("클릭 좌표: x=" + x + ", y=" + y);

            if (currentState.equals("start")) {
                currentState = "login";
                toggleInputFields(false);
                toggleAuthFields(true, "login");
                repaint();
                return;
            }

            // 1-1. 로그인 화면 처리
            else if (currentState.equals("login")) {
                // [로그인 버튼]
                if (isClickInArea(x, y, 320, 470, 350, 400)) {
                    performLogin(loginIdField.getText(), new String(loginPwField.getPassword()));
                    return;
                }
                // [회원가입 버튼]
                else if (isClickInArea(x, y, 320, 470, 410, 460)) {
                    currentState = "signup";
                    toggleAuthFields(false, "login");
                    toggleAuthFields(true, "signup");
                    repaint();
                    return;
                }
                // ... (필드 포커스 로직 유지)
                else if (isClickInArea(x, y, 150, 400, 240, 290)) {
                    loginIdField.requestFocus();
                }
                else if (isClickInArea(x, y, 150, 400, 300, 350)) {
                    loginPwField.requestFocus();
                }
            }

            // 1-2. 회원가입 화면 처리
            else if (currentState.equals("signup")) {
                // [회원가입 완료 버튼]
                if (isClickInArea(x, y, 320, 470, 350, 400)) {
                    performSignup(signupIdField.getText(), new String(signupPwField.getPassword()));
                    return;
                }
                // [로그인으로 돌아가기 버튼]
                else if (isClickInArea(x, y, 320, 470, 410, 460)) {
                    currentState = "login";
                    toggleAuthFields(false, "signup");
                    toggleAuthFields(true, "login");
                    repaint();
                    return;
                }
                // ... (필드 포커스 로직 유지)
                else if (isClickInArea(x, y, 150, 400, 240, 290)) {
                    signupIdField.requestFocus();
                }
                else if (isClickInArea(x, y, 150, 400, 300, 350)) {
                    signupPwField.requestFocus();
                }
            }

            // 2. 빵 선택 (이하 기존 로직 유지)
            else if (currentState.equals("bread_selection")) {
                if (isClickInArea(x, y, 121, 271, 26, 126)) selectedBreadType = "basic";
                else if (isClickInArea(x, y, 312, 462, 26, 126)) selectedBreadType = "strawberry";
                else if (isClickInArea(x, y, 489, 639, 18, 118)) selectedBreadType = "choco";
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
            } else if (currentState.equals("cream_selection")) {
                if (isClickInArea(x, y, 601, 751, 441, 541)) {
                    currentState = "fruit_selection";
                    selectedTool = "none";
                    repaint();
                }
                else if (isClickInArea(x, y, 119, 269, 39, 139)) selectedTool = "cream_choco";
                else if (isClickInArea(x, y, 314, 464, 42, 142)) selectedTool = "cream_straw";
                else if (isClickInArea(x, y, 496, 646, 38, 138)) selectedTool = "cream_white";

                else {
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
            } else if (currentState.equals("fruit_selection")) {
                if (isClickInArea(x, y, 601, 751, 441, 541)) {
                    currentState = "letter_selection";
                    selectedTool = "none";
                    repaint();
                }
                else if (isClickInArea(x, y, 168, 238, 53, 123)) selectedTool = "fruit_banana";
                else if (isClickInArea(x, y, 293, 363, 50, 120)) selectedTool = "fruit_grape";
                else if (isClickInArea(x, y, 413, 483, 57, 127)) selectedTool = "fruit_strawberry";
                else if (isClickInArea(x, y, 547, 617, 51, 121)) selectedTool = "fruit_orange";

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
                    currentState = "letter_save";
                    toggleInputFields(false);
                    repaint();
                }
            } else if (currentState.equals("letter_save")) {
                if (isClickInArea(x, y, 321, 471, 350, 420)) {
                    // saveCakeImage();
                }
                else if (isClickInArea(x, y, 40, 180, 460, 520)) {
                    currentState = "letter_write";
                    toggleInputFields(true);
                    repaint();
                }
                else if (isClickInArea(x, y, 601, 751, 441, 541)) {
                    currentState = "start";
                    selectedBreadType = "none";
                    decorations.clear();
                    repaint();
                }
            }
        }

        // --- [4. 인증 로직] ---

        /** 회원가입 로직 */
        private void performSignup(String username, String password) {
            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "아이디와 비밀번호를 모두 입력해주세요.", "경고", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // SQLite 구문 사용
            String sql = "INSERT INTO users (username, password) VALUES (?, ?)";

            try (Connection conn = DatabaseUtil.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                String hashedPassword = password;

                pstmt.setString(1, username);
                pstmt.setString(2, hashedPassword);

                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "회원가입 성공! 이제 로그인해주세요.", "알림", JOptionPane.INFORMATION_MESSAGE);

                // 성공 후 로그인 화면으로 전환
                currentState = "login";
                toggleAuthFields(false, "signup");
                toggleAuthFields(true, "login");
                repaint();

            } catch (SQLException e) {
                // SQLite에서 UNIQUE 제약 조건 위반(아이디 중복) 처리
                if (e.getMessage().contains("UNIQUE constraint failed")) {
                    JOptionPane.showMessageDialog(this, "이미 존재하는 아이디입니다.", "오류", JOptionPane.ERROR_MESSAGE);
                } else {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this, "DB 오류: " + e.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
                }
            }
        }

        /** 로그인 로직 */
        private void performLogin(String username, String password) {
            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "아이디와 비밀번호를 모두 입력해주세요.", "경고", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String sql = "SELECT password FROM users WHERE username = ?";

            try (Connection conn = DatabaseUtil.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, username);
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    String storedPassword = rs.getString("password");

                    if (storedPassword.equals(password)) {
                        JOptionPane.showMessageDialog(this, username + "님, 로그인 성공!", "환영", JOptionPane.INFORMATION_MESSAGE);

                        // 성공 후 다음 화면으로 전환
                        currentState = "bread_selection";
                        toggleAuthFields(false, "login");
                        repaint();
                    } else {
                        JOptionPane.showMessageDialog(this, "비밀번호가 일치하지 않습니다.", "오류", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "존재하지 않는 아이디입니다.", "오류", JOptionPane.ERROR_MESSAGE);
                }

            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "DB 오류: " + e.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
            }
        }


        // --- [5. 유틸리티 메서드] ---

        private JTextField createStyledInputField(String placeholder) {
            JTextField field = new JTextField(placeholder);
            field.setFont(FIELD_FONT);
            field.setHorizontalAlignment(JTextField.LEFT);
            field.setVisible(false);
            return field;
        }

        private JPasswordField createStyledPasswordInput() {
            JPasswordField field = new JPasswordField();
            field.setFont(FIELD_FONT);
            field.setHorizontalAlignment(JPasswordField.LEFT);
            field.setVisible(false);
            return field;
        }

        private void toggleAuthFields(boolean show, String type) {
            if (type.equals("login")) {
                loginIdField.setVisible(show);
                loginPwField.setVisible(show);
                loginIdField.setBounds(250, 250, 300, 30);
                loginPwField.setBounds(250, 310, 300, 30);
                if (show) loginIdField.requestFocus();
            } else if (type.equals("signup")) {
                signupIdField.setVisible(show);
                signupPwField.setVisible(show);
                signupIdField.setBounds(250, 250, 300, 30);
                signupPwField.setBounds(250, 310, 300, 30);
                if (show) signupIdField.requestFocus();
            }
            if (!show) {
                loginIdField.setText("");
                loginPwField.setText("");
                signupIdField.setText("");
                signupPwField.setText("");
            }
        }

        private void saveCakeImage() {
            // (기존 saveCakeImage 로직)
        }

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
                loginBackgroundImage = loadImage("login_background.png");
                signupBackgroundImage = loadImage("signup_background.png");
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

        // --- [6. 화면 그리기] ---
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            toggleInputFields(false); // 편지 필드 숨김

            // 1. 로그인/회원가입 화면
            if (currentState.equals("login")) {
                if (loginBackgroundImage != null) g.drawImage(loginBackgroundImage, 0, 0, getWidth(), getHeight(), this);
                else { g.setColor(Color.LIGHT_GRAY); g.fillRect(0, 0, getWidth(), getHeight()); }
                toggleAuthFields(true, "login");
                return;
            }
            // ★ 회원가입 화면 처리 ★
            else if (currentState.equals("signup")) {
                if (signupBackgroundImage != null) g.drawImage(signupBackgroundImage, 0, 0, getWidth(), getHeight(), this);
                else { g.setColor(Color.PINK); g.fillRect(0, 0, getWidth(), getHeight()); }
                toggleAuthFields(true, "signup");
                return;
            }

            // 로그인/회원가입 상태가 아니면 인증 필드 모두 숨김
            toggleAuthFields(false, "login");
            toggleAuthFields(false, "signup");

            // 2. 빵 선택
            if (currentState.equals("bread_selection")) {
                if (breadSelectionImage != null) g.drawImage(breadSelectionImage, 0, 0, getWidth(), getHeight(), this);
                Image overlayImg = null;
                if ("basic".equals(selectedBreadType)) overlayImg = breadBasicImage;
                else if ("choco".equals(selectedBreadType)) overlayImg = breadChocoImage;
                else if ("strawberry".equals(selectedBreadType)) overlayImg = breadStrawberryImage;
                drawCenteredImage(g, overlayImg);
                return;
            }
            // ... (이하 paintComponent의 나머지 상태 처리 로직은 기존 코드와 동일)

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
                return;
            }

            Image bg = null;
            if (currentState.equals("start")) bg = startImage;
            else if (currentState.equals("letter_selection")) bg = letterSelectionImage;
            else if (currentState.equals("letter_write")) bg = letterWriteImage;
            else if (currentState.equals("letter_save")) bg = letterSaveImage;

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
                    toggleInputFields(true);
                }
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

                    this.cakeX = x; this.cakeY = y;
                    this.cakeWidth = finalW; this.cakeHeight = finalH;

                    g.drawImage(img, x, y, finalW, finalH, this);
                }
            }
        }
    }

    public static void main(String[] args) {

        // ⚠️ 1. SQLite DB 파일 생성 및 users 테이블 초기화 ⚠️
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement()) {

            // SQLite 테이블 생성 코드: 'AUTOINCREMENT' 문법 오류 수정 완료
            String sql = "CREATE TABLE IF NOT EXISTS users ("
                    + "id INTEGER PRIMARY KEY,"
                    + "username TEXT NOT NULL UNIQUE,"
                    + "password TEXT NOT NULL"
                    + ");";
            stmt.execute(sql);
            System.out.println("SQLite DB 및 users 테이블 준비 완료.");

        } catch (SQLException e) {
            // 여기서 오류가 난다면 sqlite-jdbc 드라이버가 필요하다는 뜻입니다.
            System.err.println("DB 초기화 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
        // ----------------------------------------------------

        JFrame frame = new JFrame("나만의 케이크 만들기");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.add(new ImagePanel());
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}