package com.gioppl.humiditysliderview;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements SlideView.ScrollCallBack {
    private SlideView sv_main;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sv_main=findViewById(R.id.sv_main);
        sv_main.setScrollBack(this);
    }

    @Override
    public void scrollBack(int num) {
        Log.e("SSS",String.valueOf(num));
    }
}