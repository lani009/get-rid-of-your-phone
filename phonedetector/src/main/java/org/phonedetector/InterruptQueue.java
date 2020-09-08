package org.phonedetector;

import java.util.LinkedList;

import org.phonedetector.struct.Caller;

@SuppressWarnings("serial")
public class InterruptQueue extends LinkedList<Caller> {
    private InterruptQueue() { }

    public static InterruptQueue getInstance() {
        return Holder.INSTANCE;
    }

    private static class Holder {
        private static InterruptQueue INSTANCE = new InterruptQueue();
    }
}
