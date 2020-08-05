package org.phonedetector.struct;

import java.sql.Time;

public class StudyTime {
    /**
     * 폰을 반납한 시각
     */
    private Time returnTime = null;
    /**
     * 폰을 가져간 시각
     */
    private Time retriveTime = null;

    public StudyTime(Time returnTime, Time retriveTime) {
        this.returnTime = returnTime;
        this.retriveTime = retriveTime;
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
     * 반납한 시간과 가져간 시간의 차이. 즉 얼마나 반납했었는지를 리턴한다
     * @return 반납한 시간과 가져간 시간의 차이
     */
    public long getTimeDelta() {
        return getRetriveTime().getTime() - getReturnTime().getTime();
    }
}