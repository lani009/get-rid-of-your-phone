package org.phonedetector;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.json.simple.parser.ParseException;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class App {
    public static void main(String[] args) throws IOException, ParseException {
        InfoReader infoReader = new InfoReader("./information.json");   //parse json information
        
        MessageSender messageSender = new MessageSender(infoReader.getLength())
                        .setApiToken(infoReader.getApiToken());
        
        for (int i = 0; i < infoReader.getLength(); i++) {
            messageSender.setId(infoReader.nextId());
        }

        RpiSocket rpiConn = new RpiSocket(8888);    //RPISocket init
        Lucy lucy = new Lucy();
        PhoneThread pt = new PhoneThread()
                            .setLucy(lucy)
                            .setMessageSender(messageSender)
                            .setSocket(rpiConn);


        ApiContextInitializer.init();

        TelegramBotsApi botsApi = new TelegramBotsApi();

        try {
            botsApi.registerBot(new MyBot()
                            .setBotToken(infoReader.getApiToken())
                            .setBotUsername(infoReader.getBotName())
                            );
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }
}

class RpiSocket {
    private ServerSocket s_socket;
    private Socket c_socket;

    public RpiSocket(int port) throws IOException {
        s_socket = new ServerSocket(port);
        c_socket = s_socket.accept();
    }

    public byte[] getData() throws IOException {
        InputStream data = c_socket.getInputStream();
        byte[] receiveBuffer = new byte[10];
        data.read(receiveBuffer);
        return receiveBuffer;
    }
}

/**
 * socket으로 부터 데이터를 전송받을 때 마다 응답하기 위한 클래스
 */
class PhoneThread implements Runnable {
    private RpiSocket socket;
    private MessageSender messageSender;
    private Lucy lucy;
    /**
     * @param RpiSocket instance
     */
    public PhoneThread() {
    }

    public PhoneThread setSocket(RpiSocket rpiSocket) {
        this.socket = rpiSocket;
        return this;
    }

    public PhoneThread setMessageSender(MessageSender messageSender) {
        this.messageSender = messageSender;
        return this;
    }

    public PhoneThread setLucy(Lucy lucy) {
        this.lucy = lucy;
        return this;
    }

    @Override
    public void run() {
        while(true) {
            try {
                socket.getData();
                // TODO lucy업데이트하고, messageSender로 메시지 보내는 기능 추가하기.
            } catch (IOException e) {
                System.out.println(e.getMessage());
                break;
            }
        }

    }

}

/**
 * 핸드폰을 제출했는지, 제출하지 않았는지
 * 제출한 시간은 어떻게 되는지를 저장하는 클래스.
 * 
 * 동생 영어이름이 Lucy이다.
 */
class Lucy {

}