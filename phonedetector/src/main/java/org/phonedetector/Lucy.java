package org.phonedetector;

/**
 * 핸드폰을 제출했는지, 제출하지 않았는지
 * 제출한 시간은 어떻게 되는지를 저장하는 클래스.
 * 
 * 동생 영어이름이 Lucy이다.
 */
public class Lucy {
    private boolean doPhone;   
    private long returnTime;    //폰을 제출 한 시간.

    public Lucy() {
        this.doPhone = true;    //폰을 하고 있던 것으로 가정
    }

    /**
     * set if she has phone.
     * @param state, 제출하였으면 False
     */
    public void setDoPhone(boolean state) {
        doPhone = state;
    }

    public void clearReturnTime() {
        returnTime = System.currentTimeMillis();
    }

    /**
     * 동생이 폰을 하고 있는지 안하고 있는지 체크
     * 만약 폰을 제출 하지 않았다(폰을 하고 있다): return true
     * @return true if she hasen't returned her phone. As so reversal.
     */
    public boolean getDoPhone() {
        return doPhone;
    }

    long getReturnTimeDelta() {
        return System.currentTimeMillis() - returnTime;
    }
}