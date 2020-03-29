package org.phonedetector;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * JSON형식으로 저장된 오늘의 종합 일과를 읽어들이는 클래스
 */
public class ResultReader {
    private final String path = "./jsonData";
    private JSONArray dataArray = null;
    private int cnt;
    private JSONObject timeData = null;

    public ResultReader() {
        try {
            JSONParser parser = new JSONParser();
            JSONObject jsonData = (JSONObject) parser.parse(new FileReader(path));
            this.dataArray = (JSONArray) jsonData.get("data");
        cnt = 0;
        } catch (Exception e) {
        }
        
    }

    public int getSize() {
        return this.dataArray.length();
    }

    public int next() {
        if(dataArray.length() <= cnt) {
            cnt = 0;
            return -1;
        }
        timeData = (JSONObject) this.dataArray.get(cnt++);
        return 0;
    }

    public String getStartTime() {
        return (String)timeData.get("returnTime");
    }

    public String getEndTime() {
        return (String)timeData.get("tookTime");
    }

    public int getDuration() {
        return (int)timeData.get("duration");
    }

    protected String getFileName() {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return path + "/" + dateFormat.format(date) + ".json";
    }
}