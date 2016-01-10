package it.jaschke.alexandria.controller.fragment;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
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

import it.jaschke.alexandria.R;
import it.jaschke.alexandria.controller.adapter.BooksAdapter;
import it.jaschke.alexandria.model.data.AlexandriaContract;


public class BooksFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

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
        View view = inflater.inflate(R.layout.fragment_list_of_books, container, false);
        mSearchText = (EditText) view.findViewById(R.id.searchText);
        mBookList = (ListView) view.findViewById(R.id.bookList);


        //TODO 2.4 check that the query method is call in the background thread, but this request
        // will be called again a few ms later. put null for better perf.
        Cursor cursor = getActivity().getContentResolver().query(
                AlexandriaContract.BookEntry.CONTENT_URI,
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
                    String isbn = cursor.getString(cursor.getColumnIndex(AlexandriaContract
                            .BookEntry._ID));
                    ((Callback) getActivity()).onItemSelected(isbn);
                }
            }
        });

        return view;
    }

    private void restartLoader() {
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        final String selection = AlexandriaContract.BookEntry.TITLE + " LIKE ? OR " + AlexandriaContract.BookEntry.SUBTITLE + " LIKE ? ";
        String searchString = mSearchText.getText().toString();

        if (searchString.length() > 0) {
            searchString = "%" + searchString + "%";
            return new CursorLoader(
                    getActivity(),
                    AlexandriaContract.BookEntry.CONTENT_URI,
                    null,
                    selection,
                    new String[]{searchString, searchString},
                    null
            );
        }

        return new CursorLoader(
                getActivity(),
                AlexandriaContract.BookEntry.CONTENT_URI,
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

        //TODO 2.0 if the list is null add a message
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
