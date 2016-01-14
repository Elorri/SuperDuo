package it.jaschke.alexandria.controller.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import it.jaschke.alexandria.R;
import it.jaschke.alexandria.Status;
import it.jaschke.alexandria.controller.activity.MainActivity;
import it.jaschke.alexandria.controller.extras.Tools;
import it.jaschke.alexandria.model.data.BookContract;
import it.jaschke.alexandria.model.services.BookService;
import it.jaschke.alexandria.model.services.DownloadImage;


public class AddBookFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String TAG = "INTENT_TO_SCAN_ACTIVITY";


    private final int LOADER_ID = 1;
    private View view;
    private final String ISBN_CONTENT = "isbnContent";
    private static final String SCAN_FORMAT = "scanFormat";
    private static final String SCAN_CONTENTS = "scanContents";

    private String mScanFormat = "Format:";
    private String mScanContents = "Contents:";

    private TextView mEmptyTextView;
    private SearchView mIsbnSearchView;
    private TextView mBookTitleTextView;
    private TextView mBookSubTitleTextView;
    private TextView mAuthorsTextView;
    private TextView mCategoriesTextView;
    private ImageView mBookCover;

    private String mIsbn;
    private BroadcastReceiver internetReceiver;

    private void refresh() {
        String isbnUserInput = mIsbnSearchView.getQuery().toString();
        if (isbnUserInput.length() == 0) {
            mIsbnSearchView.setQueryHint(getResources().getString(R.string.input_hint));
            mEmptyTextView.setText(R.string.add_book_isbn);
            return;
        }
        mIsbn = Tools.fixIsbn(isbnUserInput);
        Log.e("SuperDuo", Thread.currentThread().getStackTrace()[2] + "fixed mIsbn:" + mIsbn);
        if (mIsbn.length() < 13) {
            mIsbn = null;
            clearFields();
            mEmptyTextView.setText(R.string.add_book_isbn);
            return;
        }
        //TODO : 2.1 disallow to enter more than 13 char
        mIsbn = mIsbn.substring(0, 13);
        Log.e("SuperDuo", Thread.currentThread().getStackTrace()[2] + "fixed mIsbn:" + mIsbn);
        //This will restart the loader when table book status will change.  No need to call restartLoader here.
        addBookIntent(mIsbn);




    }


    public AddBookFragment() {
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.e("SuperDuo", Thread.currentThread().getStackTrace()[2] + "");
        super.onSaveInstanceState(outState);
        if (mIsbnSearchView != null) {
            outState.putString(ISBN_CONTENT, mIsbnSearchView.getQuery().toString());
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.e("SuperDuo", Thread.currentThread().getStackTrace()[2] + "");
        getActivity().setTitle(R.string.scan);


        view = inflater.inflate(R.layout.fragment_add_book, container, false);
        mEmptyTextView = (TextView) view.findViewById(R.id.noBookFound);
        mIsbnSearchView = (SearchView) view.findViewById(R.id.isbnSearchView);
        Button scanButton = (Button) view.findViewById(R.id.scan_button);
        mBookTitleTextView = ((TextView) view.findViewById(R.id.bookTitle));
        mBookSubTitleTextView = ((TextView) view.findViewById(R.id.bookSubTitle));
        mAuthorsTextView = ((TextView) view.findViewById(R.id.authors));
        mCategoriesTextView = ((TextView) view.findViewById(R.id.categories));
        mBookCover = (ImageView) view.findViewById(R.id.bookCover);


        mIsbnSearchView.setIconified(false);
        mIsbnSearchView.setQueryHint(getResources().getString(R.string.input_hint));
        mIsbnSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                refresh();
                return true;
            }

        });

        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // This is the callback method that the system will invoke when your button is
                // clicked. You might do this by launching another app or by including the
                //functionality directly in this app.
                // Hint: Use a Try/Catch block to handle the Intent dispatch gracefully, if you
                // are using an external app.
                //when you're done, remove the toast below.
                //TODO : 2.1 remove this toast
                CharSequence text = "This button should let you scan a book for its barcode!";
                Toast toast = Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT);
                toast.show();
                //TODO : 2.1 addBookIntent(isbnValue);
            }
        });


        if (savedInstanceState != null) {
            this.mIsbnSearchView.setQuery(savedInstanceState.getString(ISBN_CONTENT), false);
        }

        return view;
    }


    private void addBookIntent(String isbnValue) {
        //Once we have an ISBN, start a book intent
        Intent bookIntent = new Intent(getActivity(), BookService.class);
        bookIntent.putExtra(BookService.ISBN, isbnValue);
        bookIntent.setAction(BookService.FETCH_BOOK);
        getActivity().startService(bookIntent);
        //TODO : Does removing this line remove the add book without internet bug ?
        //AddBookFragment.this.restartLoader();
    }

    private void restartLoader() {
        Log.e("SuperDuo", Thread.currentThread().getStackTrace()[2] + "");
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.e("SuperDuo", Thread.currentThread().getStackTrace()[2] + "");
        String userInput = mIsbnSearchView.getQuery().toString();
        long isbn;
        if (userInput.equals(""))
            isbn = -1;
        else
            isbn = Long.parseLong(Tools.fixIsbn(userInput));
        Log.e("SuperDuo", Thread.currentThread().getStackTrace()[2] + "isbn:" + isbn);
        return new CursorLoader(
                getActivity(),
                BookContract.BookEntry.buildFullBookUri(isbn),
                null,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.e("SuperDuo", Thread.currentThread().getStackTrace()[2] + "cursor.size:" + data.getCount());
        if (data.getCount() == 0) {
            updateEmptyView(data);
            return;
        }

        data.moveToFirst();

        //If the book returned has an empty title, subtitle, authors, imageUrl, or category, we
        // have 2 options :
        // - not including it in the db : meaning if the book detail isn't complete we don't want it
        // - including it in the db with its isbn, and display empty string to the user.
        // Note : setText(null) does exactly that without crashing.
        // We only need to make sure we won't access those object (here String) methods.
        String bookTitle = data.getString(data.getColumnIndex(BookContract.BookEntry.TITLE));
        String bookSubTitle = data.getString(data.getColumnIndex(BookContract.BookEntry.SUBTITLE));
        String authors = data.getString(data.getColumnIndex(BookContract.AuthorEntry.AUTHOR));
        String imgUrl = data.getString(data.getColumnIndex(BookContract.BookEntry.IMAGE_URL));
        String categories = data.getString(data.getColumnIndex(BookContract.CategoryEntry.CATEGORY));

        Log.e("SuperDuo", Thread.currentThread().getStackTrace()[2] + "bookTitle:" + bookTitle);
        Log.e("SuperDuo", Thread.currentThread().getStackTrace()[2] + "bookSubTitle:" + bookSubTitle);
        Log.e("SuperDuo", Thread.currentThread().getStackTrace()[2] + "authors:" + authors);
        Log.e("SuperDuo", Thread.currentThread().getStackTrace()[2] + "imgUrl:" + imgUrl);
        Log.e("SuperDuo", Thread.currentThread().getStackTrace()[2] + "categories:" + categories);


        mEmptyTextView.setVisibility(View.INVISIBLE);
        mBookTitleTextView.setVisibility(View.VISIBLE);
        mBookSubTitleTextView.setVisibility(View.VISIBLE);
        mCategoriesTextView.setVisibility(View.VISIBLE);

        mBookTitleTextView.setText(bookTitle);
        mBookSubTitleTextView.setText(bookSubTitle);
        mCategoriesTextView.setText(categories);

        //String[] authorsArr = authors.split(","); //could cause NullPointerException
        if (authors == null) {
            mAuthorsTextView.setVisibility(View.VISIBLE);
            mAuthorsTextView.setText("");
        } else {
            String[] authorsArr = authors.split(",");
            mAuthorsTextView.setVisibility(View.VISIBLE);
            mAuthorsTextView.setLines(authorsArr.length);
            mAuthorsTextView.setText(authors.replace(",", "\n"));
        }

        //TODO 2.4 check this method
        //TODO 2.4 also note that there is no image display if no internet + no cache not good
        // experience
        if (Patterns.WEB_URL.matcher(imgUrl).matches()) {
            new DownloadImage(mBookCover).execute(imgUrl);
            mBookCover.setVisibility(View.VISIBLE);
        }

    }

    private void updateEmptyView(Cursor cursor) {
        if (mIsbn == null) {
            mEmptyTextView.setText(R.string.add_book_isbn);
            return;
        }
        if (cursor.getCount() != 0)
            return;


        @Status.NetworkStatus int networkStatus = Status.getNetworkStatus(getContext());
        clearFields();
        if (networkStatus == Status.INTERNET_OFF) {
            mEmptyTextView.setText(R.string.no_books_internet_off);
            return;
        }
        @Status.GoogleBookApiStatus int footballApiStatus = Status.getGoogleBookApiStatus(getContext());
        if ((networkStatus == Status.INTERNET_ON) && (footballApiStatus == Status.SERVEUR_DOWN)) {
            mEmptyTextView.setText(R.string.no_books_serveur_down);
            return;
        }
        if ((networkStatus == Status.INTERNET_ON) && (footballApiStatus == Status.SERVEUR_WRONG_URL_APP_INPUT)) {
            mEmptyTextView.setText(R.string.no_books_wrong_url_app_input);
            return;
        }
        @Status.BookTableStatus int bookTableStatus = Status.getBookTableStatus(getContext());
        if ((networkStatus == Status.INTERNET_ON) && (footballApiStatus == Status.SERVEUR_OK) &&
                (bookTableStatus == Status.TABLE_STATUS_UNKNOWN)) {
            mEmptyTextView.setText(R.string.no_books_table_status_unknown);
            return;
        }
        mEmptyTextView.setText(R.string.no_books);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.e("SuperDuo", Thread.currentThread().getStackTrace()[2] + "");
    }

    private void clearFields() {
        mEmptyTextView.setVisibility(View.VISIBLE);
        mBookTitleTextView.setVisibility(View.INVISIBLE);
        mBookSubTitleTextView.setVisibility(View.INVISIBLE);
        mAuthorsTextView.setVisibility(View.INVISIBLE);
        mCategoriesTextView.setVisibility(View.INVISIBLE);
        mBookTitleTextView.setText("");
        mBookSubTitleTextView.setText("");
        mAuthorsTextView.setText("");
        mCategoriesTextView.setText("");
        mBookCover.setVisibility(View.INVISIBLE);
    }


    @Override
    public void onResume() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sp.registerOnSharedPreferenceChangeListener(this);
        super.onResume();
    }

    @Override
    public void onPause() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sp.unregisterOnSharedPreferenceChangeListener(this);
        if (MainActivity.IS_TABLET && view.findViewById(R.id.right_container) == null) {
            getActivity().getSupportFragmentManager().popBackStack();
        }
        super.onPause();
    }


    @Override
    public void onStart() {
        Log.e("SuperDuo", Thread.currentThread().getStackTrace()[2] + "");
        getLoaderManager().initLoader(LOADER_ID, null, this);
        internetReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                boolean isConnected = Tools.isNetworkAvailable(getContext());
                Log.e("SuperDuo", Thread.currentThread().getStackTrace()[2] + "isConnected" + isConnected);
                if (isConnected) {
                    Log.e("SuperDuo", Thread.currentThread().getStackTrace()[2] + "");
                    Status.setNetworkStatus(context, Status.INTERNET_ON);
                    refresh();
                    return;
                }
                Status.setNetworkStatus(context, Status.INTERNET_OFF);
            }
        };
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        getActivity().registerReceiver(internetReceiver, filter);
        super.onStart();
    }

    @Override
    public void onStop() {
        getActivity().unregisterReceiver(internetReceiver);
        super.onStop();
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_book_table_status_key))) {
            //updateEmptyView(mCursor);
            restartLoader();
        }
    }

    //TODO : 2.0 decide to keep or not the delete in BookServiec
    public static String thread() {
        if (Looper.getMainLooper().getThread() == Thread.currentThread())
            return "ThreadUI";
        else return "Background";
    }
}
