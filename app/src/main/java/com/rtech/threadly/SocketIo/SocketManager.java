package com.rtech.threadly.SocketIo;

import com.rtech.threadly.BuildConfig;
import com.rtech.threadly.constants.ApiEndPoints;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

public class SocketManager {
    private static SocketManager instance;
    private final Socket msocket;
private final String socketIo= BuildConfig.SOCKET_URL;

    private SocketManager() {
        try {
            msocket = IO.socket(socketIo);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static synchronized SocketManager getInstance() {
        if (instance == null) {
            instance = new SocketManager();
        }
        return instance;

    }

    public Socket getSocket() {
        return msocket;
    }

    public void connect() {
        msocket.connect();
    }

    public void disconnect() {
        msocket.disconnect();
        instance = null;
    }

}
