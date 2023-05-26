package com.example.findmushroom2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

public class ResultJamur extends AppCompatActivity {

    ImageView imageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_jamur);

        imageView = findViewById(R.id.imageView);

        Intent intent = getIntent();
        String hasil = intent.getStringExtra("hasil");
        Log.d("hasil", hasil);
    }


}