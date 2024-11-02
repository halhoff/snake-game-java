import javax.swing.*;
import java.util.Random;
import java.util.Queue;
import java.util.LinkedList;
import java.util.Set;
import java.awt.*;
import java.awt.event.*;

public class GamePanel extends JPanel implements ActionListener {
    static final int SCREEN_WIDTH = 1000;
    static final int SCREEN_HEIGHT = 950;
    static final int UNIT_SIZE = 50;
    static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / (UNIT_SIZE * UNIT_SIZE);
    static final int DELAY = 75;
    final int[] x = new int[GAME_UNITS];
    final int[] y = new int[GAME_UNITS];
    int bodyParts = 4;
    int applesEaten;
    int highestEaten;
    int appleX;
    int appleY;
    char direction;
    Queue<Character> directionQueue = new LinkedList<>();

    boolean firstPlay = true;
    boolean running = false;
    boolean waiting = true;
    boolean gameOverToggle = false;
    boolean pause = false;

    PauseScreen pauseScreen;
    Timer gameTimer;
    Random random;

    GamePanel() {
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH + UNIT_SIZE * 2, SCREEN_HEIGHT + UNIT_SIZE * 2));
        this.setBackground(new Color(87, 138, 52));
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        pauseScreen = new PauseScreen();
        pauseScreen.setVisible(false);
        this.add(pauseScreen);
        initialize();
    }

    public void initialize() {
        direction = 'R';
        directionQueue.clear();
        applesEaten = 0;
        bodyParts = 4;
        x[0] = UNIT_SIZE * 7;
        y[0] = (SCREEN_HEIGHT / 2) / UNIT_SIZE * UNIT_SIZE + UNIT_SIZE;
        for (int i = 1; i < bodyParts; ++i) {
            x[i] = x[0] - i * UNIT_SIZE;
            y[i] = y[0];
        }
        appleX = x[0] + UNIT_SIZE * 7;
        appleY = y[0];
        repaint();
    }

    public void startGame() {
        waiting = false;
        running = true;
        firstPlay = false;
        gameTimer = new Timer(DELAY, this);
        gameTimer.start();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        drawGame(g);
        g.setColor(Color.red);
        g.fillRect(appleX, appleY, UNIT_SIZE, UNIT_SIZE);
        g.setColor(Color.red);
        g.setFont(new Font("Consolas", Font.BOLD, 40));
        g.drawString("Score: " + applesEaten + " Highest: "
                + highestEaten, UNIT_SIZE, g.getFont().getSize());
        if (gameOverToggle) {
            gameOver(g);
        }
    }

    public void drawGame(Graphics g) {
        for (int i = 1; i < SCREEN_WIDTH / UNIT_SIZE + 1; ++i) {
            for (int j = 1; j < SCREEN_HEIGHT / UNIT_SIZE + 1; ++j) {
                if ((i + j) % 2 == 0) {
                    g.setColor(new Color(167, 218, 72));
                }
                else {
                    g.setColor(new Color(142, 205, 57));
                }
                g.fillRect(i * UNIT_SIZE, j * UNIT_SIZE, UNIT_SIZE, UNIT_SIZE);
            }
        }
        for (int i = 0; i < bodyParts; ++i) {
            if (i == 0) {
                g.setColor(Color.blue);
                g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
            }
            else {
                int shade = 255 - (int)(150 * i / bodyParts);
                g.setColor(new Color (0, 0, shade));
                g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
            }
        }
    }

    public void checkCollisions() {
        for (int i = bodyParts; i > 0; --i) {
            if (x[0] == x[i] && y[0] == y[i]) {
                running = false;
                break;
            }
        }
        if (x[0] < UNIT_SIZE) {
            running = false;
        }
        if (x[0] > SCREEN_WIDTH) {
            running = false;
        }
        if (y[0] < UNIT_SIZE){
            running = false;
        }
        if (y[0] > SCREEN_HEIGHT) {
            running = false;
        }
        if (!running) {
            waiting = true;
            gameOverToggle = true;
            gameTimer.stop();
        }
    }

    public void newApple() {
        boolean appleOnSnake = true;
        while (appleOnSnake) {
            appleOnSnake = false;
            appleX = (random.nextInt((int)((SCREEN_WIDTH - UNIT_SIZE * 2) / UNIT_SIZE)) + 1) * UNIT_SIZE;
            appleY = (random.nextInt((int)((SCREEN_HEIGHT - UNIT_SIZE * 2) / UNIT_SIZE)) + 1) * UNIT_SIZE;
            for (int i = 0; i < bodyParts; ++i) {
                if (x[i] == appleX && y[i] == appleY) {
                    appleOnSnake = true;
                    break;
                }
            }
        }
    }

    public void move() {
        if (!running) return;
        for (int i = bodyParts; i > 0; --i) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }
        if (!directionQueue.isEmpty()) {
            char newDirection = directionQueue.poll();
            if ((direction == 'U' && newDirection != 'D') ||
                    (direction == 'D' && newDirection != 'U') ||
                    (direction == 'L' && newDirection != 'R') ||
                    (direction == 'R' && newDirection != 'L'))
                direction = newDirection;
        }
        switch(direction) {
            case 'U':
                y[0] = y[0] - UNIT_SIZE;
                break;
            case 'D':
                y[0] = y[0] + UNIT_SIZE;
                break;
            case 'L':
                x[0] = x[0] - UNIT_SIZE;
                break;
            case 'R':
                x[0] = x[0] + UNIT_SIZE;
                break;
        }
    }

    public void checkApple() {
        if ((x[0] == appleX) && (y[0] == appleY)) {
            ++bodyParts;
            ++applesEaten;
            if (applesEaten > highestEaten) {
                highestEaten = applesEaten;
            }
            newApple();
        }
    }

    public void gameOver(Graphics g) {
        g.setColor(Color.red);
        Font statFont = new Font("Consolas", Font.BOLD, 40);
        Font bigFont = new Font("Consolas", Font.BOLD, 75);
        Font smallFont = new Font("Consolas", Font.BOLD, 20);
        FontMetrics bigMetrics = getFontMetrics(bigFont);
        FontMetrics smallMetrics = getFontMetrics(smallFont);
        g.setFont(statFont);
        g.drawString("Score: " + applesEaten + " Highest: " + highestEaten, UNIT_SIZE, g.getFont().getSize());
        g.setColor(Color.red);
        g.setFont(bigFont);
        g.drawString("Game Over", (SCREEN_WIDTH - bigMetrics.stringWidth("Game Over"))
                / 2 + UNIT_SIZE, SCREEN_HEIGHT / 2);
        g.setFont(smallFont);
        g.drawString("press ENTER to restart", (SCREEN_WIDTH - smallMetrics.stringWidth("press ENTER to restart"))
                / 2 + UNIT_SIZE, SCREEN_HEIGHT / 2 + UNIT_SIZE);
    }

    @Override public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkApple();
            checkCollisions();
        }
        repaint();
    }

    public class MyKeyAdapter extends KeyAdapter {
        @Override public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                pause = !pause;
                pauseScreen.setVisible(pause);
                if (!firstPlay) {
                    if (pause) gameTimer.stop();
                    else gameTimer.start();
                    directionQueue.clear();
                }
            }

            final Set<Integer> startKeyCodes = Set.of(
                    KeyEvent.VK_RIGHT,
                    KeyEvent.VK_UP,
                    KeyEvent.VK_DOWN
            );
            if (!running && !firstPlay && e.getKeyCode() == KeyEvent.VK_ENTER) {
                waiting = true;
                gameOverToggle = false;
                initialize();
            }
            if (!running && !gameOverToggle && waiting && !pause && startKeyCodes.contains(e.getKeyCode())) {
                waiting = false;
                startGame();
                initialize();
            }
            char newDirection = direction;
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT -> newDirection = 'L';
                case KeyEvent.VK_RIGHT -> newDirection = 'R';
                case KeyEvent.VK_UP -> newDirection = 'U';
                case KeyEvent.VK_DOWN -> newDirection = 'D';
            }
            if (directionQueue.size() < 3) {
                directionQueue.offer(newDirection);
            }
        }
    }
}