package com.example.heinamatti;

import android.os.AsyncTask;
import android.os.Bundle;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;

import java.net.InetAddress;
import java.net.Socket;
import java.io.IOException;
import java.net.UnknownHostException;

import android.widget.EditText;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {
    static final String KNOWME = "ESP8266";
    static final String SERVER_IP_ADDRESS = "192.168.4.2"; // Arduino IP address

    InetAddress serverAddress;
    Socket socket;
    static TextView msgMessageReceived;
    EditText txtMessage;

    CommunicationThread communitcationThread;

    static Handler UIupdater = new Handler(){
        @Override
        public void handleMessage(Message msg){
            int numOfBytesReceived = msg.arg1;
            byte[] buffer = (byte[]) msg.obj;
            String strReceived = new String(buffer);
            strReceived = strReceived.substring(0,numOfBytesReceived);
            msgMessageReceived.setText(msgMessageReceived.getText()
                    .toString() + strReceived);
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
            try {
                socket.close();
            } catch (IOException e) {

            }
            return null;
        }
    }

    public class CreateCommThreadTask extends AsyncTask<Void, Integer, Void> {
        @Override
        protected Void doInBackground(Void... params){
            try {
                // ---create a socket---
                serverAddress = InetAddress.getByName(SERVER_IP_ADDRESS);
                socket = new Socket(serverAddress, 7001); //IP, PORT NUMBER
                communitcationThread = new CommunicationThread(socket);
                communitcationThread.start();
                // ---sign in for the user; sends the nick name---
                sendToServer(KNOWME);
                //
            } catch (UnknownHostException e) {

            } catch (IOException e) {

            }
            return null;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); /*set display portrait*/

        // txtMessage = (EditText)findViewById(R.id.)


        Button yhteysButton = findViewById(R.id.yhteysButton);
        yhteysButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /* WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
                wifiManager.setWifiEnabled(true);*/

                Intent main2Activity = new Intent(getApplicationContext(), Main2Activity.class);
                startActivity(main2Activity);
                //sendToServer(txtMessage.getText().toString());
            }

        });
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



