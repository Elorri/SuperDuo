/**
 * The MIT License (MIT)

 Copyright (c) 2016 ETCHEMENDY ELORRI

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all
 copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 SOFTWARE.
 */
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

/**
 * This fragment will display data for the 'Books list' screen of the app, allowing the user to
 * search for a book taping it's name. The search will be done through the app database.
 * Created by Elorri on 23/01/2016.
 */
public class ListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,
        SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String QUERY = "query";
    private static final String SELECTED_KEY = "selected_position";
    private String mQuery;
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
        super.onCreate(savedInstanceState);
        mBooksAdapter = new BooksAdapter(getActivity(), null, 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        mSearchView = (SearchView) view.findViewById(R.id.searchView);
        mBookList = (ListView) view.findViewById(R.id.bookList);
        FloatingActionButton addButton = (FloatingActionButton) view.findViewById(R.id
                .add_button);

        mBookList.setAdapter(mBooksAdapter);
        mSearchView.setIconified(false);

        if (savedInstanceState != null) {
            mQuery = savedInstanceState.getString(QUERY);
            mSearchView.setQuery(mQuery, true);
        }

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
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
            Uri uri = BookContract.BookEntry.buildBookUri(Long.parseLong(isbn));
            ((Callback) getActivity()).onItemSelected(uri);
        }
    }


    public void setPosition(int position) {
        mPosition = position;
    }

    @Override
    public void onResume() {
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
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = BookContract.BookEntry.CONTENT_URI;

        final String selection = BookContract.BookEntry.COLUMN_TITLE + " LIKE ? OR " + BookContract.BookEntry.COLUMN_SUBTITLE + " LIKE ? ";
        String searchString = mSearchView.getQuery().toString();

        if (searchString.length() > 0) {
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
        mBooksAdapter.swapCursor(null);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(QUERY, mSearchView.getQuery().toString());
        if (mPosition != ListView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }

    }
}
