package org.phonedetector.interfaces;

import org.phonedetector.exceptions.SocketClosedException;

public interface Socketable {
    public boolean getStatus() throws SocketClosedException;
}