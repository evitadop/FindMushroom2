package com.example.findmushroom2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class RincianJamur extends AppCompatActivity {

    TextView nama,famili,genus,deskripsi,manfaat;
    Database database = new Database();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rincian_jamur);

        nama = findViewById(R.id.nama);
        manfaat = findViewById(R.id.manfaat);
        famili = findViewById(R.id.famili);
        genus = findViewById(R.id.genus);
        deskripsi = findViewById(R.id.deskripsi);

        nama.setText(database.nama);
        famili.setText(database.famili);
        genus.setText(database.genus);
        deskripsi.setText(database.deskripsi);
        manfaat.setText(database.manfaat);
    }
}