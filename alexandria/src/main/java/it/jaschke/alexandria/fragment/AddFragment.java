package it.jaschke.alexandria.fragment;

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
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.vision.barcode.Barcode;

import it.jaschke.alexandria.R;
import it.jaschke.alexandria.activity.BarcodeCaptureActivity;
import it.jaschke.alexandria.activity.ListActivity;
import it.jaschke.alexandria.activity.MainActivity;
import it.jaschke.alexandria.data.BookContract;
import it.jaschke.alexandria.extras.Status;
import it.jaschke.alexandria.extras.Tools;
import it.jaschke.alexandria.services.BookService;
import it.jaschke.alexandria.zxing.FragmentIntentIntegrator;
import it.jaschke.alexandria.zxing.IntentIntegrator;
import it.jaschke.alexandria.zxing.IntentResult;

/**
 * This fragment will display data for the 'Add a book' screen of the app, allowing the user to
 * add book by isbn code or scan and also giving him messages feedback to guide him. The book
 * will be search on the internet by starting the BookService class
 * Created by Elorri on 23/01/2016.
 */
public class AddFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, SharedPreferences.OnSharedPreferenceChangeListener {

    private static final int RC_BARCODE_CAPTURE = 0;
    private static final String LOG_TAG = AddFragment.class.getSimpleName();


    private final int LOADER_ID = 1;
    private View view;
    private final String ISBN_CONTENT = "isbnContent";


    private TextView mEmptyTextView;
    private SearchView mIsbnSearchView;
    private TextView mBookTitleTextView;
    private TextView mBookSubTitleTextView;
    private TextView mAuthorsTextView;
    private TextView mCategoriesTextView;
    private ImageView mBookCover;
    private TextView mDesc;
    private Button mDismiss;
    private Button mSave;

    private String mIsbn;
    private BroadcastReceiver internetReceiver;



    public AddFragment() {    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mIsbnSearchView != null) {
            outState.putString(ISBN_CONTENT, mIsbnSearchView.getQuery().toString());
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Remove depreciated method onAttach
        getActivity().setTitle(R.string.title_activity_add);

        if (savedInstanceState != null)
            mIsbn = savedInstanceState.getParcelable(ISBN_CONTENT);


    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_add, container, false);
        mEmptyTextView = (TextView) view.findViewById(R.id.noBookFound);
        mIsbnSearchView = (SearchView) view.findViewById(R.id.isbnSearchView);
        FloatingActionButton scanButton = (FloatingActionButton) view.findViewById(R.id.scan_button);
        mBookTitleTextView = ((TextView) view.findViewById(R.id.bookTitle));
        mBookSubTitleTextView = ((TextView) view.findViewById(R.id.bookSubTitle));
        mAuthorsTextView = ((TextView) view.findViewById(R.id.authors));
        mCategoriesTextView = ((TextView) view.findViewById(R.id.categories));
        mBookCover = (ImageView) view.findViewById(R.id.bookCover);
        mDesc=(TextView) view.findViewById(R.id.bookDesc);
        mDismiss=(Button) view.findViewById(R.id.dismiss_button);
        mSave=(Button) view.findViewById(R.id.save_button);


        mIsbnSearchView.setIconified(false);
        mIsbnSearchView.clearFocus();
        mIsbnSearchView.setQueryHint(getResources().getString(R.string.input_hint));
        mIsbnSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                if((query.length()==0)||(query.length()>13)){
                    mIsbnSearchView.clearFocus();}
                refresh();
                return true;
            }

        });

        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Tools.isDeviceReadyForGooglePlayMobileVisionApi()) {
                    Intent intent = new Intent(getContext(), BarcodeCaptureActivity.class);
                    intent.putExtra(BarcodeCaptureActivity.AutoFocus, true);
                    intent.putExtra(BarcodeCaptureActivity.UseFlash, false);
                    startActivityForResult(intent, RC_BARCODE_CAPTURE);
                } else {
                    FragmentIntentIntegrator integrator =
                            new FragmentIntentIntegrator(AddFragment.this);
                    integrator.initiateScan();
                }
            }
        });

        mDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent bookIntent = new Intent(getActivity(), BookService.class);
                bookIntent.putExtra(BookService.ISBN, mIsbn);
                bookIntent.setAction(BookService.DELETE_BOOK);
                getActivity().startService(bookIntent);
                startListScreen();
            }
        });

        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent bookIntent = new Intent(getActivity(), BookService.class);
                bookIntent.putExtra(BookService.ISBN, mIsbn);
                bookIntent.setAction(BookService.SAVE_AS_FAVORITE_BOOK);
                getActivity().startService(bookIntent);
                startListScreen();
            }
        });

        if (savedInstanceState != null) {
            this.mIsbnSearchView.setQuery(savedInstanceState.getString(ISBN_CONTENT), false);
        }
        return view;
    }

    private void startListScreen() {
        if(MainActivity.isAddFirstScreen){
            getActivity().startActivity(new Intent(getActivity(), ListActivity.class));
        }else{
            getActivity().finish();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!Tools.isDeviceReadyForGooglePlayMobileVisionApi()) {
            IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode,
                    resultCode, data);
            if (scanResult == null) {
                Log.d(LOG_TAG, "No barcode captured, intent data is null");
                return;
            }
            String barcode = scanResult.getContents();
            mIsbnSearchView.setQuery(barcode, true);
        }

        //Device support GooglePlayMobileVisionApi
        if (requestCode == RC_BARCODE_CAPTURE) {
            if (data == null) {
                Log.d(LOG_TAG, "No barcode captured, intent data is null");
                return;
            }
            Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
            mIsbnSearchView.setQuery(barcode.displayValue, true);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private void addBookIntent(String isbnValue) {
        //Once we have an ISBN, start a book intent
        Intent bookIntent = new Intent(getActivity(), BookService.class);
        bookIntent.putExtra(BookService.ISBN, isbnValue);
        bookIntent.setAction(BookService.FETCH_BOOK);
        getActivity().startService(bookIntent);
    }

    private void restartLoader() {
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }


    private void refresh() {
        String isbnUserInput = mIsbnSearchView.getQuery().toString();
        if(mBookTitleTextView.getText().length()>0){
            mIsbnSearchView.clearFocus();}
        if (isbnUserInput.length() == 0) {
            mIsbnSearchView.clearFocus();
            mIsbnSearchView.setQueryHint(getResources().getString(R.string.input_hint));
            mEmptyTextView.setText(R.string.add_book_isbn);
            return;
        }
        mIsbn = Tools.fixIsbn(isbnUserInput);
        if (mIsbn.length() < 13) {
            mIsbn = null;
            clearFields();
            mEmptyTextView.setText(R.string.add_book_isbn);
            return;
        }
        mIsbn = mIsbn.substring(0, 13);
        //This will restart the loader when table book status will change.  No need to call restartLoader here.
        addBookIntent(mIsbn);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String userInput = mIsbnSearchView.getQuery().toString();
        long isbn;
        if (userInput.equals(""))
            isbn = -1;
        else
            isbn = Long.parseLong(Tools.fixIsbn(userInput));
        return new CursorLoader(
                getActivity(),
                BookContract.BookEntry.buildBookUri(isbn),
                null,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.getCount() == 0) {
            updateEmptyView(data);
            return;
        }

        data.moveToFirst();

        //If the book returned has an empty title, subtitle, authors, imageUrl, or category, we
        // have 2 options :
        // - not including it in the db : meaning if the book detail isn't complete we don't want it
        // - including it in the db with its isbn and null entries, and display empty string to the
        // user.
        // We will choose the second option
        // Note : setText(null) will  display empty string without crashing.
        // We only need to make sure we won't access those object (here String) methods.
        String bookTitle = data.getString(data.getColumnIndex(BookContract.BookEntry.COLUMN_TITLE));
        String bookSubTitle = data.getString(data.getColumnIndex(BookContract.BookEntry.COLUMN_SUBTITLE));
        String authors = data.getString(data.getColumnIndex(BookContract.AuthorEntry.COLUMN_AUTHOR));
        String imgUrl = data.getString(data.getColumnIndex(BookContract.BookEntry.COLUMN_IMAGE_URL));
        String categories = data.getString(data.getColumnIndex(BookContract.CategoryEntry.COLUMN_CATEGORY));
        String desc = data.getString(data.getColumnIndex(BookContract.BookEntry.COLUMN_DESC));

        mEmptyTextView.setVisibility(View.INVISIBLE);
        mBookTitleTextView.setVisibility(View.VISIBLE);
        mBookSubTitleTextView.setVisibility(View.VISIBLE);
        mCategoriesTextView.setVisibility(View.VISIBLE);
        mDesc.setVisibility(View.VISIBLE);
        mDismiss.setVisibility(View.VISIBLE);
        mSave.setVisibility(View.VISIBLE);

        mBookTitleTextView.setText(bookTitle);
        mBookSubTitleTextView.setText(bookSubTitle);
        mCategoriesTextView.setText(categories);
        mDesc.setText(desc);

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

        mBookCover.setVisibility(View.VISIBLE);
        Tools.loadImage(getContext(),imgUrl,bookTitle, mBookCover);



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
    }

    private void clearFields() {
        mEmptyTextView.setVisibility(View.VISIBLE);
        mBookTitleTextView.setVisibility(View.INVISIBLE);
        mBookSubTitleTextView.setVisibility(View.INVISIBLE);
        mAuthorsTextView.setVisibility(View.INVISIBLE);
        mCategoriesTextView.setVisibility(View.INVISIBLE);
        mDismiss.setVisibility(View.INVISIBLE);
        mSave.setVisibility(View.INVISIBLE);
        mDesc.setVisibility(View.INVISIBLE);
        mBookTitleTextView.setText("");
        mBookSubTitleTextView.setText("");
        mAuthorsTextView.setText("");
        mCategoriesTextView.setText("");
        mDesc.setText("");
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
        super.onPause();
    }


    @Override
    public void onStart() {
        getLoaderManager().initLoader(LOADER_ID, null, this);
        internetReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                boolean isConnected = Tools.isNetworkAvailable(getContext());
                if (isConnected) {
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
            restartLoader();
        }
    }

    public static String thread() {
        if (Looper.getMainLooper().getThread() == Thread.currentThread())
            return "ThreadUI";
        else return "Background";
    }
}
