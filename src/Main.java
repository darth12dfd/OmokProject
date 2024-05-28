import client.Client;
import server.Server;

public class Main {
    public static void main(String[] args) {
        // 서버 시작 (서버 스레드 생성 및 시작)
        new Thread(() -> {
            Server server = new Server();
            server.startServer();
        }).start();

        // 첫 번째 클라이언트 시작 (클라이언트 스레드 생성 및 시작(흑돌))
        new Thread(() -> {
            try {
                // 잠시 대기하여 서버가 시작될 시간을 줍니다.
                Thread.sleep(1000);
                Client client1 = new Client("Player1");
                client1.startClient();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        // 두 번째 클라이언트 시작 (클라이언트 스레드 생성 및 시작(백돌))
        new Thread(() -> {
            try {
                // 잠시 대기하여 첫 번째 클라이언트가 연결될 시간을 줍니다.
                Thread.sleep(2000);
                Client client2 = new Client("Player2");
                client2.startClient();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
