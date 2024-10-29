import javax.swing.*;
import java.util.Random;
import java.awt.*;
import java.awt.event.*;

public class GamePanel extends JPanel implements ActionListener {

    static final int SCREEN_WIDTH = 1000;
    static final int SCREEN_HEIGHT = 950;
    static final int UNIT_SIZE = 50;
    static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / (UNIT_SIZE * UNIT_SIZE);
    static final int DELAY = 50;
    final int[] x = new int[GAME_UNITS];
    final int[] y = new int[GAME_UNITS];
    int bodyParts = 4;
    int applesEaten;
    int highestEaten;
    int appleX;
    int appleY;
    char direction = 'R';
    boolean running = false;
    boolean waiting = true;
    Timer timer;
    Random random;

    GamePanel() {
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH + UNIT_SIZE * 2, SCREEN_HEIGHT + UNIT_SIZE * 2));
        this.setBackground(new Color(87, 138, 52));
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        initialize();
    }

    public void initialize() {
        applesEaten = 0;
        bodyParts = 4;
        x[0] = UNIT_SIZE * 7;
        y[0] = (SCREEN_HEIGHT / 2) / UNIT_SIZE * UNIT_SIZE + UNIT_SIZE;
        for (int i = 1; i < bodyParts; i++) {
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
        timer = new Timer(DELAY, this);
        timer.start();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        drawGame(g);
        g.setColor(Color.red);
        g.fillRect(appleX, appleY, UNIT_SIZE, UNIT_SIZE);
        if (running || waiting) {
            g.setColor(Color.red);
            g.setFont(new Font("Consolas", Font.BOLD, 40));
            g.drawString("Score: " + applesEaten + " Highest: " + highestEaten, UNIT_SIZE, g.getFont().getSize());
        }
        else {
            gameOver(g);
        }
    }

    private void drawGame(Graphics g) {
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
        if (x[0] >= SCREEN_WIDTH - UNIT_SIZE) {
            running = false;
        }
        if (y[0] < UNIT_SIZE){
            running = false;
        }
        if (y[0] >= SCREEN_HEIGHT - UNIT_SIZE) {
            running = false;
        }
        if (!running) {
            timer.stop();
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
            newApple();
        }
    }

    public void gameOver(Graphics g) {
        if (applesEaten > highestEaten) {
            highestEaten = applesEaten;
        }
        g.setColor(Color.red);
        g.setFont(new Font("Consolas", Font.BOLD, 40));
        g.drawString("Score: " + applesEaten + " Highest: " + highestEaten, UNIT_SIZE, g.getFont().getSize());
        g.setColor(Color.red);
        g.setFont(new Font("Consolas", Font.BOLD, 75));
        FontMetrics metrics2 = getFontMetrics(g.getFont());
        g.drawString("Game Over", (SCREEN_WIDTH - metrics2.stringWidth("Game Over")) / 2 + UNIT_SIZE, SCREEN_HEIGHT/2);
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkApple();
            checkCollisions();
        }
        repaint();
    }

    public class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            if (waiting) {
                startGame();
                initialize();
            }
            switch(e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    if (direction != 'R') {
                        direction = 'L';
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if (direction != 'L') {
                        direction = 'R';
                    }
                    break;
                case KeyEvent.VK_UP:
                    if(direction != 'D') {
                        direction = 'U';
                    }
                    break;
                case KeyEvent.VK_DOWN:
                    if(direction != 'U') {
                        direction = 'D';
                    }
                    break;
            }
        }
    }
}