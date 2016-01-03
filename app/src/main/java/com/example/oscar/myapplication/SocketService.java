package com.example.oscar.myapplication;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by Oscar on 2015-12-29.
 */
public class SocketService extends Service {
    private final IBinder myBinder = new MyLocalBinder();
    private Socket server;
    private PrintWriter out;
    private BufferedReader in;
    private String answer;
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
    public String sendMessage(String message){

        new SendMessageTask().execute(message);
        return answer;
    }
    public class MyLocalBinder extends Binder {
        SocketService getService() {
            return SocketService.this;
        }
    }
    private class SendMessageTask extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... message) {
            String line = null;
            String msg = message[0];
            out.println(msg);
            try {
                line = in.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return line;
        }
        protected void onPostExecute(String line){
            answer = line;
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
                out = new PrintWriter(socket.getOutputStream());
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
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
