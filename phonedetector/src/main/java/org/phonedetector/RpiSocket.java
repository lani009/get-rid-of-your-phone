package org.phonedetector;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.phonedetector.exceptions.SocketClosedException;
import org.phonedetector.interfaces.Socketable;

public class RpiSocket implements Socketable {
    private ServerSocket s_socket;
    private Socket c_socket;

    public RpiSocket(int port) throws IOException {
        s_socket = new ServerSocket(port);
        System.out.println("Waiting for connection");
        c_socket = s_socket.accept();
        System.out.println("Socket Accepted!");
    }

    /**
     * Get Data from Raspberry pi socket connection
     * 1: phone is not in the PhoneCase
     * 2: phone returned to the PhoneCase
     * 0: connection closed
     * @return
     * @throws IOException
     */
    private byte[] getData() throws IOException {
        InputStream data = c_socket.getInputStream();
        byte[] receiveBuffer = new byte[10];
        data.read(receiveBuffer);
        System.out.println("got Data " + receiveBuffer[0]);
        return receiveBuffer;
    }

    private int getIntData() throws IOException {
        return getData()[0];

    }

    /**
     * 폰 제출의 상태 확인.
     * false 일 경우 폰 제출 안함
     * true 일 경우 폰 제출 함
     */
    @Override
    public boolean getStatus() throws SocketClosedException {
        int data = 0;
        try {
            data = getIntData();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        if(data == 1)
            return false;
        else if(data == 2)
            return true;
        else
            throw new SocketClosedException("Socket Closed");
    }
}