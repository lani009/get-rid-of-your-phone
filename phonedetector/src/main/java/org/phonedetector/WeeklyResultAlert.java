package org.phonedetector;

import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

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
    final String[] DAYSTRINGS = new String[] { "일", "월", "화", "수", "목", "금", "토" };
    @Override
    public void run() {
        CategoryChart chart;
        Calendar calendarStart = null;
        Calendar calendarEnd = null;
        while (true) {
            try {
                calendarEnd = Calendar.getInstance();
                calendarEnd.add(Calendar.DATE, 7);                         // 다음주
                calendarEnd.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);    // 일요일
                calendarEnd.set(Calendar.HOUR, 4);                         // 4시
                calendarEnd.set(Calendar.MINUTE, 0);                       // 0분
                calendarEnd.set(Calendar.SECOND, 0);                       // 0초
                calendarEnd.set(Calendar.AM_PM, Calendar.PM);              // 오후
                sleep(TimeCalculator.getTimeRemaining(calendarEnd));       // 까지 주무세요
            } catch (InterruptedException e) {
                // 사용자 인터럽트
                System.out.println("Weekly Result Alert 임의 실행");
                calendarEnd = Calendar.getInstance();
                calendarEnd.add(Calendar.DATE, -1);  // 캘린더 어제 날짜로 초기화
            }

            calendarStart = (Calendar) calendarEnd.clone();
            calendarStart.add(Calendar.DATE, -7);   // 일주일 전으로 초기화

            // 차트 생성
            chart = new CategoryChartBuilder().height(800).width(600).title("주간 공부량").theme(ChartTheme.GGPlot2)
                    .xAxisTitle("요일").yAxisTitle("시간").build();

            chart.getStyler().setLegendVisible(false);  // 범례 비활성화

            Map<Date, List<StudyTime>> studyList = InfoDAO.getInstance().getIntervalStudyTimes(calendarStart, calendarEnd);
            double[] studyTimeSum = new double[7]; // 요일별 누적 공부량 (시간)
            Arrays.fill(studyTimeSum, 0.0);

            int index = 0;

            System.out.println(studyList.keySet());

            // studyTimeSum(요일별 누적 공부량) 합산
            for (Date key : studyList.keySet().toArray(new Date[studyList.size()])) {
                List<StudyTime> dateStudyList = studyList.get(key);
                if (dateStudyList == null) {
                    studyTimeSum[index] = 0;
                } else {
                    for (StudyTime studyTime : dateStudyList) {
                        studyTimeSum[index] += studyTime.getTimeDelta();
                    }
                }
                index++;
            }

            // millisec를 시간 단위로 환산
            for (int i = 0; i < studyTimeSum.length; i++) {
                studyTimeSum[i] = studyTimeSum[i] / (1000 * 3600);
            }

            int dayOfWeek = calendarStart.get(Calendar.DAY_OF_WEEK);
            // 어제 날짜 - 6일을 기준으로 요일 정렬
            List<String> daysList = new ArrayList<>();
            for (int i = dayOfWeek - 1; i < 7 + dayOfWeek - 1; i++) {
                daysList.add(DAYSTRINGS[i % 7]);
            }

            chart.addSeries("공부량", daysList, DoubleStream.of(studyTimeSum).boxed().collect(Collectors.toList()));

            try {
                BitmapEncoder.saveBitmap(chart, "./studyImg/" + TimeCalculator.getTodayFormatted(), BitmapFormat.PNG);
                System.out.println("이미지 생성 완료");
            } catch (IOException e) {
                e.printStackTrace();
                MessageSender.getInstance().sendMessage(e.toString(), InfoDAO.getInstance().getSuperUserList());
            }

            // 이미지 전송
            MyBot.getInstance().sendPhoto(new File("./studyImg/" + TimeCalculator.getTodayFormatted() + ".png"), InfoDAO.getInstance().getUserTelegramIdList());
        }
    }

    private void sleep(long millis) throws InterruptedException {
        System.out.println("WeeklyResultAlert Sleep Until: " + TimeCalculator.getMilliToFormatted(millis));
        Thread.sleep(millis);
    }
    
    @Override
    public String toString() {
        return "Weekly Result Alert";
    }
}