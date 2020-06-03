package org.phonedetector.exceptions;

@SuppressWarnings("serial")
public class SocketClosedException extends Exception {
    
    public SocketClosedException(String msg) {
        super(msg);
    }
}