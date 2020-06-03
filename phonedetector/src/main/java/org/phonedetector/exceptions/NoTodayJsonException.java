package org.phonedetector.exceptions;

/**
 * 오늘의 JSON Result File가 없을 때 발생하는 Excetpion.
 * 오늘의 공부가 없을 때 발생한다.
 */
@SuppressWarnings("serial")
public class NoTodayJsonException extends Exception{

    public NoTodayJsonException(String msg) {
        super(msg);
    }
}