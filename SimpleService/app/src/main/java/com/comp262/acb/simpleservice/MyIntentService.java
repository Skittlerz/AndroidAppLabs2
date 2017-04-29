package com.comp262.acb.simpleservice;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by acb on 2017-04-27.
 */

public class MyIntentService extends IntentService {

    private Thread thread = new Thread();


    public MyIntentService(){
        //need to call superclass with name of the intent service
        super("MyIntentServiceName");
    }

    //executed on a worker thread
    //when the code is finished executing, the thread is terminated
    //and the service is stopped automatically
    @Override
    protected void onHandleIntent(Intent intent){
        thread.start();
        try {
            int result =
                    DownloadFile(new URL("http://www.amazon.ca/somefile.pdf"));
            Log.d("IntentService", "Downloaded " + result + " bytes");
        }catch (MalformedURLException e){
            e.printStackTrace();
        }
    }

    private int DownloadFile(URL url){
        try{
            // simulate the time taken to download a file
            thread.sleep(5000);
        }catch (InterruptedException e){
            e.printStackTrace();
        }

        return 100;
    }

}
