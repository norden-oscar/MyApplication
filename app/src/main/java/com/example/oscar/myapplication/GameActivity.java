package com.example.oscar.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class GameActivity extends AppCompatActivity {
    private static final String TAG = "GameActivity";
    String  hostname;
    int portNumber;
    Socket socket = null;
    PrintWriter out = null;
    BufferedReader in = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

    }
    public void btnStartOnClick(){

        //out.println("startgame");
        //System.out.println("startbutton");
    }
}
