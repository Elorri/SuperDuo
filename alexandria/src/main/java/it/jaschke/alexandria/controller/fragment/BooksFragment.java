package it.jaschke.alexandria.controller.fragment;

import android.app.Activity;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import it.jaschke.alexandria.R;
import it.jaschke.alexandria.Status;
import it.jaschke.alexandria.controller.adapter.BooksAdapter;
import it.jaschke.alexandria.model.data.BookContract;


public class BooksFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> ,
        SharedPreferences.OnSharedPreferenceChangeListener{

    private BooksAdapter mBooksAdapter;
    private ListView mBookList;
    private int position = ListView.INVALID_POSITION;
    private EditText mSearchText;

    private final int LOADER_ID = 10;



    public interface Callback {
        void onItemSelected(String ean);
    }



    public BooksFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_books, container, false);
        mSearchText = (EditText) view.findViewById(R.id.searchText);
        mBookList = (ListView) view.findViewById(R.id.bookList);


        //TODO 2.4 check that the query method is call in the background thread, but this request
        // will be called again a few ms later. put null for better perf.
        Cursor cursor = getActivity().getContentResolver().query(
                BookContract.BookEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        mBooksAdapter = new BooksAdapter(getActivity(), cursor, 0);
        mBookList.setAdapter(mBooksAdapter);


        view.findViewById(R.id.searchButton).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        BooksFragment.this.restartLoader();
                    }
                }
        );

        mBookList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Cursor cursor = mBooksAdapter.getCursor();
                if (cursor != null && cursor.moveToPosition(position)) {
                    String isbn = cursor.getString(cursor.getColumnIndex(BookContract
                            .BookEntry._ID));
                    ((Callback) getActivity()).onItemSelected(isbn);
                }
            }
        });

        return view;
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
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_book_table_status_key))) {
            updateEmptyView();
        }
    }




    private void restartLoader() {
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        final String selection = BookContract.BookEntry.TITLE + " LIKE ? OR " + BookContract.BookEntry.SUBTITLE + " LIKE ? ";
        String searchString = mSearchText.getText().toString();

        if (searchString.length() > 0) {
            searchString = "%" + searchString + "%";
            return new CursorLoader(
                    getActivity(),
                    BookContract.BookEntry.CONTENT_URI,
                    null,
                    selection,
                    new String[]{searchString, searchString},
                    null
            );
        }

        return new CursorLoader(
                getActivity(),
                BookContract.BookEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mBooksAdapter.swapCursor(data);
        if (position != ListView.INVALID_POSITION) {
            mBookList.smoothScrollToPosition(position);
        }
        updateEmptyView();
    }

    private void updateEmptyView() {
        if (mBooksAdapter.getCount() != 0)
            return;

        @Status.NetworkStatus int networkStatus=Status.getNetworkStatus(getContext());
        TextView emptyTextView = (TextView) getView().findViewById(R.id.emptyListView);
        emptyTextView.setVisibility(View.VISIBLE);
        mBookList.setEmptyView(emptyTextView);
        if(networkStatus==Status.INTERNET_OFF) {
            emptyTextView.setText(R.string.no_books_internet_off);
            return;
        }
        @Status.GoogleBookApiStatus int footballApiStatus=Status.getGoogleBookApiStatus(getContext());
        if((networkStatus==Status.INTERNET_ON)&&(footballApiStatus==Status.SERVEUR_DOWN)) {
            emptyTextView.setText(R.string.no_books_serveur_down);
            return;
        }
        if((networkStatus==Status.INTERNET_ON)&&(footballApiStatus==Status.SERVEUR_WRONG_URL_APP_INPUT)) {
            emptyTextView.setText(R.string.no_books_wrong_url_app_input);
            return;
        }
        @Status.BookTableStatus int bookTableStatus=Status.getBookTableStatus(getContext());
        if((networkStatus==Status.INTERNET_ON)&&(footballApiStatus==Status.SERVEUR_OK)&&
                (bookTableStatus==Status.TABLE_STATUS_UNKNOWN)) {
            emptyTextView.setText(R.string.no_books_table_status_unknown);
            return;
        }
        emptyTextView.setText(R.string.no_books);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mBooksAdapter.swapCursor(null);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        activity.setTitle(R.string.books);
    }
}
