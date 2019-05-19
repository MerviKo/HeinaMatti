package com.example.heinamatti;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "HeinaMatti MainAct";
    private static Context context;

    static String msgMessageReceived = "";

    public CommunicationThread communitcationThread;

    static Handler UIupdater = new Handler(){
        @Override
        public void handleMessage(Message msg){
            int numOfBytesReceived = msg.arg1;
            byte[] buffer = (byte[]) msg.obj;
            String strReceived = new String(buffer);
            try {
                strReceived = strReceived.substring(0, numOfBytesReceived);
            }
            catch (StringIndexOutOfBoundsException e){
                Log.e(TAG, strReceived);
                Log.e(TAG, Integer.toString(numOfBytesReceived));
            }
            Log.d(TAG, "Received message: " + strReceived);
            msgMessageReceived = strReceived;
            if (msgMessageReceived.contains("TimerON")){
                Intent main3Activity = new Intent(context, Main3Activity.class);
                context.startActivity(main3Activity);
                Log.d(TAG, "Got TimerON, start Main3Activity");
            }else if (msgMessageReceived.contains("TimerOFF")){
                Intent main2Activity = new Intent(context, Main2Activity.class);
                context.startActivity(main2Activity);
                Log.d(TAG, "Got TimerOFF, start Main2Activity");
            }
            }
    };

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
            try {
                communitcationThread.running = true;
                communitcationThread.start();
            }
            catch (IllegalThreadStateException e){
                // Thread already running, do nothing
            }
            return null;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        setContentView(R.layout.activity_main);
        communitcationThread = CommunicationThread.getInstance();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); /*set display portrait*/
        Button yhteysButton = findViewById(R.id.yhteysButton);
        yhteysButton.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
                sendToServer(CommunicationThread.TIME_GET);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                sendToServer(CommunicationThread.TEMPERATURE_GET);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                sendToServer(CommunicationThread.TIMER_GET);
            }
        });
    }

    @Override
    public void onStart(){
        super.onStart();
        Log.i(TAG, "MainActivity message: " + msgMessageReceived);
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
    @Override
    public void onStop(){
        // communitcationThread.running = false;
        super.onStop();
        new CloseSocketTask();
    }
}



