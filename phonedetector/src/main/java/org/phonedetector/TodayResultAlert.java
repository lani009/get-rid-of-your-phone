package org.phonedetector;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * Thread를 통해 오후 11시가 되면 하루 일과 총 정리를 보낸다.
 */
public class TodayResultAlert implements Runnable {
    private MessageSender msgSender;
    private MyBot bot;
    public TodayResultAlert() {
        msgSender = null;
    }

    /**
     * set MessageSender Instance
     * @param msgsSender
     * @return TodayResultAlert
     */
    public TodayResultAlert setMessageSender(MessageSender msgSender) {
        this.msgSender = msgSender;
        return this;
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

    private void sleep(int milli) {
        try {
            Thread.sleep(new Long(milli));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void alertSend() {
        /*
        오늘의 종합 보고
        1. 제출시간: 09:23:42
           회수시간: 10:24:01
           총 시간: 3시간 01분 35초

        2. 제출시간: 09:23:42
           회수시간: 10:24:01
           총 시간: 3시간 01분 35초
        */
        StringBuilder sb = new StringBuilder("오늘의 종합 보고\n");
        ResultReader result = new ResultReader();
        int cnt = 0;
        while(result.next() != -1) {
            sb.append(1 + (cnt++));
            sb.append(". ");
            sb.append("제출시간: ");
            sb.append(result.getStartTime());
            sb.append("\n    회수시간: ");
            sb.append(result.getEndTime());
            sb.append("\n    총 시간: ");
            sb.append(getMilliToFormat(result.getDuration()));
            sb.append("\n\n");
        }
        try {
            bot.sendMessage(sb.toString());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        
    }

    private String getMilliToFormat(long milli) {
        long Hours = TimeUnit.MILLISECONDS.toHours(milli);
        long Minutes = TimeUnit.MILLISECONDS.toMinutes(milli);
        long Seconds = TimeUnit.MILLISECONDS.toSeconds(milli) - 
        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milli));
        if(Hours == 0) {
            return String.format("%02d 분, %02d 초",  Minutes, Seconds);
        }
        else {
            return String.format("%02d 시간, %02d 분, %02d 초", Hours, Minutes, Seconds);
        }
    }
}