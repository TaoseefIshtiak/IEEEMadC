package com.example.spider_man.roads360;

import android.content.Intent;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Thread thread = new Thread()
        {
            @Override
            public void run() {
                try{
                    sleep(3000);
                    Intent intent = new Intent(MainActivity.this, SignUp.class);
                    startActivity(intent);
                    finish();


                }catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        thread.start();

    }


}
