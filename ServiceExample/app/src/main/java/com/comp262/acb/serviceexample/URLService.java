package com.comp262.acb.serviceexample;

import android.app.IntentService;
import android.content.Intent;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.StringTokenizer;

/**
 * Created by acb on 2017-04-29.
 */
/**
IntentService is a base class for Services that handle asynchronous requests (expressed as Intents)
 on demand. Clients send requests through startService(Intent) calls; the service is started as needed,
 handles each Intent in turn using a worker thread, and stops itself when it runs out of work.
*/

 public class URLService extends IntentService {

    public URLService(){
        super("URLService");
    }

    //This method is invoked on the worker thread with a request to process.
    @Override
    protected void onHandleIntent(Intent intent) {
        //receives an array of URLs and uses a StringTokenizer to extract each URL from the array.
        String urls = intent.getStringExtra("urls");
        if(urls == null){
            return;
        }
        // Each URL is used to populate a string array named targets, which is then passed to the fetchPagesAndSave method.
        StringTokenizer tokenizer = new StringTokenizer(urls);
        int tokenCount = tokenizer.countTokens();
        int index = 0;
        String[] targets = new String[tokenCount];
        while(tokenizer.hasMoreTokens()){
            targets[index++] = tokenizer.nextToken();
        }
        File saveDir = getFilesDir();
        fetchPagesAndSave(saveDir, targets);
    }

    // This method employs a java.net.URL to send an HTTP request for each target
    // and saves its content in internal storage.
    private void fetchPagesAndSave(File saveDir, String[] targets){

        for (String target: targets){

            URL url = null;

            try{
                url = new URL(target);
            }catch (MalformedURLException e){
                e.printStackTrace();
            }
            String fileName = target.replaceAll("/", "-").replaceAll(":","-");
            File file = new File(saveDir, fileName);
            PrintWriter writer = null;
            BufferedReader reader = null;
            try{
                writer = new PrintWriter(file);
                reader = new BufferedReader(
                        new InputStreamReader(url.openStream()));
                String line;
                while ((line = reader.readLine()) != null){
                    writer.write(line);
                }
            }catch (Exception e){
            }finally {
                if(writer != null){
                    try{
                        writer.close();
                    }catch (Exception e){
                    }
                }
                if (reader != null){
                    try{
                        reader.close();
                    }catch (Exception e){
                    }
                }
            }

        }
    }
}
