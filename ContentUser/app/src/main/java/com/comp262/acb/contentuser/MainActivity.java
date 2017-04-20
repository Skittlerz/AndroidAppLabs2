package com.comp262.acb.contentuser;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    static final String PROVIDER_NAME = "com.comp262.acb.provider.Books";
    //uri format - content://authority/path/id
    static final Uri CONTENT_URI = Uri.parse("content://"+PROVIDER_NAME+"/books");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClickAddTitle(View view) {
        //---add a book---
        ContentValues values = new ContentValues();
        values.put("title", ((EditText)
                findViewById(R.id.txtTitle)).getText().toString());
        values.put("isbn", ((EditText)
                findViewById(R.id.txtISBN)).getText().toString());
        // When you want to access data in a content provider,
        // you use the ContentResolver object in your application's
        // Context to communicate with the provider as a client
        Uri uri = getContentResolver().insert(
                CONTENT_URI, values);
        Toast.makeText(getBaseContext(),uri.toString(),
                Toast.LENGTH_LONG).show();
    }
    public void onClickRetrieveTitles(View view) {
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
                CONTENT_URI, null, null, null,
                "title desc");
        c = cursorLoader.loadInBackground();
        if (c.moveToFirst()) {
            do{
                Toast.makeText(this,
                        c.getString(c.getColumnIndex(
                                "_id")) + ", " +
                                c.getString(c.getColumnIndex(
                                        "title")) + ", " +
                                c.getString(c.getColumnIndex(
                                        "isbn")),
                        Toast.LENGTH_SHORT).show();
            } while (c.moveToNext());
        }
    }
}
