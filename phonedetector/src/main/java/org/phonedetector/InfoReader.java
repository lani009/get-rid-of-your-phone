package org.phonedetector;

import java.io.FileReader;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * json상에 저장된 API키, user ID를 제공하기 위한 클래스
 */
public class InfoReader {
    private int length;
    private JSONArray idArray;
    private String apiToken;
    private String botName;
    private String password;

    public InfoReader(String path) throws IOException, ParseException {
        JSONParser parser = new JSONParser();
        JSONObject jsonData;

        jsonData = (JSONObject) parser.parse(new FileReader(path));
        this.idArray = (JSONArray) jsonData.get("user");
        this.apiToken = (String) jsonData.get("token");
        this.botName = (String) jsonData.get("botName");
        this.password = (String) jsonData.get("password");
        this.length = idArray.size();
    }

    /**
     * returns the number of persons.
     * @return length 길이
     */
    public int getLength() {
        return length;
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

    /**
     * returns the User Id in string array
     * @return String[] id
     */
    public String[] getId() {
        String[] id = new String[length];
        for (int i = 0; i < length; i++) {
            JSONObject data = (JSONObject) idArray.get(i);
            id[i] = Long.toString((long)data.get("id"));
        }
        return id;
    }
}