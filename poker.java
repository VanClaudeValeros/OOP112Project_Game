import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.*;
import java.util.List;
import javax.imageio.ImageIO;
import javax.sound.sampled.*;

public class poker extends JFrame {
    private int balance = 100;
    private int currentBet = 0;
    private int winMultiplier = 1;
    private List<Card> deck;
    private List<Card> playerHand;
    private List<Card> dealerHand;
    private boolean playerTurn = false;
    private boolean gameOver = true;
    private String message = "Spin the Slot or Place a Bet!";
    private String slotMessage = "Ready to Spin!";

    private GamePanel gamePanel;
    private JButton btnPlaceBet, btnDeal, btnHit, btnStand, btnSpinSlot;
    private JTextField txtBetAmount;

    // --- MULTIMEDIA CACHE ---
    private static Map<String, Image> imageCache = new HashMap<>();
    private Image tableBgSprite;
    private Image cardBackSprite;
    private String[] slotReels = {"🍒", "🔔", "💎"}; // Simple emojis if sprites missing
    private String[] currentSlotResult = {"?", "?", "?"};

    public poker() {
        setTitle("Coins & Poker: Multimedia Hybrid Edition");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setResizable(true);

        loadGlobalSprites();

        deck = new ArrayList<>();
        playerHand = new ArrayList<>();
        dealerHand = new ArrayList<>();

        gamePanel = new GamePanel();
        add(gamePanel, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel();
        controlPanel.setBackground(new Color(40, 40, 40));

        JLabel lblBet = new JLabel("Bet: $");
        lblBet.setForeground(Color.WHITE);
        txtBetAmount = new JTextField(4);

        btnPlaceBet = new JButton("Place Bet");
        btnSpinSlot = new JButton("Spin Slot ($5)");
        btnDeal = new JButton("Deal / Play");
        btnHit = new JButton("Hit");
        btnStand = new JButton("Stand");

        btnHit.setEnabled(false);
        btnStand.setEnabled(false);

        controlPanel.add(lblBet);
        controlPanel.add(txtBetAmount);
        controlPanel.add(btnPlaceBet);
        controlPanel.add(btnSpinSlot);
        controlPanel.add(btnDeal);
        controlPanel.add(btnHit);
        controlPanel.add(btnStand);

        add(controlPanel, BorderLayout.SOUTH);

        btnPlaceBet.addActionListener(e -> placeCustomBet());
        txtBetAmount.addActionListener(e -> placeCustomBet());
        btnSpinSlot.addActionListener(e -> spinSlot());
        btnDeal.addActionListener(e -> attemptDeal());
        btnHit.addActionListener(e -> hit());
        btnStand.addActionListener(e -> stand());
    }

    private void playSound(String fileName) {
        new Thread(() -> {
            try {
                File soundFile = new File("assets/" + fileName);
                if (soundFile.exists()) {
                    AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
                    Clip clip = AudioSystem.getClip();
                    clip.open(audioIn);
                    clip.start();
                    clip.addLineListener(event -> {
                        if (event.getType() == LineEvent.Type.STOP) {
                            clip.close();
                        }
                    });
                }
            } catch (Exception e) {}
        }).start();
    }

    private Image getImage(String path) {
        if (imageCache.containsKey(path)) return imageCache.get(path);
        try {
            Image img = ImageIO.read(new File(path));
            imageCache.put(path, img);
            return img;
        } catch (Exception e) {
            return null;
        }
    }

    private void loadGlobalSprites() {
        tableBgSprite = getImage("assets/table_bg.png");
        cardBackSprite = getImage("assets/card_back.png");
    }

    private void spinSlot() {
        if (!gameOver) return;
        if (balance < 5) {
            slotMessage = "Not enough for Slot!";
            gamePanel.repaint();
            return;
        }
        balance -= 5;
        playSound("coin.wav");

        Random rand = new Random();
        for (int i = 0; i < 3; i++) {
            currentSlotResult[i] = slotReels[rand.nextInt(slotReels.length)];
        }

        if (currentSlotResult[0].equals(currentSlotResult[1]) && currentSlotResult[1].equals(currentSlotResult[2])) {
            winMultiplier = 3;
            slotMessage = "JACKPOT! 3x Multiplier!";
            playSound("win.wav");
        } else if (currentSlotResult[0].equals(currentSlotResult[1]) || currentSlotResult[1].equals(currentSlotResult[2]) || currentSlotResult[0].equals(currentSlotResult[2])) {
            winMultiplier = 2;
            slotMessage = "MATCH! 2x Multiplier!";
            playSound("coin.wav");
        } else {
            winMultiplier = 1;
            slotMessage = "No Match. 1x Multiplier.";
        }
        gamePanel.repaint();
    }

    private void placeCustomBet() {
        if (!gameOver) return;
        try {
            int bet = Integer.parseInt(txtBetAmount.getText().trim());
            if (bet > 0 && balance >= bet) {
                balance -= bet;
                currentBet += bet;
                message = "Bet: $" + currentBet + ". Ready?";
                txtBetAmount.setText("");
                playSound("coin.wav");
                gamePanel.repaint();
            } else {
                message = "Invalid or insufficient funds.";
                playSound("error.wav");
                gamePanel.repaint();
            }
        } catch (NumberFormatException ex) {
            message = "Please enter a valid number!";
            gamePanel.repaint();
        }
    }

    private void attemptDeal() {
        if (currentBet == 0) {
            if (balance >= 10) {
                balance -= 10;
                currentBet = 10;
                playSound("coin.wav");
            } else {
                message = "Not enough money! Restart?";
                if (balance <= 0 && currentBet <= 0) restartGame();
                gamePanel.repaint();
                return;
            }
        }
        startNewHand();
    }

    private void restartGame() {
        balance = 100;
        currentBet = 0;
        winMultiplier = 1;
        message = "Game Restarted! $100 added.";
    }

    private void initializeDeck() {
        deck.clear();
        String[] suits = {"hearts", "diamonds", "clubs", "spades"};
        String[] ranks = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};

        for (String suit : suits) {
            for (String rank : ranks) {
                int value = 0;
                if (rank.equals("A")) value = 11;
                else if (rank.equals("J") || rank.equals("Q") || rank.equals("K")) value = 10;
                else value = Integer.parseInt(rank);
                deck.add(new Card(suit, rank, value));
            }
        }
        Collections.shuffle(deck);
    }

    private void startNewHand() {
        initializeDeck();
        playerHand.clear();
        dealerHand.clear();

        playSound("shuffle.wav");

        playerHand.add(drawCard());
        dealerHand.add(drawCard());
        playerHand.add(drawCard());
        dealerHand.add(drawCard());

        playerTurn = true;
        gameOver = false;
        message = "Hit or Stand? (x" + winMultiplier + " active)";

        btnPlaceBet.setEnabled(false);
        txtBetAmount.setEnabled(false);
        btnSpinSlot.setEnabled(false);
        btnDeal.setEnabled(false);
        btnHit.setEnabled(true);
        btnStand.setEnabled(true);

        checkInstantWin();
        gamePanel.repaint();
    }

    private Card drawCard() {
        playSound("deal_card.wav");
        return deck.remove(deck.size() - 1);
    }

    private void hit() {
        playerHand.add(drawCard());
        int playerTotal = getHandValue(playerHand);

        if (playerTotal > 21) {
            message = "Bust! You lose.";
            endGame(false);
        } else if (playerTotal == 21) {
            message = "21! Multiplier Applied!";
            endGame(true);
        }
        gamePanel.repaint();
    }

    private void stand() {
        playerTurn = false;
        while (getHandValue(dealerHand) < 17 && !gameOver) {
            dealerHand.add(drawCard());
            if (getHandValue(dealerHand) == 21) {
                message = "Dealer hits 21! Dealer Wins!";
                endGame(false);
                gamePanel.repaint();
                return;
            }
        }
        if (!gameOver) determineWinner();
        gamePanel.repaint();
    }

    private void checkInstantWin() {
        int playerTotal = getHandValue(playerHand);
        int dealerTotal = getHandValue(dealerHand);

        if (playerTotal == 21 && dealerTotal == 21) {
            message = "Double 21! Push.";
            balance += currentBet;
            endGame(false);
        } else if (playerTotal == 21) {
            message = "Blackjack! Win x" + winMultiplier + "!";
            endGame(true);
        } else if (dealerTotal == 21) {
            playerTurn = false;
            message = "Dealer Blackjack! Lose.";
            endGame(false);
        }
    }

    private void determineWinner() {
        int playerTotal = getHandValue(playerHand);
        int dealerTotal = getHandValue(dealerHand);

        if (dealerTotal > 21 || playerTotal > dealerTotal) {
            message = "You Win! x" + winMultiplier + "!";
            endGame(true);
        } else if (playerTotal == dealerTotal) {
            message = "Push (Tie).";
            balance += currentBet;
            endGame(false);
        } else {
            message = "Dealer Wins!";
            endGame(false);
        }
    }

    private void endGame(boolean playerWon) {
        if (playerWon) {
            balance += (currentBet * (1 + winMultiplier));
            playSound("win.wav");
        } else if (message.contains("lose") || message.contains("Bust") || message.contains("Dealer Wins") || message.contains("Lose")) {
            playSound("lose.wav");
        }

        currentBet = 0;
        winMultiplier = 1; // Reset multiplier
        gameOver = true;
        slotMessage = "Ready to Spin!";

        btnPlaceBet.setEnabled(true);
        txtBetAmount.setEnabled(true);
        btnSpinSlot.setEnabled(true);
        btnDeal.setEnabled(true);
        btnHit.setEnabled(false);
        btnStand.setEnabled(false);
    }

    private int getHandValue(List<Card> hand) {
        int value = 0;
        int aces = 0;
        for (Card card : hand) {
            value += card.value;
            if (card.rank.equals("A")) aces++;
        }
        while (value > 21 && aces > 0) {
            value -= 10;
            aces--;
        }
        return value;
    }

    private class GamePanel extends JPanel {
        private final int BASE_WIDTH = 1000;
        private final int BASE_HEIGHT = 700;

        public GamePanel() {
            setBackground(new Color(34, 139, 34));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            double scale = Math.min((double) getWidth() / BASE_WIDTH, (double) getHeight() / BASE_HEIGHT);
            g2d.translate((getWidth() - BASE_WIDTH * scale) / 2, (getHeight() - BASE_HEIGHT * scale) / 2);
            g2d.scale(scale, scale);

            if (tableBgSprite != null) g2d.drawImage(tableBgSprite, 0, 0, BASE_WIDTH, BASE_HEIGHT, null);

            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 22));
            g2d.drawString("Balance: $" + balance, 20, 40);
            g2d.drawString("Current Bet: $" + currentBet, 20, 75);
            g2d.drawString("Multiplier: x" + winMultiplier, 20, 110);

            // SLOT DISPLAY
            g2d.setColor(new Color(0, 0, 0, 150));
            g2d.fillRoundRect(750, 20, 220, 120, 20, 20);
            g2d.setColor(Color.YELLOW);
            g2d.drawRoundRect(750, 20, 220, 120, 20, 20);
            g2d.setFont(new Font("Serif", Font.PLAIN, 50));
            g2d.drawString(currentSlotResult[0] + " " + currentSlotResult[1] + " " + currentSlotResult[2], 775, 90);
            g2d.setFont(new Font("Arial", Font.BOLD, 14));
            g2d.drawString(slotMessage, 775, 130);

            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 36));
            int msgWidth = g2d.getFontMetrics().stringWidth(message);
            g2d.drawString(message, (BASE_WIDTH - msgWidth) / 2, 320);

            if (!dealerHand.isEmpty() && !playerHand.isEmpty()) drawHands(g2d);
        }

        private void drawHands(Graphics2D g2d) {
            int cardWidth = 80, cardSpacing = 90;

            int dealerVal = playerTurn ? getHandValue(Collections.singletonList(dealerHand.get(0))) : getHandValue(dealerHand);
            g2d.setFont(new Font("Arial", Font.BOLD, 20));
            g2d.drawString("Dealer: " + (playerTurn ? dealerVal + " + ?" : dealerVal), 450, 60);

            int startXDealer = (BASE_WIDTH - (dealerHand.size() * cardSpacing)) / 2;
            for (int i = 0; i < dealerHand.size(); i++) {
                drawCardSprite(g2d, dealerHand.get(i), startXDealer + (i * cardSpacing), 80, (playerTurn && i == 1));
            }

            g2d.drawString("Player: " + getHandValue(playerHand), 450, 420);
            int startXPlayer = (BASE_WIDTH - (playerHand.size() * cardSpacing)) / 2;
            for (int i = 0; i < playerHand.size(); i++) {
                drawCardSprite(g2d, playerHand.get(i), startXPlayer + (i * cardSpacing), 450, false);
            }
        }

        private void drawCardSprite(Graphics2D g2d, Card card, int x, int y, boolean hidden) {
            int w = 80, h = 120;
            if (hidden) {
                if (cardBackSprite != null) g2d.drawImage(cardBackSprite, x, y, w, h, null);
                else {
                    g2d.setColor(new Color(178, 34, 34));
                    g2d.fillRoundRect(x, y, w, h, 10, 10);
                    g2d.setColor(Color.WHITE);
                    g2d.drawRoundRect(x, y, w, h, 10, 10);
                }
                return;
            }

            if (card.frontSprite != null) g2d.drawImage(card.frontSprite, x, y, w, h, null);
            else {
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(x, y, w, h, 10, 10);
                g2d.setColor(Color.BLACK);
                g2d.drawRoundRect(x, y, w, h, 10, 10);
                String sym = card.suit.equals("hearts") ? "♥" : card.suit.equals("diamonds") ? "♦" : card.suit.equals("clubs") ? "♣" : "♠";
                if (card.suit.equals("hearts") || card.suit.equals("diamonds")) g2d.setColor(Color.RED);
                g2d.setFont(new Font("Arial", Font.BOLD, 24));
                g2d.drawString(card.rank, x + 10, y + 30);
                g2d.drawString(sym, x + 10, y + 60);
            }
        }
    }

    private class Card {
        String suit, rank;
        int value;
        Image frontSprite;

        public Card(String suit, String rank, int value) {
            this.suit = suit; this.rank = rank; this.value = value;
            this.frontSprite = getImage("assets/" + suit + "_" + rank + ".png");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            poker game = new poker();
            game.setVisible(true);
            game.setLocationRelativeTo(null);
        });
    }
}
