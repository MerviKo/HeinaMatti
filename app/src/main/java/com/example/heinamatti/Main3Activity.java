package com.example.heinamatti;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Main3Activity extends AppCompatActivity {

    TextView timeInformation;
    TextView weatherInformation;
    Button nollausButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        timeInformation = (TextView)findViewById(R.id.textView2);
        timeInformation.setText("Heinät annetaan klo: "+"C");

        weatherInformation =(TextView) findViewById(R.id.textView3);
        weatherInformation.setText("Lämpötila on "+" C");

        Button nollausButton = findViewById(R.id.nollausButton);
        nollausButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent main4Activity = new Intent(getApplicationContext(), Main4Activity.class);
                startActivity(main4Activity);
    }});
}}