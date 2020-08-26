package org.phonedetector;

import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.CategoryChartBuilder;
import org.knowm.xchart.BitmapEncoder.BitmapFormat;
import org.knowm.xchart.style.Styler.ChartTheme;
import org.phonedetector.jdbc.InfoDAO;
import org.phonedetector.struct.StudyTime;

/**
 * 주간 공부량을 알려준다. 일요일 오후 4시에 실행
 */
public class WeeklyResultAlert implements Runnable {

    @Override
    public void run() {
        CategoryChart chart;

        while (true) {
            try {
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DATE, 7);                         // 다음주
                calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);    // 일요일
                calendar.set(Calendar.HOUR, 4);                         // 4시
                calendar.set(Calendar.MINUTE, 0);                       // 0분
                calendar.set(Calendar.SECOND, 0);                       // 0초
                calendar.set(Calendar.AM_PM, Calendar.PM);              // 오후
                sleep(TimeCalculator.getTimeRemaining(calendar));// 까지 주무세요
            } catch (InterruptedException e) {
                e.printStackTrace();
                MessageSender.getInstance().sendMessage(e.toString(), InfoDAO.getInstance().getSuperUserList());
            }

            chart = new CategoryChartBuilder().height(800).width(600).title("주간 공부량").theme(ChartTheme.GGPlot2)
                    .xAxisTitle("요일").yAxisTitle("시간").build();

            chart.getStyler().setLegendVisible(false);

            List<List<StudyTime>> studyList = InfoDAO.getInstance().getWeeklyStudyTimes();
            Long[] studyTimeSum = new Long[7]; // 요일별 누적 공부량 (시간)
            Arrays.fill(studyTimeSum, 0);

            int index = 0;
            for (List<StudyTime> dateStudyList : studyList) {
                for (StudyTime studyTime : dateStudyList) {
                    studyTimeSum[index] += studyTime.getTimeDelta();
                }
                studyTimeSum[index] /= 3600 * 1000;
                index++;
            }
            chart.addSeries("공부량", Arrays.asList(new String[] { "일", "월", "화", "수", "목", "금", "토" }),
                    Arrays.asList(studyTimeSum));

            try {
                BitmapEncoder.saveBitmap(chart, "./studyImg/" + TimeCalculator.getTodayFormatted(), BitmapFormat.JPG);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // 이미지 전송
            MyBot.getInstance().sendPhoto("./studyImg" + TimeCalculator.getTodayFormatted() + ".jpg", InfoDAO.getInstance().getUserTelegramIdList());
        }
    }

    private void sleep(long millis) throws InterruptedException {
        System.out.println("WeeklyResultAlert Sleep Until: " + TimeCalculator.getMilliToFormatted(millis));
        Thread.sleep(millis);
    }
    
}