package com.comp262.acb.simpleservice;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by acb on 2017-04-27.
 */

//All services extend the Service class
public class MyService extends Service {

    int counter = 0;
    static final int UPDATE_INTERVAL = 1000;
    private Timer timer = new Timer();

    //this method enables you to bind an activity to a service
    //which allows the activity to access members/methods inside a service
    //implementation of this method is mandatory
    //here it does nothing, just returns null
    @Override
    public IBinder onBind(Intent arg0){
        return null;
    }

    //starts the service when startService() is called
    public int onStartCommand(Intent intent, int flags, int startId){
        //Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
        doSomethingRepeatedly();
        try{
            //downloads the files in the background and reports progress as a percentage of files downloaded
            //ensures that the activity remains responsive
            new DoBackgroundTask().execute(
                    new URL("http://amazon.com/somefiles.pdf"),
                    new URL("http://www.wrox.com/somefiles.pdf"),
                    new URL("http://www.google.com/somefiels.pdf"),
                    new URL("http://www.learn2develop.net/somefiles.pdf"));
            //Toast.makeText(getBaseContext(), "Downloaded " + result + " bytes",
                   // Toast.LENGTH_LONG).show();
        }catch (MalformedURLException e){
            e.printStackTrace();
        }
        //We want this service to continue running  until it is explicitly stopped
        //so return sticky
        return START_STICKY;
    }

    private void doSomethingRepeatedly(){
        //repeatedly executes the run() method
        //scheduleAtFixedRate(TimerTask, time before first execution, time between subsequent executions
        //because the TimerTask implements Runnable it is able to run on a separate thread, meaning that
        //it does not need to be wrapped in a subclass of AsyncTask
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                Log.d("MyService",String.valueOf(++counter));
            }
        }, 0, UPDATE_INTERVAL);
    }
    //Inner class that executes asynchronous task
    private class DoBackgroundTask extends AsyncTask<URL,Integer,Long>{

        //accepts an array of first generic type specified in class signature
        //method is executed in the background thread
        //put long-running code here
        //return type of this method takes the third generic type specified in class signature
        protected Long doInBackground(URL... urls){
            int count = urls.length;
            long totalBytesDownloaded = 0;
            for (int i = 0; i < count; i++){
                totalBytesDownloaded += DownloadFile(urls[i]);
                //calculate percentage downloaded and report its progess
                //invokes onProgressUpdate
                publishProgress((int) (((i+1) / (float) count) * 100));
            }
            return totalBytesDownloaded;
        }
        //this method is invoked in the UI  thread
        //called when publishProgess() is called
        //accepts array of the second generic type specified in the class signature
        // use this method to report the progress of the background task to the user
        protected void onProgressUpdate(Integer... progress){
            Log.d("Downloading files", String.valueOf(progress[0]) + "% downloaded");
            Toast.makeText(getBaseContext(), String.valueOf(progress[0]) + "% downloaded", Toast.LENGTH_LONG).show();
        }

        //this method is invoked in the UI thread
        //called when the doInBackground() method finishes
        //accepts an argument of the third generic type specified in the class signature
        protected void onPostExecute(Long result){
            Toast.makeText(getBaseContext(), "Downloaded " + result + " bytes", Toast.LENGTH_LONG).show();
            //stops the service
            stopSelf();
        }
    }

    private int DownloadFile(URL url){
        try{
            //simulate taking  some time to download a file
            Thread.sleep(5000);
        }catch (InterruptedException e){
            e.printStackTrace();
        }

        //return an arbitrary number representing the size of the downloaded file
        return 100;
    }

    //stops the service when stopService() is called
    //clean up resources here
    @Override
    public void onDestroy(){
        super.onDestroy();
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
        if(timer != null){
            timer.cancel();
        }
        Toast.makeText(this,"Service Destroyed", Toast.LENGTH_LONG).show();
    }
}
