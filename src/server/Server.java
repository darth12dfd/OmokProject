package server;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;

import java.net.Socket;
import javax.swing.JOptionPane;

class Omok_Board extends Canvas {

        public static final int BLACK = 1, WHITE = -1;
        public static final int size = 30; // 한 칸 크기
        public static final int num = 20; // 놓을 수 있는 돌 최대 갯수
        public static final int x = 30; // 시작 위치 x
        public static final int y = 30; // 시작 위치 y
        public static final int width = 30; // 돌 크기 : 가로
        public static final int height = 30; // 돌 크기 : 세로

        // true이면 사용자가 돌을 놓을 수 있는 상태를 의미하고,
        // false이면 사용자가 돌을 놓을 수 없는 상태를 의미한다.
        private int color; // 서버 돌 색
        private int client_color; // 클라이언트 돌 색
        private String info = "접속 대기"; // 출력 스트링
        private String str_color; // 서버의 돌색 출력 스트링
        private boolean enable = false; // 오목판 활성화 여부를 저장
        private PrintWriter writer; // 상대편에게 메시지를 전달하기 위한 스트림
        private int stone[][] = new int[num][num]; // 돌 놓은 곳 저장



        Omok_Board(){
                this.setVisible(true);
                this.setBackground(new Color(200, 200, 100));
                addMouseListener(new MouseAdapter() {
                        public void mousePressed(MouseEvent me) { // 마우스를 누르면
                                if(!enable) { // 오목판이 비활성화일 경우
                                        return;
                                }

                                else if(me.getX()>x+size*(num-1) || me.getY()>y+size*(num-1)) { // 오목판 범위를 벗어 났을 경우(아래, 오른쪽)
                                        return;
                                }

                                else if(me.getX()<(x-size/2)|| me.getY()<(y-size/2)) // 오목판 범위를 벗어 났을 경우(위, 왼쪽)
                                        return;
                                else if(stone[(me.getX()-x+size/2)/size][(me.getY()-y+size/2)/size] != 0) // 돌이 놓여 있으면
                                        return;
                                else { // 돌 놓음
                                        stone[(me.getX()-x+size/2)/size][(me.getY()-y+size/2)/size] = color;
                                }

                                // 클라이언트에게 돌 놓은 위치를 전송
                                writer.println("[STONE]" + (me.getX()-x+size/2)/size + "," + (me.getY()-y+size/2)/size);

                                if(check(color)==true) { // 서버 오목 체크
                                        repaint();
                                        JOptionPane.showMessageDialog(null, "내가 이겼다"); // 승리 메세지 출력
                                        writer.println("[LOSE]당신이 졌습니다."); // 상대방에게 패배를 알림
                                        info="상대가 돌을 선택하고 있습니다."; // 메세지 수정
                                        reset();
                                        return;
                                }
                                info="상대편이 돌을 놓고 있습니다.";
                                repaint();
                                enable = false; // 턴 종료 설정
                        }
                });
        }
        boolean check(int color) { // 오목 체크 메소드
                for(int i=0;i<num-4;i++) {
                        for(int j=0;j<num;j++) {
                                if(stone[i][j]==color)
                                        if(stone[i+1][j]==color)
                                                if(stone[i+2][j]==color)
                                                        if(stone[i+3][j]==color)
                                                                if(stone[i+4][j]==color)
                                                                        return true;
                        }
                }
                for(int i=0;i<num;i++) {
                        for(int j=0;j<num-4;j++) {
                                if(stone[i][j]==color)
                                        if(stone[i][j+1]==color)
                                                if(stone[i][j+2]==color)
                                                        if(stone[i][j+3]==color)
                                                                if(stone[i][j+4]==color)
                                                                        return true;
                        }
                }
                for(int i=0;i<num-4;i++) {
                        for(int j=0;j<num-4;j++) {
                                if(stone[i][j]==color)
                                        if(stone[i+1][j+1]==color)
                                                if(stone[i+2][j+2]==color)
                                                        if(stone[i+3][j+3]==color)
                                                                if(stone[i+4][j+4]==color)
                                                                        return true;
                        }
                }
                for(int i=19;i>3;i--) {
                        for(int j=0;j<num-4;j++) {
                                if(stone[i][j]==color)
                                        if(stone[i-1][j+1]==color)
                                                if(stone[i-2][j+2]==color)
                                                        if(stone[i-3][j+3]==color)
                                                                if(stone[i-4][j+4]==color)
                                                                        return true;
                        }
                }
                return false;
        }

        void reset() { // 게임 종료 시 초기화
                for(int i=0;i<num;i++) {
                        for(int j=0;j<num;j++) {
                                stone[i][j]=0;
                        }
                }
                repaint();
                setEnable(false); // 바둑판 비활성화
        }

        public void paint(Graphics g) {
                g.clearRect(0, 0, getWidth(), getHeight());

                g.setColor(Color.RED);
                g.drawString(info, 30, 20); // Info 출력 부분

                for(int i=0;i<num;i++) { // 라인을 그린다.
                        g.setColor(Color.BLACK); // 색설정
                        g.drawLine(x,y+(size*i),x+(size*(num-1)),y+(size*i)); // 가로선
                        g.drawLine(x+(size*i),y, x+(size*i),y+(size*(num-1))); // 세로선
                }
                for(int i=0;i<num;i++) { // 돌을 놓는다.
                        for(int j=0;j<num;j++) {
                                if(stone[i][j]==BLACK) { // 흑돌이 놓여있을 경우
                                        g.setColor(Color.BLACK);
                                        g.fillOval((x-size/2)+i*size, (y-size/2)+j*size, width, height);
                                }
                                else if(stone[i][j]==WHITE) { // 백돌이 놓여있을 경우
                                        g.setColor(Color.WHITE);
                                        g.fillOval((x-size/2)+i*size, (y-size/2)+j*size, width, height);
                                }
                        }
                }
        }

        public void stoneSelect() { // 돌 선택 메세지
                if(JOptionPane.showOptionDialog(this, "흑과 백 중 하나 선택하세요", "돌 색깔 선택",
                        JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE,
                null, new String[]{"흑", "백"},"흑") == 0) { // 흑 선택시
                        setColor(BLACK,WHITE);
                        writer.println("[COLOR]" + WHITE + "," + BLACK);
                }
                else { // 백 선택시
                        setColor(WHITE,BLACK);
                        writer.println("[COLOR]"+ BLACK + "," + WHITE);
                }
        }

        public void setEnable(boolean enable) { // 바둑판 활성화 변경 메소드
                this.enable = enable;
        }

        public void setWriter(PrintWriter writer) { // 클라이언트와의 연결을 담당하는 PrintWriter
                this.writer = writer;
        }

        public void setColor(int color,int client_color) { // 돌 색 설정
                this.color = color; // 서버의 돌 색 설정
                this.client_color = client_color; // 클라이언트의 돌 색 설정

                if(color==BLACK){ // 검은 돌일 경우 선공
                        setEnable(true);
                        info="돌 선택이 완료되었습니다 - 흑(선공)";
                        str_color = "흑";
                }
                else { // 흰 돌일 경우 후공
                        setEnable(false);
                        info="돌 선택이 완료되었습니다 - 백(후공)";
                        str_color = "백";
                }
                repaint();
        }

        public void setInfo(String info) {
                this.info = info;
        }

        public void putOpponent(int x, int y) { // 클라이언트의 돌을 놓는다.
                stone[x][y] = client_color;
                info="나의 턴(" + str_color + ") - 돌을 놓으세요";
                repaint();

                if(check(client_color)==true) { // 클라이언트 오목 체크
                        repaint();
                        JOptionPane.showMessageDialog(null, "내가 졌다"); // 패배 메세지 출력
                        writer.println("[WIN]당신이 이겼습니다."); // 클라이언트에게 승리를 알림
                        info="돌을 선택";
                        reset();
                        stoneSelect(); // 돌 색 선택 화면 호출
                }
        }
}


public class Server extends Frame {
        private Omok_Board board = new Omok_Board(); // 오목판 객체
        ServerSocket serverSocket = null;
        Socket socket = null;
        private BufferedReader reader; // 입력 스트림
        private PrintWriter writer; // 출력 스트림

        public Server(String title) { // 생성자
                super(title);add(board);

                addWindowListener(new WindowAdapter() {
                        public void windowClosing(WindowEvent we) {
                                System.exit(0);
                        }
                });
        }

        private void connect() { // 연결
                try {
                        serverSocket = new ServerSocket(9002);
                        socket = serverSocket.accept();
                        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        writer = new PrintWriter(socket.getOutputStream(),true);
                        board.setWriter(writer);
                        board.setInfo("상대가 돌을 선택하고 있습니다.");

                        String msg;

                        while((msg = reader.readLine()) != null) {
                                if (msg.startsWith("[STONE]")) { // 서버가 돌을 놓았을 경우
                                        msg=msg.substring(7);
                                        board.setEnable(true); // 서버의 턴임을 설정
                                        board.putOpponent(Integer.parseInt(msg.substring(0, msg.indexOf(","))),Integer.parseInt(msg.substring(msg.indexOf(",") + 1)));
                                }
                                else if (msg.startsWith("[COLOR]")) { // 클라이언트가 돌 색을 지정했을 경우
                                        msg=msg.substring(7);
                                        board.setColor(Integer.parseInt(msg.substring(0, msg.indexOf(","))),Integer.parseInt(msg.substring(msg.indexOf(",") + 1)));
                                }
                        }
                }
                catch(Exception e) {
                        System.out.println(e.getMessage());
                }
                finally {
                        try {
                                serverSocket.close();
                                socket.close();
                        }
                        catch(Exception e) { }
                }
        }


        public static void main(final String[] args) {
                Server server = new Server("네트워크 오목 게임 - 서버"); // 오목판 객체
                server.setBounds(0,0,650,670);
                server.setVisible(true);
                server.connect();
        }
}