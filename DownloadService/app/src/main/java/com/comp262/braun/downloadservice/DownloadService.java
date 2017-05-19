package com.comp262.braun.downloadservice;

import android.app.IntentService;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Created by acb on 2017-05-17.
 */

// I chose to use an IntentService for this class because it uses sequential task execution
// For handling database entries it is important that these requests are handled in order to support
// database dependencies
public class DownloadService extends IntentService {
    static final String PROVIDER_NAME = "com.comp262.acb.provider.URL";
    //uri format - content://authority/path/id
    static final Uri WEBPAGES_CONTENT_URI = Uri.parse("content://"+PROVIDER_NAME+"/webpages");
    static final Uri IMAGES_CONTENT_URI = Uri.parse("content://"+PROVIDER_NAME+"/images");

    public DownloadService(){
        super("DownloadService");
    }

    protected void onHandleIntent(Intent intent) {

        try {
            //receives an array of URLs and uses Jsoup to retrieve the html page and parse for images
            String[] strings = intent.getStringArrayExtra("urls");
            if (strings == null) {
                return;
            }
            for (String s: strings) {
                //set the ContentValues to store webpage info
                // use the contentProvider to insert a row into the database
                ContentValues values = new ContentValues();
                values.put("url", s);
                Uri wUri = getContentResolver().insert(WEBPAGES_CONTENT_URI, values);
                //store the newly inserted row id
                //this will be used for the image inserts
                int webpageRow = (int) ContentUris.parseId(wUri);
                //parse the page for images
                StringBuffer buffer = new StringBuffer();
                Log.d("JSwa", "Connecting to [" + s + "]");
                Document doc = Jsoup.connect(s).get();
                Log.d("JSwa", "Connected to [" + s + "]");
                Elements media = doc.select("img[src]");
                buffer.append("Image list\r\n");


                for (Element img : media) {
                    //store the image url and parent webpage url as content values
                    String src = media.attr("src");
                    ContentValues imageValues = new ContentValues();
                    imageValues.put("url", src);
                    imageValues.put("webpages_id", webpageRow);
                    //use the contentprovider to insert info into images table
                    Uri iUri = getContentResolver().insert(IMAGES_CONTENT_URI, imageValues);
                    buffer.append("img URL [" + src + "] \r\n");
                }

                Log.d("End of method for url",s);
            }
        }catch(Throwable t){
            t.printStackTrace();
        }

    }

}
