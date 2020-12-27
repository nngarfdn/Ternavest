package com.example.ternavest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.example.ternavest.testing.Wilayah;
import com.example.ternavest.testing.WilayahTest;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnTestWilayah = findViewById(R.id.btn_testwilayah);

        btnTestWilayah.setOnClickListener(v -> startActivity(new Intent(this, WilayahTest.class)));
    }
}