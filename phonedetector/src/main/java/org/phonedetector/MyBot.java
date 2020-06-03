package org.phonedetector;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.phonedetector.interfaces.MessageSendable;
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
    private Lucy lucy;
    private String idArray[];
    private String[] registerState = new String[10];
    private int member = 0;
    private String password = "null";
    private String path;

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

            boolean behavior = true;   //가입되지 않은 사용자 일 경우 true
            for (int i = 0; i < idArray.length; i++) {
                if(idArray[i].equals(ID)) { behavior = false; break; }
            }
            if(behavior) {
                System.out.println("가입되지 않은 사용자");
                sendMessage("가입되지 않은 사용자로부터 메시지\n" + TEXT, idArray[0]);

                //가입하려는 목적이 아닐 경우
                if(!TEXT.equals("가입")) {
                    sendMessage("가입되지 않은 사용자 입니다.", ID);
                    return; //가입되지 않은 사용자 일경우 리턴
                }
                
            }
            System.out.println(sb.toString());

            if(member != 0) {
                boolean register = false;
                int index = -1;
                for (int i = 0; i < member; i++) {
                    if(registerState[i].equals(ID)) {
                        register = true;
                        index = i;
                        break;
                    }
                }

                if(register) {
                    if(password.equals(TEXT)) {
                        RegisterUser ru = new RegisterUser(path);
                        ru.savePassword(Long.valueOf(ID));
                        System.out.println(ID + " 가입완료");
                        return;
                    }
                    else {
                        sendMessage("비밀번호가 틀립니다.", ID);
                        for (int i = index; i < member - 1; i++) {
                            registerState[i] = registerState[i+1];
                        }
                        member--;
                        return;
                    }
                }
            }

            String text = "없는 명령어 입니다.";
            switch (TEXT) {
                case "시간":
                if(lucy.getDoPhone()) {
                    text = "아직 공부를 시작하지 않았습니다!";
                    break;
                }
                else {
                    text = TimeCalculator.getMilliToFormatted(lucy.getReturnTimeDelta());
                }
                    break;
                
                case "/폰":
                System.out.println("폰");
                    break;

                case "가입":
                System.out.println("가입");
                boolean duplicate = false;  //duplicate registeration check flag
                for (int i = 0; i < idArray.length; i++) {
                    if(idArray[i].equals(ID)) {
                        System.out.println("중복가입 오류");
                        text = "이미 가입되어 있습니다!";
                        duplicate = true;
                        break;
                    }
                }
                if(!duplicate) {
                    if(password.equals("null")) {
                        text = "비밀번호가 등록되어 있지 않습니다.";
                    }
                    else {
                        text = "비밀번호를 입력해 주세요";
                        registerState[member++] = ID;
                    }
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

    public void messageExecute(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
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

    public MyBot setPassword(String password) {
        this.password = password;
        return this;
    }

    /**
     * information.json이 저장되어 있는 경로를 설정
     */
    public MyBot setInfoPath(String path) {
        this.path = path;
        return this;
    }

    public void setIdArray(String[] idArray) {
        this.idArray = idArray;
    }

    /**
     * idArray에 저장되어 있는 모든 사용자에게 메시지를 전송
     * @param text
     */
    public void sendMessageAll(String text) {
        for (int i = 0; i < idArray.length; i++) {
            SendMessage message = new SendMessage()
            .setChatId(idArray[i]).setText(text);
    
            try {
                execute(message); // Call method to send the message
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendMessage(String text, long user) {
        SendMessage message = new SendMessage()
        .setChatId(user).setText(text);
        try {
            execute(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String text, String user) {
        SendMessage message = new SendMessage()
        .setChatId(user).setText(text);
        try {
            execute(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}