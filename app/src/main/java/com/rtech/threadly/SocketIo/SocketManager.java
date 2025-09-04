package com.rtech.threadly.SocketIo;

import com.rtech.threadly.constants.ApiEndPoints;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class SocketManager {
    private static SocketManager instance;
    private final Socket msocket;

    private SocketManager(){
        try {
            msocket= IO.socket(ApiEndPoints.SOCKET_ID);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
    public static synchronized SocketManager getInstance(){
        if(instance==null){
            instance=new SocketManager();
        }
        return instance;

    }
    public Socket getSocket(){
        return  msocket;
    }
    public void connect(){
        msocket.connect();
    }
    public void disconnect(){
        msocket.disconnect();
    }






}
