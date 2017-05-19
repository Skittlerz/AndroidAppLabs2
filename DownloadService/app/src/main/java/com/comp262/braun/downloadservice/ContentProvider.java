package com.comp262.braun.downloadservice;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;


/**
 * Created by acb on 2017-05-17.
 */

public class ContentProvider extends android.content.ContentProvider {

    static final String PROVIDER_NAME = "com.comp262.acb.provider.URL";
    //uri format - content://authority/path/id
    //each table will need its own URI
    static final Uri WEBPAGES_CONTENT_URI = Uri.parse("content://"+PROVIDER_NAME+"/webpages");
    static final Uri IMAGES_CONTENT_URI = Uri.parse("content://"+PROVIDER_NAME+"/images");
    static final String _ID = "_id";
    static final String URL = "url";

    static final int WEBPAGES = 1;
    static final int WEBPAGES_ID = 2;
    static final int IMAGES = 3;
    static final int IMAGES_ID =4;
    //Utility class to aid in matching URIs in content providers
    private static final UriMatcher uriMatcher;
    //set the UriMatcher Definitions
    //You pass the authority, a path pattern and an int value to the addURI() method
    //Android returns the int value later on when you try to match patterns
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME,"webpages",WEBPAGES);
        uriMatcher.addURI(PROVIDER_NAME,"webpages/#",WEBPAGES_ID);
        uriMatcher.addURI(PROVIDER_NAME,"images",IMAGES);
        uriMatcher.addURI(PROVIDER_NAME,"images/#",IMAGES_ID);
    }

    //for database use
    SQLiteDatabase urlsDB;
    static final String DATABASE_NAME = "Urls";
    static final String DATABASE_TABLE_1 = "Webpages";
    static final String DATABASE_TABLE_2 = "Images";
    static final int DATABASE_VERSION = 1;
    static final String CREATE_TABLE_WEBPAGES =
            "create table " + DATABASE_TABLE_1 +
                    "(_id integer primary key autoincrement, "
                    + "url text not null);";
    static final String CREATE_TABLE_IMAGES =
            "create table " + DATABASE_TABLE_2 +
                    "(_id integer primary key autoincrement, "
                    + "url text not null, " +
                    "webpages_id integer not null);";

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context){
            super(context,DATABASE_NAME,null,DATABASE_VERSION);
        }
        @Override
        public void onCreate(SQLiteDatabase db){
            db.execSQL(CREATE_TABLE_WEBPAGES);
            db.execSQL(CREATE_TABLE_IMAGES);
        }
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
            Log.w("Provider database", "Upgrading database from version " +
                    oldVersion + " to " + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_1);
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_2);
            onCreate(db);
        }
    }

    //delete an entry
    @Override
    public int delete(Uri arg0, String arg1, String[] arg2){
        //arg0 = uri
        //arg1 = selection
        //arg2 = selectionArgs
        int count = 0;
        //able to use switch case here because of the use of UriMatcher
        //uriMatcher matches the parameter uri (passed to this method) to uris stored within the uriMatcher
        //as defined at the top of the class
        switch (uriMatcher.match(arg0)){
            case WEBPAGES:
                count = urlsDB.delete(DATABASE_TABLE_1,arg1,arg2);
                break;
            case WEBPAGES_ID:
                String web_id = arg0.getPathSegments().get(1);
                count = urlsDB.delete(DATABASE_TABLE_1, _ID + " = " + web_id +
                        (!TextUtils.isEmpty(arg1) ? " AND (" + arg1 + ')':""), arg2);
                break;
            case IMAGES:
                count = urlsDB.delete(DATABASE_TABLE_2,arg1,arg2);
                break;
            case IMAGES_ID:
                String image_id = arg0.getPathSegments().get(1);
                count = urlsDB.delete(DATABASE_TABLE_2, _ID + " = " + image_id +
                        (!TextUtils.isEmpty(arg1) ? " AND (" + arg1 + ')':""), arg2);
                break;
            default:throw new IllegalArgumentException("Unknown URI " + arg0);
        }
        // notify the content resolver that the dataset has changed
        getContext().getContentResolver().notifyChange(arg0,null);
        return count;
    }

    // returns the content type of all supported URIs
    @Override
    public String getType(Uri uri){
        //UriMatcher matches the uri parameter to uris stored within the UriMatcher
        // then returns the relevant content type as a string
        switch(uriMatcher.match(uri)){
            // get all books
            //TODO fix return string
            case WEBPAGES:
                return "vnd.android.cursor.dir/vnd.com.comp262.acb.provider.URL.Webpages";
            //get a particular webpage url
            case WEBPAGES_ID:
                return "vnd.android.cursor.item/vnd.com.comp262.acb.provider.URL.Webpages";
            case IMAGES:
                return "vnd.android.cursor.dir/vnd.com.comp262.acb.provider.URL.Images";
            //get a particular image url
            case IMAGES_ID:
                return "vnd.android.cursor.item/vnd.com.comp262.acb.provider.URL.Images";
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    //insert a new webpage or image url
    @Override
    public Uri insert(Uri uri, ContentValues values){

        Uri _uri = null;

        switch(uriMatcher.match(uri)) {
            // add a new record
            case WEBPAGES:
                //TODO insert into webpage and image table
                long rowID = urlsDB.insert(DATABASE_TABLE_1, "", values);
                // if added successfully
                if (rowID > 0) {
                    // appends the new row number to the books uri format
                    // ex: content://com.comp262.acb.provider.URLS/webpages/2
                    _uri = ContentUris.withAppendedId(WEBPAGES_CONTENT_URI, rowID);
                    //notifies the content resolver that the dataset has changed
                    getContext().getContentResolver().notifyChange(_uri, null);
                }
                break;
            case IMAGES:
                long imageRowID = urlsDB.insert(DATABASE_TABLE_2, "", values);
                // if added successfully
                if (imageRowID > 0) {
                    // appends the new row number to the books uri format
                    // ex: content://com.comp262.acb.provider.URLS/images/2
                    _uri = ContentUris.withAppendedId(IMAGES_CONTENT_URI, imageRowID);
                    //notifies the content resolver that the dataset has changed
                    getContext().getContentResolver().notifyChange(_uri, null);
                }
                break;
            default:
                throw new SQLException("Failed to insert row into " + uri);
        }
        return _uri;
    }

    // method called when the app is started
    @Override
    public boolean onCreate(){
        Context context = getContext();
        //open a connection to the database
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        urlsDB = dbHelper.getWritableDatabase();
        return (urlsDB == null)? false:true;
    }

    // user can query the content provider for urls
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder){
        SQLiteQueryBuilder sqlBuilder = new SQLiteQueryBuilder();
        Cursor c = null;
        switch (uriMatcher.match(uri)) {
            case WEBPAGES:
                sqlBuilder.setTables(DATABASE_TABLE_1);
                // UriMatcher checks if the query is for a particular book
                // if it is, it appends id = number (number retrieved from uri parameter) to the where clause of the query
                // uri.getPathSegments returns List<String> of the path segments
                if (uriMatcher.match(uri) == WEBPAGES_ID) {
                    sqlBuilder.appendWhere(_ID + " = " + uri.getPathSegments().get(1));
                }
                if (sortOrder == null || sortOrder == "") {
                    sortOrder = URL;
                }
                c = sqlBuilder.query(
                        urlsDB,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                // register to watch a content URI for changes
                c.setNotificationUri(getContext().getContentResolver(), uri);
                break;
            case IMAGES:
                sqlBuilder.setTables(DATABASE_TABLE_2);
                // UriMatcher checks if the query is for a particular book
                // if it is, it appends id = number (number retrieved from uri parameter) to the where clause of the query
                // uri.getPathSegments returns List<String> of the path segments
                if (uriMatcher.match(uri) == IMAGES_ID) {
                    sqlBuilder.appendWhere(_ID + " = " + uri.getPathSegments().get(1));
                }
                if (sortOrder == null || sortOrder == "") {
                    sortOrder = URL;
                }
                c = sqlBuilder.query(
                        urlsDB,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                // register to watch a content URI for changes
                c.setNotificationUri(getContext().getContentResolver(), uri);
                break;
            default:
                break;
        }
        return c;
    }
    // update a url entry
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs){
        int count = 0;
        switch (uriMatcher.match(uri)){
            case WEBPAGES:
                count = urlsDB.update(DATABASE_TABLE_1,values,selection,selectionArgs);
                break;
            case WEBPAGES_ID:
                count = urlsDB.update(DATABASE_TABLE_1,values,_ID + " = " + uri.getPathSegments().get(1) +
                        (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;
            case IMAGES:
                count = urlsDB.update(DATABASE_TABLE_2,values,selection,selectionArgs);
                break;
            case IMAGES_ID:
                count = urlsDB.update(DATABASE_TABLE_2,values,_ID + " = " + uri.getPathSegments().get(1) +
                        (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;
            default: throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return count;
    }
}
