package com.comp262.braun.downloadservice;

import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    static final String PROVIDER_NAME = "com.comp262.acb.provider.URL";
    //uri format - content://authority/path/id
    //each table will need its own URI
    static final Uri WEBPAGES_CONTENT_URI = Uri.parse("content://"+PROVIDER_NAME+"/webpages");
    static final Uri IMAGES_CONTENT_URI = Uri.parse("content://"+PROVIDER_NAME+"/images");
    EditText url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        url = (EditText) findViewById(R.id.etURL);
    }

    public void onClickAddURLs (View view) {

        String temp = url.getText().toString();
        String[] urls = temp.split("\\s");
        Log.d("String array", Arrays.toString(urls));
        Intent i = new Intent(this, DownloadService.class);
        i.putExtra("urls",urls);
        startService(i);
    }

    public void onClickRetrieveURLs(View view) {
        //A common pattern for accessing a ContentProvider from your UI uses
        // a CursorLoader to run an asynchronous query in the background.
        // The Activity or Fragment in your UI call a CursorLoader to the query,
        // which in turn gets the ContentProvider using the ContentResolver.
        // This allows the UI to continue to be available to the user while the query is running.

        //---retrieve the titles---
        Cursor c;
        // CursorLoader(Context context, Uri uri, String[] projection,
        //              String selection, String[] selectionArgs, String sortOrder)
        CursorLoader cursorLoader = new CursorLoader(
                this,
                IMAGES_CONTENT_URI, null, null, null,
                "_id asc");
        c = cursorLoader.loadInBackground();
        if (c.moveToFirst()) {
            do{
                Toast.makeText(this,
                        c.getString(c.getColumnIndex(
                                "_id")) + ", " +
                                c.getString(c.getColumnIndex(
                                        "url")) + ", " +
                                c.getString(c.getColumnIndex(
                                        "webpages_id")),
                        Toast.LENGTH_SHORT).show();
            } while (c.moveToNext());
        }
    }
}
