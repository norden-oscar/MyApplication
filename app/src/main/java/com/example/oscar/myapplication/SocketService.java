package com.example.oscar.myapplication;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by Oscar on 2015-12-29.
 */
public class SocketService extends Service {
    private final IBinder myBinder = new MyLocalBinder();
    private Socket server;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return myBinder;
    }
    public void createSocket(String hostname,int port) throws IOException {
        server = new Socket(hostname,port);
    }
    public Socket getSocket(){
        return server;
    }
    public class MyLocalBinder extends Binder {
        SocketService getService() {
            return SocketService.this;
        }
    }
}
