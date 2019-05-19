package com.example.heinamatti;

import android.icu.util.Calendar;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Locale;

public class CommunicationThread extends Thread{
    private Socket socket = null;
    private InputStream inputStream = null;
    private OutputStream outputStream = null;
    static final String SERVER_IP_ADDRESS = "192.168.4.1"; // Arduino IP address

    private static CommunicationThread INSTANCE = null;

    private static final String TAG = "HeinaMatti CommThread";

//Message adributes between Arduino and Ardroid
    public static final String TEMPERATURE_GET = "TEMP";
    public static final String TEMPERATURE_RESP = "TEMP:";
    public static final String TIMER_GET = "TIMER_GET";
    public static final String TIMER_SET = "TIMER_SET:";
    public static final String TIME_GET = "TIME_GET";
    public static final String TIME_SET = "TIME_SET";

    public volatile boolean running = true;

    // other instance variables can be here

    private CommunicationThread() {};

    public static synchronized CommunicationThread getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new CommunicationThread();
        }
        return(INSTANCE);
    }

    public void open(){
        InetAddress serverAddress = null;
        try {
            serverAddress = InetAddress.getByName(SERVER_IP_ADDRESS);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        try {
            socket = new Socket(serverAddress, 80); //IP, PORT NUMBER
        } catch (IOException e) {
            e.printStackTrace();
            return;
        } catch (Exception e){
            e.printStackTrace();
            return;
        }
        InputStream inPut =  null;
        OutputStream outPut = null;

        try{
            inPut = socket.getInputStream();
            outPut = socket.getOutputStream();
        } catch (IOException e){

        }
        inputStream =inPut;
        outputStream = outPut;
    }

    public void close(){
        try {
            socket.close();
        } catch (IOException e) {

        }
        socket = null;
    }

    public void run(){
        byte[ ] buffer = new byte[128];
        int bytes;

        // sending received message for every activities
        while(running){
            if (socket == null){
                open();
            }
            Arrays.fill(buffer, 0, 127,(byte)0);
            try{
                bytes = inputStream.read(buffer);
                if (bytes == -1){
                    // Failed to read socket
                    try {
                        sleep(50);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                    continue;  // No data received, read again
                }
                String strReceived = new String(buffer);
                Log.i(TAG, "Received: " + strReceived);
                String commands [] = strReceived.split(";");
                for (String str: commands) {
                    Log.i(TAG, "Command: " + str);
                    if (str.contains(CommunicationThread.TEMPERATURE_RESP)) {
                        String temperature = str.replace(CommunicationThread.TEMPERATURE_RESP, "");
                        Main3Activity.weather = temperature + " C";
                    }
                    if (str.contains(CommunicationThread.TIME_SET)) {
                        String timeSet = str.replace(CommunicationThread.TIME_SET, "");
                        Log.i(TAG, "timeSet: " + timeSet);
                        Calendar c = Calendar.getInstance();
                        Long millisecs = Long.valueOf(timeSet);
                        int minutes = (int) (millisecs / 1000 / 60);
                        Log.i(TAG, "Add " + String.valueOf(minutes) + " minutes");
                        c.add(Calendar.MINUTE, minutes);
                        String hay_time = String.valueOf(c.get(Calendar.HOUR)) + ":" + String.valueOf(c.get(Calendar.MINUTE));
                        Main3Activity.timeInfo = hay_time;
                    }
                    int len = str.length();
                    byte[] _buffer = str.getBytes();
                    MainActivity.UIupdater.obtainMessage(0, len, -1, _buffer)
                            .sendToTarget();
                    Main2Activity.UIupdater.obtainMessage(0, len, -1, _buffer)
                            .sendToTarget();
                    Main3Activity.UIupdater.obtainMessage(0, len, -1, _buffer)
                            .sendToTarget();
                }
            } catch (IOException e){
                socket = null;
                try {
                    sleep(250);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            } catch (NullPointerException e){
                try {
                    sleep(250);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    public void write(byte[]bytes){
        if (socket == null){
            open();
        }
        try{
            outputStream.write(bytes);

        }catch (IOException e){
    }
        catch (NullPointerException e){

        }
        catch (Exception e){

        }


}
    public void cancel(){
        try{
            socket.close();
        }catch (IOException e){

        }
        socket = null;
    }
}
