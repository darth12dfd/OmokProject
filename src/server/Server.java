package server;

import java.io.*;
import java.net.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Server 클래스는 게임 서버를 관리합니다.
 * 서버 소켓을 생성하고 클라이언트 연결을 처리하며, 게임의 상태를 관리합니다.
 */
public class Server {
    private ServerSocket serverSocket;  // 서버 소켓 객체
    private final int PORT = 12345;  // 서버 포트 번호
    private int[][] board;  // 게임 보드 상태를 저장하는 2차원 배열
    private char currentPlayer = 'X';  // 현재 턴의 플레이어 ('X' 또는 'O')
    private ReentrantLock lock = new ReentrantLock();  // 동시성 제어를 위한 락
    private PrintWriter outPlayer1;  // 첫 번째 플레이어의 출력 스트림
    private PrintWriter outPlayer2;  // 두 번째 플레이어의 출력 스트림

    /**
     * startServer 메서드는 서버를 시작하고, 클라이언트 연결을 대기합니다.
     */
    public void startServer() {
        try {
            // 서버 소켓 생성 및 포트 바인딩
            serverSocket = new ServerSocket(PORT);
            System.out.println("Server started on port " + PORT);

            // 게임 보드 초기화
            board = new int[15][15];

            // 첫 번째 플레이어 연결 대기
            Socket player1 = serverSocket.accept();
            System.out.println("Player 1 connected");
            // 두 번째 플레이어 연결 대기
            Socket player2 = serverSocket.accept();
            System.out.println("Player 2 connected");

            // 두 플레이어의 출력 스트림 설정
            outPlayer1 = new PrintWriter(player1.getOutputStream(), true);
            outPlayer2 = new PrintWriter(player2.getOutputStream(), true);

            // 각 플레이어에 대한 클라이언트 핸들러 스레드 시작
            new Thread(new ClientHandler(player1, 'X', outPlayer2, outPlayer1)).start();
            new Thread(new ClientHandler(player2, 'O', outPlayer1, outPlayer2)).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * ClientHandler 클래스는 클라이언트의 요청을 처리합니다.
     */
    private class ClientHandler implements Runnable {
        private Socket socket;  // 클라이언트 소켓
        private char player;  // 플레이어 ('X' 또는 'O')
        private BufferedReader in;  // 클라이언트 입력 스트림
        private PrintWriter outOpponent;  // 상대방 플레이어의 출력 스트림
        private PrintWriter outSelf;  // 자신의 출력 스트림

        /**
         * ClientHandler 생성자는 클라이언트 소켓과 플레이어 정보를 초기화합니다.
         * @param socket 클라이언트 소켓
         * @param player 플레이어 ('X' 또는 'O')
         * @param outOpponent 상대방 플레이어의 출력 스트림
         * @param outSelf 자신의 출력 스트림
         */
        public ClientHandler(Socket socket, char player, PrintWriter outOpponent, PrintWriter outSelf) {
            this.socket = socket;
            this.player = player;
            this.outOpponent = outOpponent;
            this.outSelf = outSelf;
        }

        @Override
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String input;

                // 클라이언트로부터 메시지를 수신하고 처리
                while ((input = in.readLine()) != null) {
                    String[] parts = input.split(" ");
                    int x = Integer.parseInt(parts[0]);
                    int y = Integer.parseInt(parts[1]);

                    lock.lock();  // 락을 사용하여 동시성 제어
                    try {
                        // 보드 상태 업데이트 및 현재 플레이어 확인
                        if (board[x][y] == 0 && currentPlayer == player) {
                            board[x][y] = player == 'X' ? 1 : 2;
                            outSelf.println(x + " " + y + " true");
                            outOpponent.println(x + " " + y + " false");

                            // 승리 조건 확인
                            if (isWinner(x, y)) {
                                outSelf.println("You win!");
                                outOpponent.println("You lose!");
                                break;  // 게임 종료
                            }

                            // 턴을 상대방에게 넘김
                            currentPlayer = (currentPlayer == 'X') ? 'O' : 'X';
                        } else {
                            outSelf.println("Invalid move");
                        }
                    } finally {
                        lock.unlock();  // 락 해제
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * isWinner 메서드는 주어진 좌표를 기준으로 승리 조건을 확인합니다.
         * @param x 확인할 x 좌표
         * @param y 확인할 y 좌표
         * @return 승리 조건 충족 여부
         */
        private boolean isWinner(int x, int y) {
            return checkDirection(x, y, 1, 0) || // 가로 방향 확인
                    checkDirection(x, y, 0, 1) || // 세로 방향 확인
                    checkDirection(x, y, 1, 1) || // 대각선 방향 확인 (/)
                    checkDirection(x, y, 1, -1);  // 대각선 방향 확인 (\)
        }

        /**
         * checkDirection 메서드는 특정 방향으로 연속된 돌의 개수를 확인합니다.
         * @param x 시작 x 좌표
         * @param y 시작 y 좌표
         * @param dx x 방향 증가 값
         * @param dy y 방향 증가 값
         * @return 해당 방향으로 연속된 돌의 개수가 5개 이상인지 여부
         */
        private boolean checkDirection(int x, int y, int dx, int dy) {
            int count = 1;
            // 지정된 방향으로 돌 개수 카운트
            for (int i = 1; i < 5; i++) {
                int nx = x + i * dx;
                int ny = y + i * dy;
                if (nx >= 0 && nx < 15 && ny >= 0 && ny < 15 && board[nx][ny] == (player == 'X' ? 1 : 2)) {
                    count++;
                } else {
                    break;
                }
            }
            // 반대 방향으로 돌 개수 카운트
            for (int i = 1; i < 5; i++) {
                int nx = x - i * dx;
                int ny = y - i * dy;
                if (nx >= 0 && nx < 15 && ny >= 0 && ny < 15 && board[nx][ny] == (player == 'X' ? 1 : 2)) {
                    count++;
                } else {
                    break;
                }
            }
            return count >= 5;
        }
    }
}
