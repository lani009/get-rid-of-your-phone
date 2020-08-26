package org.phonedetector;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 시간관련 계산 클래스
 */
public class TimeCalculator {

    private TimeCalculator() { }

    /**
     * returns MilliSeconds into Formatted String
     * @param milliSeconds 밀리세컨드 시간
     * @return "%02d 시간, %02d 분, %02d 초" or "%02d 분, %02d 초"
     */
    public static String getMilliToFormatted(long milliSeconds) {
        long Days = TimeUnit.MILLISECONDS.toDays(milliSeconds);
        long Hours = TimeUnit.MILLISECONDS.toHours(milliSeconds) - Days * 24;
        long Minutes = TimeUnit.MILLISECONDS.toMinutes(milliSeconds) - Hours * 60;
        long Seconds = TimeUnit.MILLISECONDS.toSeconds(milliSeconds) - 
        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliSeconds));

        if (Days != 0) {
            return String.format("%02d 일, %02d 시간, %02d 분, %02d 초", Days, Hours, Minutes, Seconds);
        } else if (Hours != 0) {
            return String.format("%02d 시간, %02d 분, %02d 초", Hours, Minutes, Seconds);
        } else if (Minutes != 0) {
            return String.format("%02d 분, %02d 초",  Minutes, Seconds);
        } else {
            return String.format("%02d 초", Seconds);
        }
    }

    /**
     * returns today's formatted form
     * @return "yyyy-MM-dd"
     */
    public static String getTodayFormatted() {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(date);
    }

    /**
     * 현재부터 특정 시간까지 남은 밀리세컨드를 리턴
     * @param time 목표 날짜
     * @return 남은 밀리세턴드 시간
     */
    public static long getTimeRemaining(Calendar time) {
        return time.getTimeInMillis() - System.currentTimeMillis();
    }
}