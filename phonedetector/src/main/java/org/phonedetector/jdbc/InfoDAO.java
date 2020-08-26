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
import java.util.Calendar;
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

            Class.forName("org.mariadb.jdbc.Driver"); // 마리아 DB 적재
            this.conn = DriverManager.getConnection(
                    "jdbc:mariadb://lanihome.iptime.org:3306/PhoneDetector?autoReconnect=true", databaseID, databasePW); // 커넥션
                                                                                                                         // 연결
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 데이터베이스 커넥션을 제공.
     */
    public Connection getConnection() {
        try {
            if (conn.isValid(2)) {
                return conn;
            } else {
                this.conn = DriverManager.getConnection(
                        "jdbc:mariadb://lanihome.iptime.org:3306/PhoneDetector?autoReconnect=true", databaseID,
                        databasePW); // 커넥션 연결
                return conn;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 사용자들의 텔레그램 ID를 리턴한다
     * 
     * @return User Telegram Id List
     */
    public List<String> getUserTelegramIdList() {
        try (PreparedStatement pstmt = getConnection().prepareStatement("SELECT user_telegram_id FROM subscriber");
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
     * 
     * @param userId 아이디
     */
    @Deprecated
    public void registerUser(String userId) {

    }

    /**
     * 폰을 <strong>반납한</strong> 시간 등록
     */
    public void setReturnTime() {
        try (PreparedStatement pstmt = getConnection()
                .prepareStatement("INSERT INTO time_recorder VALUES (?, ?, ?, ?)")) {
            Time currentTime = new Time(System.currentTimeMillis()); // 현재 시간
            pstmt.setNull(1, Types.BIGINT); // id 자리는 null로 설정
            pstmt.setTime(2, currentTime); // 반납한 시간
            pstmt.setNull(3, Types.TIME); // 다시 가져간 시간은 Null로 설정
            pstmt.setDate(4, new java.sql.Date(System.currentTimeMillis())); // 반납한 날짜
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
        Time currentTime = new Time(System.currentTimeMillis()); // 현재 시간
        try (PreparedStatement pstmt = getConnection()
                .prepareStatement("UPDATE time_recorder SET retrieve_time = ? ORDER BY id DESC LIMIT 1")) {
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
     * 
     * @return 오늘 하루의 공부 시간 리스트
     */
    public List<StudyTime> getTodayStudyTimes() throws NotExistTodayStudy {
        List<StudyTime> studyTimes = getDateStudyTimes(new Date(System.currentTimeMillis()));   // 오늘의 날짜
        if (studyTimes.size() == 0) {
            throw new NotExistTodayStudy("오늘의 공부 시간이 없습니다.");
        }
        return studyTimes;
    }

    /**
     * 특정 날짜의 공부시간 리턴
     * @param date 날짜
     */
    public List<StudyTime> getDateStudyTimes(Date date) {
        List<StudyTime> studyTimes = new ArrayList<>();
        try (PreparedStatement pstmt = getConnection().prepareStatement(
                "SELECT return_time, retrieve_time FROM time_recorder WHERE date=? AND retrieve_time IS NOT NULL")) {
            pstmt.setDate(1, date);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    studyTimes.add(new StudyTime(rs.getTime("return_time"), rs.getTime("retrieve_time"))); // 공부 시간 리스트에
                                                                                                           // 추가
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return studyTimes;
    }

    /**
     * 주간 공부량을 리턴
     * @exception RuntimeException 본 메소드는 일요일에만 호출 가능함.
     * @return 주간 공부량
     */
    public List<List<StudyTime>> getWeeklyStudyTimes() {
        Calendar cal = Calendar.getInstance();
        if (cal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
            throw new RuntimeException("getWeeklyStudyTimes method can only be called on Sunday.");
        }

        // 무조건 일요일에만 호출가능하다는 가정 하에
        List<List<StudyTime>> studyList = new ArrayList<>();

        for (int i = 7; i > 0; i--) {
            studyList.add(getDateStudyTimes(new Date(System.currentTimeMillis() - 3600 * 24 * i * 1000)));
        }

        return studyList;
    }

    /**
     * 사용자가 가입되어 있는지 아닌지 확인
     * 
     * @param id 사용자 텔레그램 아이디
     * @return 가입 여부
     */
    public boolean isRegistered(String id) {
        List<String> userList = getUserTelegramIdList();
        return userList.stream().anyMatch(userId -> userId.equals(id)); // 입력받은 아이디와 DB상의 아이디 중에 일치하는 것이 있는지 검증
    }

    /**
     * 슈퍼사용자의 텔레그램 아이디 리스트 리턴
     * 
     * @return 슈퍼자용자
     */
    public List<String> getSuperUserList() {
        List<String> superUserList = new ArrayList<>();
        try (PreparedStatement pstmt = getConnection()
                .prepareStatement("SELECT user_telegram_id FROM subscriber WHERE is_super_user = ?")) {
            pstmt.setBoolean(1, true);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    superUserList.add(rs.getString("user_telegram_id"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return superUserList;
    }

    /**
     * 폰을 제출하였는지 아닌지
     * 
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