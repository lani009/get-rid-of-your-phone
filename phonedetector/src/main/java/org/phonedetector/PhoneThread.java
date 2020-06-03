package org.phonedetector;

import java.io.IOException;

import org.phonedetector.exceptions.SocketClosedException;
import org.phonedetector.interfaces.MessageSendable;
import org.phonedetector.interfaces.Socketable;

/**
 * 버튼과 통신하여 사용자가 폰을 제출하였나 제출하지 않았나를 감지.
 * 사용자에게 메시지를 전달한다.
 */
public class PhoneThread implements Runnable {
    private Socketable socket;
    private MessageSendable messageSender;
    private Lucy lucy;

    public PhoneThread setMessageSender(MessageSendable messageSender) {
        this.messageSender = messageSender;
        return this;
    }

    public PhoneThread setLucy(Lucy lucy) {
        this.lucy = lucy;
        return this;
    }

    public PhoneThread setSocket(Socketable socket) {
        this.socket = socket;
        return this;
    }

    @Override
    public void run() {
        InfoSaver infoSaver = new InfoSaver("./jsonData");
        while(true) {
            try {
                boolean isReturned = socket.getStatus();
                //핸드폰 회수함. 폰을 안하고 있던 상태여야함
                if(!isReturned && !lucy.getDoPhone()) {
                    lucy.setDoPhone(true);
                    messageSender.sendMessageAll(String.format("폰을 가져갔습니다! 반납 유지 시간: %s",
                                    TimeCalculator.getMilliToFormatted(lucy.getReturnTimeDelta())));
                    infoSaver.saveData(lucy.getReturnTimeDelta());
                    System.out.println("\n폰을 가져감.");
                }
                else if(isReturned && lucy.getDoPhone()) {
                    lucy.setDoPhone(false);
                    lucy.clearReturnTime();
                    messageSender.sendMessageAll("폰을 제출했습니다!");
                    System.out.println("\n폰을 제출함.");
                }
            } catch (SocketClosedException e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
    }
}