package org.phonedetector;

import java.io.FileNotFoundException;
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
    private int cnt;

    public InfoReader(String path) throws FileNotFoundException, IOException, ParseException {
        JSONParser parser = new JSONParser();
        JSONObject jsonData = (JSONObject) parser.parse(new FileReader(path));
        this.idArray = (JSONArray) jsonData.get("user");
        this.apiToken = (String) jsonData.get("token");
        this.botName = (String) jsonData.get("botName");
        this.length = idArray.size();
        this.cnt = 0;
    }

    /**
     * returns the number of persons.
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

    public String nextId() {
        JSONObject data = (JSONObject) idArray.get(cnt++);
        long id = (long) data.get("id");
        String sId = Long.toString(id);
        return sId;
    }
}