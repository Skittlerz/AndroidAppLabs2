package com.comp262.acb.contactscontentprovider;

import android.Manifest;
import android.app.ListActivity;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends ListActivity {

    final private int REQUEST_READ_CONTACTS = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Check for permissions to read contacts
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED){
            //If permissions not yet granted, request permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS},
                    REQUEST_READ_CONTACTS);
        }else{
            //If permissions granted list contacts
            ListContacts();
        }
    }

    //Method automatically called after permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        switch(requestCode){
            //evaluate the result from the permission request
            case REQUEST_READ_CONTACTS:
                //if permission is granted, list contacts
                // else, display permission denied message
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    ListContacts();
                }else {
                    Toast.makeText(MainActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    protected void ListContacts(){
        //Uniform Resource Identifier used to query a content provider
        //format: <standard prefix>://<authority>/<data_path>/<id>
        Uri allContacts = Uri.parse("content://contacts/people");
        Cursor c;
        //Loader that is used to load cursor objects from ContentProvider data sources
        CursorLoader cursorLoader = new CursorLoader(
                this,
                allContacts,
                null,
                null,
                null,
                null);
        //This method used a worker thread instead of the UI thread
        c = cursorLoader.loadInBackground();
        //column names that are needed
        String[] columns = new String[]{
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts._ID};
        //Store references to textViews
        int[] views = new int[]{R.id.contactName, R.id.contactID};
        SimpleCursorAdapter adapter;
        //adapter to get content from the cursorLoader and display it to the UI
        //takes context, target layout, cursor (data), target columns, target textviews
        adapter = new SimpleCursorAdapter(
                this, R.layout.activity_main, c, columns, views,
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        this.setListAdapter(adapter);
    }
}
