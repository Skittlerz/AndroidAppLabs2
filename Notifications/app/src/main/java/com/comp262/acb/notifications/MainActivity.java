package com.comp262.acb.notifications;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    int notificationID = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick(View view) {
        displayNotification();
    }

    protected void displayNotification()
    {
        //---PendingIntent to launch activity if the user selects
        // this notification---
        Intent i = new Intent(this, NotificationView.class);
        i.putExtra("notificationID", notificationID);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i, 0);
        //Class to notify the user of events that happen.
        //This is how you tell the user that something has happened in the background.
        //Notifications can take different forms:
        //A persistent icon that goes in the status bar and is accessible through the launcher,
        //(when the user selects it, a designated Intent can be launched),
        // Turning on or flashing LEDs on the device, or
        //Alerting the user by flashing the backlight, playing a sound, or vibrating.
        NotificationManager nm = (NotificationManager)getSystemService
                (NOTIFICATION_SERVICE);
        NotificationCompat.Builder notifBuilder;
        notifBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Meeting Reminder")
                .setContentText("Reminder: Meeting starts in 5 minutes");
        nm.notify(notificationID, notifBuilder.build());
    }
}
