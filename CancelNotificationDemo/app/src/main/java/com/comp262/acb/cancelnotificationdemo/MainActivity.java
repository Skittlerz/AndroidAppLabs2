package com.comp262.acb.cancelnotificationdemo;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private static final String CANCEL_NOTIFICATION_ACTION = "cancel_notification";
    int notificationID = 1002;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //register the receiver
        BroadcastReceiver receiver = new BroadcastReceiver() {
            //this method of the receiver cancels the notification
            @Override
            public void onReceive(Context context, Intent intent) {
                NotificationManager notificationManager =
                        (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                notificationManager.cancel(notificationID);
            }
        };
        IntentFilter filter = new IntentFilter();
        //pass a user-defined action
        filter.addAction(CANCEL_NOTIFICATION_ACTION);
        this.registerReceiver(receiver,filter);
    }

    public void setNotification(View view){
        Intent cancelIntent = new Intent("cancel_notification");
        PendingIntent cancelPendingIntent = PendingIntent.getBroadcast(this, 100, cancelIntent,0);

        //build the notification with title, text, icon and pending intent
        // the pending intent allows the user to cancel the notification, when the user presses dismiss
        // a broadcast is sent and picked up by the receiver's onReceive method defined above
        Notification notification = new Notification.Builder(this)
                .setContentTitle("Stop Press")
                .setContentText("Everyone gets one extra vacation week!")
                .setSmallIcon(android.R.drawable.star_on)
                .setAutoCancel(true)
                .addAction(android.R.drawable.btn_dialog, "Dismiss", cancelPendingIntent)
                .build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(notificationID, notification);
    }

    //cancel the notification by pushing UI button
    public void clearNotification(View view){
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(notificationID);
    }
}
