package org.phonedetector;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class RegisterUser {
    private String path;

    /**
     * @param path of information.json
     */
    public RegisterUser(String path) {
        this.path = path;
    }

    @SuppressWarnings("unchecked")
    public void savePassword(long userId) {
        File file = new File(path);
        
        try {
            JSONParser parser = new JSONParser();
            JSONObject jsonData = (JSONObject) parser.parse(new FileReader(file));
            JSONArray user = (JSONArray)jsonData.get("user");
            JSONObject newId = new JSONObject();
            newId.put("id", userId);
            user.add(newId);
            jsonData.replace("user", user);
            FileWriter fw = new FileWriter(file.getCanonicalPath());
            fw.write(jsonData.toJSONString());
            fw.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
}