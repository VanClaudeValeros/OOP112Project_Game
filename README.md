# Texas Hold'em Poker: Classic Casino Edition

A fully interactive, desktop-based Texas Hold'em Poker game built entirely in Java using Swing. The platform delivers an immersive casino experience featuring animated card dealing, customized vector graphics rendering, ambient soundtrack configuration, and real-time betting mechanics against automated CPU opponents.

📊 GitHub Repository: https://github.com/vanclaudevaleros/oop112project_game

---

## Game Overview
This project brings the complete lifecycle of a classic casino Texas Hold'em tournament to your desktop. Players compete sequentially through traditional betting streets to assemble the optimal 5-card hand or clean out the table by strategically inducing folds.

### Core Architecture Components
* The Betting Engine: Implements standard poker rule sets with automatic small/big blind tracking, pot management, and classic table operations including Fold, Check, Call, and Raise inputs.
* Vector Canvas Rendering: Features custom drawing logic that renders casino felt patterns, radial light vignettes, and card layouts mathematically through standard 2D vector drawing rather than utilizing external image files.
* Physics-Based Card Animations: Card layouts are distributed from the deck coordinates to player hands using linear interpolation for clean animation transitions.
* Hand Evaluation Framework: Employs a combinatorial subset generator (7 choose 5) to automatically score and break ties for all hand categories sequentially—ranging from a simple High Card up to a Royal Flush.

---

## Gameplay Mechanics

### 1. Game Flow Phases
* Pre-Flop: Every competitor receives two concealed hole cards. Forced blinds automatically seed the initial pot structure.
* The Flop: The dealer distributes three mutual community cards face-up to the center of the board.
* The Turn: A fourth community card is revealed to alter the table's probabilistic landscape.
* The River: The fifth and ultimate board card is revealed, freezing the final combinations.
* Showdown: Active remaining players reveal their hole cards; the system determines the highest 5-card combination and allocates the chips to the winner.

### 2. Available Strategic Actions
* Fold: Forfeit your hole cards immediately to halt chip losses for the current deal.
* Check: Pass the action down the line without expanding your bet (only valid if the current outstanding bet threshold equals zero).
* Call: Match the highest existing bet on the board to remain active in the hand.
* Raise: Amplify the current high bet stakes by an increment of +50 chips to apply strategic pressure on opponents.

---

## Technical Highlights
* Object-Oriented Architecture: Strictly modularized design structures decoupled across clean game abstractions including Player, Card, Deck, and AnimatedCard classes.
* Decoupled SFX Threads: Utilizes concurrent worker threads to stream uncompressed .wav audio clips through the Java Sound API simultaneously without bottlenecking or freezing the primary GUI event loop.
* Mathematical Canvas Texturing: Background felt patterns generate faint translucent white diamond configurations and a custom spade logo watermark vector generated dynamically on the canvas buffer.

---

## Getting Started

### Prerequisites
* Java Development Kit (JDK): Version 8 or higher.

### Compilation and Execution
To assemble and execute the application locally via your command terminal, navigate to the source directory and invoke the following commands:

```bash
# Clone the repository
git clone [https://github.com/vanclaudevaleros/oop112project_game.git](https://github.com/vanclaudevaleros/oop112project_game.git)

# Navigate to the source folder
cd oop112project_game/src

# Compile the source Java files
javac PokerGame.java

# Execute the application
java PokerGame
