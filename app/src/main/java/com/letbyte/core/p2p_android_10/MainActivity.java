package com.letbyte.core.p2p_android_10;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.letbyte.core.p2p_android_10.p2p.GO;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new GO(getApplicationContext()).start();
    }
}