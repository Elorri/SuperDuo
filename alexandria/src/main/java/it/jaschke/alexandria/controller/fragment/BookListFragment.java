package it.jaschke.alexandria.controller.fragment;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import it.jaschke.alexandria.R;
import it.jaschke.alexandria.controller.adapter.BooksAdapter;
import it.jaschke.alexandria.model.data.BookContract;


public class BookListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,
        SharedPreferences.OnSharedPreferenceChangeListener {

    private BooksAdapter mBooksAdapter;
    private ListView mBookList;
    private int position = ListView.INVALID_POSITION;
    private SearchView mSearchView;

    private final int LOADER_ID = 10;
    private boolean areAllBooksDisplayed;


    public interface Callback {
        void onItemSelected(String ean);
    }


    public BookListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.e("SuperDuo", Thread.currentThread().getStackTrace()[2] + "");
        super.onCreate(savedInstanceState);

        //Remove depreciated method onAttach
        getActivity().setTitle(R.string.books);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.e("SuperDuo", Thread.currentThread().getStackTrace()[2] + "");
        View view = inflater.inflate(R.layout.fragment_book_list, container, false);
        mSearchView = (SearchView) view.findViewById(R.id.searchView);
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

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
//                Toast.makeText(getActivity(), "query has been submitted : "
//                                + mSearchView.getQuery().toString(),
//                        Toast.LENGTH_SHORT).show();
//                restartLoader();
//                return true;
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                Toast.makeText(getActivity(), "text query has changed", Toast.LENGTH_SHORT).show();
                restartLoader();
                return true;
            }

        });

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
        Log.e("SuperDuo", Thread.currentThread().getStackTrace()[2] + "");
        getLoaderManager().initLoader(LOADER_ID, null, this);
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
        Log.e("SuperDuo", Thread.currentThread().getStackTrace()[2] + "");
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.e("SuperDuo", Thread.currentThread().getStackTrace()[2] + "");

        final String selection = BookContract.BookEntry.TITLE + " LIKE ? OR " + BookContract.BookEntry.SUBTITLE + " LIKE ? ";
        String searchString = mSearchView.getQuery().toString();

        if (searchString.length() > 0) {
            searchString = "%" + searchString + "%";
            areAllBooksDisplayed = false;
            return new CursorLoader(
                    getActivity(),
                    BookContract.BookEntry.CONTENT_URI,
                    null,
                    selection,
                    new String[]{searchString, searchString},
                    null
            );
        }
        areAllBooksDisplayed = true;
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
        Log.e("SuperDuo", Thread.currentThread().getStackTrace()[2] + "");
        mBooksAdapter.swapCursor(data);
        if (position != ListView.INVALID_POSITION) {
            mBookList.smoothScrollToPosition(position);
        }
        updateEmptyView();
    }

    private void updateEmptyView() {
        if (mBooksAdapter.getCount() != 0)
            return;
        TextView emptyTextView = (TextView) getView().findViewById(R.id.emptyListView);
        emptyTextView.setVisibility(View.VISIBLE);
        mBookList.setEmptyView(emptyTextView);
        if (areAllBooksDisplayed)
            emptyTextView.setText(R.string.no_books_in_your_library);
        else
            emptyTextView.setText(R.string.no_books);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.e("SuperDuo", Thread.currentThread().getStackTrace()[2] + "");
        mBooksAdapter.swapCursor(null);
    }


}
