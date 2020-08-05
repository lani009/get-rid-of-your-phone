package org.phonedetector.interfaces;

import org.phonedetector.exceptions.SocketClosedException;

public interface Socketable {
    /**
     * 폰 제출의 상태 확인.
     * false 일 경우 폰 제출 안함
     * true 일 경우 폰 제출 함
     */
    public boolean getStatus() throws SocketClosedException;
}