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
        boolean isFirst = true; // 처음 실행 여부
        while(true) {
            Calendar alertTime = Calendar.getInstance();
            alertTime.set(Calendar.HOUR, 11);           // 11시
            alertTime.set(Calendar.AM_PM, Calendar.PM); // 오후

            if (alertTime.before(Calendar.getInstance()) && isFirst) {
                // 알람 시간 후이고 처음 실행하는 경우
                isFirst = false;
            } else if (isFirst) {
                // 알람 시간 전이지만 처음 실행인 경우
                sleep(TimeCalculator.getTimeRemaining(alertTime));
                isFirst = false;
            } else {
                // 처음 실행이 아닌 경우
                alertTime.add(Calendar.DATE, 1);
                sleep(TimeCalculator.getTimeRemaining(alertTime));
            }
            alertSend();
        }

    }

    /**
     * Thread sleep
     * @param milliSeconds to be sleeped
     */
    private void sleep(long milliSeconds) {
        try {
            System.out.println("TodayResultAlert Sleep Until: " + TimeCalculator.getMilliToFormatted(milliSeconds));
            Thread.sleep(milliSeconds);
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
            return;
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

    @Override
    public String toString() {
        return "Today Result Alert";
    }
}