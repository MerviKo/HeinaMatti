package com.example.heinamatti;

import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class CommunicationThread extends Thread{
    private Socket socket = null;
    private InputStream inputStream = null;
    private OutputStream outputStream = null;
    static final String SERVER_IP_ADDRESS = "192.168.4.1"; // Arduino IP address

    private static CommunicationThread INSTANCE = null;

//Message adributes between Arduino and Ardroid
    public static final String TEMPERATURE_GET = "TEMP";
    public static final String TEMPERATURE_RESP = "TEMP:";
    public static final String TIMER_GET = "TIMER_GET";
    public static final String TIMER_SET = "TIMER_SET:";
    static final String TIME_GET = "TIME_GET";
    static final String TIME_SET = "TIME_SET";


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
        byte[ ] buffer = new byte[1024];
        int bytes;

        // sending received message for every activities
        while(true){
            try{
                bytes = inputStream.read(buffer);
                MainActivity.UIupdater.obtainMessage(0,bytes,-1,buffer)
                        .sendToTarget();
                Main2Activity.UIupdater.obtainMessage(0,bytes,-1,buffer)
                        .sendToTarget();
                Main3Activity.UIupdater.obtainMessage(0,bytes,-1,buffer)
                        .sendToTarget();
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
