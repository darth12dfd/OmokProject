import client.Client;
import org.junit.jupiter.api.Test;
import server.Server;

class MainTest {

    @Test
    void main() {
        Thread serverThread = new Thread(() -> {
            Server server = new Server();
            server.startServer();
        });
        serverThread.start();

        // 클라이언트 실행
        Thread client1Thread = new Thread(() -> {
            Client client1 = new Client("Player1");
        });
        client1Thread.start();

        Thread client2Thread = new Thread(() -> {
            Client client2 = new Client("Player2");
        });
        client2Thread.start();
    }
}