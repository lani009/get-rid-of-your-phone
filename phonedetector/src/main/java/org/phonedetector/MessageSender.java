package org.phonedetector;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * Telegram으로 메시지를 전송하기 위한 클래스
 * MessageSender ms = new MesageSender()
 *                      .setApiToken(apiToken)
 *                      .setId(id);
 */
public class MessageSender {
    private String idArray[];
    private String urlString = "https://api.telegram.org/bot%s/sendMessage?chat_id=%s&text=%s";
    private String apiToken;

    public MessageSender() { }

    /**
     * set Bot's unique token
     * @param botToken
     * @return self
     */
    public MessageSender setApiToken(String apiToken) {
        this.apiToken = apiToken;
        return this;
    }

    public MessageSender setId(String id) {
        idArray = new String[1];
        idArray[0] = id;
        return this;
    }

    public MessageSender setId(String[] id) {
        idArray = id;
        return this;
    }

    public void sendMessage(String text) throws IOException {
        for (int i = 0; i < idArray.length; i++) {
            URL url = new URL(String.format(urlString, apiToken, idArray[i], text));
            URLConnection conn = url.openConnection();
    
            StringBuilder sb = new StringBuilder();
            InputStream is = new BufferedInputStream(conn.getInputStream());
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String inputLine = "";
            while ((inputLine = br.readLine()) != null) {
                sb.append(inputLine);
            }
            String response = sb.toString();
            System.out.print(response);
        }
    }
}