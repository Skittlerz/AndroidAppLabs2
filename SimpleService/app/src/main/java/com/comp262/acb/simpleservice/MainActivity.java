package com.comp262.acb.simpleservice;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void startService(View view){
        //starts the MyService service
        //startService(new Intent(getBaseContext(), MyService.class));
        //OR
        //startService(new Intent("com.comp262.MyService"));
        startService(new Intent(getBaseContext(), MyIntentService.class));
    }

    public void stopService(View view){
        //stops the MyService service
        //stopService(new Intent(getBaseContext(), MyService.class));
        stopService(new Intent(MainActivity.this, MyIntentService.class));
    }
}
