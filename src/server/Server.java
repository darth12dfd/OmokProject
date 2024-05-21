package server;

import java.io.*;
import java.net.*;
import java.util.concurrent.locks.ReentrantLock;

public class Server {
    private ServerSocket serverSocket;
    private final int PORT = 12345;
    private int[][] board;
    private char currentPlayer = 'X';
    private ReentrantLock lock = new ReentrantLock();
    private PrintWriter outPlayer1;
    private PrintWriter outPlayer2;

    public void startServer() {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Server started on port " + PORT);

            board = new int[15][15];

            Socket player1 = serverSocket.accept();
            System.out.println("Player 1 connected");
            Socket player2 = serverSocket.accept();
            System.out.println("Player 2 connected");

            outPlayer1 = new PrintWriter(player1.getOutputStream(), true);
            outPlayer2 = new PrintWriter(player2.getOutputStream(), true);

            new Thread(new ClientHandler(player1, 'X', outPlayer2)).start();
            new Thread(new ClientHandler(player2, 'O', outPlayer1)).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class ClientHandler implements Runnable {
        private Socket socket;
        private char player;
        private BufferedReader in;
        private PrintWriter outOpponent;

        public ClientHandler(Socket socket, char player, PrintWriter outOpponent) {
            this.socket = socket;
            this.player = player;
            this.outOpponent = outOpponent;
        }

        @Override
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                String input;

                while ((input = in.readLine()) != null) {
                    String[] parts = input.split(" ");
                    int x = Integer.parseInt(parts[0]);
                    int y = Integer.parseInt(parts[1]);

                    lock.lock();
                    try {
                        if (board[x][y] == 0 && currentPlayer == player) {
                            board[x][y] = player == 'X' ? 1 : 2;
                            out.println(x + " " + y + " true");
                            outOpponent.println(x + " " + y + " false");

                            if (isWinner(x, y)) {
                                out.println("You win!");
                                outOpponent.println("You lose!");
                                break;
                            }

                            currentPlayer = (currentPlayer == 'X') ? 'O' : 'X';
                        } else {
                            out.println("Invalid move");
                        }
                    } finally {
                        lock.unlock();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private boolean isWinner(int x, int y) {
            return checkDirection(x, y, 1, 0) || // Horizontal
                    checkDirection(x, y, 0, 1) || // Vertical
                    checkDirection(x, y, 1, 1) || // Diagonal /
                    checkDirection(x, y, 1, -1);  // Diagonal \
        }

        private boolean checkDirection(int x, int y, int dx, int dy) {
            int count = 1;
            for (int i = 1; i < 5; i++) {
                int nx = x + i * dx;
                int ny = y + i * dy;
                if (nx >= 0 && nx < 15 && ny >= 0 && ny < 15 && board[nx][ny] == (currentPlayer == 'X' ? 1 : 2)) {
                    count++;
                } else {
                    break;
                }
            }
            for (int i = 1; i < 5; i++) {
                int nx = x - i * dx;
                int ny = y - i * dy;
                if (nx >= 0 && nx < 15 && ny >= 0 && ny < 15 && board[nx][ny] == (currentPlayer == 'X' ? 1 : 2)) {
                    count++;
                } else {
                    break;
                }
            }
            return count >= 5;
        }
    }
}
