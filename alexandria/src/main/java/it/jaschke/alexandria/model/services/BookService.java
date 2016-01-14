package it.jaschke.alexandria.model.services;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Looper;
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

import it.jaschke.alexandria.Status;
import it.jaschke.alexandria.controller.extras.Tools;
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
        Log.d("SuperDuo", "current thread : " + thread());
        if (isbn != null) {
            getContentResolver().delete(BookContract.BookEntry.buildBookUri(Long.parseLong(isbn)), null, null);
        }
    }

    /**
     * Handle action fetchBook in the provided background thread with the provided
     * parameters.
     */
    private void fetchBook(String isbn) {
        Log.d("SuperDuo", "current thread : " + thread());
        Log.e("SuperDuo", Thread.currentThread().getStackTrace()[2] + "isbn"+isbn);


        // re-init table status
        Log.e("SuperDuo", Thread.currentThread().getStackTrace()[2] + "TABLE_STATUS_UNKNOWN");
        Status.setBookTableStatus(getApplicationContext(), Status.TABLE_STATUS_UNKNOWN);


        if (isbn.length() != 13) {
            Log.e("SuperDuo", Thread.currentThread().getStackTrace()[2] + "isbn.length() != 13");
            Status.setBookTableStatus(getApplicationContext(), Status.TABLE_SYNC_DONE);
            return;
        }

        Cursor bookEntry = getContentResolver().query(
                BookContract.BookEntry.buildBookUri(Long.parseLong(isbn)),
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );


        if (bookEntry.getCount() > 0) {
            Log.e("SuperDuo", Thread.currentThread().getStackTrace()[2] + "bookEntry.getCount() > 0");
            bookEntry.close();

            //this line will induce a restartLoader in AddBookFragment
            Status.setBookTableStatus(getApplicationContext(), Status.TABLE_SYNC_DONE);
            return;
        }
        bookEntry.close();

        syncDB(isbn);
    }

    private void syncDB(String isbn) {
        Context context = getApplicationContext();
        if (!Tools.isNetworkAvailable(context)) {
            Status.setNetworkStatus(context, Status.INTERNET_OFF);
            return;
        }
        Status.setNetworkStatus(context, Status.INTERNET_ON);


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
            Log.e("Lifecycle", Thread.currentThread().getStackTrace()[2] + "url " + url);

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");

            //This is where we get an error if manifest has no internet permission
            urlConnection.connect();

            Log.e("SuperDuo", Thread.currentThread().getStackTrace()[2] + "urlConnection" +
                    urlConnection);

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            Log.e("SuperDuo", Thread.currentThread().getStackTrace()[2] + "inputStream" + inputStream);
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
            //This string will be null if user device not connected to network
            bookJsonString = buffer.toString();

            setServeurStatus(getApplicationContext(), new JSONObject(bookJsonString));
            if (!(Status.getGoogleBookApiStatus(getApplicationContext()) == Status.SERVEUR_OK))
                // we won't be able to fetch data, no need to go further
                return;

            final String ITEMS = "items";

            final String VOLUME_INFO = "volumeInfo";

            final String TITLE = "title";
            final String SUBTITLE = "subtitle";
            final String AUTHORS = "authors";
            final String DESC = "description";
            final String CATEGORIES = "categories";
            final String IMG_URL_PATH = "imageLinks";
            final String IMG_URL = "thumbnail";



            Log.e("SuperDuo", Thread.currentThread().getStackTrace()[2] + "bookJsonString" +
                    bookJsonString);
            JSONObject bookJson = new JSONObject(bookJsonString);
            JSONArray bookArray;
            if(bookJson.has(ITEMS)){
                bookArray = bookJson.getJSONArray(ITEMS);
            }else{
                Log.e("SuperDuo", Thread.currentThread().getStackTrace()[2] + "TABLE_SYNC_DONE");
                Status.setBookTableStatus(getApplicationContext(), Status.TABLE_SYNC_DONE);
                //TODO : if book not found the db does not change hence the fragment. Add
                // callback here
                return;
            }

            JSONObject bookInfo = ((JSONObject) bookArray.get(0)).getJSONObject(VOLUME_INFO);
            String title = bookInfo.getString(TITLE);

            String subtitle = "";
            if (bookInfo.has(SUBTITLE)) {
                subtitle = bookInfo.getString(SUBTITLE);
            }

            String desc = "";
            if (bookInfo.has(DESC)) {
                desc = bookInfo.getString(DESC);
            }

            String imgUrl = "";
            if (bookInfo.has(IMG_URL_PATH) && bookInfo.getJSONObject(IMG_URL_PATH).has(IMG_URL)) {
                imgUrl = bookInfo.getJSONObject(IMG_URL_PATH).getString(IMG_URL);
            }

            writeBackBook(isbn, title, subtitle, desc, imgUrl);

            if (bookInfo.has(AUTHORS)) {
                writeBackAuthors(isbn, bookInfo.getJSONArray(AUTHORS));
            }
            if (bookInfo.has(CATEGORIES)) {
                writeBackCategories(isbn, bookInfo.getJSONArray(CATEGORIES));
            }
            Log.e("SuperDuo", Thread.currentThread().getStackTrace()[2] + "TABLE_SYNC_DONE");
            Status.setBookTableStatus(getApplicationContext(), Status.TABLE_SYNC_DONE);

        } catch (IOException e) {
            //catch exceptions more precisely
            Log.e(LOG_TAG, "IOException" + e.getMessage());
            Log.e("SuperDuo", "IOException" + e.getMessage());
            Status.setGoogleBookApiStatus(getApplicationContext(), Status.SERVEUR_DOWN);
            e.printStackTrace();
        } catch (JSONException e) {
            Log.e(LOG_TAG, "JSONException" + e.getMessage());
            Log.e("SuperDuo", "JSONException" + e.getMessage());
            e.printStackTrace();
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


    }


    void setServeurStatus(Context context, JSONObject jsonObject) throws JSONException {
        Log.e("SuperDuo", Thread.currentThread().getStackTrace()[2] + "");
        if (jsonObject == null) {
            Log.e("SuperDuo", Thread.currentThread().getStackTrace()[2] + "jsonObject is null");
            Status.setGoogleBookApiStatus(context, Status.SERVEUR_WRONG_URL_APP_INPUT);
            return;
        }

        String ERROR_TAG = "error";

        if (jsonObject.has(ERROR_TAG)) {
            int errorCode = jsonObject.getInt(ERROR_TAG);
            switch (errorCode) {
                case HttpURLConnection.HTTP_BAD_REQUEST:
                    Log.e("SuperDuo", Thread.currentThread().getStackTrace()[2] + "HTTP_BAD_REQUEST");
                    Status.setGoogleBookApiStatus(context, Status.SERVEUR_WRONG_URL_APP_INPUT);
                    break;
                case HttpURLConnection.HTTP_FORBIDDEN:
                    Log.e("SuperDuo", Thread.currentThread().getStackTrace()[2] + "HTTP_FORBIDDEN");
                    Status.setGoogleBookApiStatus(context, Status.SERVEUR_WRONG_URL_APP_INPUT);
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    Log.e("SuperDuo", Thread.currentThread().getStackTrace()[2] + "HTTP_NOT_FOUND");
                    Status.setGoogleBookApiStatus(context, Status.SERVEUR_WRONG_URL_APP_INPUT);
                    break;
            }
        } else {
            Log.e("SuperDuo", Thread.currentThread().getStackTrace()[2] + "SERVEUR_OK");
            Status.setGoogleBookApiStatus(context, Status.SERVEUR_OK);
        }
    }

    private void writeBackBook(String isbn, String title, String subtitle, String desc, String imgUrl) {
        ContentValues values = new ContentValues();
        values.put(BookContract.BookEntry._ID, isbn);
        values.put(BookContract.BookEntry.TITLE, title);
        values.put(BookContract.BookEntry.IMAGE_URL, imgUrl);
        values.put(BookContract.BookEntry.SUBTITLE, subtitle);
        values.put(BookContract.BookEntry.DESC, desc);
        getContentResolver().insert(BookContract.BookEntry.CONTENT_URI, values);
    }

    private void writeBackAuthors(String isbn, JSONArray jsonArray) throws JSONException {
        ContentValues values = new ContentValues();
        for (int i = 0; i < jsonArray.length(); i++) {
            values.put(BookContract.AuthorEntry._ID, isbn);
            values.put(BookContract.AuthorEntry.AUTHOR, jsonArray.getString(i));
            getContentResolver().insert(BookContract.AuthorEntry.CONTENT_URI, values);
            values = new ContentValues();
        }
    }

    private void writeBackCategories(String isbn, JSONArray jsonArray) throws JSONException {
        ContentValues values = new ContentValues();
        for (int i = 0; i < jsonArray.length(); i++) {
            values.put(BookContract.CategoryEntry._ID, isbn);
            values.put(BookContract.CategoryEntry.CATEGORY, jsonArray.getString(i));
            getContentResolver().insert(BookContract.CategoryEntry.CONTENT_URI, values);
            values = new ContentValues();
        }
    }

    public static String thread() {
        if (Looper.getMainLooper().getThread() == Thread.currentThread())
            return "ThreadUI";
        else return "Background";
    }
}