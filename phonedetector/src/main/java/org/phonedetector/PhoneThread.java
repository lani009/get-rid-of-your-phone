package org.phonedetector;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

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
        while(true) {
            try {
                byte[] byteData = socket.getData();
                int intData = byteData[0];
                System.out.println(intData);
                //핸드폰 제출 안했을 때
                if(intData == 0) {
                    lucy.setDoPhone(false);
                    messageSender.sendMessage(String.format("폰을 회수했습니다! 반납 유지 시간: %s", lucy.getFormattedReturnTimeDelta()));
                }
                else {
                    lucy.setDoPhone(true);
                    lucy.clearReturnTime();
                    messageSender.sendMessage("폰을 반납했습니다!");
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
                if(e.getMessage().equals("Connection reset")) { break; }
            }
        }
    }

    // private int byteArrayToInt(byte[] bytes) {
    //     final int size = Integer.SIZE / 8;
    //     final byte[] newBytes = new byte[size];
    //     for (int i = 0; i < size; i++) {
    //             if(i + bytes.length < size) {
    //                 newBytes[i] = (byte) 0x00;
    //             }
    //             else {
    //                 newBytes[i] = bytes[i + bytes.length - size];
    //             }
    //     }
    //     ByteBuffer buff = ByteBuffer.wrap(newBytes);
    //     buff.order(ByteOrder.BIG_ENDIAN);
    //     return buff.getInt();
    // }

}

/**
 * 핸드폰을 제출했는지, 제출하지 않았는지
 * 제출한 시간은 어떻게 되는지를 저장하는 클래스.
 * 
 * 동생 영어이름이 Lucy이다.
 */
class Lucy {
    private boolean returnPhone;    //폰을 제출 하였나? True->제출함.
    private long returnTime;    //폰을 제출 한 시간.

    public Lucy() {
        this.returnPhone = false;
    }

    /**
     * set if she has phone.
     * @param state, 제출하였으면 True
     */
    public void setDoPhone(boolean state) {
        returnPhone = state;
    }

    public void clearReturnTime() {
        returnTime = System.currentTimeMillis();
    }

    /**
     * 동생이 폰을 하고 있는지 안하고 있는지 체크
     * @return true if she hasen't returned her phone. As so reversal.
     */
    public boolean getReturnPhone() {
        return returnPhone;
    }

    protected long getReturnTimeDelta() {
        return System.currentTimeMillis() - returnTime;
    }

    public String getFormattedReturnTimeDelta() {
        return String.format("%02d 분, %02d 초", 
        TimeUnit.MILLISECONDS.toMinutes(getReturnTimeDelta()),
        TimeUnit.MILLISECONDS.toSeconds(getReturnTimeDelta()) - 
        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(getReturnTimeDelta()))
    );
    }
}