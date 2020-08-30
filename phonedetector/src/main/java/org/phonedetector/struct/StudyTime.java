package org.phonedetector.struct;

import java.sql.Time;
import java.sql.Date;

public class StudyTime {
    /**
     * 폰을 반납한 시각
     */
    private Time returnTime = null;
    /**
     * 폰을 가져간 시각
     */
    private Time retriveTime = null;
    /**
     * 날짜
     */
    private Date date = null;

    public StudyTime(Time returnTime, Time retriveTime, Date date) {
        this.returnTime = returnTime;
        this.retriveTime = retriveTime;
        this.date = date;
    }

    /**
     * 폰을 반납한 시간
     * @return 폰을 반납한 시간
     */
    public Time getReturnTime() {
        return this.returnTime;
    }

    /**
     * 폰을 가져간 시간
     * @return 폰을 가져간 시간
     */
    public Time getRetriveTime() {
        return this.retriveTime;
    }

    /**
     * 날짜 리턴
     * @return 날짜
     */
    public Date getDate() {
        return this.date;
    }

    /**
     * 반납한 시간과 가져간 시간의 차이. 즉 얼마나 반납했었는지를 리턴한다
     * @return 반납한 시간과 가져간 시간의 차이
     */
    public long getTimeDelta() {
        return getRetriveTime().getTime() - getReturnTime().getTime();
    }
}