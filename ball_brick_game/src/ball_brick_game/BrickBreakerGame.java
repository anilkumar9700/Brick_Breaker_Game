package ball_brick_game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class BrickBreakerGame extends JFrame {

    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private static final int PADDLE_WIDTH = 100;
    private static final int PADDLE_HEIGHT = 10;
    private static final int BALL_RADIUS = 10;
    private static final int PADDLE_SPEED = 10;
    private static final int BALL_SPEED = 5;
    private static final int BRICK_WIDTH = 70;
    private static final int BRICK_HEIGHT = 20;
    private static final int NUM_BRICKS = 40;

    private int paddleX = WIDTH / 2 - PADDLE_WIDTH / 2;
    private int paddleY = HEIGHT - PADDLE_HEIGHT - 50;
    private int ballX = WIDTH / 2;
    private int ballY = HEIGHT - 2 * PADDLE_HEIGHT - BALL_RADIUS - 5;
    private int ballSpeedX = BALL_SPEED;
    private int ballSpeedY = -BALL_SPEED;

    private boolean leftKeyPressed = false;
    private boolean rightKeyPressed = false;

    private ArrayList<Rectangle> bricks;

    private boolean gameOver = false;

    public BrickBreakerGame() {
        setTitle("Brick Breaker Game");
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        bricks = new ArrayList<>();

        // Create bricks
        int brickX = 0;
        int brickY = 50;
        for (int i = 0; i < NUM_BRICKS; i++) {
            bricks.add(new Rectangle(brickX, brickY, BRICK_WIDTH, BRICK_HEIGHT));
            brickX += BRICK_WIDTH;
            if (brickX >= WIDTH) {
                brickX = 0;
                brickY += BRICK_HEIGHT;
            }
        }

        // Create a panel for the game
        JPanel gamePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawGame(g);
            }
        };
        gamePanel.setBackground(Color.BLACK);
        setContentPane(gamePanel);

        // Add key listeners for paddle movement
        gamePanel.setFocusable(true);
        gamePanel.requestFocusInWindow();
        gamePanel.addKeyListener(new KeyHandler());

        // Timer for game loop
        Timer timer = new Timer(1000 / 60, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!gameOver) {
                    update();
                    gamePanel.repaint();
                }
            }
        });
        timer.start();
    }

    private void update() {
        // Update paddle position
        if (leftKeyPressed && paddleX > 0) {
            paddleX -= PADDLE_SPEED;
        }
        if (rightKeyPressed && paddleX < WIDTH - PADDLE_WIDTH) {
            paddleX += PADDLE_SPEED;
        }

        // Update ball position
        ballX += ballSpeedX;
        ballY += ballSpeedY;

        // Ball-wall collisions
        if (ballX < BALL_RADIUS || ballX > WIDTH - BALL_RADIUS) {
            ballSpeedX *= -1;
        }
        if (ballY < BALL_RADIUS) {
            ballSpeedY *= -1;
        }

        // Ball-paddle collision
        if (ballY + BALL_RADIUS >= paddleY &&
            ballX + BALL_RADIUS >= paddleX &&
            ballX - BALL_RADIUS <= paddleX + PADDLE_WIDTH) {
            ballSpeedY *= -1;
        }

        // Ball-brick collision
        Rectangle ballRect = new Rectangle(ballX - BALL_RADIUS, ballY - BALL_RADIUS, 2 * BALL_RADIUS, 2 * BALL_RADIUS);
        for (int i = 0; i < bricks.size(); i++) {
            Rectangle brickRect = bricks.get(i);
            if (ballRect.intersects(brickRect)) {
                bricks.remove(i);
                ballSpeedY *= -1;
                if (bricks.isEmpty()) {
                    gameOver = true;
                    JOptionPane.showMessageDialog(this, "Congratulations! You broke all the bricks!");
                    restartGame();
                }
                break;
            }
        }

        // Ball falls out of the bottom
        if (ballY > HEIGHT) {
            gameOver = true;
            int choice = JOptionPane.showConfirmDialog(this, "Game Over!\nDo you want to restart the game?", "Game Over", JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                restartGame();
            } else {
                System.exit(0);
            }
        }
    }

    private void restartGame() {
        paddleX = WIDTH / 2 - PADDLE_WIDTH / 2;
        ballX = WIDTH / 2;
        ballY = HEIGHT - 2 * PADDLE_HEIGHT - BALL_RADIUS - 5;
        ballSpeedX = BALL_SPEED;
        ballSpeedY = -BALL_SPEED;

        bricks.clear();
        int brickX = 0;
        int brickY = 50;
        for (int i = 0; i < NUM_BRICKS; i++) {
            bricks.add(new Rectangle(brickX, brickY, BRICK_WIDTH, BRICK_HEIGHT));
            brickX += BRICK_WIDTH;
            if (brickX >= WIDTH) {
                brickX = 0;
                brickY += BRICK_HEIGHT;
            }
        }

        gameOver = false;
    }

    private void drawGame(Graphics g) {
        // Draw paddle
        g.setColor(Color.RED);
        g.fillRect(paddleX, paddleY, PADDLE_WIDTH, PADDLE_HEIGHT);

        // Draw ball
        g.setColor(Color.RED);
        g.fillOval(ballX - BALL_RADIUS, ballY - BALL_RADIUS, 2 * BALL_RADIUS, 2 * BALL_RADIUS);

        // Draw bricks
        g.setColor(Color.GREEN);
        for (Rectangle brick : bricks) {
            g.fillRect(brick.x, brick.y, brick.width, brick.height);
        }
    }

    private class KeyHandler extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                leftKeyPressed = true;
            }
            if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                rightKeyPressed = true;
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                leftKeyPressed = false;
            }
            if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                rightKeyPressed = false;
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new BrickBreakerGame().setVisible(true);
            }
        });
    }
}
