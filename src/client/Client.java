package client;

import gui.Board;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;

/**
 * Client 클래스는 서버와의 통신을 처리하고, GUI 보드를 업데이트하는 클라이언트 애플리케이션을 나타냅니다.
 */
public class Client {
    private String playerName;  // 플레이어 이름
    private Socket socket;  // 서버와의 연결 소켓
    private BufferedReader in;  // 서버로부터의 입력 스트림
    private PrintWriter out;  // 서버로의 출력 스트림
    private final String SERVER_ADDRESS = "localhost";  // 서버 주소
    private final int SERVER_PORT = 12345;  // 서버 포트 번호
    private Board board;  // GUI 보드 객체

    /**
     * Client 생성자는 플레이어 이름을 설정합니다.
     * @param playerName 플레이어 이름
     */
    public Client(String playerName) {
        this.playerName = playerName;
    }

    /**
     * startClient 메서드는 서버에 연결하고, 보드를 초기화하며, 서버로부터의 메시지를 수신하는 스레드를 시작합니다.
     */
    public void startClient() {
        try {
            // 서버에 연결
            socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            System.out.println(playerName + " connected to server");

            // 자신과 상대방의 돌 색상 지정 (임의로 검은색과 흰색 사용)
            Color ownColor = playerName.equals("Player1") ? Color.BLACK : Color.WHITE;
            Color opponentColor = playerName.equals("Player1") ? Color.WHITE : Color.BLACK;

            // 보드 객체 생성 및 초기화
            board = new Board(this, ownColor, opponentColor, playerName);
            new Thread(new ServerListener()).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * sendMove 메서드는 서버로 움직임 정보를 전송합니다.
     * @param x 움직임의 x 좌표
     * @param y 움직임의 y 좌표
     */
    public void sendMove(int x, int y) {
        out.println(x + " " + y);
    }

    /**
     * ServerListener 클래스는 서버로부터의 메시지를 수신하고, 보드를 업데이트하는 Runnable 구현 클래스입니다.
     */
    private class ServerListener implements Runnable {
        @Override
        public void run() {
            try {
                String response;
                // 서버로부터 메시지를 계속 수신하여 처리
                while ((response = in.readLine()) != null) {
                    String[] parts = response.split(" ");
                    if (parts.length == 3) {
                        int x = Integer.parseInt(parts[0]);
                        int y = Integer.parseInt(parts[1]);
                        boolean isOwnMove = Boolean.parseBoolean(parts[2]);
                        // 보드를 업데이트
                        board.updateBoard(x, y, isOwnMove);
                    } else {
                        // 메시지를 다이얼로그로 표시
                        JOptionPane.showMessageDialog(board, response);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
