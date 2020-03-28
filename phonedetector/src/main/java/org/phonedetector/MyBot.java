package org.phonedetector;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

/**
 * Telegram으로 전송받은 User Command를 처리
 */
public class MyBot extends TelegramLongPollingBot {
    String botUsername;
    String botToken;

    @Override
    public void onUpdateReceived(Update update) {
        // We check if the update has a message and the message has text
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            System.out.println(update.getMessage().getChatId());
            switch (messageText) {
                case "/시간":
                System.out.println("시간");
                    break;
                
                case "/폰":
                System.out.println("폰");
                    break;

                default:
                    break;
            }
            SendMessage message = new SendMessage() // Create a SendMessage object with mandatory fields
                    .setChatId(update.getMessage().getChatId()).setText(update.getMessage().getText());
            try {
                execute(message); // Call method to send the message
            } catch (TelegramApiException e) {
                e.printStackTrace();
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

}