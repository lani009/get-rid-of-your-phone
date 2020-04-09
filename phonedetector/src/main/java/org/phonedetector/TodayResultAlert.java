package org.phonedetector;

import java.util.Calendar;

import org.phonedetector.exceptions.NoTodayJsonException;

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

    private int alertSend() {
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
        ResultReader result;
        try {
            result = new ResultReader();
        } catch (NoTodayJsonException e) {
            sb.append("오늘은 핸드폰을 제출하지 않았습니다.");
            bot.sendMessage(sb.toString());
            return -1;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
        int cnt = 0;
        long durationTotal = 0;
        long duration;
        while(result.next() != -1) {
            sb.append(1 + (cnt++));
            sb.append(". ");
            sb.append("제출시간: ");
            sb.append(result.getStartTime());
            sb.append("\n    회수시간: ");
            sb.append(result.getEndTime());
            sb.append("\n    총 시간: ");
            duration = result.getDuration();
            durationTotal += duration;
            sb.append(TimeCalculator.getMilliToFormatted(duration));
            sb.append("\n\n");
        }
        sb.append("오늘의 총 시간: ");
        sb.append(TimeCalculator.getMilliToFormatted(durationTotal));
        try {
            bot.sendMessage(sb.toString());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return 0;
        
    }
}