package org.phonedetector;

import java.io.FileReader;
import java.io.IOException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * json상에 저장된 API키 등을 제공하기 위한 클래스
 */
public class InfoReader {
    private String apiToken;
    private String botName;
    private String password;

    public InfoReader(String path) throws IOException, ParseException {
        JSONParser parser = new JSONParser();
        JSONObject jsonData;

        jsonData = (JSONObject) parser.parse(new FileReader(path));
        this.apiToken = (String) jsonData.get("token");
        this.botName = (String) jsonData.get("botName");
        this.password = (String) jsonData.get("password");
    }

    public String getApiToken() {
        return apiToken;
    }

    public String getBotName() {
        return botName;
    }

    public String getPassword() {
        return password;
    }
}