package com.example.heinamatti;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class CommunicationThread extends Thread{
    private final Socket socket;
    private final InputStream inputStream;
    private final OutputStream outputStream;

    public CommunicationThread(Socket sock){
        socket = sock;
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

    public void run(){
        byte[ ] buffer = new byte[1024];
        int bytes;

        while(true){
            try{
                bytes = inputStream.read(buffer);
                MainActivity.UIupdater.obtainMessage(0,bytes,-1,buffer)
                        .sendToTarget();
            } catch (IOException e){
                break;
            }
        }
    }
    public void write(byte[]bytes){
        try{
            outputStream.write(bytes);

        }catch (IOException e){
    }


}
    public void cancel(){
        try{
            socket.close();
        }catch (IOException e){

        }
    }
}
