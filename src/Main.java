import client.Client;
import server.Server;

public class Main {
    public static void main(String[] args) {
        // 서버 실행
        Thread serverThread = new Thread(() -> {
            Server server = new Server();
            //server.start();
        });
        serverThread.start();

        // 클라이언트 실행
        Thread client1Thread = new Thread(() -> {
            Client client1 = new Client();
        });
        client1Thread.start();

        Thread client2Thread = new Thread(() -> {
            Client client2 = new Client();
        });
        client2Thread.start();
    }
}