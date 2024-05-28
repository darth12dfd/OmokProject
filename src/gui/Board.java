package gui;

import client.Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Board 클래스는 게임 보드를 그래픽 인터페이스로 나타내는 JFrame을 상속합니다.
 * 게임 보드 크기와 셀 크기를 정의하고, 클라이언트와 색상 등의 설정을 초기화합니다.
 */
public class Board extends JFrame {
    private static final int SIZE = 15;  // 보드 크기 (15x15)
    private static final int CELL_SIZE = 40;  // 셀 크기 (픽셀 단위)
    private int[][] board;  // 게임 보드 상태를 저장하는 2차원 배열
    private Client client;  // 클라이언트 객체
    private Color ownColor;  // 자신의 돌 색상
    private Color opponentColor;  // 상대방의 돌 색상
    private String message;  // 윈도우 타이틀 메시지
    private boolean isOwnTurn;  // 현재 턴이 자신의 턴인지 여부

    /**
     * Board 생성자는 게임 보드의 기본 설정을 초기화하고 GUI 요소를 설정합니다.
     * @param client 클라이언트 객체
     * @param ownColor 자신의 돌 색상
     * @param opponentColor 상대방의 돌 색상
     * @param message 윈도우 타이틀 메시지
     */
    public Board(Client client, Color ownColor, Color opponentColor, String message) {
        this.client = client;
        this.ownColor = ownColor;
        this.opponentColor = opponentColor;
        this.message = message;
        this.board = new int[SIZE][SIZE];  // 보드 배열 초기화
        this.isOwnTurn = true;  // 초기 설정, 필요에 따라 변경
        setTitle(message);
        setSize(SIZE * CELL_SIZE + 50, SIZE * CELL_SIZE + 70);  // 윈도우 크기 설정
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  // 창 닫기 설정
        setLocationRelativeTo(null);  // 창 위치를 화면 중앙으로 설정

        BoardPanel boardPanel = new BoardPanel();  // 보드 패널 생성
        add(boardPanel);  // 보드 패널을 프레임에 추가

        setVisible(true);  // 프레임을 화면에 표시
    }

    /**
     * updateBoard 메서드는 게임 보드의 상태를 업데이트하고 화면을 다시 그립니다.
     * @param x 업데이트할 x 좌표
     * @param y 업데이트할 y 좌표
     * @param isOwnMove 자신의 움직임인지 여부
     */
    public void updateBoard(int x, int y, boolean isOwnMove) {
        board[x][y] = isOwnMove ? 1 : 2;  // 자신의 움직임이면 1, 상대의 움직임이면 2로 설정
        repaint();  // 보드를 다시 그리기
        if (isOwnMove) {
            isOwnTurn = false;  // 자신의 턴이 끝남
        } else {
            isOwnTurn = true;  // 상대의 턴이 끝남
        }
    }

    /**
     * BoardPanel 클래스는 게임 보드를 그리는 JPanel을 상속합니다.
     */
    private class BoardPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            drawBoard(g);  // 보드를 그리는 메서드 호출
            drawStones(g);  // 돌을 그리는 메서드 호출
        }

        /**
         * drawBoard 메서드는 보드의 격자를 그립니다.
         * @param g 그래픽 객체
         */
        private void drawBoard(Graphics g) {
            g.setColor(Color.BLACK);
            for (int i = 0; i < SIZE; i++) {
                g.drawLine(CELL_SIZE / 2, CELL_SIZE / 2 + i * CELL_SIZE, CELL_SIZE / 2 + (SIZE - 1) * CELL_SIZE, CELL_SIZE / 2 + i * CELL_SIZE);
                g.drawLine(CELL_SIZE / 2 + i * CELL_SIZE, CELL_SIZE / 2, CELL_SIZE / 2 + i * CELL_SIZE, CELL_SIZE / 2 + (SIZE - 1) * CELL_SIZE);
            }
        }

        /**
         * drawStones 메서드는 보드 위에 돌을 그립니다.
         * @param g 그래픽 객체
         */
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

        /**
         * BoardPanel 생성자는 마우스 클릭 이벤트를 추가하여 플레이어의 움직임을 처리합니다.
         */
        public BoardPanel() {
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (isOwnTurn) {
                        int x = e.getY() / CELL_SIZE;
                        int y = e.getX() / CELL_SIZE;
                        if (x >= 0 && x < SIZE && y >= 0 && y < SIZE && board[x][y] == 0) {
                            client.sendMove(x, y);  // 클라이언트로 움직임 전송
                        }
                    }
                }
            });
        }
    }
}
