package com.comp262.braun.imageurldisplay;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    static final String PROVIDER_NAME = "com.comp262.acb.provider.URL";
    //need the uris for each table in the database
    static final Uri WEBPAGES_CONTENT_URI = Uri.parse("content://"+PROVIDER_NAME+"/webpages");
    static final Uri IMAGES_CONTENT_URI = Uri.parse("content://"+PROVIDER_NAME+"/images");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null){
            //launch add url fragment- entry point to the app
            AddURLFragment auf = new AddURLFragment();
            getFragmentManager().beginTransaction().add(R.id.fragmentContainer,auf).commit();
        }
    }
    //This method is called when the add url button is clicked
    //it parses the urls and passes it to the downloadservice service
    //downloadservice is an intent service and will stop itself
    public void onClickAddURLs(View view) {

        EditText input = (EditText)findViewById(R.id.etURL);
        String temp = input.getText().toString();
        String[] urls = temp.split("\\s");
        Intent i = new Intent();
        //this is necessary for an explicit intent call
        i.setComponent(new ComponentName("com.comp262.braun.downloadservice","com.comp262.braun.downloadservice.DownloadService"));
        i.putExtra("urls",urls);
        startService(i);
    }
    //This method gets all the webpages stored in the database using a cursorloader
    //It is called from the viewURLFragment
    public ArrayList<String> getWebpages(){
        ArrayList<String> webpages = new ArrayList<>();
        Cursor c;
        CursorLoader cursorLoader = new CursorLoader(
                this,
                WEBPAGES_CONTENT_URI, null, null, null,
                "_id asc");
        c = cursorLoader.loadInBackground();
        if (c.moveToFirst()) {
            do{
                webpages.add(c.getString(c.getColumnIndex("_id")) + ", "
                        + c.getString(c.getColumnIndex("url")));
            } while (c.moveToNext());
        }
        return webpages;
    }

    //This method gets all the images stored in the database that match the webpage id that is passed
    //as a parameter
    //This method is called from the viewURLFragment
    public ArrayList<String> getImages(String id){
        ArrayList<String> images = new ArrayList<>();
        Cursor c;
        CursorLoader cursorLoader = new CursorLoader(
                this,
                IMAGES_CONTENT_URI, null, "webpages_id="+id, null,
                "_id asc");
        c = cursorLoader.loadInBackground();
        if (c.moveToFirst()) {
            do{
                images.add(c.getString(c.getColumnIndex("_id")) + ", "
                        + c.getString(c.getColumnIndex("url")));
            } while (c.moveToNext());
        }
        return images;
    }
    //This method launches the add new url fragment
    //contains edit text for user input (can handle multiple entries at once)
    //and a button to start the service that adds the url and its images to the content provider database
    public void launchAddURLFragment(){

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        AddURLFragment auf = new AddURLFragment();
        fragmentTransaction.replace(R.id.fragmentContainer, auf);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
    //This method launches the view fragment
    //it populates a spinner with all the saved webpages
    //when a webpage is selected a listview of its images is displayed
    public void launchViewURLFragment(){

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        ViewURLFragment vf = new ViewURLFragment();
        //pass the list of webpages as an arraylist to the fragment
        final Bundle bundle = new Bundle();
        bundle.putStringArrayList("webpages",getWebpages());
        vf.setArguments(bundle);
        fragmentTransaction.replace(R.id.fragmentContainer, vf);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_add:
                launchAddURLFragment();
                return true;
            case R.id.action_view:
                launchViewURLFragment();
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

}
