package com.example.heinamatti;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.icu.util.Calendar;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Locale;
//import com.example.heinamatti.CommunicationThread;


public class Main2Activity extends AppCompatActivity {

   // static final String SERVER_IP_ADDRESS = "192.168.0.177";

    Socket socket;
    DataOutputStream out;
    DataInputStream dataInputStream;
    TextView teksti;

    private RadioGroup radioGroup;
    RadioButton radioButton2;
    private Button okButton;
    static TextView tmpFromServer;
    Calendar c = Calendar.getInstance();
    static String msgMessageReceived;

    private static final String TAG = "HeinaMatti Main2Act";

    String sendValue;
    int time1 = 4;
    int time2 = 6;
    int time3 = 8;
    int time4 = 10;
    int time5 = 12;
    int selectedId;

    String sendValue2;
    /* int hostPort = socket.getPort();*/

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
                tmpFromServer.setText("Lämpötila on: "+temperature);
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        tmpFromServer = (TextView)findViewById(R.id.textView2);

        communitcationThread = CommunicationThread.getInstance();

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); /*set display portrait*/
        addListenerOnButton();
        sendToServer(CommunicationThread.TEMPERATURE_GET);

    }

    public void addListenerOnButton() {

        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        okButton = (Button) findViewById(R.id.okButton);

        okButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                radioButton2 = (RadioButton) findViewById(selectedId);
                sendToServer(CommunicationThread.TIMER_SET + sendValue);
                Intent mainActivity = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(mainActivity);
            }
        });
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // get current time and add selected hours to tell user when is feeding time
                selectedId = radioGroup.getCheckedRadioButtonId();
                int hours = c.get(Calendar.HOUR_OF_DAY);
                int minutes = c.get(Calendar.MINUTE);
                int nextHour = 0;

                if (selectedId == R.id.radioButton1) {
                    nextHour = hours + time1;
                    sendValue = "time1";
                } else if (selectedId == R.id.radioButton2) {
                    nextHour = hours + time2;
                    sendValue = "time2";
                } else if (selectedId == R.id.radioButton3) {
                    nextHour = hours + time3;
                    sendValue = "time3";
                } else if (selectedId == R.id.radioButton4) {
                    nextHour = hours + time4;
                    sendValue = "time4";
                } else if (selectedId == R.id.radioButton5) {
                    nextHour = hours + time5;
                    sendValue = "time5";
                }

                if (nextHour >= 24) {
                    nextHour = nextHour - 24;
                }

                Toast.makeText(getApplicationContext(), "Heinät annetaan seuraavan kerran " +
                        String.format(Locale.getDefault(),"%02d", nextHour) + ":" +
                        String.format(Locale.getDefault(),"%02d", minutes), Toast.LENGTH_SHORT).show();
            }
        });


            }

    public void sendToServer(String message){
        byte[] theByteArray = message.getBytes();
        new Main2Activity.WriteToServerTask().execute(theByteArray);
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
            return null;
        }
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

