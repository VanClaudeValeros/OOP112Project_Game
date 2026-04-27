import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.imageio.ImageIO;
import javax.sound.sampled.*;

public class CoinsAndPoker extends JFrame {
    private int balance = 100;
    private int currentBet = 0;
    private List<Card> deck;
    private List<Card> playerHand;
    private List<Card> dealerHand;
    private boolean playerTurn = false;
    private boolean gameOver = true;
    private String message = "Place a bet or press Deal!";

    private GamePanel gamePanel;
    private JButton btnPlaceBet, btnDeal, btnHit, btnStand;
    private JTextField txtBetAmount;

    // --- SPRITE ASSETS ---
    private Image tableBgSprite;
    private Image cardBackSprite;

    public CoinsAndPoker() {
        setTitle("Coins & Poker: Multimedia Edition");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setResizable(true);

        loadGlobalSprites(); // Try to load global images

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
        btnDeal = new JButton("Deal");
        btnHit = new JButton("Hit");
        btnStand = new JButton("Stand");

        btnHit.setEnabled(false);
        btnStand.setEnabled(false);

        controlPanel.add(lblBet);
        controlPanel.add(txtBetAmount);
        controlPanel.add(btnPlaceBet);
        controlPanel.add(btnDeal);
        controlPanel.add(btnHit);
        controlPanel.add(btnStand);

        add(controlPanel, BorderLayout.SOUTH);

        btnPlaceBet.addActionListener(e -> placeCustomBet());
        btnDeal.addActionListener(e -> attemptDeal());
        btnHit.addActionListener(e -> hit());
        btnStand.addActionListener(e -> stand());
    }

    // --- MULTIMEDIA SYSTEM: SOUND ---
    // Make sure your sound files are .wav format and in an "assets" folder!
    private void playSound(String fileName) {
        try {
            File soundFile = new File("assets/" + fileName);
            if (soundFile.exists()) {
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
                Clip clip = AudioSystem.getClip();
                clip.open(audioIn);
                clip.start();
            }
        } catch (Exception e) {
            // Fails silently if sound is missing
        }
    }

    // --- MULTIMEDIA SYSTEM: GLOBAL SPRITES ---
    private void loadGlobalSprites() {
        try {
            tableBgSprite = ImageIO.read(new File("assets/table_bg.png"));
            cardBackSprite = ImageIO.read(new File("assets/card_back.png"));
        } catch (Exception e) {
            // Fails silently, uses solid colors instead
        }
    }

    private void placeCustomBet() {
        if (!gameOver) return;
        try {
            int bet = Integer.parseInt(txtBetAmount.getText().trim());
            if (bet > 0 && balance >= bet) {
                balance -= bet;
                currentBet += bet;
                message = "Bet: $" + currentBet + ". Press Deal!";
                txtBetAmount.setText("");
                playSound("coin.wav"); // Trigger coin sound!
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
                playSound("coin.wav"); // Trigger coin sound!
            } else {
                message = "Not enough money!";
                gamePanel.repaint();
                return;
            }
        }
        startNewHand();
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

        playSound("shuffle.wav"); // Trigger shuffle sound!

        playerHand.add(drawCard());
        dealerHand.add(drawCard());
        playerHand.add(drawCard());
        dealerHand.add(drawCard());

        playerTurn = true;
        gameOver = false;
        message = "Hit or Stand?";

        btnPlaceBet.setEnabled(false);
        txtBetAmount.setEnabled(false);
        btnDeal.setEnabled(false);
        btnHit.setEnabled(true);
        btnStand.setEnabled(true);

        checkInstantWin();
        gamePanel.repaint();
    }

    private Card drawCard() {
        playSound("deal_card.wav"); // Sound for every card drawn
        return deck.remove(deck.size() - 1);
    }

    private void hit() {
        playerHand.add(drawCard());
        int playerTotal = getHandValue(playerHand);

        if (playerTotal > 21) {
            message = "Bust! You lose.";
            endGame(false);
        } else if (playerTotal == 21) {
            message = "21! You automatically win!";
            endGame(true);
        }
        gamePanel.repaint();
    }

    private void stand() {
        playerTurn = false;

        while (getHandValue(dealerHand) < 17 && !gameOver) {
            dealerHand.add(drawCard());
            int dealerTotal = getHandValue(dealerHand);

            if (dealerTotal == 21) {
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
            message = "Double 21! It's a Push (Tie).";
            balance += currentBet;
            endGame(false);
        } else if (playerTotal == 21) {
            message = "Dealt 21! You automatically win!";
            endGame(true);
        } else if (dealerTotal == 21) {
            playerTurn = false;
            message = "Dealer dealt 21! You lose.";
            endGame(false);
        }
    }

    private void determineWinner() {
        int playerTotal = getHandValue(playerHand);
        int dealerTotal = getHandValue(dealerHand);

        if (dealerTotal > 21 || playerTotal > dealerTotal) {
            message = "You Win!";
            endGame(true);
        } else if (playerTotal == dealerTotal) {
            message = "Push (Tie).";
            balance += currentBet;
            endGame(false); // Push doesn't trigger win sound
        } else {
            message = "Dealer Wins!";
            endGame(false);
        }
    }

    private void endGame(boolean playerWon) {
        if (playerWon) {
            balance += (currentBet * 2);
            playSound("win.wav"); // Trigger Win Sound!
        } else if (message.contains("lose") || message.contains("Bust") || message.contains("Dealer Wins")) {
            playSound("lose.wav"); // Trigger Lose Sound!
        }

        currentBet = 0;
        gameOver = true;

        btnPlaceBet.setEnabled(true);
        txtBetAmount.setEnabled(true);
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
        private final int BASE_WIDTH = 800;
        private final int BASE_HEIGHT = 600;

        public GamePanel() {
            setBackground(new Color(34, 139, 34));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            double scaleWidth = (double) getWidth() / BASE_WIDTH;
            double scaleHeight = (double) getHeight() / BASE_HEIGHT;
            double scale = Math.min(scaleWidth, scaleHeight);

            int xOffset = (int) ((getWidth() - (BASE_WIDTH * scale)) / 2);
            int yOffset = (int) ((getHeight() - (BASE_HEIGHT * scale)) / 2);

            g2d.translate(xOffset, yOffset);
            g2d.scale(scale, scale);

            // DRAW BACKGROUND SPRITE IF AVAILABLE
            if (tableBgSprite != null) {
                g2d.drawImage(tableBgSprite, 0, 0, BASE_WIDTH, BASE_HEIGHT, null);
            }

            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 20));
            g2d.drawString("Balance: $" + balance, 20, 30);
            g2d.drawString("Current Bet: $" + currentBet, 20, 60);

            g2d.setFont(new Font("Arial", Font.BOLD, 30));
            int msgWidth = g2d.getFontMetrics().stringWidth(message);
            g2d.drawString(message, (BASE_WIDTH - msgWidth) / 2, 280);

            if (!dealerHand.isEmpty() && !playerHand.isEmpty()) {
                drawHands(g2d);
            }
        }

        private void drawHands(Graphics2D g2d) {
            int cardWidth = 70;
            int cardSpacing = 80;

            // DEALER HAND
            int dealerDisplayTotal;
            if (playerTurn) {
                List<Card> visibleDealerCard = new ArrayList<>();
                visibleDealerCard.add(dealerHand.get(0));
                dealerDisplayTotal = getHandValue(visibleDealerCard);
            } else {
                dealerDisplayTotal = getHandValue(dealerHand);
            }

            g2d.setFont(new Font("Arial", Font.BOLD, 20));
            String dealerText = "Dealer's Hand: " + dealerDisplayTotal;
            int dealerTextWidth = g2d.getFontMetrics().stringWidth(dealerText);
            g2d.drawString(dealerText, (BASE_WIDTH - dealerTextWidth) / 2, 50);

            int totalDealerHandWidth = (dealerHand.size() - 1) * cardSpacing + cardWidth;
            int startXDealer = (BASE_WIDTH - totalDealerHandWidth) / 2;

            for (int i = 0; i < dealerHand.size(); i++) {
                boolean hideCard = (playerTurn && i == 1);
                drawCardSprite(g2d, dealerHand.get(i), startXDealer + (i * cardSpacing), 70, hideCard);
            }

            // PLAYER HAND
            int playerDisplayTotal = getHandValue(playerHand);
            String playerText = "Your Hand: " + playerDisplayTotal;
            int playerTextWidth = g2d.getFontMetrics().stringWidth(playerText);
            g2d.drawString(playerText, (BASE_WIDTH - playerTextWidth) / 2, 380);

            int totalPlayerHandWidth = (playerHand.size() - 1) * cardSpacing + cardWidth;
            int startXPlayer = (BASE_WIDTH - totalPlayerHandWidth) / 2;

            for (int i = 0; i < playerHand.size(); i++) {
                drawCardSprite(g2d, playerHand.get(i), startXPlayer + (i * cardSpacing), 400, false);
            }
        }

        private void drawCardSprite(Graphics2D g2d, Card card, int x, int y, boolean hidden) {
            int width = 70;
            int height = 100;

            // DRAW HIDDEN CARD (BACK)
            if (hidden) {
                if (cardBackSprite != null) {
                    g2d.drawImage(cardBackSprite, x, y, width, height, null);
                } else {
                    g2d.setColor(new Color(178, 34, 34)); // Fallback red back
                    g2d.fillRoundRect(x, y, width, height, 10, 10);
                    g2d.setColor(Color.WHITE);
                    g2d.drawRoundRect(x, y, width, height, 10, 10);
                }
                return;
            }

            // DRAW VISIBLE CARD (FRONT)
            if (card.frontSprite != null) {
                g2d.drawImage(card.frontSprite, x, y, width, height, null);
            } else {
                // Fallback white card with text
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(x, y, width, height, 10, 10);
                g2d.setColor(Color.BLACK);
                g2d.drawRoundRect(x, y, width, height, 10, 10);

                // Convert text string back to unicode suit symbol for fallback
                String symbol = "";
                if (card.suit.equals("hearts")) { symbol = "♥"; g2d.setColor(Color.RED); }
                else if (card.suit.equals("diamonds")) { symbol = "♦"; g2d.setColor(Color.RED); }
                else if (card.suit.equals("clubs")) { symbol = "♣"; g2d.setColor(Color.BLACK); }
                else if (card.suit.equals("spades")) { symbol = "♠"; g2d.setColor(Color.BLACK); }

                g2d.setFont(new Font("Arial", Font.BOLD, 24));
                g2d.drawString(card.rank, x + 10, y + 25);
                g2d.drawString(symbol, x + 10, y + 50);
            }
        }
    }

    // --- UPDATED CARD CLASS WITH SPRITE LOADING ---
    private class Card {
        String suit;
        String rank;
        int value;
        Image frontSprite;

        public Card(String suit, String rank, int value) {
            this.suit = suit;
            this.rank = rank;
            this.value = value;

            // Try to load a sprite like: "assets/hearts_A.png" or "assets/spades_10.png"
            try {
                String fileName = "assets/" + suit + "_" + rank + ".png";
                frontSprite = ImageIO.read(new File(fileName));
            } catch (Exception e) {
                frontSprite = null; // Stays null if file isn't found
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CoinsAndPoker game = new CoinsAndPoker();
            game.setVisible(true);
            game.setLocationRelativeTo(null);
        });
    }
}
