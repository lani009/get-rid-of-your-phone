package org.phonedetector;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.phonedetector.interfaces.MessageSendable;
import org.phonedetector.jdbc.InfoDAO;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

/**
 * Telegram으로 전송받은 User Command를 처리
 */
public class MyBot extends TelegramLongPollingBot implements MessageSendable {
    private String botUsername;
    private String botToken;

    private static class Holder {
        private static final MyBot INSTANCE = new MyBot();
    }

    public static MyBot getInstance() {
        return Holder.INSTANCE;
    }

    private MyBot() {

    }

    @Override
    public void onUpdateReceived(Update update) {
        // check if the update has a message and the message has text
        if (update.hasMessage() && update.getMessage().hasText()) {
            final String TEXT = update.getMessage().getText();
            final String ID = String.valueOf(update.getMessage().getChatId());

            StringBuilder sb = new StringBuilder(ID);
            String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            sb.append("의 메세지: ");
            sb.append(TEXT);
            sb.append(" - ");
            sb.append(date);

            if (InfoDAO.getInstance().isRegistered(ID)) {
                // 가입되지 않은 사용자로 부터 온 메시지 처리

                sendMessage("가입되지 않은 사용자로부터 메시지\n" + TEXT, InfoDAO.getInstance().getSuperUserList());

                //가입하려는 목적이 아닐 경우
                if(!TEXT.equals("가입")) {
                    sendMessage("가입되지 않은 사용자 입니다.", ID);
                    return; //가입되지 않은 사용자 일경우 리턴하여 메시지 전송 로직 종료
                }
            }
            System.out.println(sb.toString());

            String text = "없는 명령어 입니다.";
            switch (TEXT) {
                case "시간":
                if (!InfoDAO.getInstance().isReturnedPhone()) {
                    text = "아직 공부를 시작하지 않았습니다!";
                    break;
                } else {
                    text = TimeCalculator.getMilliToFormatted(InfoDAO.getInstance().getCurrentTimeDelta());
                }
                    break;

                default:
                    break;
            }
            sendMessage(text, ID);
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

    /**
     * 사용자의 텔레그램 아이디를 리턴
     * @return 사용자의 텔레그램 아이디 리스트
     */
    private List<String> getUserTelegramIdList() {
        return InfoDAO.getInstance().getUserTelegramIdList();
    }

    /**
     * 모든 사용자에게 메시지를 전송
     * @param text
     */
    public void sendMessageAll(String text) {
        for (String userTelegramId : getUserTelegramIdList()) {
            sendMessage(text, userTelegramId);
        }
    }

    /**
     * 단체 사용자에게 메시지를 전송
     * @param text 메시지 내용
     * @param userList 단체 사용자
     */
    public void sendMessage(String text, List<String> userList) {
        for (String user : userList) {
            sendMessage(text, user);
        }
    }

    /**
     * 단일 사용자에게 메시지 전송
     * @param text 메시지 내용
     * @param user 유저 텔레그램 아이디
     */
    public void sendMessage(String text, long user) {
        SendMessage message = new SendMessage()
        .setChatId(user).setText(text);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    /**
     * 단일 사용자에게 메시지 전송
     * @param text 메시지 내용
     * @param user 유저 텔레그램 아이디
     */
    public void sendMessage(String text, String user) {
        SendMessage message = new SendMessage()
        .setChatId(user).setText(text);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

}