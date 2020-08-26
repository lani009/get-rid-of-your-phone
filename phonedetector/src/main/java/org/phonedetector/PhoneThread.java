package org.phonedetector;

import org.phonedetector.exceptions.SocketClosedException;
import org.phonedetector.interfaces.Socketable;
import org.phonedetector.jdbc.InfoDAO;

/**
 * 버튼과 통신하여 사용자가 폰을 제출하였나 제출하지 않았나를 감지.
 * 사용자에게 메시지를 전달한다.
 */
public class PhoneThread implements Runnable {
    private Socketable socket;

    public PhoneThread setSocket(Socketable socket) {
        this.socket = socket;
        return this;
    }

    @Override
    public void run() {
        while(true) {
            try {
                boolean isReturned = socket.getStatus();    // true일 경우 폰 제출함
                if (isReturned) {
                    // 폰을 제출했을 경우
                    InfoDAO.getInstance().setReturnTime();
                    MessageSender.getInstance().sendMessageAll("폰을 제출했습니다!");
                } else {
                    // 폰을 다시 가져간 경우
                    MessageSender.getInstance().sendMessageAll(String.format("폰을 가져갔습니다! 반납 유지 시간: %s",
                    TimeCalculator.getMilliToFormatted(InfoDAO.getInstance().setRetriveTime().getTimeDelta())));
                }
            } catch (SocketClosedException e) {
                e.printStackTrace();
                MessageSender.getInstance().sendMessage(e.toString(), InfoDAO.getInstance().getSuperUserList());
            }
        }
    }
}