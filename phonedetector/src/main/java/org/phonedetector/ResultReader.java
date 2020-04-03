package org.phonedetector;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.phonedetector.exceptions.NoTodayJsonException;

/**
 * JSON형식으로 저장된 오늘의 종합 일과를 읽어들이는 클래스
 */
public class ResultReader {
    private final String path = "./jsonData";
    private JSONArray dataArray = null;
    private int cnt;
    private JSONObject timeData = null;

    public ResultReader() throws IOException, ParseException, NoTodayJsonException {

        JSONParser parser = new JSONParser();
        JSONObject jsonData;
        try {
            jsonData = (JSONObject) parser.parse(new FileReader(getFileName()));
        } catch (FileNotFoundException e) {
            throw new NoTodayJsonException("오늘의 결과보고 JSON이 없습니다.");
        }
        this.dataArray = (JSONArray) jsonData.get("data");
        cnt = 0;
        
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
        return path + "/" + TimeCalculator.getTodayFormatted() + ".json";
    }
}