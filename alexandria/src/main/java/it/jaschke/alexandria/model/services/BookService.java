package it.jaschke.alexandria.model.services;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import it.jaschke.alexandria.controller.activity.MainActivity;
import it.jaschke.alexandria.R;
import it.jaschke.alexandria.model.data.BookContract;

// I made the decision to keep the IntentService class and not changing using a SyncAdapter. With
// a SyncAdapter we could have offer the user a selection of most read book and let him choose
// beetween them. But there would have been little chance that we got the isbn he was looking for.
// Searching through the network everytime he looks for a book, although more battery draining
// (especially if the user search more than one book), seems the best option to me.
/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 */
public class BookService extends IntentService {

    private final String LOG_TAG = BookService.class.getSimpleName();

    public static final String FETCH_BOOK = "it.jaschke.alexandria.services.action.FETCH_BOOK";
    public static final String DELETE_BOOK = "it.jaschke.alexandria.services.action.DELETE_BOOK";

    public static final String ISBN = "it.jaschke.alexandria.services.extra.ISBN";

    public BookService() {
        super("Alexandria");
    }

    //TODO : 2.0 add a connectivity check

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            final String isbn = intent.getStringExtra(ISBN);

            if (FETCH_BOOK.equals(action)) {
                fetchBook(isbn);
            } else if (DELETE_BOOK.equals(action)) {
                deleteBook(isbn);
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void deleteBook(String isbn) {
        if(isbn!=null) {
            getContentResolver().delete(BookContract.BookEntry.buildBookUri(Long.parseLong(isbn)), null, null);
        }
    }

    /**
     * Handle action fetchBook in the provided background thread with the provided
     * parameters.
     */
    private void fetchBook(String isbn) {

        if(isbn.length()!=13){
            return;
        }

        Cursor bookEntry = getContentResolver().query(
                BookContract.BookEntry.buildBookUri(Long.parseLong(isbn)),
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        //TODO: 2.0 why do we get a broadcast receiver message if we leave here ? when adding
        //TODO: 2.0 no we don't get broadcast receiver message (mistakefrom me) the registered
        // book is displayed.
        // same book
        if(bookEntry.getCount()>0){
            bookEntry.close();
            return;
        }

        bookEntry.close();

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String bookJsonString = null;

        try {
            final String FORECAST_BASE_URL = "https://www.googleapis.com/books/v1/volumes?";
            final String QUERY_PARAM = "q";
            final String ISBN_PARAM = "isbn:" + isbn;

            Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                    .appendQueryParameter(QUERY_PARAM, ISBN_PARAM)
                    .build();

            URL url = new URL(builtUri.toString());
            Log.e("Lifecycle", Thread.currentThread().getStackTrace()[2] +"url"+url);

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");

            //This is where we get an error if manifest has no internet permission
            urlConnection.connect();

            Log.e("Lifecycle", Thread.currentThread().getStackTrace()[2] + "urlConnection" + urlConnection);

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            Log.e("Lifecycle", Thread.currentThread().getStackTrace()[2] + "inputStream" + inputStream);
            if (inputStream == null) {
                return;
            }

            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
                buffer.append("\n");
            }

            if (buffer.length() == 0) {
                return;
            }
            //TODO : 2.0 enregistrer le status du serveur for UX message
            //This string will be null if user device not connected to network
            bookJsonString = buffer.toString();
            Log.e("Lifecycle", Thread.currentThread().getStackTrace()[2] + "bookJsonString" + bookJsonString);
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error ", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }

        }

        final String ITEMS = "items";

        final String VOLUME_INFO = "volumeInfo";

        final String TITLE = "title";
        final String SUBTITLE = "subtitle";
        final String AUTHORS = "authors";
        final String DESC = "description";
        final String CATEGORIES = "categories";
        final String IMG_URL_PATH = "imageLinks";
        final String IMG_URL = "thumbnail";

        try {
            Log.e("Lifecycle", Thread.currentThread().getStackTrace()[2] + "bookJsonString" + bookJsonString);
            JSONObject bookJson = new JSONObject(bookJsonString);
            JSONArray bookArray;
            if(bookJson.has(ITEMS)){
                bookArray = bookJson.getJSONArray(ITEMS);
            }else{
                Intent messageIntent = new Intent(MainActivity.NO_BOOK_AT_GOOGLE_MESSAGE);
                messageIntent.putExtra(MainActivity.NO_BOOKS_AT_GOOGLE_MESSAGE_KEY,getResources().getString(R.string.not_found));
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(messageIntent);
                return;
            }

            JSONObject bookInfo = ((JSONObject) bookArray.get(0)).getJSONObject(VOLUME_INFO);

            String title = bookInfo.getString(TITLE);

            String subtitle = "";
            if(bookInfo.has(SUBTITLE)) {
                subtitle = bookInfo.getString(SUBTITLE);
            }

            String desc="";
            if(bookInfo.has(DESC)){
                desc = bookInfo.getString(DESC);
            }

            String imgUrl = "";
            if(bookInfo.has(IMG_URL_PATH) && bookInfo.getJSONObject(IMG_URL_PATH).has(IMG_URL)) {
                imgUrl = bookInfo.getJSONObject(IMG_URL_PATH).getString(IMG_URL);
            }

            writeBackBook(isbn, title, subtitle, desc, imgUrl);

            if(bookInfo.has(AUTHORS)) {
                writeBackAuthors(isbn, bookInfo.getJSONArray(AUTHORS));
            }
            if(bookInfo.has(CATEGORIES)){
                writeBackCategories(isbn,bookInfo.getJSONArray(CATEGORIES) );
            }

        } catch (JSONException e) {
            Log.e(LOG_TAG, "Error ", e);
        }
    }

    private void writeBackBook(String isbn, String title, String subtitle, String desc, String imgUrl) {
        ContentValues values= new ContentValues();
        values.put(BookContract.BookEntry._ID, isbn);
        values.put(BookContract.BookEntry.TITLE, title);
        values.put(BookContract.BookEntry.IMAGE_URL, imgUrl);
        values.put(BookContract.BookEntry.SUBTITLE, subtitle);
        values.put(BookContract.BookEntry.DESC, desc);
        getContentResolver().insert(BookContract.BookEntry.CONTENT_URI,values);
    }

    private void writeBackAuthors(String isbn, JSONArray jsonArray) throws JSONException {
        ContentValues values= new ContentValues();
        for (int i = 0; i < jsonArray.length(); i++) {
            values.put(BookContract.AuthorEntry._ID, isbn);
            values.put(BookContract.AuthorEntry.AUTHOR, jsonArray.getString(i));
            getContentResolver().insert(BookContract.AuthorEntry.CONTENT_URI, values);
            values= new ContentValues();
        }
    }

    private void writeBackCategories(String isbn, JSONArray jsonArray) throws JSONException {
        ContentValues values= new ContentValues();
        for (int i = 0; i < jsonArray.length(); i++) {
            values.put(BookContract.CategoryEntry._ID, isbn);
            values.put(BookContract.CategoryEntry.CATEGORY, jsonArray.getString(i));
            getContentResolver().insert(BookContract.CategoryEntry.CONTENT_URI, values);
            values= new ContentValues();
        }
    }
}