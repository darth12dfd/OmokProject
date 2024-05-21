package client;

import gui.Board;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;

public class Client {
    private String playerName;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private final String SERVER_ADDRESS = "localhost";
    private final int SERVER_PORT = 12345;
    private Board board;

    public Client(String playerName) {
        this.playerName = playerName;
    }

    public void startClient() {
        try {
            socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            System.out.println(playerName + " connected to server");

            // 자신과 상대방의 돌 색상 지정 (임의로 흰색과 검은색 사용)
            Color ownColor = Color.BLACK;
            Color opponentColor = Color.WHITE;

            board = new Board(this, ownColor, opponentColor, playerName);
            new Thread(new ServerListener()).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMove(int x, int y) {
        out.println(x + " " + y);
    }

    private class ServerListener implements Runnable {
        @Override
        public void run() {
            try {
                String response;
                while ((response = in.readLine()) != null) {
                    String[] parts = response.split(" ");
                    if (parts.length == 3) {
                        int x = Integer.parseInt(parts[0]);
                        int y = Integer.parseInt(parts[1]);
                        boolean isOwnMove = Boolean.parseBoolean(parts[2]);
                        board.updateBoard(x, y, isOwnMove);
                    } else {
                        JOptionPane.showMessageDialog(board, response);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
