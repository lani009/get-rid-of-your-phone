package org.phonedetector;

import java.util.ArrayList;
import java.util.List;

import org.phonedetector.struct.Caller;

/**
 * Thread를 사용하는 알리미들 초기화 + 인터럽트
 */
public class AlertInit {
    private static List<Thread> threads = new ArrayList<>();

    /**
     * Runnable을 구현하는 알리미를 매개변수에 넣으면 스레드 스타트.
     * @param runnables 알리미
     */
    public static void init(Runnable...runnables) {
        for (Runnable runnable : runnables) {
            Thread temp = new Thread(runnable, runnable.toString());
            temp.start();
            threads.add(temp);
        }
    }

    /**
     * 주간 알리미 스레드 인터럽트
     * @param id 실행한 유저의 id
     */
    public static void weeklyAlertInterupt(String id) {
        for (Thread thread : threads) {
            if (thread.getName().equals("Weekly Result Alert")) {
                InterruptQueue.getInstance().add(new Caller("Weekly Result Alert", id));
                thread.interrupt();
                return;
            }
        }
    }

    /**
     * 일간 알리미 스레드 인터럽트
     * @param id 실행한 유저의 id
     */
    public static void dailyAlertInterupt(String id) {
        // TODO
    }
}