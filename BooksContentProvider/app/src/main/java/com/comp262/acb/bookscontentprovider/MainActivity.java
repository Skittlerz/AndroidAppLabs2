package com.comp262.acb.bookscontentprovider;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Comments taken from Android Developer Documentation
 * Sources:
 * https://developer.android.com/guide/topics/providers/content-provider-basics.html
 */

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClickAddTitle(View view) {
        //---add a book---
        ContentValues values = new ContentValues();
        values.put(BooksProvider.TITLE, ((EditText)
                findViewById(R.id.txtTitle)).getText().toString());
        values.put(BooksProvider.ISBN, ((EditText)
                findViewById(R.id.txtISBN)).getText().toString());
        // When you want to access data in a content provider,
        // you use the ContentResolver object in your application's
        // Context to communicate with the provider as a client
        Uri uri = getContentResolver().insert(
                BooksProvider.CONTENT_URI, values);
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
        Uri allTitles = Uri.parse(
                "content://com.comp262.acb.provider.Books/books");
        Cursor c;
        // CursorLoader(Context context, Uri uri, String[] projection,
        //              String selection, String[] selectionArgs, String sortOrder)
        CursorLoader cursorLoader = new CursorLoader(
                this,
                allTitles, null, null, null,
                "title desc");
        c = cursorLoader.loadInBackground();
        if (c.moveToFirst()) {
            do{
                Toast.makeText(this,
                        c.getString(c.getColumnIndex(
                                BooksProvider._ID)) + ", " +
                                c.getString(c.getColumnIndex(
                                        BooksProvider.TITLE)) + ", " +
                                c.getString(c.getColumnIndex(
                                        BooksProvider.ISBN)),
                        Toast.LENGTH_SHORT).show();
            } while (c.moveToNext());
        }
    }
}
