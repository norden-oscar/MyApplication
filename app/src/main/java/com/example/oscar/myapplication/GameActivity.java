package com.example.oscar.myapplication;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.concurrent.LinkedBlockingQueue;

public class GameActivity extends AppCompatActivity {
    private static final String TAG = "GameActivity";
    SocketService socketService;
    boolean isBound = false;
    LinkedBlockingQueue<String> messages = new LinkedBlockingQueue<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        Button guessButton = (Button) findViewById(R.id.btnGuess);
        Button startButton = (Button) findViewById(R.id.btnStart);
        guessButton.setOnClickListener(buttonHandler);
        startButton.setOnClickListener(buttonHandler);
        guessButton.setClickable(false);
        guessButton.setEnabled(false);
        Intent socketServiceIntent = new Intent(this, SocketService.class);
        bindService(socketServiceIntent, myConnection, Context.BIND_AUTO_CREATE);
    }

    private ServiceConnection myConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            SocketService.MyLocalBinder binder = (SocketService.MyLocalBinder) service;
            socketService = binder.getService();
            isBound = true;
        }

        public void onServiceDisconnected(ComponentName arg0) {
            isBound = false;
        }
    };

    View.OnClickListener buttonHandler = new View.OnClickListener() {
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.btnGuess:
                    try {
                        EditText guessField = (EditText) findViewById(R.id.inputGuess);
                        addMessage(guessField.getText().toString());
                        new SendToServer().execute(messages.take());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
                case R.id.btnStart:
                    new SendToServer().execute("startgame");
                    break;
            }
        }
    };

    void addMessage(String message) {
        messages.add(message);

    }

    protected class SendToServer extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... message) {
            String msg = message[0];

            return socketService.sendMessage(msg) + "|" + msg;
        }

        protected void onPostExecute(String result) {

            if (result.equals("hope you had fun playing")) {
                System.exit(0);
            }
            if (result == null) {
                System.exit(0);
            }
            System.out.println(result);
            String[] results = result.split("\\|");

            Button startButton = (Button) findViewById(R.id.btnStart);
            Button guessButton = (Button) findViewById(R.id.btnGuess);
            TextView guessField = (TextView) findViewById(R.id.showState);
            TextView life = (TextView) findViewById(R.id.textLife);
            TextView total = (TextView) findViewById(R.id.textTotalNumber);

            if (results[2].equals("startgame")) {        // om det var ett start meddelande vi skickade, gÃ¶r start callback

                startButton.setClickable(false);
                startButton.setEnabled(false);
                guessField.setText(results[0]);
                life.setText(results[1]);
                guessButton.setClickable(true);
                guessButton.setEnabled(true);

            } else if (results[0].contains("[")) {     // om det ska fortsÃ¤tta gissas
                guessField.setText(results[0]);
                life.setText(results[1]);
                guessField.setText("");


            } else if (results[0].contains("Congratulations") || results[0].contains("Game over!")) {      // om spelet Ã¤r slut
                guessField.setText(results[0]);
                life.setText("0");
                startButton.setClickable(true);
                startButton.setEnabled(true);
                guessButton.setClickable(false);
                guessButton.setEnabled(false);
                total.setText(results[1]);
            }
        }
    }
}