package org.phonedetector;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

/**
 * Telegram으로 전송받은 User Command를 처리
 */
public class MyBot extends TelegramLongPollingBot {
    private String botUsername;
    private String botToken;
    private Lucy lucy;
    private String idArray[];
    private int cnt = 0;

    @Override
    public void onUpdateReceived(Update update) {
        // We check if the update has a message and the message has text
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            System.out.println(update.getMessage().getChatId());
            String text = "없는 명령어 입니다.";
            switch (messageText) {
                case "시간":
                if(lucy.getDoPhone()) {
                    text = "아직 공부를 시작하지 않았습니다!";
                    break;
                }
                else {
                    text = lucy.getFormattedReturnTimeDelta();
                }
                    break;
                
                case "/폰":
                System.out.println("폰");
                    break;

                default:
                    break;
            }
            SendMessage message = new SendMessage() // Create a SendMessage object with mandatory fields
                    .setChatId(update.getMessage().getChatId()).setText(text);
            
            try {
                execute(message); // Call method to send the message
            } catch (TelegramApiException e) {
                e.getMessage();
            }
        }
    }
    
    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    public MyBot setBotUsername(String botUsername) {
        this.botUsername = botUsername;
        return this;
    }

    public MyBot setBotToken(String botToken) {
        this.botToken = botToken;
        return this;
    }

    public MyBot setLucy(Lucy lucy) {
        this.lucy = lucy;
        return this;
    }

    public void initIdArray(int len) {
        idArray = new String[len];
    }

    public void setId(String id) {
        idArray[cnt++] = id;
    }

    public void sendMessage(String text) {
        for (int i = 0; i < idArray.length; i++) {
            SendMessage message = new SendMessage()
            .setChatId(idArray[i]).setText(text);
    
            try {
                execute(message); // Call method to send the message
            } catch (TelegramApiException e) {
                e.getMessage();
            }
        }
    }

}