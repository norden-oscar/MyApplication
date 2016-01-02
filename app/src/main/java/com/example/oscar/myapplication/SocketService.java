package com.example.oscar.myapplication;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
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
       // server = new Socket(hostname,port);
        new ConnectSocketTask().execute(hostname,port);
    }
    public Socket getSocket(){
        return server;
    }
    public class MyLocalBinder extends Binder {
        SocketService getService() {
            return SocketService.this;
        }
    }
    private class ConnectSocketTask extends AsyncTask<Object,Void,Socket>{


        @Override
        protected Socket doInBackground(Object... params) {
            String hostname = (String) params[0];
            int port = (int) params[1];
            Socket socket = null;
            try {
                socket = new Socket(hostname, port);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return socket;
        }
        protected void onPostExecute(Socket socket){
            server = socket;
        }
    }
}
