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
        
        //messageSender init
        MessageSender messageSender = new MessageSender(infoReader.getLength())
                        .setApiToken(infoReader.getApiToken());
        //id input
        for (int i = 0; i < infoReader.getLength(); i++) {
            messageSender.setId(infoReader.nextId());
        }

        RpiSocket rpiConn = new RpiSocket(8887);    //RPISocket init
        Lucy lucy = new Lucy();
        PhoneThread pt = new PhoneThread()
                            .setLucy(lucy)
                            .setMessageSender(messageSender)
                            .setSocket(rpiConn);
        Thread thread = new Thread(pt, "PhoneThread");

        TodayResultAlert todayResult = new TodayResultAlert()
                        .setMessageSender(messageSender);
        Thread resultThread = new Thread(todayResult, "todayResultAlert");
        thread.start();
        resultThread.start();

     
        ApiContextInitializer.init();
        //telegram bot init
        TelegramBotsApi botsApi = new TelegramBotsApi();

        try {
            botsApi.registerBot(new MyBot()
                            .setBotToken(infoReader.getApiToken())
                            .setBotUsername(infoReader.getBotName())
                            .setLucy(lucy)
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
        System.out.println("Socket Accepted!");
    }

    public byte[] getData() throws IOException {
        InputStream data = c_socket.getInputStream();
        byte[] receiveBuffer = new byte[10];
        data.read(receiveBuffer);
        System.out.println("get Data" + receiveBuffer[0]);
        return receiveBuffer;
    }
}