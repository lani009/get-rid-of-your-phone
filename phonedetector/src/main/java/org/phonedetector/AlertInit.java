package org.phonedetector;

public class AlertInit {
    public static void init(Runnable...runnables) {
        for (Runnable runnable : runnables) {
            new Thread(runnable, runnable.toString()).start();
        }
    }
}