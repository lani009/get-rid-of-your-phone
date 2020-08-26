package org.phonedetector;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.RaspiPin;

public class GPIO implements Runnable {
    private Socket socket = null;
    private RpiSocket rpiSocket;
    private OutputStream socketOutputStream;

    // private static final int PUSHED = 0;
    private static final int UNPUSHED = 1;

    private long originTime;
    private int originState;
    private boolean isOrigin = true;
    private int originData = 1;
    private boolean isAlerted = false;
    private int data;

    public GPIO(RpiSocket rpiSocket) {
        this.rpiSocket = rpiSocket;
    }

    @Override
    public void run() {
        try {
            synchronized (rpiSocket) {
                rpiSocket.wait(10000);
                Thread.sleep(1000);
            }
            socket = new Socket("localhost", 8887);
            System.out.println("client socket connetcion success");
            socketOutputStream = socket.getOutputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        originState = UNPUSHED;

        GpioController con = GpioFactory.getInstance();
        GpioPinDigitalInput in = con.provisionDigitalInputPin(RaspiPin.GPIO_29);
        in.setShutdownOptions(true);

        originState = UNPUSHED;
        int inputIO;
        while (true) {
            inputIO = in.getState().getValue();
            if (isOrigin && inputIO == originState) {
                originTime = System.currentTimeMillis();
                isOrigin = false;
            } else if (inputIO == originState) {
                if (isTimeoever(originTime)) {
                    if (inputIO != 0) {
                        data = 1;
                    } else {
                        data = 2;
                    }

                    if (originData != data) {
                        isAlerted = false;
                        originData = data;
                    }

                    if (!isAlerted) {
                        try {
                            socketOutputStream.write(data);
                            socketOutputStream.flush();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        isAlerted = true;
                    }
                }
            } else {
                originState = inputIO;
                isOrigin = true;
            }
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean isTimeoever(long time) {
        return System.currentTimeMillis() - time > 3000;
    }

}