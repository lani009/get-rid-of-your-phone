package org.phonedetector;

import java.sql.Time;
import java.util.Calendar;
import java.util.List;

import org.phonedetector.exceptions.NotExistTodayStudy;
import org.phonedetector.jdbc.InfoDAO;
import org.phonedetector.struct.StudyTime;

/**
 * Thread를 통해 오후 11시가 되면 하루 일과 총 정리를 보낸다.
 */
public class TodayResultAlert implements Runnable {
    private MyBot bot;
    public TodayResultAlert() {
    }

    public TodayResultAlert setBot(MyBot bot) {
        this.bot = bot;
        return this;
    }

    @Override
    public void run() {
        Calendar alertTime = Calendar.getInstance();
        Calendar now = Calendar.getInstance();
        alertTime.set(Calendar.HOUR, 23);
        
        while(true) {
            now.setTimeInMillis(System.currentTimeMillis());

            if(now.get(Calendar.AM_PM) == 1 && now.get(Calendar.HOUR) >= alertTime.get(Calendar.HOUR)) {
                System.out.println("Alert Sending!");
                alertSend();
                sleep(7200000);
            }
            sleep(3000);
        }

    }

    /**
     * Thread sleep
     * @param milliSeconds to be sleeped
     */
    private void sleep(int milliSeconds) {
        try {
            Thread.sleep(Long.valueOf(milliSeconds));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 오늘의 총합 보고
     * @return state
     */
    private void alertSend() {
        /*
        오늘의 종합 보고
        1. 제출시간: 09:23:42
           회수시간: 10:24:01
           총 시간: 3시간 01분 35초

        2. 제출시간: 09:23:42
           회수시간: 10:24:01
           총 시간: 3시간 01분 35초
        총 시간: 00시간 00분 00초
        */
        StringBuilder sb = new StringBuilder("오늘의 종합 보고\n");
        List<StudyTime> studyTimes = null;
        try {
            studyTimes = InfoDAO.getInstance().getTodayStudyTimes();
        } catch (NotExistTodayStudy e) {
            sb.append("오늘은 핸드폰을 제출하지 않았습니다.");
            bot.sendMessageAll(sb.toString());
        }
        int cnt = 0;    // 순번
        long durationTotal = 0; // 총 제출 시간
        long duration;  // 제출 시간

        for (StudyTime studyTime : studyTimes) {
            sb.append(1 + (cnt++));
            sb.append(". ");
            sb.append("제출시간: ");
            sb.append(timeToTimeString(studyTime.getReturnTime()));
            sb.append("\n    회수시간: ");
            sb.append(timeToTimeString(studyTime.getRetriveTime()));
            sb.append("\n    총 시간: ");
            duration = studyTime.getTimeDelta();
            durationTotal += duration;
            sb.append(TimeCalculator.getMilliToFormatted(duration));
            sb.append("\n\n");
        }
        sb.append("오늘의 총 시간: ");
        sb.append(TimeCalculator.getMilliToFormatted(durationTotal));
        MyBot.getInstance().sendMessageAll(sb.toString());
    }

    private String timeToTimeString(Time time) {
        return time.toString();
    }
}