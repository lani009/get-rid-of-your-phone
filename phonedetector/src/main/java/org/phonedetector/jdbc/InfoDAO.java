package org.phonedetector.jdbc;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.phonedetector.exceptions.NotExistTodayStudy;
import org.phonedetector.struct.StudyTime;

public class InfoDAO {
    private final String databaseID;
    private final String databasePW;
    /**
     * 마리아 DB 커넥션
     */
    Connection conn = null;
    /**
     * db 오류 검증용, 공부 하는 중인지, 아닌지
     */
    private boolean isReturnedPhone = false;

    /**
     * 폰을 제출한 시간
     */
    private Time returnedTime = null;

    /**
     * 마리아 DB커넥션 연결
     */
    private InfoDAO() {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream("dbPassword.txt")));
            this.databaseID = br.readLine();
            this.databasePW = br.readLine();
            br.close();
            
            Class.forName("org.mariadb.jdbc.Driver");   // 마리아 DB 적재
            this.conn = DriverManager.getConnection("jdbc:mariadb://lanihome.iptime.org:3306/PhoneDetector", databaseID, databasePW);   // 커넥션 연결
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 사용자들의 텔레그램 ID를 리턴한다
     * @return User Telegram Id List
     */
    public List<String> getUserTelegramIdList() {
        try (PreparedStatement pstmt = conn.prepareStatement("SELECT user_telegram_id FROM subscriber");
             ResultSet rs = pstmt.executeQuery();) {

            List<String> userIdList = new ArrayList<>();
            while (rs.next()) {
                userIdList.add(rs.getString("user_telegram_id"));
            }
            return userIdList;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 새로운 유저 가입
     * @param userId 아이디
     */
    @Deprecated
    public void registerUser(String userId) {

    }

    /**
     * 폰을 <strong>반납한</strong> 시간 등록
     */
    public void setReturnTime() {
        try (PreparedStatement pstmt = conn.prepareStatement("INSERT INTO time_recorder VALUES (?, ?, ?, ?)")) {
            Time currentTime = new Time(System.currentTimeMillis());    // 현재 시간
            pstmt.setNull(1, Types.BIGINT); // id 자리는 null로 설정
            pstmt.setTime(2, currentTime); // 반납한 시간
            pstmt.setNull(3, Types.TIME);   // 다시 가져간 시간은 Null로 설정
            pstmt.setDate(4, new java.sql.Date(System.currentTimeMillis()));    // 반납한 날짜
            pstmt.executeUpdate();
            this.returnedTime = currentTime;
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 폰을 다시 <strong>가져간</strong> 시간 등록
     */
    public StudyTime setRetriveTime() {
        Time currentTime = new Time(System.currentTimeMillis());    // 현재 시간
        try (PreparedStatement pstmt = conn.prepareStatement("UPDATE time_recorder SET retrieve_time = ? ORDER BY id DESC LIMIT 1")) {
            pstmt.setTime(1, currentTime); // 다시 가져간 시간
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        return new StudyTime(returnedTime, currentTime);
    }

    /**
     * 오늘 하루의 공부 시간 리턴
     * @return 오늘 하루의 공부 시간 리스트
     */
    public List<StudyTime> getTodayStudyTimes() throws NotExistTodayStudy {
        Date toDay = new Date(System.currentTimeMillis());  // 오늘 날짜
        List<StudyTime> studyTimes = new ArrayList<>();

        // 오늘 날짜의 공부 시간만 선택
        try (PreparedStatement pstmt = conn.prepareStatement("SELECT return_time, retrieve_time FROM time_recorder WHERE date=?")) {
            pstmt.setDate(1, toDay);
            try (ResultSet rs = pstmt.executeQuery()) {
                while(rs.next()) {
                    studyTimes.add(new StudyTime(rs.getTime("return_time"), rs.getTime("retrieve_time")));  // 공부 시간 리스트에 추가
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (studyTimes.size() == 0) {
            throw new NotExistTodayStudy("오늘의 공부 시간이 없습니다.");
        }
        return studyTimes;
    }

    /**
     * 사용자가 가입되어 있는지 아닌지 확인
     * @param id 사용자 텔레그램 아이디
     * @return 가입 여부
     */
    public boolean isRegistered(String id) {
        List<String> userList = getUserTelegramIdList();
        return userList.stream().anyMatch(userId -> userId.equals(id)); // 입력받은 아이디와 DB상의 아이디 중에 일치하는 것이 있는지 검증
    }

    /**
     * 슈퍼사용자의 텔레그램 아이디 리스트 리턴
     * @return 슈퍼자용자
     */
    public List<String> getSuperUserList() {
        List<String> superUserList = new ArrayList<>();
        try (PreparedStatement pstmt = conn.prepareStatement("SELECT user_telegram_id, is_super_user FROM subscriber")) {
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    if (rs.getBoolean("is_super_user")) {
                        superUserList.add(rs.getString("user_telegram_id"));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return superUserList;
    }

    /**
     * 폰을 제출하였는지 아닌지
     * @return 폰을 제출하였다면 true 리턴 - 공부를 하고 있는 것임
     */
    public boolean isReturnedPhone() {
        return isReturnedPhone;
    }

    public long getCurrentTimeDelta() {
        if (!isReturnedPhone()) {
            throw new RuntimeException("폰을 제출하지 않았으나, getCurrentTimeDelta를 호출함");
        }
        return System.currentTimeMillis() - returnedTime.getTime();
    }

    public static InfoDAO getInstance() {
        return Holder.INSTANCE;
    }

    private static class Holder {
        private static final InfoDAO INSTANCE = new InfoDAO();
    }
}