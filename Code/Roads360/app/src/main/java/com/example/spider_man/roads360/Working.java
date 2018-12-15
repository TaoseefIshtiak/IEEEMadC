package com.example.spider_man.roads360;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Working extends AppCompatActivity {
    Button upload, showImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_working);


        upload = (Button)findViewById(R.id.upload);
        showImage = (Button)findViewById(R.id.showImage);

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Working.this,NewUploadImage.class);
                startActivity(intent);
            }
        });

        showImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Working.this, ShowImagesActivity.class);
                startActivity(intent);
            }
        });
    }
}
