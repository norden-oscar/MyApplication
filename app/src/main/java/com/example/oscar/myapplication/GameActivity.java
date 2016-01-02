package com.example.oscar.myapplication;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;

public class GameActivity extends AppCompatActivity {
    private static final String TAG = "GameActivity";
    String hostname;
    int portNumber;
    Socket socket = null;
    PrintWriter out = null;
    BufferedReader in = null;

    private static class Params {
        PrintWriter pw;
        BufferedReader br;
        String message;

        Params(PrintWriter pw, BufferedReader br, String message) {
            this.pw = pw;
            this.br = br;
            this.message = message;
        }
    }

    LinkedBlockingQueue<String> messages = new LinkedBlockingQueue<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        hostname = getIntent().getStringExtra("hostname");
        portNumber = getIntent().getIntExtra("port", 0);
        Button guessButton = (Button) findViewById(R.id.btnGuess);
        guessButton.setClickable(false);
        //Log.v(TAG, "hostname=" + hostname);
        //Log.v(TAG, "port=" + portNumber);
        try {
            socket = new Socket(hostname, portNumber);
            out = new PrintWriter(socket.getOutputStream());
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    View.OnClickListener buttonHandler = new View.OnClickListener() {
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.btnGuess:
                    try {
                            addMessage("guess");
                            Params params = new Params(out, in, messages.take());
                            new SendToServer().execute(params);
                        }
                    catch (InterruptedException e){
                        e.printStackTrace();
                    }

                    break;
                case R.id.btnStart:
                    try {
                        Params params = new Params(out, in, messages.take());
                        new SendToServer().execute(params);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };

    void addMessage(String message) {
        messages.add(message);

    }

    protected class SendToServer extends AsyncTask<Params, Void, String[]> {

        protected String[] doInBackground(Params... params) {

            PrintWriter printer = params[0].pw;
            BufferedReader reader = params[0].br;
            String msg = params[0].message;

            printer.print(msg);
            printer.flush();
            String line;
            try {
                line = reader.readLine();
            }
            catch (IOException e){
                e.printStackTrace();
                line = null;
            }
            if (line.equals("hope you had fun playing")) {
                System.exit(0);
            }
            if (line == null){
                System.exit(0);
            }
            System.out.println(line);
            line = line + "|" + msg;
            String[] result = line.split("\\|");

            return result;


        }
        protected void onPostExecute(String[] result){

            if (result[2].equals("startgame")) {        // om det var ett start meddelande vi skickade, gÃ¶r start callback

                Button startButton = (Button) findViewById(R.id.btnStart);
                Button guessButton = (Button) findViewById(R.id.btnGuess);
                TextView guessField = (TextView) findViewById(R.id.showState);
                TextView life = (TextView) findViewById(R.id.textLife);

                startButton.setClickable(false);
                guessField.setText(result[0]);
                life.setText(result[1]);


                guessButton.setClickable(true);

            } else if (line.contains("[")) {     // om det ska fortsÃ¤tta gissas
                gui.sentGuess(result[0], result[1]);
            } else if (line.contains("Congratulations") || line.contains("Game over!")) {      // om spelet Ã¤r slut
                gui.gameDone(result[0], result[1]);
            }
        }
    }



    /**
     * The system calls this to perform work in the UI thread and delivers
     * the result from doInBackground()
     */


}




