package com.example.heinamatti;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.icu.util.Calendar;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Main3Activity extends AppCompatActivity {

    static TextView timeInformation;
    static TextView weatherInformation;
    Button nollausButton;
    Button okButton;
    Button noButton;
    static String msgMessageReceived;
    private static final String TAG = "HeinaMatti logger";

    public CommunicationThread communitcationThread;

    static Handler UIupdater = new Handler(){
        @Override
        public void handleMessage(Message msg){
            int numOfBytesReceived = msg.arg1;
            byte[] buffer = (byte[]) msg.obj;
            String strReceived = new String(buffer);
            strReceived = strReceived.substring(0,numOfBytesReceived);
            Log.d(TAG, "Received message: " + strReceived);
            msgMessageReceived = strReceived;
            if (msgMessageReceived.startsWith(CommunicationThread.TEMPERATURE_RESP)){
                String temperature =  msgMessageReceived.replace(CommunicationThread.TEMPERATURE_RESP,"");
                weatherInformation.setText("Lämpötila on: "+temperature+ " C");
            }
            if(msgMessageReceived.startsWith(CommunicationThread.TIME_SET)){
                String timeGet = msgMessageReceived.replace(CommunicationThread.TIME_SET,"");
                milSecToTime(timeGet);
                timeInformation.setText("Heinät annetaan klo: "+timeGet);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        weatherInformation =(TextView) findViewById(R.id.textView3);
        timeInformation = (TextView)findViewById(R.id.textView2);
        communitcationThread = CommunicationThread.getInstance();
        sendToServer(CommunicationThread.TIME_GET);
        sendToServer(CommunicationThread.TEMPERATURE_GET);



        nollausButton = findViewById(R.id.nollausButton);
        nollausButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(getApplicationContext(), "Ajastin nollataan.", Toast.LENGTH_SHORT).show();
                sendToServer("Nollaus");

                Intent mainActivity = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(mainActivity);
            }
        });

        okButton = findViewById(R.id.okButton);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent main2Activity = new Intent(getApplicationContext(), Main2Activity.class);
                startActivity(main2Activity);
            }
        });


        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainActivity = new Intent(getApplicationContext(), MainActivity.class);
                sendToServer(CommunicationThread.TIME_SET);
                startActivity(mainActivity);
            }
        });
}
    static private String milSecToTime(String timeLeft){

        long miliSec = 0;
        miliSec = (Long.parseLong(timeLeft));

        // Creating date format
        DateFormat simple = new SimpleDateFormat(" HH:mm:ss");

        // Creating date from milliseconds

        Calendar date = Calendar.getInstance();
        long t= date.getTimeInMillis();
        Date timeToGiveHays=new Date(t + (miliSec));
        String timeToUser = simple.format(timeToGiveHays);

        return timeToUser;

    }

    public class WriteToServerTask extends AsyncTask<byte[], Void, Void> {
        protected Void doInBackground(byte[]... data) {
            communitcationThread.write(data[0]);
            return null;
        }
    }

    public class CloseSocketTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            communitcationThread.close();
            return null;
        }
    }

    public class CreateCommThreadTask extends AsyncTask<Void, Integer, Void> {
        @Override
        protected Void doInBackground(Void... params){
            communitcationThread = CommunicationThread.getInstance();
            communitcationThread.start();
            // ---sign in for the user; sends the nick name---
            //
            return null;
        }
    }

    public void sendToServer(String message){
        byte[] theByteArray = message.getBytes();
        new WriteToServerTask().execute(theByteArray);
    }

    @Override
    public void onResume(){
        super.onResume();
        new CreateCommThreadTask().execute();
    }

    @Override
    public void onPause(){
        super.onPause();
        new CloseSocketTask();
    }
}