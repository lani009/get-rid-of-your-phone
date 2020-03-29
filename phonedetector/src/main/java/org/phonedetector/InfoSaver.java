package org.phonedetector;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * 날짜에 맞추어 공부량을 json에 저장
 */
public class InfoSaver {
    private String path;

    public InfoSaver(String path) {
        this.path = path;
    }

    /**
     * 날짜에 맞추어 파일 명을 리턴
     */
    private String getDate() {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(date);
    }

    private String getBeforeTime(int milli) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"));
        calendar.setTime(new Date());

        calendar.add(Calendar.MILLISECOND, -milli);
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        return dateFormat.format(calendar.getTime());
    }

    private String getNowTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        return dateFormat.format(new Date());
    }

    public void saveData(long milli) throws IOException {
        File file = new File(path + "/" + getDate() + ".json");
        
        try {
            if(file.exists()) {
                JSONParser parser = new JSONParser();
                JSONObject jsonData = (JSONObject) parser.parse(new FileReader(file));
                JSONArray data = (JSONArray)jsonData.get("data");
                JSONObject timeData = new JSONObject();
                timeData.put("returnTime", getBeforeTime((int)milli));   //제출시간
                timeData.put("tookTime", getNowTime()); //회수시간
                timeData.put("duration", milli);
                data.add(timeData);
                jsonData.clear();
                jsonData.put("data", data);
                FileWriter fw = new FileWriter(file.getCanonicalPath());
                fw.write(jsonData.toJSONString());
                fw.close();
            }
            else {
                file.createNewFile();
                JSONArray data = new JSONArray();
                JSONObject timeData = new JSONObject();
                timeData.put("returnTime", getBeforeTime((int)milli));   //제출시간
                timeData.put("tookTime", getNowTime()); //회수시간
                timeData.put("duration", milli);
                data.add(timeData);

                JSONObject jsonData = new JSONObject();
                jsonData.put("data", data);
                FileWriter fw = new FileWriter(file.getCanonicalPath());
                fw.write(jsonData.toJSONString());
                System.out.println(jsonData.toJSONString());
                fw.close();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
}