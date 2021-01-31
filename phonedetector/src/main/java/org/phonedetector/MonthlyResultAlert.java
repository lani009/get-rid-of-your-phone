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
public class MonthlyResultAlert implements Runnable {
    private MyBot bot;
    public MonthlyResultAlert() {
    }

    public MonthlyResultAlert setBot(MyBot bot) {
        this.bot = bot;
        return this;
    }

    @Override
    public void run() {
        while(true) {
            Calendar alertTime = Calendar.getInstance();
            int dayOfMonth = alertTime.getActualMaximum(Calendar.DAY_OF_MONTH);
            alertTime.set(Calendar.DATE, dayOfMonth);
            alertTime.set(Calendar.HOUR, 4);           // 4시
            alertTime.set(Calendar.MINUTE, 0);          // 0분
            alertTime.set(Calendar.SECOND, 0);          // 0초
            alertTime.set(Calendar.AM_PM, Calendar.PM); // 오후
            sleep(TimeCalculator.getTimeRemaining(alertTime));
            alertSend();
        }

    }

    /**
     * Thread sleep
     * @param milliSeconds to be sleeped
     */
    private void sleep(long milliSeconds) {
        try {
            System.out.println("MonthlyResultAlert Sleep Until: " + TimeCalculator.getMilliToFormatted(milliSeconds));
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
        StringBuilder sb = new StringBuilder("월간 종합 보고\n");
        Calendar startTime = Calendar.getInstance();
        startTime.set(Calendar.DATE, 1);
        startTime.set(Calendar.AM_PM, Calendar.AM);
        startTime.set(Calendar.HOUR, 0);
        startTime.set(Calendar.MINUTE, 0);
        startTime.set(Calendar.SECOND, 0);
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
