package com.example.heinamatti;

import android.content.pm.ActivityInfo;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;
import java.io.DataInputStream;
import java.io.OutputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Main2Activity extends AppCompatActivity {

   // static final String SERVER_IP_ADDRESS = "192.168.0.177";

    Socket socket;
    DataOutputStream out;
    DataInputStream dataInputStream;
    TextView teksti;

    private RadioGroup radioGroup;
    RadioButton radioButton2;
    private Button okButton;
    Calendar c = Calendar.getInstance();


    int sendValue;
    String sendValue2;
    /* int hostPort = socket.getPort();*/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); /*set display portrait*/
        addListenerOnButton();
    }

    public void addListenerOnButton() {

        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        okButton = (Button) findViewById(R.id.okButton);

        okButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                int selectedId = radioGroup.getCheckedRadioButtonId();

                radioButton2 = (RadioButton) findViewById(selectedId);

                Toast.makeText(Main2Activity.this,
                        radioButton2.getText(), Toast.LENGTH_SHORT).show();

            }
        });
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected
                int selectedId = radioGroup.getCheckedRadioButtonId();
               // radioButton2 = (RadioButton) findViewById(selectedId);
                // get current time and add selected hours to tell user when is feeding time


                int hours = c.get(Calendar.HOUR_OF_DAY);
                int minutes = c.get(Calendar.MINUTE);
                int nextHour = 0;

                if (selectedId == 2131165280) {
                    nextHour = hours + 4;
                } else if (selectedId == 2131165281) {
                    nextHour = hours + 6;
                } else if (selectedId == 2131165282) {
                    nextHour = hours + 8;
                } else if (selectedId == 2131165283) {
                    nextHour = hours + 10;
                } else if (selectedId == 2131165284) {
                    nextHour = hours + 12;
                }

                if (nextHour > 24) {
                    nextHour = nextHour - 24;
                }


                //Toast.makeText(Main2Activity.this,
                //      radioButton2.getText(), Toast.LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext(), "Heinät annetaan seuraavan kerran " + nextHour + ":" + minutes, Toast.LENGTH_SHORT).show();
            }
        });




            try {
                Socket socket = new Socket("192.168.0.1", 7001);  //1755);
                DataOutputStream DOS = new DataOutputStream(socket.getOutputStream());
                DOS.writeUTF("HELLO");
                socket.close();
            }catch(IOException e){};




            }


}

