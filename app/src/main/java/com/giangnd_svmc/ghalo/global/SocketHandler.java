package com.giangnd_svmc.ghalo.global;

/**
 * Created by GIANGND-SVMC on 19/01/2016.
 */
import com.github.nkzawa.socketio.client.Socket;

public class SocketHandler {
    private static Socket socket;

    public static synchronized Socket getSocket(){
        return socket;
    }

    public static synchronized void setSocket(Socket socket){
        SocketHandler.socket = socket;
    }
}