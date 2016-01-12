package it.jaschke.alexandria.controller.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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


    EditText mIsbnEditText;
    Button mSaveButton;
    Button mDeleteButton;
    TextView mBookTitleTextView;
    TextView mBookSubTitleTextView;
    TextView mAuthorsTextView;
    TextView mCategoriesTextView;
    ImageView mBookCover;

    String mIsbn;
    BroadcastReceiver internetReceiver;


    public AddBookFragment() {
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mIsbnEditText != null) {
            outState.putString(ISBN_CONTENT, mIsbnEditText.getText().toString());
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_add_book, container, false);

        mIsbnEditText = (EditText) view.findViewById(R.id.isbnEditText);
        Button scanButton = (Button) view.findViewById(R.id.scan_button);
        mBookTitleTextView = ((TextView) view.findViewById(R.id.bookTitle));
        mBookSubTitleTextView = ((TextView) view.findViewById(R.id.bookSubTitle));
        mAuthorsTextView = ((TextView) view.findViewById(R.id.authors));
        mCategoriesTextView = ((TextView) view.findViewById(R.id.categories));
        mBookCover = (ImageView) view.findViewById(R.id.bookCover);


        mIsbnEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //no need
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //no need
            }

            @Override
            public void afterTextChanged(Editable isbnUserInput) {
                mIsbn = Tools.fixIsbn(isbnUserInput.toString());
                if (mIsbn.length() < 13) {
                    clearFields();
                    return;
                }
                addBookIntent(mIsbn);
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

        mSaveButton = (Button) view.findViewById(R.id.save_button);
        mDeleteButton = (Button) view.findViewById(R.id.delete_button);

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddBookFragment.this.mIsbnEditText.setText("");
                //TODO : 2.0 addToFavorites();?
            }
        });

        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteBookIntent(mIsbnEditText.getText().toString());
                AddBookFragment.this.mIsbnEditText.setText("");
            }
        });

        if (savedInstanceState != null) {
            this.mIsbnEditText.setText(savedInstanceState.getString(ISBN_CONTENT));
            //TODO : 2.0 see behavior of this hint
            this.mIsbnEditText.setHint("Toto is my hint did it worked ?");
        }

        return view;
    }

    private void deleteBookIntent(String isbnValue) {
        Intent bookIntent = new Intent(getActivity(), BookService.class);
        bookIntent.putExtra(BookService.ISBN, isbnValue);
        bookIntent.setAction(BookService.DELETE_BOOK);
        getActivity().startService(bookIntent);
        //TODO : 2.0 AddBookFragment.this.restartLoader(); ?
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
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        //TODO : 2.0 when does this happen ?
        if (mIsbnEditText.getText().length() == 0) {
            return null;
        }
        long isbn = Long.parseLong(Tools.fixIsbn(mIsbnEditText.getText().toString()));
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
        if (!data.moveToFirst()) {
            //TODO : 2.0 add empty message
            return;
        }

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


        mBookTitleTextView.setText(bookTitle);
        mBookSubTitleTextView.setText(bookSubTitle);
        mCategoriesTextView.setText(categories);

        //String[] authorsArr = authors.split(","); //could cause NullPointerException
        if (authors == null)
            mAuthorsTextView.setText("");
        else {
            String[] authorsArr = authors.split(",");
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

        //TODO android:drawableLeft="@drawable/ic_action_accept" save_button  in all layouts
        //TODO android:drawableLeft="@drawable/delete" delete_button in all layouts
        mSaveButton.setVisibility(View.VISIBLE);
        mDeleteButton.setVisibility(View.VISIBLE);


        updateEmptyView(data);
    }

    private void updateEmptyView(Cursor cursor) {
        //TODO : 2.0  if(mIsbn.equals("")) ?
        if (mIsbn == null)
            return;
        if (cursor.getCount() != 0)
            return;


        @Status.NetworkStatus int networkStatus = Status.getNetworkStatus(getContext());
        TextView emptyTextView = (TextView) getView().findViewById(R.id.noBookFound);
        clearFields();
        emptyTextView.setVisibility(View.VISIBLE);
        if (networkStatus == Status.INTERNET_OFF) {
            emptyTextView.setText(R.string.no_books_internet_off);
            return;
        }
        @Status.GoogleBookApiStatus int footballApiStatus = Status.getGoogleBookApiStatus(getContext());
        if ((networkStatus == Status.INTERNET_ON) && (footballApiStatus == Status.SERVEUR_DOWN)) {
            emptyTextView.setText(R.string.no_books_serveur_down);
            return;
        }
        if ((networkStatus == Status.INTERNET_ON) && (footballApiStatus == Status.SERVEUR_WRONG_URL_APP_INPUT)) {
            emptyTextView.setText(R.string.no_books_wrong_url_app_input);
            return;
        }
        @Status.BookTableStatus int bookTableStatus = Status.getBookTableStatus(getContext());
        if ((networkStatus == Status.INTERNET_ON) && (footballApiStatus == Status.SERVEUR_OK) &&
                (bookTableStatus == Status.TABLE_STATUS_UNKNOWN)) {
            emptyTextView.setText(R.string.no_books_table_status_unknown);
            return;
        }
        emptyTextView.setText(R.string.no_books);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
//TODO: 2.0 swapCursor(null) ?
    }

    private void clearFields() {
        //TODO : 2.0 check if set INVISIBLE is enough
        mBookTitleTextView.setVisibility(View.INVISIBLE);
        mBookSubTitleTextView.setVisibility(View.INVISIBLE);
        mAuthorsTextView.setVisibility(View.INVISIBLE);
        mCategoriesTextView.setVisibility(View.INVISIBLE);

//        mBookTitleTextView.setText("");
//        mBookSubTitleTextView.setText("");
//        mAuthorsTextView.setText("");
//        mCategoriesTextView.setText("");
        mBookCover.setVisibility(View.INVISIBLE);
        mSaveButton.setVisibility(View.INVISIBLE);
        mDeleteButton.setVisibility(View.INVISIBLE);
    }


    //TODO : 4.0 remove onAttach methods
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        activity.setTitle(R.string.scan);
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
        internetReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (Tools.isNetworkAvailable(getContext()))
                    Log.e("SuperDuo", Thread.currentThread().getStackTrace()[2] + "");
                    restartLoader();
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
            // updateEmptyView();
        }
    }
}
