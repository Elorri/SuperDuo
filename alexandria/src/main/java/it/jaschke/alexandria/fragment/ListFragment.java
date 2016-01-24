package it.jaschke.alexandria.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import it.jaschke.alexandria.R;
import it.jaschke.alexandria.activity.AddActivity;
import it.jaschke.alexandria.adapter.BooksAdapter;
import it.jaschke.alexandria.data.BookContract;


public class ListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,
        SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String QUERY = "query";
    private static final String SELECTED_KEY = "selected_position";
    private CharSequence mQuery;
    private int mPosition = ListView.INVALID_POSITION;

    private BooksAdapter mBooksAdapter;
    private ListView mBookList;
    private SearchView mSearchView;

    private final int LOADER_ID = 10;



    public interface Callback {
        void onItemSelected(Uri uri);
    }


    public ListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.e("SuperDuo", Thread.currentThread().getStackTrace()[2] + "");
        super.onCreate(savedInstanceState);

//        //Remove depreciated method onAttach
//        getActivity().setTitle(R.string.title_activity_list);

        if (savedInstanceState != null) {
            mQuery = savedInstanceState.getCharSequence(QUERY);
        }
        Log.e("SuperDuo", Thread.currentThread().getStackTrace()[2] + "");
        mBooksAdapter = new BooksAdapter(getActivity(), null, 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.e("SuperDuo", Thread.currentThread().getStackTrace()[2] + "");
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        mSearchView = (SearchView) view.findViewById(R.id.searchView);
        mBookList = (ListView) view.findViewById(R.id.bookList);
        FloatingActionButton addButton = (FloatingActionButton) view.findViewById(R.id
                .add_button);

        mBookList.setAdapter(mBooksAdapter);
        mSearchView.setIconified(false);
        mSearchView.clearFocus();
        if (mQuery != null) mSearchView.setQuery(mQuery, true);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                if (query.equals("")) mSearchView.clearFocus();
                restartLoader();
                return true;
            }

        });

        mBookList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                onItemClicked(adapterView, position);
                setPosition(position);
            }
        });


        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AddActivity.class));
            }
        });

        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }
        return view;
    }

    private void onItemClicked(AdapterView<?> adapterView, int position) {
        Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
        if (cursor != null) {
            String isbn = cursor.getString(cursor.getColumnIndex(BookContract
                    .BookEntry._ID));
            Uri uri = BookContract.BookEntry.buildFullBookUri(Long.parseLong(isbn));
            ((Callback) getActivity()).onItemSelected(uri);
        }
    }


    public void setPosition(int position) {
        mPosition = position;
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
        Uri uri = BookContract.BookEntry.CONTENT_URI;

        final String selection = BookContract.BookEntry.COLUMN_TITLE + " LIKE ? OR " + BookContract.BookEntry.COLUMN_SUBTITLE + " LIKE ? ";
        String searchString = mSearchView.getQuery().toString();

        if (searchString.length() > 0) {
            Log.e("SuperDuo", Thread.currentThread().getStackTrace()[2] + "");
            searchString = "%" + searchString + "%";
            return new CursorLoader(
                    getActivity(),
                    uri,
                    null,
                    selection,
                    new String[]{searchString, searchString},
                    null
            );
        }
        Log.e("SuperDuo", Thread.currentThread().getStackTrace()[2] + "mUri" + uri);
        return new CursorLoader(
                getActivity(),
                uri,
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
        if (mPosition != ListView.INVALID_POSITION) {
            mBookList.smoothScrollToPosition(mPosition);
        }
        updateEmptyView();
    }

    private void updateEmptyView() {
        if (mBooksAdapter.getCount() != 0)
            return;
        TextView emptyTextView = (TextView) getView().findViewById(R.id.emptyListView);
        emptyTextView.setVisibility(View.VISIBLE);
        mBookList.setEmptyView(emptyTextView);
        if (mSearchView.getQuery().length()<=0)
            emptyTextView.setText(R.string.no_books_in_your_library);
        else
            emptyTextView.setText(R.string.no_books);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.e("SuperDuo", Thread.currentThread().getStackTrace()[2] + "");
        mBooksAdapter.swapCursor(null);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.e("SuperDuo", Thread.currentThread().getStackTrace()[2] + "");
        outState.putCharSequence(QUERY, mSearchView.getQuery());
        if (mPosition != ListView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }

    }
}
