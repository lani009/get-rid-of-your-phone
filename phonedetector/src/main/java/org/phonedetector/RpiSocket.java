package org.phonedetector;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class RpiSocket {
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
    public byte[] getData() throws IOException {
        InputStream data = c_socket.getInputStream();
        byte[] receiveBuffer = new byte[10];
        data.read(receiveBuffer);
        System.out.println("got Data " + receiveBuffer[0]);
        return receiveBuffer;
    }

    public int getIntData() throws IOException {
        return getData()[0];
    }
}