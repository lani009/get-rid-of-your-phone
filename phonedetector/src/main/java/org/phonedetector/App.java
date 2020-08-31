package org.phonedetector;

import java.io.IOException;

import org.json.simple.parser.ParseException;
import org.phonedetector.interfaces.Socketable;
import org.phonedetector.jdbc.InfoDAO;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class App {
    public static void main(String[] args) throws IOException, ParseException {
        String informationPath = "./information.json";
        InfoReader infoReader = new InfoReader(informationPath); // parse json information

        Socketable rpiConn = new RpiSocket(8887); // RPISocket init

        GPIO gpio = new GPIO((RpiSocket) rpiConn);

        Thread gpioThread = new Thread(gpio, "GPIO Button");
        gpioThread.start();

        ((RpiSocket) rpiConn).init();

        // MessageSender init
        MessageSender.getInstance().setApiToken(infoReader.getApiToken());

        // telegram bot init
        ApiContextInitializer.init();
        TelegramBotsApi botsApi = new TelegramBotsApi();
        MyBot.getInstance().setBotToken(infoReader.getApiToken()).setBotUsername(infoReader.getBotName());
        try {
            botsApi.registerBot(MyBot.getInstance());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

        // 사용자에게 공부 상황을 전달하기 위해 시작
        PhoneThread pt = new PhoneThread().setSocket(rpiConn);

        Thread thread = new Thread(pt, "PhoneThread");
        thread.start();

        // 오늘의 공부 상황을 알리기 위해 실행
        TodayResultAlert todayResult = new TodayResultAlert().setBot(MyBot.getInstance());
        AlertInit.init(todayResult, new WeeklyResultAlert());

        System.out.println("\n\nTelegram Study Alert Ready!!\n");

        System.out.println("Super User List\n" + InfoDAO.getInstance().getSuperUserList());
    }

}