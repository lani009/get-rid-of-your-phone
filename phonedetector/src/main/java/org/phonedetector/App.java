package org.phonedetector;

import java.io.IOException;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioProvider;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiBcmPin;
import com.pi4j.io.gpio.RaspiGpioProvider;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.GpioPinListener;
import com.pi4j.io.gpio.impl.PinImpl;
import com.pi4j.wiringpi.Gpio;

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
        MessageSender.getInstance().setUserTelegramId(InfoDAO.getInstance().getUserTelegramIdList());

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

        // 오늘의 공부 상황을 알리기 위해 실행
        TodayResultAlert todayResult = new TodayResultAlert().setBot(MyBot.getInstance());
        Thread resultThread = new Thread(todayResult, "todayResultAlert");
        thread.start();
        resultThread.start();
        System.out.println("\n\nTelegram Study Alert Ready!!\n");
    }

}