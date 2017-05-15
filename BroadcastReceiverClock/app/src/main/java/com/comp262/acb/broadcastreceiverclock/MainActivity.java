package com.comp262.acb.broadcastreceiverclock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.text.format.DateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    BroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onResume(){
        super.onResume();
        setTime();
        //register the receiver
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                setTime();
            }
        };
        //need intentFilter to register the receiver (specifies which intent will trigger the receiver)
        //this app uses the action ACTION_TIME_TICK which is called every minute
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_TIME_TICK);
        this.registerReceiver(receiver, intentFilter);
    }
    //deregister the receiver
    //When deciding when to reregister the receiver, generally speaking,
    //the complimentary methods are as follows:
    //onCreate - onDestroy
    //onResume - onPause
    //onStart  - onStop
    public void onPause(){
        this.unregisterReceiver(receiver);
        super.onPause();
    }

    //Uses Calendar to update the time and display result in textview
    private void setTime(){
        Calendar calendar = Calendar.getInstance();
        CharSequence newTime = DateFormat.format("kk:mm", calendar);
        TextView textView = (TextView) findViewById(R.id.textView1);
        textView.setText(newTime);
    }
}
