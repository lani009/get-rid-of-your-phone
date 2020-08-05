package org.phonedetector;

import java.text.SimpleDateFormat;
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
        long Hours = TimeUnit.MILLISECONDS.toHours(milliSeconds);
        long Minutes = TimeUnit.MILLISECONDS.toMinutes(milliSeconds) - Hours * 60;
        long Seconds = TimeUnit.MILLISECONDS.toSeconds(milliSeconds) - 
        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliSeconds));

        if(Hours != 0) {
            return String.format("%02d 시간, %02d 분, %02d 초", Hours, Minutes, Seconds);
            
        }
        else if(Minutes != 0) {
            return String.format("%02d 분, %02d 초",  Minutes, Seconds);
        }
        else {
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
}