package com.example.findmushroom2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class DaftarJamur extends AppCompatActivity {
    List<Jamur> jamurList = new ArrayList<>();
    RecyclerView listJamur;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daftar_jamur);
        listJamur = findViewById(R.id.daftarJamur);
        tambahJamur();
        JamurAdapter emailAdapter = new JamurAdapter(getApplicationContext(),DaftarJamur.this,jamurList);
        listJamur.setLayoutManager(new LinearLayoutManager(DaftarJamur.this, LinearLayoutManager.VERTICAL,false));
        listJamur.setAdapter(emailAdapter);
    }

    private void tambahJamur() {
        jamurList.add(new Jamur("Laughing gym mushrooms","Nama ilmiah : Gymnopilus junonius ","Beracun"));
        jamurList.add(new Jamur("Laughing gym mushrooms","Nama ilmiah : Gymnopilus junonius ","Beracun"));
        jamurList.add(new Jamur("Laughing gym mushrooms","Nama ilmiah : Gymnopilus junonius ","Beracun"));
        jamurList.add(new Jamur("Laughing gym mushrooms","Nama ilmiah : Gymnopilus junonius ","Beracun"));
        jamurList.add(new Jamur("Laughing gym mushrooms","Nama ilmiah : Gymnopilus junonius ","Beracun"));
        jamurList.add(new Jamur("Laughing gym mushrooms","Nama ilmiah : Gymnopilus junonius ","Beracun"));
        jamurList.add(new Jamur("Laughing gym mushrooms","Nama ilmiah : Gymnopilus junonius ","Beracun"));
        jamurList.add(new Jamur("Laughing gym mushrooms","Nama ilmiah : Gymnopilus junonius ","Beracun"));
    }
}