package org.phonedetector;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import org.phonedetector.interfaces.MessageSendable;

/**
 * Telegram으로 메시지를 전송하기 위한 클래스
 * MessageSender ms = new MesageSender()
 *                      .setApiToken(apiToken)
 *                      .setId(id);
 */
public class MessageSender implements MessageSendable {
    private ArrayList<String> idArray= new ArrayList<String>();
    private String urlString = "https://api.telegram.org/bot%s/sendMessage?chat_id=%s&text=%s";
    private String apiToken;

    private static class LazyHolder {
        public static final PhoneThread INSTANCE = new PhoneThread();
    }

    public static PhoneThread getInstance() {
        return LazyHolder.INSTANCE;
    }

    public void setApiToken(String apiToken) {
        this.apiToken = apiToken;
    }

    public void addId(String id) {
        idArray.add(id);
    }

    public void addId(String[] id) {
        for (String i : id) {
            addId(i);
        }
    }

    private String toURLString(String apiToken, long id, String text) {
        return String.format(urlString, apiToken, Long.toString(id), text);
    }

    private String toURLString(String apiToken, String id, String text) {
        return String.format(urlString, apiToken, id, text);
    }

    /**
     * 특정 id에게 메시지 전송
     */
    @Override
    public void sendMessage(String text, long id) {
        try {
            URL url = new URL(toURLString(apiToken, id, text));
            URLConnection conn = url.openConnection();
            conn.getInputStream();
            System.out.println(id + "에게 전송: " + text);
        } catch (IOException e) {
            System.out.println("!!! 메시지 전송 실패");
            e.printStackTrace();
        }
    }

    @Override
    public void sendMessageAll(String text) {
        for (String id : idArray) {
            sendMessage(text, id);
        }
        System.out.println("모두에게 메시지 전송: " + text);
    }

    /**
     * 특정 id에게 메시지 전송
     */
    @Override
    public void sendMessage(String text, String id) {
        try {
            URL url = new URL(toURLString(apiToken, id, text));
            URLConnection conn = url.openConnection();
            conn.getInputStream();
            System.out.println(id + "에게 전송: " + text);
        } catch (IOException e) {
            System.out.println("!!! sendMessage 실패");
            e.printStackTrace();
        }
    }
}