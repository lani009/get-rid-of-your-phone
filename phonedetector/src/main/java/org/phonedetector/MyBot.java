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
    private String[] registerState = new String[10];
    private int member = 0;
    private String password = "null";
    private String path;

    @Override
    public void onUpdateReceived(Update update) {
        // We check if the update has a message and the message has text
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();

            StringBuilder sb = new StringBuilder(Long.toString(update.getMessage().getChatId()));
            sb.append("의 메세지: ");
            sb.append(update.getMessage().getText());
            sb.append(" - ");
            sb.append(update.getMessage().getDate());

            System.out.println(sb.toString());

            if(member != 0) {
                boolean register = false;
                int index = -1;
                for (int i = 0; i < member; i++) {
                    if(registerState[i].equals(Long.toString(update.getMessage().getChatId()))) {
                        register = true;
                        index = i;
                        break;
                    }
                }

                if(register) {
                    if(password.equals(messageText)) {
                        RegisterUser ru = new RegisterUser(path);
                        ru.savePassword(update.getMessage().getChatId());
                        System.out.println("가입완료");
                        return;
                    }
                    else {
                        privateSendMessage("비밀번호가 틀립니다.", update.getMessage().getChatId());
                        for (int i = index; i < member - 1; i++) {
                            registerState[i] = registerState[i+1];
                        }
                        member--;
                        return;
                    }
                }
            }

            String text = "없는 명령어 입니다.";
            switch (messageText) {
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
                    if(idArray[i].equals(Long.toString(update.getMessage().getChatId()))) {
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
                        registerState[member++] = Long.toString(update.getMessage().getChatId());
                    }
                }
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

    public MyBot setPassword(String password) {
        this.password = password;
        return this;
    }

    public MyBot setInfoPath(String path) {
        this.path = path;
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

    public void privateSendMessage(String text, long user) {
        SendMessage message = new SendMessage()
        .setChatId(user).setText(text);
        try {
            execute(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}