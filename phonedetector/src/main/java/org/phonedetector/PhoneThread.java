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
                    messageSender.sendMessage(String.format("폰을 가져갔습니다! 반납 유지 시간: %s", TimeCalculator.getMilliToFormatted(lucy.getReturnTimeDelta())));
                    infoSaver.saveData(lucy.getReturnTimeDelta());
                    System.out.println("\n폰을 가져감.");
                }
                else if(intData == 2 && lucy.getDoPhone()) {
                    lucy.setDoPhone(false);
                    lucy.clearReturnTime();
                    messageSender.sendMessage("폰을 제출했습니다!");
                    System.out.println("\n폰을 제출함.");
                }
                else if(intData == 0) {
                    return;
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }
    }
}