package com.example.heinamatti;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
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
import java.util.Locale;

public class Main2Activity extends AppCompatActivity {

   // static final String SERVER_IP_ADDRESS = "192.168.0.177";

    Socket socket;
    DataOutputStream out;
    DataInputStream dataInputStream;
    TextView teksti;

    private RadioGroup radioGroup;
    RadioButton radioButton2;
    private Button okButton;
    TextView msgFromServer;
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
                Intent main3Activity = new Intent(getApplicationContext(), Main3Activity.class);
                startActivity(main3Activity);
               /* Toast.makeText(Main2Activity.this,
                        radioButton2.getText(), Toast.LENGTH_SHORT).show();*/

            }
        });
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected
                int selectedId = radioGroup.getCheckedRadioButtonId();
                msgFromServer = (TextView)findViewById(R.id.textView2);
                msgFromServer.setText("Lämpötila on nyt: "+"C");
               // radioButton2 = (RadioButton) findViewById(selectedId);
                // get current time and add selected hours to tell user when is feeding time


                int hours = c.get(Calendar.HOUR_OF_DAY);
                int minutes = c.get(Calendar.MINUTE);
                int nextHour = 0;

                if (selectedId == R.id.radioButton1) {
                    nextHour = hours + 4;
                } else if (selectedId == R.id.radioButton2) {
                    nextHour = hours + 6;
                } else if (selectedId == R.id.radioButton3) {
                    nextHour = hours + 8;
                } else if (selectedId == R.id.radioButton4) {
                    nextHour = hours + 10;
                } else if (selectedId == R.id.radioButton5) {
                    nextHour = hours + 12;
                }

                if (nextHour >= 24) {
                    nextHour = nextHour - 24;
                }


                //Toast.makeText(Main2Activity.this,
                //      radioButton2.getText(), Toast.LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext(), "Heinät annetaan seuraavan kerran " +
                        String.format(Locale.getDefault(),"%02d", nextHour) + ":" +
                        String.format(Locale.getDefault(),"%02d", minutes), Toast.LENGTH_SHORT).show();
            }
        });



            /*
            try {
                Socket socket = new Socket("192.168.0.1", 7001);  //1755);
                DataOutputStream DOS = new DataOutputStream(socket.getOutputStream());
                DOS.writeUTF("HELLO");
                socket.close();
            }catch(IOException e){};


            sendtoserver
*/
            }


}

