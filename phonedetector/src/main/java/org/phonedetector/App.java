package org.phonedetector;

import java.io.IOException;

import org.json.simple.parser.ParseException;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class App {
    public static void main(String[] args) throws IOException, ParseException {
        InfoReader infoReader = new InfoReader("./information.json");   //parse json information
        
        RpiSocket rpiConn = new RpiSocket(8887);    //RPISocket init
        Lucy lucy = new Lucy();
        
        //telegram bot init
        ApiContextInitializer.init();
        TelegramBotsApi botsApi = new TelegramBotsApi();
        MyBot bot = new MyBot()
                    .setBotToken(infoReader.getApiToken())
                    .setBotUsername(infoReader.getBotName())
                    .setLucy(lucy);
        try {
            botsApi.registerBot(bot);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        bot.initIdArray(infoReader.getLength());
        
        //id input
        for (int i = 0; i < infoReader.getLength(); i++) {
            String id = infoReader.nextId();
            bot.setId(id);
        }

        //messageSender init
        MessageSender messageSender = new MessageSender()
                        .setApiToken(infoReader.getApiToken())
                        .setId(infoReader.getId());
        
        PhoneThread pt = new PhoneThread()
                            .setLucy(lucy)
                            .setMessageSender(messageSender)
                            .setSocket(rpiConn);
        Thread thread = new Thread(pt, "PhoneThread");

        TodayResultAlert todayResult = new TodayResultAlert()
                        .setBot(bot);
        Thread resultThread = new Thread(todayResult, "todayResultAlert");
        thread.start();
        resultThread.start();
    }
}