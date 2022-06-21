package com.example.trip;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.os.Handler;
import android.os.Bundle;
import android.content.Intent;

import com.example.trip.LoginActivity;

public class LoadingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        startLoading();
    }


    private void startLoading() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable(){
            @Override
            public void run(){
                Intent intent = new Intent(getApplicationContext(), BluetoothActivity.class);//로그인 액티비티로 이동
                startActivity(intent);
                finish();
            }
        },2000);//화면에 로고 2초간 보일것
    }
}

