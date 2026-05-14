package src;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Texas Hold'em Poker - Classic Casino Edition
 */
public class PokerGame extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;

    public PokerGame() {
        setTitle("Texas Hold'em Poker");
        setSize(1000, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(new MenuPanel(this), "Menu");
        mainPanel.add(new TutorialPanel(this), "Tutorial");
        mainPanel.add(new PlayerSelectPanel(this), "PlayerSelect");
        mainPanel.add(new SettingsPanel(this), "Settings");

        add(mainPanel);
        cardLayout.show(mainPanel, "Menu");

        // Start Background Music
        SoundManager.getInstance().playMusic("sounds/Nior.wav");
    }

    public void showScreen(String screenName) {
        cardLayout.show(mainPanel, screenName);
    }

    public void startGame(int numPlayers) {
        GamePanel gamePanel = new GamePanel(this, numPlayers);
        mainPanel.add(gamePanel, "Game");
        cardLayout.show(mainPanel, "Game");
        gamePanel.startNewHand();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new PokerGame().setVisible(true);
        });
    }
}

// --- UI UTILITIES ---
class UIUtils {
    public static void paintCasinoBackground(Graphics g, int w, int h) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 1. Base Rich Green Felt Gradient
        GradientPaint gp = new GradientPaint(0, 0, new Color(15, 65, 25), 0, h, new Color(5, 30, 10));
        g2.setPaint(gp);
        g2.fillRect(0, 0, w, h);

        // 2. Subtle Diamond Pattern (Casino Themed Style)
        g2.setColor(new Color(255, 255, 255, 10)); // Very faint white
        int size = 60;
        for (int x = 0; x < w + size; x += size) {
            for (int y = 0; y < h + size; y += size) {
                int[] px = {x, x + size / 2, x, x - size / 2};
                int[] py = {y - size / 2, y, y + size / 2, y};
                g2.drawPolygon(px, py, 4);
            }
        }

        // 3. Large Subtle Central Spade Logo
        g2.setColor(new Color(0, 0, 0, 30));
        int sw = Math.min(w, h) / 2;
        int sx = w / 2, sy = h / 2;
        // Simple Spade Shape using paths
        int[] sxp = {sx, sx + sw/2, sx, sx - sw/2};
        int[] syp = {sy - sw/2, sy + sw/4, sy + sw/4, sy + sw/4};
        // Top circle
        g2.fillOval(sx - sw/3, sy - sw/3, sw/2, sw/2);
        g2.fillOval(sx - sw/6, sy - sw/3, sw/2, sw/2);
        // Base
        int[] bx = {sx, sx - sw/6, sx + sw/6};
        int[] by = {sy + sw/4, sy + sw/2, sy + sw/2};
        g2.fillPolygon(bx, by, 3);

        // 4. Radial Vignette for Depth
        float[] fractions = {0.0f, 1.0f};
        Color[] colors = {new Color(0, 0, 0, 0), new Color(0, 0, 0, 150)};
        RadialGradientPaint rgp = new RadialGradientPaint(w / 2f, h / 2f, Math.max(w, h) / 1.1f, fractions, colors);
        g2.setPaint(rgp);
        g2.fillRect(0, 0, w, h);

        // 5. Subtle Noise/Texture
        g2.setColor(new Color(255, 255, 255, 5));
        java.util.Random rnd = new java.util.Random(42);
        for (int i = 0; i < 8000; i++) {
            int nx = rnd.nextInt(w);
            int ny = rnd.nextInt(h);
            g2.fillRect(nx, ny, 1, 1);
        }
    }

    public static JButton createButton(String text, Dimension size) {
        return new CasinoButton(text, size);
    }
}

class CasinoButton extends JButton {
    private final Color gold = new Color(218, 165, 32);
    private final Color goldBright = new Color(255, 215, 0);
    private boolean isHovered = false;

    public CasinoButton(String text, Dimension size) {
        super(text);
        setFont(new Font("Arial", Font.BOLD, 20));
        setForeground(Color.WHITE);
        setPreferredSize(size);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) { isHovered = true; repaint(); }
            public void mouseExited(java.awt.event.MouseEvent evt) { isHovered = false; repaint(); }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                if (isEnabled()) {
                    SoundManager.getInstance().playSFX("sounds/button1.wav");
                }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // Background Gradient
        Color c1 = isHovered ? new Color(60, 100, 60) : new Color(40, 40, 40);
        Color c2 = isHovered ? new Color(30, 60, 30) : new Color(20, 20, 20);
        GradientPaint gp = new GradientPaint(0, 0, c1, 0, h, c2);
        g2.setPaint(gp);
        g2.fillRoundRect(0, 0, w, h, 15, 15);

        // Gold Border
        g2.setColor(isHovered ? goldBright : gold);
        g2.setStroke(new BasicStroke(isHovered ? 3 : 2));
        g2.drawRoundRect(1, 1, w - 3, h - 3, 15, 15);

        if (isHovered) {
            g2.setColor(new Color(255, 215, 0, 40)); // Subtle glow
            g2.setStroke(new BasicStroke(5));
            g2.drawRoundRect(0, 0, w - 1, h - 1, 15, 15);
        }

        super.paintComponent(g);
    }
}

class ShadowLabel extends JLabel {
    public ShadowLabel(String text) { super(text); }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        String text = getText();
        FontMetrics fm = g2.getFontMetrics(getFont());
        int x = 0;
        if (getHorizontalAlignment() == SwingConstants.CENTER) {
            x = (getWidth() - fm.stringWidth(text)) / 2;
        } else if (getHorizontalAlignment() == SwingConstants.RIGHT) {
            x = getWidth() - fm.stringWidth(text);
        }

        int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;

        // Draw shadow
        g2.setColor(new Color(0, 0, 0, 180));
        g2.drawString(text, x + 4, y + 4);
        
        // Draw main text
        g2.setColor(getForeground());
        g2.drawString(text, x, y);
    }
}

// --- MENU PANEL ---
class MenuPanel extends JPanel {
    public MenuPanel(PokerGame frame) {
        setLayout(new GridBagLayout());
        setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(20, 20, 20, 20);

        JLabel titleLabel = new ShadowLabel("TEXAS HOLD'EM POKER");
        titleLabel.setFont(new Font("Serif", Font.BOLD, 64));
        titleLabel.setForeground(new Color(255, 215, 0)); // Gold
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 50, 0);
        add(titleLabel, gbc);

        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;

        JButton startBtn = UIUtils.createButton("Start Game", new Dimension(280, 50));
        startBtn.addActionListener(e -> frame.showScreen("PlayerSelect"));
        gbc.gridy = 1;
        add(startBtn, gbc);

        JButton tutorialBtn = UIUtils.createButton("Tutorial", new Dimension(280, 50));
        tutorialBtn.addActionListener(e -> frame.showScreen("Tutorial"));
        gbc.gridy = 2;
        add(tutorialBtn, gbc);

        JButton settingsBtn = UIUtils.createButton("Settings", new Dimension(280, 50));
        settingsBtn.addActionListener(e -> frame.showScreen("Settings"));
        gbc.gridy = 3;
        add(settingsBtn, gbc);

        JButton exitBtn = UIUtils.createButton("Exit", new Dimension(280, 50));
        exitBtn.addActionListener(e -> System.exit(0));
        gbc.gridy = 4;
        add(exitBtn, gbc);
    }

    @Override
    protected void paintComponent(Graphics g) {
        UIUtils.paintCasinoBackground(g, getWidth(), getHeight());
        super.paintComponent(g);
    }
}

// --- TUTORIAL PANEL ---
class TutorialPanel extends JPanel {
    public TutorialPanel(PokerGame frame) {
        setLayout(new BorderLayout());
        setOpaque(false);

        JLabel title = new ShadowLabel("POKER SCHOOL");
        title.setFont(new Font("Serif", Font.BOLD, 48));
        title.setForeground(new Color(255, 215, 0));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(title, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 16));
        tabbedPane.setBackground(new Color(50, 50, 50));
        tabbedPane.setForeground(Color.BLACK);

        // Tab 1: Rules
        JTextArea tutorialText = new JTextArea();
        tutorialText.setEditable(false);
        tutorialText.setFont(new Font("SansSerif", Font.PLAIN, 16));
        tutorialText.setForeground(Color.WHITE);
        tutorialText.setBackground(new Color(50, 50, 50));
        tutorialText.setLineWrap(true);
        tutorialText.setWrapStyleWord(true);
        tutorialText.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        tutorialText.setText(
            "Welcome to Texas Hold'em Poker!\n\n" +
            "1. Objective: Win the chips in the pot by forming the best 5-card poker hand or by forcing all other players to fold.\n\n" +
            "2. Hand Rules:\n" +
            "   - Pre-Flop: Every player is dealt 2 private cards (Hole Cards). A betting round occurs.\n" +
            "   - Flop: 3 community cards are dealt face-up in the center. Another betting round occurs.\n" +
            "   - Turn: A 4th community card is dealt. Another betting round occurs.\n" +
            "   - River: The 5th and final community card is dealt. The final betting round occurs.\n" +
            "   - Showdown: Remaining players reveal their cards. The best 5-card combination wins.\n\n" +
            "3. Actions:\n" +
            "   - Fold: Forfeit your cards and exit the current hand.\n" +
            "   - Check: Pass the action without betting (only possible if no bets have been made this round).\n" +
            "   - Call: Match the current highest bet.\n" +
            "   - Raise: Increase the current highest bet.\n\n" +
            "Hand Rankings (Highest to Lowest):\n" +
            "Royal Flush > Straight Flush > Four of a Kind > Full House > Flush > Straight > Three of a Kind > Two Pair > One Pair > High Card."
        );
        tabbedPane.addTab("Rules", new JScrollPane(tutorialText));

        // Tab 2: How to Play (Step-by-Step)
        tabbedPane.addTab("How to Play", new JScrollPane(new StepByStepPanel()));

        // Tab 3: Hand Rankings Visual Showcase
        tabbedPane.addTab("Hand Rankings", new JScrollPane(new HandShowcasePanel()));

        // Tab 4: Advanced Tips and Tricks
        tabbedPane.addTab("Advanced Tips", new JScrollPane(new AdvancedTipsPanel()));

        add(tabbedPane, BorderLayout.CENTER);

        JButton backBtn = UIUtils.createButton("Back to Menu", new Dimension(200, 45));
        backBtn.addActionListener(e -> frame.showScreen("Menu"));
        
        JPanel bottomPanel = new JPanel();
        bottomPanel.setOpaque(false);
        bottomPanel.add(backBtn);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    @Override
    protected void paintComponent(Graphics g) {
        UIUtils.paintCasinoBackground(g, getWidth(), getHeight());
        super.paintComponent(g);
    }

    private static class StepByStepPanel extends JPanel {
        public StepByStepPanel() {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setBackground(new Color(30, 70, 40));
            setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

            addStep("1. Pre-Flop", "Every player is dealt 2 private cards. First betting round begins.", 
                new Card[]{new Card(14, Card.Suit.SPADES), new Card(13, Card.Suit.SPADES)}, 0);
            
            addStep("2. The Flop", "3 community cards are dealt. Players use them to improve their hand.", 
                new Card[]{new Card(14, Card.Suit.SPADES), new Card(13, Card.Suit.SPADES)}, 3);

            addStep("3. The Turn", "A 4th community card is dealt, followed by more betting.", 
                new Card[]{new Card(14, Card.Suit.SPADES), new Card(13, Card.Suit.SPADES)}, 4);

            addStep("4. The River", "The 5th and final community card is revealed. Final bets are made.", 
                new Card[]{new Card(14, Card.Suit.SPADES), new Card(13, Card.Suit.SPADES)}, 5);

            addStep("5. Showdown", "Best 5-card hand wins the pot! (In this example: Royal Flush)", 
                new Card[]{new Card(14, Card.Suit.SPADES), new Card(13, Card.Suit.SPADES)}, 5);
        }

        private void addStep(String title, String desc, Card[] hole, int commCount) {
            JPanel row = new JPanel(new BorderLayout());
            row.setOpaque(false);
            row.setMaximumSize(new Dimension(850, 160));
            row.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(255, 255, 255, 50)));

            JPanel textPanel = new JPanel(new GridLayout(2, 1));
            textPanel.setOpaque(false);
            textPanel.setPreferredSize(new Dimension(300, 100));
            
            JLabel tLabel = new JLabel(title);
            tLabel.setFont(new Font("Serif", Font.BOLD, 24));
            tLabel.setForeground(new Color(255, 215, 0));
            
            JLabel dLabel = new JLabel("<html><body style='width: 250px'>" + desc + "</body></html>");
            dLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            dLabel.setForeground(Color.WHITE);
            
            textPanel.add(tLabel);
            textPanel.add(dLabel);
            row.add(textPanel, BorderLayout.WEST);

            JPanel cardVis = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    // Draw Hole Cards
                    CardRenderer.drawCardFace(g2, hole[0], 10, 20);
                    CardRenderer.drawCardFace(g2, hole[1], 70, 20);
                    
                    g2.setColor(new Color(255, 255, 255, 100));
                    g2.setFont(new Font("Arial", Font.BOLD, 12));
                    g2.drawString("HOLE", 35, 15);

                    // Draw Community
                    Card[] comm = {
                        new Card(12, Card.Suit.SPADES), new Card(11, Card.Suit.SPADES), 
                        new Card(10, Card.Suit.SPADES), new Card(2, Card.Suit.HEARTS),
                        new Card(7, Card.Suit.DIAMONDS)
                    };

                    g2.drawString("COMMUNITY", 240, 15);
                    for (int i = 0; i < 5; i++) {
                        int cx = 160 + (i * 65);
                        if (i < commCount) {
                            CardRenderer.drawCardFace(g2, comm[i], cx, 20);
                        } else {
                            g2.setColor(new Color(0, 0, 0, 100));
                            g2.fillRoundRect(cx, 20, 55, 85, 8, 8);
                            g2.setColor(new Color(255, 255, 255, 50));
                            g2.drawRoundRect(cx, 20, 55, 85, 8, 8);
                        }
                    }
                }
            };
            cardVis.setOpaque(false);
            row.add(cardVis, BorderLayout.CENTER);

            add(row);
            add(Box.createRigidArea(new Dimension(0, 15)));
        }
    }

    private static class HandShowcasePanel extends JPanel {
        public HandShowcasePanel() {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setBackground(new Color(30, 70, 40));
            setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

            addHandRow("Royal Flush", new Card[] {
                new Card(14, Card.Suit.SPADES), new Card(13, Card.Suit.SPADES),
                new Card(12, Card.Suit.SPADES), new Card(11, Card.Suit.SPADES), new Card(10, Card.Suit.SPADES)
            });
            addHandRow("Straight Flush", new Card[] {
                new Card(8, Card.Suit.HEARTS), new Card(7, Card.Suit.HEARTS),
                new Card(6, Card.Suit.HEARTS), new Card(5, Card.Suit.HEARTS), new Card(4, Card.Suit.HEARTS)
            });
            addHandRow("Four of a Kind", new Card[] {
                new Card(14, Card.Suit.CLUBS), new Card(14, Card.Suit.DIAMONDS),
                new Card(14, Card.Suit.HEARTS), new Card(14, Card.Suit.SPADES), new Card(13, Card.Suit.DIAMONDS)
            });
            addHandRow("Full House", new Card[] {
                new Card(10, Card.Suit.SPADES), new Card(10, Card.Suit.HEARTS),
                new Card(10, Card.Suit.CLUBS), new Card(2, Card.Suit.DIAMONDS), new Card(2, Card.Suit.CLUBS)
            });
            addHandRow("Flush", new Card[] {
                new Card(13, Card.Suit.CLUBS), new Card(11, Card.Suit.CLUBS),
                new Card(8, Card.Suit.CLUBS), new Card(5, Card.Suit.CLUBS), new Card(3, Card.Suit.CLUBS)
            });
            addHandRow("Straight", new Card[] {
                new Card(9, Card.Suit.SPADES), new Card(8, Card.Suit.DIAMONDS),
                new Card(7, Card.Suit.CLUBS), new Card(6, Card.Suit.HEARTS), new Card(5, Card.Suit.SPADES)
            });
            addHandRow("Three of a Kind", new Card[] {
                new Card(7, Card.Suit.DIAMONDS), new Card(7, Card.Suit.HEARTS),
                new Card(7, Card.Suit.SPADES), new Card(13, Card.Suit.CLUBS), new Card(12, Card.Suit.DIAMONDS)
            });
            addHandRow("Two Pair", new Card[] {
                new Card(11, Card.Suit.HEARTS), new Card(11, Card.Suit.CLUBS),
                new Card(9, Card.Suit.DIAMONDS), new Card(9, Card.Suit.SPADES), new Card(14, Card.Suit.CLUBS)
            });
            addHandRow("One Pair", new Card[] {
                new Card(12, Card.Suit.SPADES), new Card(12, Card.Suit.HEARTS),
                new Card(11, Card.Suit.DIAMONDS), new Card(8, Card.Suit.CLUBS), new Card(4, Card.Suit.SPADES)
            });
            addHandRow("High Card", new Card[] {
                new Card(14, Card.Suit.HEARTS), new Card(13, Card.Suit.DIAMONDS),
                new Card(10, Card.Suit.SPADES), new Card(7, Card.Suit.CLUBS), new Card(2, Card.Suit.HEARTS)
            });
        }

        private void addHandRow(String name, Card[] cards) {
            JPanel row = new JPanel(new BorderLayout());
            row.setOpaque(false);
            row.setMaximumSize(new Dimension(800, 120));
            row.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(255, 255, 255, 50)));

            JLabel label = new JLabel(name);
            label.setFont(new Font("Arial", Font.BOLD, 20));
            label.setForeground(new Color(240, 180, 40));
            label.setPreferredSize(new Dimension(180, 100));
            row.add(label, BorderLayout.WEST);

            JPanel cardContainer = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    for (int i = 0; i < cards.length; i++) {
                        CardRenderer.drawCardFace(g2, cards[i], i * 65, 5);
                    }
                }
            };
            cardContainer.setOpaque(false);
            cardContainer.setPreferredSize(new Dimension(350, 100));
            row.add(cardContainer, BorderLayout.CENTER);

            add(row);
            add(Box.createRigidArea(new Dimension(0, 10)));
        }
    }

    private static class AdvancedTipsPanel extends JPanel {
        public AdvancedTipsPanel() {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setBackground(new Color(25, 60, 35));
            setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

            addHeader("MASTER THE GAME: ADVANCED STRATEGY");

            addTipSection("1. THE POWER OF POSITION", 
                "Being the 'Button' (acting last) is the single biggest advantage in poker. " +
                "You gain information from every other player's action before you have to make your own decision.",
                new PositionVisualizer());

            addTipSection("2. UNDERSTANDING POT ODDS", 
                "Math is your friend. If the pot is 100 chips and it costs 20 to call, you're getting 5-to-1 odds. " +
                "If your hand has a better than 20% chance to win, you MUST call.",
                new PotOddsVisualizer());
        }

        private void addHeader(String text) {
            JLabel header = new JLabel(text);
            header.setFont(new Font("Serif", Font.BOLD, 28));
            header.setForeground(new Color(255, 223, 0));
            header.setAlignmentX(Component.CENTER_ALIGNMENT);
            header.setBorder(BorderFactory.createEmptyBorder(0, 0, 40, 0));
            add(header);
        }

        private void addTipSection(String title, String desc, JPanel visual) {
            JPanel section = new JPanel(new GridBagLayout());
            section.setOpaque(true);
            section.setBackground(new Color(0, 0, 0, 60));
            section.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 215, 0, 100), 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
            ));
            section.setMaximumSize(new Dimension(900, 250));

            GridBagConstraints gbc = new GridBagConstraints();
            
            // Text Content
            JPanel textPanel = new JPanel();
            textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
            textPanel.setOpaque(false);
            
            JLabel tLabel = new JLabel(title);
            tLabel.setFont(new Font("Arial", Font.BOLD, 20));
            tLabel.setForeground(new Color(255, 215, 0));
            tLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
            
            JLabel dLabel = new JLabel("<html><body style='width: 350px; line-height: 1.4'>" + desc + "</body></html>");
            dLabel.setFont(new Font("Arial", Font.PLAIN, 15));
            dLabel.setForeground(new Color(230, 230, 230));
            
            textPanel.add(tLabel);
            textPanel.add(dLabel);

            gbc.gridx = 0; gbc.weightx = 0.5; gbc.fill = GridBagConstraints.BOTH;
            section.add(textPanel, gbc);

            // Visualization
            visual.setPreferredSize(new Dimension(400, 180));
            gbc.gridx = 1; gbc.weightx = 0.5;
            section.add(visual, gbc);

            add(section);
            add(Box.createRigidArea(new Dimension(0, 25)));
        }
    }

    private static class PositionVisualizer extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            int w = getWidth(), h = getHeight();
            
            // Draw Table
            g2.setColor(new Color(15, 75, 30));
            g2.fillRoundRect(50, 40, w-100, h-80, 50, 50);
            g2.setColor(new Color(255, 215, 0, 150));
            g2.setStroke(new BasicStroke(2));
            g2.drawRoundRect(50, 40, w-100, h-80, 50, 50);

            // Draw Players
            String[] labels = {"BTN (Last)", "SB", "BB", "UTG (First)"};
            Color[] colors = {Color.GREEN, Color.RED, Color.RED, Color.WHITE};
            Point[] pts = {new Point(w/2, h-30), new Point(60, h/2), new Point(w/2, 30), new Point(w-60, h/2)};
            
            for(int i=0; i<4; i++) {
                g2.setColor(new Color(30, 30, 30));
                g2.fillOval(pts[i].x-15, pts[i].y-15, 30, 30);
                g2.setColor(colors[i]);
                g2.drawOval(pts[i].x-15, pts[i].y-15, 30, 30);
                
                g2.setFont(new Font("Arial", Font.BOLD, 11));
                g2.setColor(Color.WHITE);
                g2.drawString(labels[i], pts[i].x - g2.getFontMetrics().stringWidth(labels[i])/2, pts[i].y + (i==2 ? -20 : 30));
            }

            // Arrow for Turn Order
            g2.setColor(new Color(255, 255, 255, 80));
            g2.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1f, new float[]{10}, 0f));
            g2.drawArc(100, 50, w-200, h-100, 0, 360);
            
            g2.setColor(new Color(255, 215, 0));
            g2.setFont(new Font("Arial", Font.BOLD | Font.ITALIC, 14));
            g2.drawString("Information Flow →", w/2 - 65, h/2 + 5);
        }
    }

    private static class PotOddsVisualizer extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            int w = getWidth(), h = getHeight();
            
            // Pot Box
            g2.setColor(new Color(40, 40, 40));
            g2.fillRoundRect(30, 40, 120, 100, 15, 15);
            g2.setColor(new Color(255, 215, 0));
            g2.drawRoundRect(30, 40, 120, 100, 15, 15);
            
            g2.setFont(new Font("Arial", Font.BOLD, 14));
            g2.drawString("POT", 70, 75);
            g2.setFont(new Font("Arial", Font.BOLD, 20));
            g2.drawString("100", 72, 105);

            // Call Box
            g2.setColor(new Color(60, 20, 20));
            g2.fillRoundRect(220, 60, 100, 60, 15, 15);
            g2.setColor(Color.RED);
            g2.drawRoundRect(220, 60, 100, 60, 15, 15);
            
            g2.setFont(new Font("Arial", Font.BOLD, 12));
            g2.setColor(Color.WHITE);
            g2.drawString("YOUR CALL", 235, 85);
            g2.setFont(new Font("Arial", Font.BOLD, 18));
            g2.drawString("20", 258, 110);

            // Math
            g2.setColor(new Color(255, 215, 0));
            g2.setFont(new Font("Monospaced", Font.BOLD, 18));
            g2.drawString("20 / (100+20) = 16.6%", 90, 165);
            
            g2.setStroke(new BasicStroke(2));
            g2.drawLine(155, 90, 210, 90);
            g2.drawLine(200, 80, 210, 90);
            g2.drawLine(200, 100, 210, 90);
        }
    }

    private static class BluffVisualizer extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            int w = getWidth(), h = getHeight();
            
            // Scary Board
            Card[] board = {
                new Card(14, Card.Suit.SPADES), new Card(13, Card.Suit.SPADES), 
                new Card(2, Card.Suit.SPADES), new Card(10, Card.Suit.DIAMONDS), new Card(5, Card.Suit.CLUBS)
            };
            
            g2.setColor(new Color(255, 255, 255, 40));
            g2.setFont(new Font("Arial", Font.BOLD, 12));
            g2.drawString("COMMUNITY BOARD (The Story)", 30, 25);
            
            for(int i=0; i<5; i++) {
                CardRenderer.drawCardFace(g2, board[i], 30 + i*65, 40);
            }

            // Your Hand (Weak)
            g2.setColor(Color.RED);
            g2.setFont(new Font("Arial", Font.BOLD, 13));
            g2.drawString("YOUR HAND: 7 & 2 (Trash)", 30, 145);
            
            g2.setColor(new Color(255, 215, 0));
            g2.setFont(new Font("Arial", Font.ITALIC, 14));
            g2.drawString("\"I HAVE THE ACE FLUSH!\"", 200, 145);
            
            g2.setStroke(new BasicStroke(2));
            g2.drawRoundRect(190, 125, 175, 30, 10, 10);
        }
    }
}

// --- PLAYER SELECTION PANEL ---
class PlayerSelectPanel extends JPanel {
    public PlayerSelectPanel(PokerGame frame) {
        setLayout(new GridBagLayout());
        setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(15, 15, 15, 15);

        JLabel label = new ShadowLabel("SELECT TOTAL PLAYERS");
        label.setFont(new Font("Serif", Font.BOLD, 48));
        label.setForeground(new Color(255, 215, 0));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 40, 0);
        add(label, gbc);

        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;

        for (int i = 2; i <= 4; i++) {
            final int pCount = i;
            JButton btn = UIUtils.createButton(i + " Players (1 Human vs " + (i - 1) + " CPU)", new Dimension(400, 50));
            btn.addActionListener(e -> frame.startGame(pCount));
            gbc.gridy = i;
            add(btn, gbc);
        }

        JButton backBtn = UIUtils.createButton("Back", new Dimension(400, 50));
        backBtn.setBackground(new Color(80, 80, 80));
        backBtn.addActionListener(e -> frame.showScreen("Menu"));
        gbc.gridy = 5;
        add(backBtn, gbc);
    }

    @Override
    protected void paintComponent(Graphics g) {
        UIUtils.paintCasinoBackground(g, getWidth(), getHeight());
        super.paintComponent(g);
    }
}

// --- POKER DATA STRUCTURES ---
class Card implements Comparable<Card> {
    public enum Suit { HEARTS, DIAMONDS, CLUBS, SPADES }
    private final int rank; // 2 to 14 (Ace)
    private final Suit suit;

    public Card(int rank, Suit suit) {
        this.rank = rank;
        this.suit = suit;
    }

    public int getRank() { return rank; }
    public Suit getSuit() { return suit; }

    public String getRankString() {
        switch (rank) {
            case 11: return "J";
            case 12: return "Q";
            case 13: return "K";
            case 14: return "A";
            default: return String.valueOf(rank);
        }
    }

    public String getSuitSymbol() {
        switch (suit) {
            case HEARTS: return "♥";
            case DIAMONDS: return "♦";
            case CLUBS: return "♣";
            case SPADES: return "♠";
            default: return "";
        }
    }

    public Color getColor() {
        return (suit == Suit.HEARTS || suit == Suit.DIAMONDS) ? Color.RED : Color.BLACK;
    }

    @Override
    public int compareTo(Card o) {
        return Integer.compare(this.rank, o.rank);
    }
}

class CardRenderer {
    public static final int CARD_WIDTH = 55;
    public static final int CARD_HEIGHT = 85;

    public static void drawCardFace(Graphics2D g2, Card card, int x, int y) {
        // Main white card body
        g2.setColor(Color.WHITE);
        g2.fillRoundRect(x, y, CARD_WIDTH, CARD_HEIGHT, 8, 8);
        
        // Subtle inner edge for texture
        g2.setColor(new Color(230, 230, 230));
        g2.drawRoundRect(x + 2, y + 2, CARD_WIDTH - 4, CARD_HEIGHT - 4, 6, 6);
        
        // Outer dark border
        g2.setColor(new Color(40, 40, 40));
        g2.setStroke(new BasicStroke(1));
        g2.drawRoundRect(x, y, CARD_WIDTH, CARD_HEIGHT, 8, 8);

        g2.setColor(card.getColor());
        String rankStr = card.getRankString();
        String suitSym = card.getSuitSymbol();

        // 1. Top-Left Pip (Rank + small suit)
        g2.setFont(new Font("Serif", Font.BOLD, 14));
        g2.drawString(rankStr, x + 5, y + 16);
        g2.setFont(new Font("Serif", Font.PLAIN, 12));
        g2.drawString(suitSym, x + 5, y + 28);

        // 2. Bottom-Right Pip (Mirrored)
        java.awt.geom.AffineTransform old = g2.getTransform();
        g2.translate(x + CARD_WIDTH, y + CARD_HEIGHT);
        g2.rotate(Math.PI);
        g2.setFont(new Font("Serif", Font.BOLD, 14));
        g2.drawString(rankStr, 5, 16);
        g2.setFont(new Font("Serif", Font.PLAIN, 12));
        g2.drawString(suitSym, 5, 28);
        g2.setTransform(old);

        // 3. Center large suit symbol
        g2.setFont(new Font("Serif", Font.PLAIN, 36));
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(suitSym, x + (CARD_WIDTH - fm.stringWidth(suitSym)) / 2, y + (CARD_HEIGHT + fm.getAscent() / 2) / 2 + 5);
    }

    public static void drawCardBack(Graphics2D g2, int x, int y) {
        // White border
        g2.setColor(Color.WHITE);
        g2.fillRoundRect(x, y, CARD_WIDTH, CARD_HEIGHT, 8, 8);
        
        // Inner Pattern Background (Classic Casino Red)
        g2.setColor(new Color(160, 20, 20));
        g2.fillRoundRect(x + 4, y + 4, CARD_WIDTH - 8, CARD_HEIGHT - 8, 5, 5);

        // Diamond Crosshatch Pattern
        g2.setColor(new Color(255, 255, 255, 40)); // Semi-transparent white
        g2.setStroke(new BasicStroke(1));
        
        Shape oldClip = g2.getClip();
        g2.setClip(new java.awt.geom.RoundRectangle2D.Double(x + 4, y + 4, CARD_WIDTH - 8, CARD_HEIGHT - 8, 5, 5));
        
        int spacing = 6;
        for (int i = -CARD_HEIGHT; i < CARD_WIDTH + CARD_HEIGHT; i += spacing) {
            g2.drawLine(x + i, y, x + i + CARD_HEIGHT, y + CARD_HEIGHT);
            g2.drawLine(x + i, y + CARD_HEIGHT, x + i + CARD_HEIGHT, y);
        }
        
        g2.setClip(oldClip);

        // Dark outer boundary
        g2.setColor(new Color(60, 60, 60));
        g2.drawRoundRect(x, y, CARD_WIDTH, CARD_HEIGHT, 8, 8);
    }
}

class AnimatedCard {
    Card card;
    double currentX, currentY;
    double targetX, targetY;
    boolean faceUp;
    boolean reached;

    public AnimatedCard(Card card, double startX, double startY, double targetX, double targetY, boolean faceUp) {
        this.card = card;
        this.currentX = startX;
        this.currentY = startY;
        this.targetX = targetX;
        this.targetY = targetY;
        this.faceUp = faceUp;
        this.reached = false;
    }

    public void update() {
        double dx = targetX - currentX;
        double dy = targetY - currentY;
        if (Math.abs(dx) < 1 && Math.abs(dy) < 1) {
            currentX = targetX;
            currentY = targetY;
            reached = true;
        } else {
            currentX += dx * 0.15;
            currentY += dy * 0.15;
        }
    }
}

class Deck {
    private final List<Card> cards = new ArrayList<>();
    public Deck() {
        for (Card.Suit suit : Card.Suit.values()) {
            for (int rank = 2; rank <= 14; rank++) {
                cards.add(new Card(rank, suit));
            }
        }
        Collections.shuffle(cards);
    }
    public Card drawCard() {
        return cards.remove(cards.size() - 1);
    }
}

class Player {
    private final String name;
    private final boolean isHuman;
    private final Card[] holeCards = new Card[2];
    private int chips = 200;
    private int currentBet = 0;
    private boolean folded = false;
    private String lastAction = "";

    public Player(String name, boolean isHuman) {
        this.name = name;
        this.isHuman = isHuman;
    }

    public String getName() { return name; }
    public boolean isHuman() { return isHuman; }
    public Card[] getHoleCards() { return holeCards; }
    public void setHoleCards(Card c1, Card c2) { holeCards[0] = c1; holeCards[1] = c2; }
    public int getChips() { return chips; }
    public void deductChips(int amt) { chips -= amt; }
    public void addChips(int amt) { chips += amt; }
    public int getCurrentBet() { return currentBet; }
    public void setCurrentBet(int currentBet) { this.currentBet = currentBet; }
    public boolean isFolded() { return folded; }
    public void setFolded(boolean folded) { this.folded = folded; }
    public String getLastAction() { return lastAction; }
    public void setLastAction(String action) { this.lastAction = action; }
    public void resetForRound() { currentBet = 0; lastAction = ""; }
}

// --- GAME PANEL ---
class GamePanel extends JPanel {
    private final PokerGame frame;
    private final List<Player> players = new ArrayList<>();
    private final List<Card> communityCards = new ArrayList<>();
    private final List<AnimatedCard> visualCards = new ArrayList<>();
    private Deck deck;
    private int pot = 0;
    private int currentHighestBet = 0;
    private int currentPlayerIndex = 0;
    private int dealerIndex = -1;
    private int playersToAct = 0;
    private enum Phase { PRE_FLOP, FLOP, TURN, RIVER, SHOWDOWN }
    
    private int getActivePlayerCount() {
        int count = 0;
        for (Player p : players) if (!p.isFolded()) count++;
        return count;
    }
    private Phase currentPhase = Phase.PRE_FLOP;
    private String gameStatusText = "Game initializing...";
    private boolean isAnimating = false;
    private final Timer animationTimer;
    private float notifyAlpha = 0.0f;
    private long notifyStartTime = 0;
    
    private final JButton foldBtn, checkCallBtn, raiseBtn, nextHandBtn, menuBtn;
    private final TableViewPanel tablePanel;

    public GamePanel(PokerGame frame, int numPlayers) {
        this.frame = frame;
        setLayout(new BorderLayout());

        players.add(new Player("You", true));
        for (int i = 1; i < numPlayers; i++) players.add(new Player("CPU " + i, false));

        tablePanel = new TableViewPanel();
        add(tablePanel, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        controlPanel.setBackground(new Color(25, 25, 25));

        foldBtn = createActionButton("Fold", new Color(150, 40, 40));
        checkCallBtn = createActionButton("Check / Call", new Color(40, 120, 180));
        raiseBtn = createActionButton("Raise (+50)", new Color(50, 150, 50));
        nextHandBtn = createActionButton("Next Hand", new Color(180, 120, 30));
        menuBtn = createActionButton("Exit to Menu", new Color(80, 80, 80));

        foldBtn.addActionListener(e -> humanAction("Fold"));
        checkCallBtn.addActionListener(e -> humanAction("Call"));
        raiseBtn.addActionListener(e -> humanAction("Raise"));
        nextHandBtn.addActionListener(e -> startNewHand());
        menuBtn.addActionListener(e -> frame.showScreen("Menu"));

        controlPanel.add(foldBtn); controlPanel.add(checkCallBtn); controlPanel.add(raiseBtn);
        controlPanel.add(nextHandBtn); controlPanel.add(menuBtn);

        add(controlPanel, BorderLayout.SOUTH);
        animationTimer = new Timer(16, e -> updateAnimations());
        animationTimer.start();
    }

    private void updateAnimations() {
        boolean anyAnimating = false;
        for (AnimatedCard ac : visualCards) {
            ac.update();
            if (!ac.reached) anyAnimating = true;
        }
        if (isAnimating && !anyAnimating) {
            isAnimating = false;
            processTurn();
        }

        if (notifyStartTime > 0) {
            long elapsed = System.currentTimeMillis() - notifyStartTime;
            if (elapsed < 500) {
                notifyAlpha = (float)elapsed / 500.0f;
            } else if (elapsed < 2000) {
                notifyAlpha = 1.0f;
            } else if (elapsed < 2500) {
                notifyAlpha = 1.0f - (float)(elapsed - 2000) / 500.0f;
            } else {
                notifyAlpha = 0;
                notifyStartTime = 0;
            }
            anyAnimating = true; // Keep repainting while notify is active
        }

        if (anyAnimating || isAnimating || notifyAlpha > 0) tablePanel.repaint();
    }

    private JButton createActionButton(String text, Color bg) {
        JButton b = new JButton(text);
        b.setFont(new Font("Arial", Font.BOLD, 16));
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setPreferredSize(new Dimension(140, 40));
        return b;
    }

    public void startNewHand() {
        deck = new Deck();
        communityCards.clear();
        visualCards.clear();
        pot = 0;
        currentHighestBet = 0;
        currentPhase = Phase.PRE_FLOP;
        isAnimating = true;

        for (Player p : players) {
            p.setFolded(false);
            p.resetForRound();
            p.setHoleCards(deck.drawCard(), deck.drawCard());
        }

        Point deckPos = tablePanel.getDeckPosition();
        for (int pIdx = 0; pIdx < players.size(); pIdx++) {
            Player p = players.get(pIdx);
            for (int cIdx = 0; cIdx < 2; cIdx++) {
                Point target = tablePanel.getPlayerCardPosition(pIdx, cIdx);
                visualCards.add(new AnimatedCard(p.getHoleCards()[cIdx], deckPos.x, deckPos.y, target.x, target.y, p.isHuman()));
            }
        }
        
        SoundManager.getInstance().playSFX("sounds/Cards2.wav");

        nextHandBtn.setVisible(false);
        setStatus("Pre-Flop: Dealing cards...");
        
        dealerIndex = (dealerIndex + 1) % players.size();
        int sbIndex = (players.size() == 2) ? dealerIndex : (dealerIndex + 1) % players.size();
        int bbIndex = (players.size() == 2) ? (dealerIndex + 1) % 2 : (dealerIndex + 2) % players.size();

        forceBet(players.get(sbIndex), 10);
        forceBet(players.get(bbIndex), 20);
        currentHighestBet = 20;
        
        currentPlayerIndex = (bbIndex + 1) % players.size();
        playersToAct = getActivePlayerCount();
        
        updateUIControls();
        tablePanel.repaint();
    }

    private void forceBet(Player p, int amt) {
        int actual = Math.min(amt, p.getChips());
        p.deductChips(actual);
        p.setCurrentBet(p.getCurrentBet() + actual);
        pot += actual;
    }

    private void setStatus(String str) {
        gameStatusText = str;
        tablePanel.repaint();
    }

    private void triggerTurnNotify() {
        notifyAlpha = 0.0f;
        notifyStartTime = System.currentTimeMillis();
        tablePanel.repaint();
    }

    private void processTurn() {
        if (isAnimating) return;
        int activePlayers = getActivePlayerCount();
        Player lastActive = null;
        for (Player p : players) if (!p.isFolded()) lastActive = p;

        if (activePlayers <= 1) {
            if (lastActive != null) {
                setStatus(lastActive.getName() + " wins pot of " + pot + " chips!");
                lastActive.addChips(pot);
            }
            endRound();
            return;
        }

        if (playersToAct <= 0) { advancePhase(); return; }

        Player p = players.get(currentPlayerIndex);
        if (p.isFolded()) { 
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size(); 
            processTurn(); 
            return; 
        }

        if (p.isHuman()) {
            triggerTurnNotify();
            updateUIControls();
        } else {
            setButtonsEnabled(false);
            Timer timer = new Timer(1000, e -> { cpuAction(p); currentPlayerIndex = (currentPlayerIndex + 1) % players.size(); processTurn(); });
            timer.setRepeats(false);
            timer.start();
        }
    }

    private void updateUIControls() {
        if (currentPhase == Phase.SHOWDOWN || isAnimating) { setButtonsEnabled(false); return; }
        Player human = players.get(0);
        setButtonsEnabled(true);
        int owed = currentHighestBet - human.getCurrentBet();
        checkCallBtn.setText(owed > 0 ? "Call (" + owed + ")" : "Check");
        raiseBtn.setEnabled(human.getChips() > owed + 50);
        tablePanel.repaint();
    }

    private void setButtonsEnabled(boolean enabled) {
        foldBtn.setEnabled(enabled); checkCallBtn.setEnabled(enabled); raiseBtn.setEnabled(enabled);
    }

    private void humanAction(String actionType) {
        Player human = players.get(0);
        SoundManager.getInstance().playSFX("sounds/Cards2.wav");
        if (actionType.equals("Fold")) { human.setFolded(true); human.setLastAction("Folded"); setStatus("You folded."); playersToAct--; }
        else if (actionType.equals("Call")) {
            int owed = currentHighestBet - human.getCurrentBet();
            int actual = Math.min(owed, human.getChips());
            human.deductChips(actual);
            human.setCurrentBet(human.getCurrentBet() + actual);
            pot += actual;
            human.setLastAction(owed > 0 ? "Called" : "Checked");
            setStatus("You " + human.getLastAction().toLowerCase() + ".");
            playersToAct--;
        } else if (actionType.equals("Raise")) {
            int owed = currentHighestBet - human.getCurrentBet();
            int raiseAmt = owed + 50;
            int actual = Math.min(raiseAmt, human.getChips());
            human.deductChips(actual);
            human.setCurrentBet(human.getCurrentBet() + actual);
            currentHighestBet = human.getCurrentBet();
            pot += actual;
            human.setLastAction("Raised");
            setStatus("You raised the bet!");
            playersToAct = getActivePlayerCount() - 1;
        }
        tablePanel.repaint(); setButtonsEnabled(false); 
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size(); 
        processTurn();
    }

    private void cpuAction(Player cpu) {
        int owed = currentHighestBet - cpu.getCurrentBet();
        if (owed > cpu.getChips() || (owed > 100 && Math.random() < 0.25)) {
            cpu.setFolded(true); cpu.setLastAction("Folded"); setStatus(cpu.getName() + " folded.");
            playersToAct--;
        } else if (owed == 0 && Math.random() < 0.2) {
            int actual = Math.min(50, cpu.getChips());
            cpu.deductChips(actual); cpu.setCurrentBet(cpu.getCurrentBet() + actual);
            currentHighestBet = cpu.getCurrentBet(); pot += actual;
            cpu.setLastAction("Raised"); setStatus(cpu.getName() + " raised!");
            playersToAct = getActivePlayerCount() - 1;
        } else {
            int actual = Math.min(owed, cpu.getChips());
            cpu.deductChips(actual); cpu.setCurrentBet(cpu.getCurrentBet() + actual);
            pot += actual; cpu.setLastAction(owed > 0 ? "Called" : "Checked");
            setStatus(cpu.getName() + " " + cpu.getLastAction().toLowerCase() + ".");
            playersToAct--;
        }
        tablePanel.repaint();
    }

    private void advancePhase() {
        for (Player p : players) p.resetForRound();
        currentHighestBet = 0; 
        currentPlayerIndex = (dealerIndex + 1) % players.size();
        playersToAct = getActivePlayerCount();
        isAnimating = true;
        Point deckPos = tablePanel.getDeckPosition();
        switch (currentPhase) {
            case PRE_FLOP:
                currentPhase = Phase.FLOP;
                for (int i = 0; i < 3; i++) {
                    Card c = deck.drawCard(); communityCards.add(c);
                    Point target = tablePanel.getCommunityCardPosition(communityCards.size() - 1);
                    visualCards.add(new AnimatedCard(c, deckPos.x, deckPos.y, target.x, target.y, true));
                }
                setStatus("Flop revealed."); break;
            case FLOP:
                currentPhase = Phase.TURN;
                Card turn = deck.drawCard(); communityCards.add(turn);
                Point targetT = tablePanel.getCommunityCardPosition(communityCards.size() - 1);
                visualCards.add(new AnimatedCard(turn, deckPos.x, deckPos.y, targetT.x, targetT.y, true));
                setStatus("Turn card revealed."); break;
            case TURN:
                currentPhase = Phase.RIVER;
                Card river = deck.drawCard(); communityCards.add(river);
                Point targetR = tablePanel.getCommunityCardPosition(communityCards.size() - 1);
                visualCards.add(new AnimatedCard(river, deckPos.x, deckPos.y, targetR.x, targetR.y, true));
                setStatus("River card revealed."); break;
            case RIVER:
                currentPhase = Phase.SHOWDOWN; isAnimating = false; evaluateShowdown(); return;
            default: isAnimating = false; break;
        }
        SoundManager.getInstance().playSFX("sounds/Cards2.wav");
        tablePanel.repaint();
    }

    private void evaluateShowdown() {
        setStatus("Showdown!");
        for (int i = 1; i < players.size(); i++) {
            Player cpu = players.get(i);
            if (!cpu.isFolded()) for (AnimatedCard ac : visualCards) if (ac.card == cpu.getHoleCards()[0] || ac.card == cpu.getHoleCards()[1]) ac.faceUp = true;
        }
        tablePanel.repaint();

        List<Player> active = new ArrayList<>();
        for (Player p : players) if (!p.isFolded()) active.add(p);

        Player winner = active.get(0);
        HandScore bestScore = evaluateBest5CardHand(winner);
        for (int i = 1; i < active.size(); i++) {
            HandScore score = evaluateBest5CardHand(active.get(i));
            if (score.compareTo(bestScore) > 0) { bestScore = score; winner = active.get(i); }
        }

        final Player winningPlayer = winner;
        final String handName = bestScore.handName;
        Timer t = new Timer(1500, e -> {
            setStatus(winningPlayer.getName() + " wins with " + handName + "!");
            winningPlayer.addChips(pot); endRound();
        });
        t.setRepeats(false); t.start();
    }

    private void endRound() {
        currentPhase = Phase.SHOWDOWN;
        
        List<Player> toRemove = new ArrayList<>();
        boolean humanLost = false;
        
        for (Player p : players) {
            if (p.getChips() <= 0) {
                if (p.isHuman()) humanLost = true;
                else toRemove.add(p);
            }
        }
        
        for (Player p : toRemove) players.remove(p);
        
        if (humanLost) {
            setStatus("GAME OVER! You ran out of chips.");
            nextHandBtn.setVisible(false);
        } else if (players.size() == 1) {
            setStatus("CONGRATULATIONS! You won the tournament!");
            nextHandBtn.setVisible(false);
        } else {
            nextHandBtn.setVisible(true);
        }
        tablePanel.repaint();
    }

    private static class HandScore implements Comparable<HandScore> {
        int category; List<Integer> tieBreakerRanks; String handName;
        public HandScore(int cat, List<Integer> tieBreakers, String name) { this.category = cat; this.tieBreakerRanks = tieBreakers; this.handName = name; }
        @Override
        public int compareTo(HandScore o) {
            if (this.category != o.category) return Integer.compare(this.category, o.category);
            for (int i = 0; i < Math.min(this.tieBreakerRanks.size(), o.tieBreakerRanks.size()); i++) {
                int cmp = Integer.compare(this.tieBreakerRanks.get(i), o.tieBreakerRanks.get(i));
                if (cmp != 0) return cmp;
            }
            return 0;
        }
    }

    private HandScore evaluateBest5CardHand(Player p) {
        List<Card> pool = new ArrayList<>(); pool.add(p.getHoleCards()[0]); pool.add(p.getHoleCards()[1]); pool.addAll(communityCards);
        List<List<Card>> combs = new ArrayList<>(); generateCombinations(pool, 5, 0, new ArrayList<>(), combs);
        HandScore best = null;
        for (List<Card> hand : combs) {
            HandScore score = score5CardHand(hand);
            if (best == null || score.compareTo(best) > 0) best = score;
        }
        return best;
    }

    private void generateCombinations(List<Card> src, int k, int start, List<Card> current, List<List<Card>> result) {
        if (current.size() == k) { result.add(new ArrayList<>(current)); return; }
        for (int i = start; i < src.size(); i++) { current.add(src.get(i)); generateCombinations(src, k, i + 1, current, result); current.remove(current.size() - 1); }
    }

    private HandScore score5CardHand(List<Card> hand) {
        hand.sort((c1, c2) -> Integer.compare(c2.getRank(), c1.getRank()));
        boolean isFlush = true; Card.Suit s = hand.get(0).getSuit();
        for (Card c : hand) if (c.getSuit() != s) { isFlush = false; break; }
        boolean isStraight = true;
        for (int i = 0; i < hand.size() - 1; i++) if (hand.get(i).getRank() - 1 != hand.get(i + 1).getRank()) { isStraight = false; break; }
        boolean isLowAceStraight = false;
        if (!isStraight && hand.get(0).getRank() == 14 && hand.get(1).getRank() == 5 && hand.get(2).getRank() == 4 && hand.get(3).getRank() == 3 && hand.get(4).getRank() == 2) { isStraight = true; isLowAceStraight = true; }

        int[] counts = new int[15];
        for (Card c : hand) counts[c.getRank()]++;
        List<Integer> four = new ArrayList<>(), three = new ArrayList<>(), pairs = new ArrayList<>(), singles = new ArrayList<>();
        for (int r = 14; r >= 2; r--) {
            if (counts[r] == 4) four.add(r); else if (counts[r] == 3) three.add(r); else if (counts[r] == 2) pairs.add(r); else if (counts[r] == 1) singles.add(r);
        }

        List<Integer> tieBreakers = new ArrayList<>();
        if (isFlush && isStraight) {
            int topRank = isLowAceStraight ? 5 : hand.get(0).getRank(); tieBreakers.add(topRank);
            return new HandScore(9, tieBreakers, topRank == 14 ? "Royal Flush" : "Straight Flush");
        }
        if (!four.isEmpty()) { tieBreakers.add(four.get(0)); tieBreakers.add(singles.isEmpty() ? pairs.get(0) : singles.get(0)); return new HandScore(8, tieBreakers, "Four of a Kind"); }
        if (!three.isEmpty() && !pairs.isEmpty()) { tieBreakers.add(three.get(0)); tieBreakers.add(pairs.get(0)); return new HandScore(7, tieBreakers, "Full House"); }
        if (isFlush) { for (Card c : hand) tieBreakers.add(c.getRank()); return new HandScore(6, tieBreakers, "Flush"); }
        if (isStraight) { int topRank = isLowAceStraight ? 5 : hand.get(0).getRank(); tieBreakers.add(topRank); return new HandScore(5, tieBreakers, "Straight"); }
        if (!three.isEmpty()) { tieBreakers.add(three.get(0)); tieBreakers.addAll(singles); return new HandScore(4, tieBreakers, "Three of a Kind"); }
        if (pairs.size() >= 2) { tieBreakers.add(pairs.get(0)); tieBreakers.add(pairs.get(1)); tieBreakers.addAll(singles); return new HandScore(3, tieBreakers, "Two Pair"); }
        if (pairs.size() == 1) { tieBreakers.add(pairs.get(0)); tieBreakers.addAll(singles); return new HandScore(2, tieBreakers, "One Pair"); }
        for (Card c : hand) tieBreakers.add(c.getRank());
        return new HandScore(1, tieBreakers, "High Card");
    }

    private class TableViewPanel extends JPanel {
        public TableViewPanel() { setBackground(new Color(20, 90, 40)); }
        public Point getDeckPosition() { return new Point(getWidth() / 2 - 220, getHeight() / 2 - 40); }
        public Point getCommunityCardPosition(int index) { return new Point(getWidth() / 2 - 130 + (index * 65), getHeight() / 2 - 40); }
        public Point getPlayerCardPosition(int pIdx, int cIdx) {
            int w = getWidth(), h = getHeight();
            if (pIdx == 0) return new Point(w / 2 - 60 + (cIdx * 65), h - 150);
            if (players.size() == 2) return new Point(w / 2 - 60 + (cIdx * 65), 110);
            if (players.size() == 3) {
                if (pIdx == 1) return new Point(100 + (cIdx * 65), 150);
                return new Point(w - 220 + (cIdx * 65), 150);
            }
            if (pIdx == 1) return new Point(80 + (cIdx * 65), h / 2 - 60);
            if (pIdx == 2) return new Point(w / 2 - 60 + (cIdx * 65), 110);
            return new Point(w - 200 + (cIdx * 65), h / 2 - 60);
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g); Graphics2D g2 = (Graphics2D) g; g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight();
            g2.setColor(new Color(10, 60, 20)); g2.fillRoundRect(20, 20, w - 40, h - 40, 100, 100);
            g2.setColor(new Color(100, 50, 10)); g2.setStroke(new BasicStroke(8)); g2.drawRoundRect(20, 20, w - 40, h - 40, 100, 100);
            g2.setColor(new Color(0, 0, 0, 140)); g2.fillRoundRect(w / 2 - 250, 40, 500, 40, 15, 15);
            g2.setColor(Color.WHITE); g2.setFont(new Font("Arial", Font.BOLD, 16)); g2.drawString(gameStatusText, w / 2 - g2.getFontMetrics().stringWidth(gameStatusText) / 2, 66);
            String potStr = "POT: " + pot + " CHIPS"; g2.setFont(new Font("Arial", Font.BOLD, 22)); g2.setColor(new Color(255, 215, 0)); g2.drawString(potStr, w / 2 - g2.getFontMetrics().stringWidth(potStr) / 2, h / 2 - 70);
            Point dp = getDeckPosition(); CardRenderer.drawCardBack(g2, dp.x, dp.y);
            for (int i = 0; i < 5; i++) {
                Point cp = getCommunityCardPosition(i); g2.setColor(new Color(15, 75, 30)); g2.fillRoundRect(cp.x, cp.y, CardRenderer.CARD_WIDTH, CardRenderer.CARD_HEIGHT, 8, 8);
                g2.setColor(new Color(30, 110, 50)); g2.setStroke(new BasicStroke(2)); g2.drawRoundRect(cp.x, cp.y, CardRenderer.CARD_WIDTH, CardRenderer.CARD_HEIGHT, 8, 8);
            }
            for (int i = 0; i < players.size(); i++) {
                Player p = players.get(i); Point cp = getPlayerCardPosition(i, 0); drawPlayerInfo(g2, p, cp.x + 60, cp.y - 10);
            }
            for (AnimatedCard ac : visualCards) { if (ac.faceUp) CardRenderer.drawCardFace(g2, ac.card, (int)ac.currentX, (int)ac.currentY); else CardRenderer.drawCardBack(g2, (int)ac.currentX, (int)ac.currentY); }

            for (int i = 0; i < players.size(); i++) {
                Player p = players.get(i);
                if (p.isFolded()) {
                    Point cp = getPlayerCardPosition(i, 0);
                    g2.setColor(new Color(0, 0, 0, 180));
                    g2.fillRoundRect(cp.x, cp.y, 120, 85, 8, 8);
                    g2.setColor(new Color(220, 50, 50));
                    g2.setStroke(new BasicStroke(2));
                    g2.drawRoundRect(cp.x, cp.y, 120, 85, 8, 8);
                    g2.setColor(Color.WHITE);
                    g2.setFont(new Font("Arial", Font.BOLD, 18));
                    int tw = g2.getFontMetrics().stringWidth("FOLDED");
                    g2.drawString("FOLDED", cp.x + (120 - tw) / 2, cp.y + 50);
                }
            }

            if (notifyAlpha > 0) {
                Point cp = getPlayerCardPosition(0, 0);
                drawTurnNotify(g2, cp.x + 60, cp.y - 45);
            }
        }
        private void drawTurnNotify(Graphics2D g2, int centerX, int centerY) {
            String text = "YOUR TURN";
            g2.setFont(new Font("Arial", Font.BOLD, 14));
            FontMetrics fm = g2.getFontMetrics();
            int tw = fm.stringWidth(text);
            int th = fm.getHeight();
            
            Composite oldComp = g2.getComposite();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, notifyAlpha));
            
            g2.setColor(new Color(0, 0, 0, 200));
            g2.fillRoundRect(centerX - tw/2 - 10, centerY - th/2 - 5, tw + 20, th + 10, 10, 10);
            
            g2.setColor(new Color(255, 215, 0));
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawRoundRect(centerX - tw/2 - 10, centerY - th/2 - 5, tw + 20, th + 10, 10, 10);
            
            g2.setColor(Color.WHITE);
            g2.drawString(text, centerX - tw/2, centerY + fm.getAscent() - th/2 - 1);
            
            g2.setComposite(oldComp);
        }
        private void drawPlayerInfo(Graphics2D g2, Player p, int centerX, int bottomY) {
            g2.setFont(new Font("Arial", Font.BOLD, 14)); String text = p.getName() + " (" + p.getChips() + " C)"; FontMetrics fm = g2.getFontMetrics();
            g2.setColor(new Color(0, 0, 0, 160)); g2.fillRoundRect(centerX - fm.stringWidth(text) / 2 - 10, bottomY - 18, fm.stringWidth(text) + 20, 24, 8, 8);
            g2.setColor(p.isFolded() ? Color.GRAY : Color.WHITE); g2.drawString(text, centerX - fm.stringWidth(text) / 2, bottomY - 2);
            if (!p.getLastAction().isEmpty()) { g2.setFont(new Font("Arial", Font.ITALIC, 12)); g2.setColor(new Color(200, 200, 255)); g2.drawString(p.getLastAction(), centerX - g2.getFontMetrics().stringWidth(p.getLastAction()) / 2, bottomY + 110); }
        }
    }
}

// --- SETTINGS PANEL ---
class SettingsPanel extends JPanel {
    public SettingsPanel(PokerGame frame) {
        setLayout(new GridBagLayout());
        setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(15, 15, 15, 15);

        JLabel titleLabel = new ShadowLabel("SETTINGS");
        titleLabel.setFont(new Font("Serif", Font.BOLD, 48));
        titleLabel.setForeground(new Color(255, 215, 0));
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 40, 0);
        add(titleLabel, gbc);

        // Music Toggle
        JCheckBox musicToggle = createStyledCheckbox("Enable Background Music", SoundManager.getInstance().isMusicEnabled());
        gbc.gridy = 1;
        gbc.insets = new Insets(10, 10, 10, 10);
        add(musicToggle, gbc);

        // Volume Slider Container
        JPanel volPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        volPanel.setOpaque(false);
        JLabel volLabel = new JLabel("Music Volume:");
        volLabel.setFont(new Font("Arial", Font.BOLD, 18));
        volLabel.setForeground(Color.WHITE);
        JSlider volSlider = new JSlider(0, 100, (int)(SoundManager.getInstance().getMusicVolume() * 100));
        volSlider.setPreferredSize(new Dimension(200, 40));
        volSlider.setOpaque(false);
        volSlider.addChangeListener(e -> SoundManager.getInstance().setMusicVolume(volSlider.getValue() / 100f));
        volPanel.add(volLabel);
        volPanel.add(volSlider);
        
        gbc.gridy = 2;
        add(volPanel, gbc);
        volPanel.setVisible(musicToggle.isSelected());

        musicToggle.addActionListener(e -> {
            boolean enabled = musicToggle.isSelected();
            SoundManager.getInstance().setMusicEnabled(enabled);
            volPanel.setVisible(enabled);
            revalidate();
            repaint();
        });

        // SFX Toggle
        JCheckBox sfxToggle = createStyledCheckbox("Enable Sound Effects", SoundManager.getInstance().isSfxEnabled());
        sfxToggle.addActionListener(e -> SoundManager.getInstance().setSfxEnabled(sfxToggle.isSelected()));
        gbc.gridy = 3;
        add(sfxToggle, gbc);

        // Back Button
        JButton backBtn = UIUtils.createButton("Back to Menu", new Dimension(250, 50));
        backBtn.addActionListener(e -> frame.showScreen("Menu"));
        gbc.gridy = 4;
        gbc.insets = new Insets(40, 0, 0, 0);
        add(backBtn, gbc);
    }

    private JCheckBox createStyledCheckbox(String text, boolean selected) {
        JCheckBox cb = new JCheckBox(text, selected);
        cb.setFont(new Font("Arial", Font.BOLD, 20));
        cb.setForeground(Color.WHITE);
        cb.setOpaque(false);
        cb.setFocusPainted(false);
        return cb;
    }

    @Override
    protected void paintComponent(Graphics g) {
        UIUtils.paintCasinoBackground(g, getWidth(), getHeight());
        super.paintComponent(g);
    }
}

// --- SOUND MANAGER ---
class SoundManager {
    private static SoundManager instance;
    private boolean musicEnabled = true;
    private boolean sfxEnabled = true;
    private float musicVolume = 0.2f;
    private javax.sound.sampled.Clip musicClip;

    private SoundManager() {}

    public static SoundManager getInstance() {
        if (instance == null) instance = new SoundManager();
        return instance;
    }

    public void playMusic(String filePath) {
        if (!musicEnabled) return;
        try {
            stopMusic();
            java.io.File file = new java.io.File(filePath);
            if (!file.exists()) return;
            javax.sound.sampled.AudioInputStream ais = javax.sound.sampled.AudioSystem.getAudioInputStream(file);
            musicClip = javax.sound.sampled.AudioSystem.getClip();
            musicClip.open(ais);
            setMusicVolume(musicVolume);
            musicClip.loop(javax.sound.sampled.Clip.LOOP_CONTINUOUSLY);
            musicClip.start();
        } catch (Exception e) {
            System.err.println("Error playing music: " + e.getMessage());
        }
    }

    public void stopMusic() {
        if (musicClip != null) {
            musicClip.stop();
            musicClip.close();
            musicClip = null;
        }
    }

    public void setMusicEnabled(boolean enabled) {
        this.musicEnabled = enabled;
        if (enabled) playMusic("sounds/Nior.wav");
        else stopMusic();
    }

    public void setMusicVolume(float volume) {
        this.musicVolume = volume;
        if (musicClip != null && musicClip.isControlSupported(javax.sound.sampled.FloatControl.Type.MASTER_GAIN)) {
            javax.sound.sampled.FloatControl gainControl = (javax.sound.sampled.FloatControl) musicClip.getControl(javax.sound.sampled.FloatControl.Type.MASTER_GAIN);
            float dB = (float) (Math.log(volume == 0 ? 0.0001 : volume) / Math.log(10.0) * 20.0);
            gainControl.setValue(dB);
        }
    }

    public void playSFX(String filePath) {
        if (!sfxEnabled) return;
        new Thread(() -> {
            try {
                java.io.File file = new java.io.File(filePath);
                if (!file.exists()) return;
                javax.sound.sampled.AudioInputStream ais = javax.sound.sampled.AudioSystem.getAudioInputStream(file);
                javax.sound.sampled.Clip clip = javax.sound.sampled.AudioSystem.getClip();
                clip.open(ais);
                clip.start();
                clip.addLineListener(event -> {
                    if (event.getType() == javax.sound.sampled.LineEvent.Type.STOP) {
                        clip.close();
                    }
                });
            } catch (Exception e) {
                System.err.println("Error playing SFX: " + e.getMessage());
            }
        }).start();
    }

    public boolean isMusicEnabled() { return musicEnabled; }
    public boolean isSfxEnabled() { return sfxEnabled; }
    public float getMusicVolume() { return musicVolume; }
    public void setSfxEnabled(boolean enabled) { this.sfxEnabled = enabled; }
}
