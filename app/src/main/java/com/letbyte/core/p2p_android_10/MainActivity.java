package com.letbyte.core.p2p_android_10;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.letbyte.core.p2p_android_10.p2p.SS;

public class MainActivity extends AppCompatActivity {
    public static final String SERVICE_TYPE = "_presence._tcp";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(this, RequestActivity.class);
        startActivityForResult(intent, RequestActivity.REQUEST_ENABLE_DSC);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RequestActivity.REQUEST_ENABLE_DSC) {


//            new GO(getApplicationContext()).start();
        new SS(getApplicationContext()).start();
        }
    }
}