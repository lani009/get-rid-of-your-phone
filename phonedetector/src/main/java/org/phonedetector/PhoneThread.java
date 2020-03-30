package org.phonedetector;

import java.io.IOException;

/**
 * socket으로 부터 데이터를 전송받을 때 마다 응답하기 위한 클래스
 */
public class PhoneThread implements Runnable {
    private RpiSocket socket;
    private MessageSender messageSender;
    private Lucy lucy;
    /**
     * @param RpiSocket instance
     */
    public PhoneThread() {
    }

    public PhoneThread setSocket(RpiSocket rpiSocket) {
        this.socket = rpiSocket;
        return this;
    }

    public PhoneThread setMessageSender(MessageSender messageSender) {
        this.messageSender = messageSender;
        return this;
    }

    public PhoneThread setLucy(Lucy lucy) {
        this.lucy = lucy;
        return this;
    }

    @Override
    public void run() {
        InfoSaver infoSaver = new InfoSaver("./jsonData");
        while(true) {
            try {
                int intData = socket.getIntData();
                //핸드폰 회수함. 폰을 안하고 있던 상태여야함
                if(intData == 1 && !lucy.getDoPhone()) {
                    lucy.setDoPhone(true);
                    messageSender.sendMessage(String.format("폰을 회수했습니다! 반납 유지 시간: %s", TimeCalculator.getMilliToFormatted(lucy.getReturnTimeDelta())));
                    infoSaver.saveData(lucy.getReturnTimeDelta());
                    System.out.println("\n폰을 회수함.");
                }
                else if(intData == 2 && lucy.getDoPhone()) {
                    lucy.setDoPhone(false);
                    lucy.clearReturnTime();
                    messageSender.sendMessage("폰을 반납했습니다!");
                    System.out.println("\n폰을 반납함.");
                }
                else if(intData == 0) {
                    return;
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}

/**
 * 핸드폰을 제출했는지, 제출하지 않았는지
 * 제출한 시간은 어떻게 되는지를 저장하는 클래스.
 * 
 * 동생 영어이름이 Lucy이다.
 */
class Lucy {
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
     * 만약 폰을 제출 하지 않았다(폰을 하고 있다) -> return true
     * @return true if she hasen't returned her phone. As so reversal.
     */
    public boolean getDoPhone() {
        return doPhone;
    }

    protected long getReturnTimeDelta() {
        return System.currentTimeMillis() - returnTime;
    }
}