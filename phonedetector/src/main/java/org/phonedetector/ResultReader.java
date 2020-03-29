package org.phonedetector;

import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.simple.JSONArray;
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
            JSONObject jsonData = (JSONObject) parser.parse(new FileReader(getFileName()));
            this.dataArray = (JSONArray) jsonData.get("data");
        cnt = 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }

    public int getSize() {
        return this.dataArray.size();
    }

    public int next() {
        if(dataArray.size() <= cnt) {
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

    public long getDuration() {
        return (long)timeData.get("duration");
    }

    protected String getFileName() {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return path + "/" + dateFormat.format(date) + ".json";
    }
}