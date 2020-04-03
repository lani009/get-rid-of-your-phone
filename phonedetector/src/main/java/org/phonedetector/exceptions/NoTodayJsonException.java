package org.phonedetector.exceptions;

/**
 * 오늘의 JSON Result File가 없을 때 발생하는 Excetpion.
 * 오늘의 공부가 없을 때 발생한다.
 */
public class NoTodayJsonException extends Exception{
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public NoTodayJsonException(String msg) {
        super(msg);
    }
}