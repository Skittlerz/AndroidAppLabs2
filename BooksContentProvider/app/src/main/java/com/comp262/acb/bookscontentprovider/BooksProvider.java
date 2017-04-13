package com.comp262.acb.bookscontentprovider;

import android.content.ContentProvider;
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
 * Created by acb on 2017-04-06.
 *
 * Creating your own content provider involves the following steps:
 *
 * Create a class that extends ContentProvider
 * Create a contract class (this app does not use a contract class)
 * Create the UriMatcher definition
 * Implement the onCreate() method
 * Implement the getType() method
 * Implement the CRUD methods
 * Add the content provider to your AndroidManifest.xml
 */

public class BooksProvider extends ContentProvider {

    /**
     * This app does not use a contract class, however it is advised to do so
     *
     * As per Android documentation:
     * A contract class is a public final class that contains constant definitions
     * for the URIs, column names, MIME types, and other meta-data that pertain to
     * the provider. The class establishes a contract between the provider and other
     * applications by ensuring that the provider can be correctly accessed even if
     * there are changes to the actual values of URIs, column names, and so forth.
     */

    static final String PROVIDER_NAME = "com.comp262.acb.provider.Books";
    //uri format - content://authority/path/id
    static final Uri CONTENT_URI = Uri.parse("content://"+PROVIDER_NAME+"/books");
    static final String _ID = "_id";
    static final String TITLE = "title";
    static final String ISBN = "isbn";

    static final int BOOKS = 1;
    static final int BOOK_ID = 2;
    //Utility class to aid in matching URIs in content providers
    private static final UriMatcher uriMatcher;
    //set the UriMatcher Definitions
    //You pass the authority, a path pattern and an int value to the addURI() method
    //Android returns the int value later on when you try to match patterns
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME,"books",BOOKS);
        uriMatcher.addURI(PROVIDER_NAME,"books/#",BOOK_ID);
    }

    //for database use
    SQLiteDatabase booksDB;
    static final String DATABASE_NAME = "Books";
    static final String DATABASE_TABLE = "titles";
    static final int DATABASE_VERSION = 1;
    static final String DATABASE_CREATE =
            "create table " + DATABASE_TABLE +
            "(_id integer primary key autoincrement, "
            + "title text not null, isbn text not null);";
    private static class DatabaseHelper extends SQLiteOpenHelper{

        DatabaseHelper(Context context){
            super(context,DATABASE_NAME,null,DATABASE_VERSION);
        }
        @Override
        public void onCreate(SQLiteDatabase db){
            db.execSQL(DATABASE_CREATE);
        }
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
            Log.w("Provider database", "Upgrading database from version " +
                oldVersion + " to " + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
            onCreate(db);
        }
    }

    //delete a book entry
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
            case BOOKS:
                count = booksDB.delete(DATABASE_TABLE,arg1,arg2);
                break;
            case BOOK_ID:
                String id = arg0.getPathSegments().get(1);
                count = booksDB.delete(DATABASE_TABLE, _ID + " = " + id +
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
            case BOOKS:
                return "vnd.android.cursor.dir/vnd.learn2develop.books ";
            //get a particular book
            case BOOK_ID:
                return "vnd.android.cursor.item/vnd.learn2develop.books ";
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    //insert a book entry
    @Override
    public Uri insert(Uri uri, ContentValues values){
        // add a new book
        long rowID = booksDB.insert(DATABASE_TABLE,"",values);
        // if added successfully
        if (rowID > 0){
            // appends the new row number to the books uri format
            // ex: content://com.comp262.acb.provider.Books/books/2
            Uri _uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
            //notifies the content resolver that the dataset has changed
            getContext().getContentResolver().notifyChange(_uri, null);
            return _uri;
        }
        throw new SQLException("Failed to insert row into " + uri);
    }

    // method called when the app is started
    @Override
    public boolean onCreate(){
        Context context = getContext();
        //open a connection to the database
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        booksDB = dbHelper.getWritableDatabase();
        return (booksDB == null)? false:true;
    }

    // user can query the content provider (in this case for books)
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder){
        SQLiteQueryBuilder sqlBuilder = new SQLiteQueryBuilder();
        sqlBuilder.setTables(DATABASE_TABLE);
        // UriMatcher checks if the query is for a particular book
        // if it is, it appends id = number (number retrieved from uri parameter) to the where clause of the query
        // uri.getPathSegments returns List<String> of the path segments
        if(uriMatcher.match(uri) == BOOK_ID){
            sqlBuilder.appendWhere(_ID + " = " + uri.getPathSegments().get(1));
        }
        if(sortOrder == null || sortOrder == ""){
            sortOrder = TITLE;
        }
        Cursor c = sqlBuilder.query(
                booksDB,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);
        // register to watch a content URI for changes
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }
    // update a book entry
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs){
        int count = 0;
        switch (uriMatcher.match(uri)){
            case BOOKS:
                count = booksDB.update(DATABASE_TABLE,values,selection,selectionArgs);
                break;
            case BOOK_ID:
                count = booksDB.update(DATABASE_TABLE,values,_ID + " = " + uri.getPathSegments().get(1) +
                        (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;
            default: throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return count;
    }
}
