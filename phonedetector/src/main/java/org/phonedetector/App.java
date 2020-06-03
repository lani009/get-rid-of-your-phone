package org.phonedetector;

import java.io.IOException;

import org.json.simple.parser.ParseException;
import org.phonedetector.interfaces.Socketable;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class App {
    public static void main(String[] args) throws IOException, ParseException {
        String informationPath = "./information.json";
        InfoReader infoReader = new InfoReader(informationPath);   //parse json information
        
        Socketable rpiConn = new RpiSocket(8887);    //RPISocket init
        Lucy lucy = new Lucy();
        
        //telegram bot init
        ApiContextInitializer.init();
        TelegramBotsApi botsApi = new TelegramBotsApi();
        MyBot bot = new MyBot()
                    .setBotToken(infoReader.getApiToken())
                    .setBotUsername(infoReader.getBotName())
                    .setLucy(lucy)
                    .setPassword(infoReader.getPassword())
                    .setInfoPath(informationPath);
        try {
            botsApi.registerBot(bot);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        bot.setIdArray(infoReader.getId());

        //messageSender init
        MessageSender messageSender = new MessageSender();
        messageSender.setApiToken(infoReader.getApiToken());
        messageSender.addId(infoReader.getId());
        
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