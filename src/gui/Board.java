package gui;

import client.Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Board extends JFrame {
    private static final int SIZE = 15;
    private static final int CELL_SIZE = 40;
    private int[][] board;
    private Client client;
    private Color ownColor;
    private Color opponentColor;
    private String message;
    private boolean isOwnTurn;

    public Board(Client client, Color ownColor, Color opponentColor, String message) {
        this.client = client;
        this.ownColor = ownColor;
        this.opponentColor = opponentColor;
        this.message = message;
        this.board = new int[SIZE][SIZE];
        this.isOwnTurn = true;  // 초기 설정, 필요에 따라 변경
        setTitle(message);
        setSize(SIZE * CELL_SIZE + 50, SIZE * CELL_SIZE + 70);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        BoardPanel boardPanel = new BoardPanel();
        add(boardPanel);

        setVisible(true);
    }

    public void updateBoard(int x, int y, boolean isOwnMove) {
        board[x][y] = isOwnMove ? 1 : 2;
        repaint();
        if (isOwnMove) {
            isOwnTurn = false;
        } else {
            isOwnTurn = true;
        }
    }

    private class BoardPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            drawBoard(g);
            drawStones(g);
        }

        private void drawBoard(Graphics g) {
            g.setColor(Color.BLACK);
            for (int i = 0; i < SIZE; i++) {
                g.drawLine(CELL_SIZE / 2, CELL_SIZE / 2 + i * CELL_SIZE, CELL_SIZE / 2 + (SIZE - 1) * CELL_SIZE, CELL_SIZE / 2 + i * CELL_SIZE);
                g.drawLine(CELL_SIZE / 2 + i * CELL_SIZE, CELL_SIZE / 2, CELL_SIZE / 2 + i * CELL_SIZE, CELL_SIZE / 2 + (SIZE - 1) * CELL_SIZE);
            }
        }

        private void drawStones(Graphics g) {
            for (int i = 0; i < SIZE; i++) {
                for (int j = 0; j < SIZE; j++) {
                    if (board[i][j] == 1) {
                        g.setColor(ownColor);
                        g.fillOval(j * CELL_SIZE + CELL_SIZE / 4, i * CELL_SIZE + CELL_SIZE / 4, CELL_SIZE / 2, CELL_SIZE / 2);
                    } else if (board[i][j] == 2) {
                        g.setColor(opponentColor);
                        g.fillOval(j * CELL_SIZE + CELL_SIZE / 4, i * CELL_SIZE + CELL_SIZE / 4, CELL_SIZE / 2, CELL_SIZE / 2);
                    }
                }
            }
        }

        public BoardPanel() {
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (isOwnTurn) {
                        int x = e.getY() / CELL_SIZE;
                        int y = e.getX() / CELL_SIZE;
                        if (x >= 0 && x < SIZE && y >= 0 && y < SIZE && board[x][y] == 0) {
                            client.sendMove(x, y);
                        }
                    }
                }
            });
        }
    }
}
