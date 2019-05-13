package com.example.heinamatti;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.ScanResult;
import android.os.AsyncTask;
import android.os.Bundle;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.app.ListActivity;
import android.content.BroadcastReceiver;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import java.util.List;

import java.net.InetAddress;
import java.net.Socket;
import java.io.IOException;
import java.net.UnknownHostException;

import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {
    //static final String HELLO = "esp8266";
    static final String HELLO = "HELLO";
    static final String TEMPERATURE = "TEMP";

    ListView list;
    String wifis[];

    private static final String TAG = "HeinaMatti logger";


    static String msgMessageReceived;
    WifiManager wifiManager;

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
            communitcationThread.start();
            // ---sign in for the user; sends the nick name---
            sendToServer(HELLO);
            //
            return null;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        communitcationThread = CommunicationThread.getInstance();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); /*set display portrait*/
        sendToServer("TimerGet");

        Button yhteysButton = findViewById(R.id.yhteysButton);
        yhteysButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connectToWifi();
                if (msgMessageReceived =="TimerON"){
                   Intent main3Activity = new Intent(getApplicationContext(), Main3Activity.class);
                   startActivity(main3Activity);

                }else if (msgMessageReceived == "TimerOFF"){
                   Intent main2Activity = new Intent(getApplicationContext(), Main2Activity.class);
                   startActivity(main2Activity);
                }
            }

        });
    }

    public void connectToWifi(){
        try{
            WifiManager wifiManager = (WifiManager) super.getSystemService(android.content.Context.WIFI_SERVICE);
            WifiConfiguration wc = new WifiConfiguration();
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            wc.SSID = "FaryLink_26E82E";
            wc.preSharedKey = "\"PASSWORD\"";
            wc.status = WifiConfiguration.Status.ENABLED;
            wc.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);

            wifiManager.setWifiEnabled(true);
            int netId = wifiManager.addNetwork(wc);
            if (netId == -1) {
                netId = getExistingNetworkId(wc.SSID);
            }
            wifiManager.disconnect();
            wifiManager.enableNetwork(netId, true);
            wifiManager.reconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private int getExistingNetworkId(String SSID) {
        WifiManager wifiManager = (WifiManager) super.getSystemService(Context.WIFI_SERVICE);
        List<WifiConfiguration> configuredNetworks = wifiManager.getConfiguredNetworks();
        if (configuredNetworks != null) {
            for (WifiConfiguration existingConfig : configuredNetworks) {
                if (existingConfig.SSID.equals(SSID)) {
                    return existingConfig.networkId;
                }
            }
        }
        return -1;
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



