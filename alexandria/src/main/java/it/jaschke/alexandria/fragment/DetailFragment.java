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
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import it.jaschke.alexandria.R;
import it.jaschke.alexandria.activity.DetailActivity;
import it.jaschke.alexandria.data.BookContract;
import it.jaschke.alexandria.extras.Tools;
import it.jaschke.alexandria.services.BookService;

/**
 * This fragment will display data for the 'Book detail' screen of the app, it will display info
 * about the chosen book
 * Created by Elorri on 23/01/2016.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String URI = "uri";
    private Uri mUri;


    private final int LOADER_ID = 10;


    private Toolbar mToolbarView;


    private MenuItem mActivityMenuItem;
    private MenuItem mFragmentMenuItem;


    private TextView mBookTitleTextView;
    private TextView mBookSubTitleTextView;
    private TextView mAuthorsTextView;
    private TextView mCategoriesTextView;
    private ImageView mBookCover;
    private TextView mDesc;
    private Button mDelete;


    public DetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null)
            mUri = savedInstanceState.getParcelable(URI);

    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        mToolbarView = (Toolbar) view.findViewById(R.id.toolbar);

        if (mToolbarView != null)
            inflateFragmentMenuItem();
        else {
            setHasOptionsMenu(true);
        }
        return view;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (getActivity() instanceof DetailActivity){
            inflateActivityMenuItem(menu, inflater);
        }
    }

    private void inflateFragmentMenuItem() {
        Menu menu = mToolbarView.getMenu();
        if (null != menu) menu.clear();
        mToolbarView.inflateMenu(R.menu.fragment_detail);
        mFragmentMenuItem = menu.findItem(R.id.action_share);
        mFragmentMenuItem.setVisible(false);
    }

    private void inflateActivityMenuItem(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_detail, menu);
        mActivityMenuItem = menu.findItem(R.id.action_share);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(LOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader cursorLoader = null;
        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(URI);
            if (mUri != null) {
                cursorLoader = new CursorLoader(getActivity(),
                        mUri,
                        null,
                        null,
                        null,
                        null);
            }
        }
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (!data.moveToFirst()) {
            return;
        }
        String bookTitle = data.getString(data.getColumnIndex(BookContract.BookEntry.COLUMN_TITLE));
        String bookSubTitle = data.getString(data.getColumnIndex(BookContract.BookEntry.COLUMN_SUBTITLE));
        String desc = data.getString(data.getColumnIndex(BookContract.BookEntry.COLUMN_DESC));
        String authors = data.getString(data.getColumnIndex(BookContract.AuthorEntry.COLUMN_AUTHOR));
        String imgUrl = data.getString(data.getColumnIndex(BookContract.BookEntry.COLUMN_IMAGE_URL));
        String categories = data.getString(data.getColumnIndex(BookContract.CategoryEntry.COLUMN_CATEGORY));

        View view = getView();
        mBookTitleTextView = (TextView) view.findViewById(R.id.bookTitle);
        mBookSubTitleTextView = (TextView) view.findViewById(R.id.bookSubTitle);
        mDesc = (TextView) view.findViewById(R.id.bookDesc);
        mCategoriesTextView = (TextView) view.findViewById(R.id.categories);
        mDelete = (Button) view.findViewById(R.id.delete_button);
        mBookCover = (ImageView) view.findViewById(R.id.bookCover);


        mBookTitleTextView.setVisibility(View.VISIBLE);
        mBookSubTitleTextView.setVisibility(View.VISIBLE);
        mCategoriesTextView.setVisibility(View.VISIBLE);
        mDesc.setVisibility(View.VISIBLE);
        mDelete.setVisibility(View.VISIBLE);
        mBookCover.setVisibility(View.VISIBLE);


        mBookTitleTextView.setText(bookTitle);
        mBookSubTitleTextView.setText(bookSubTitle);
        mDesc.setText(desc);
        mCategoriesTextView.setText(categories);
        mAuthorsTextView = ((TextView) view.findViewById(R.id.authors));

        //String[] authorsArr = authors.split(","); //could cause NullPointerException
        if (authors == null)
            mAuthorsTextView.setText("");
        else {
            String[] authorsArr = authors.split(",");
            mAuthorsTextView.setLines(authorsArr.length);
            mAuthorsTextView.setText(authors.replace(",", "\n"));
        }


        Tools.loadImage(getContext(), imgUrl, bookTitle, mBookCover);

        mDelete.setVisibility(View.VISIBLE);
        mDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent bookIntent = new Intent(getActivity(), BookService.class);
                String isbn = BookContract.BookEntry.getIsbnFromBookUri(mUri);
                bookIntent.putExtra(BookService.ISBN, isbn);
                bookIntent.setAction(BookService.DELETE_BOOK);
                getActivity().startService(bookIntent);
                if (getActivity() instanceof DetailActivity) {
                    getActivity().finish();
                }
                clearFields();
            }
        });


        Intent shareIntent = createShareIntent(bookTitle);
        if (mActivityMenuItem != null) {
            mActivityMenuItem.setIntent(shareIntent);
        }
        if (mFragmentMenuItem != null) {
            mFragmentMenuItem.setVisible(true);
            mFragmentMenuItem.setIntent(shareIntent);
        }
    }

    private void clearFields() {
        mBookTitleTextView.setVisibility(View.INVISIBLE);
        mBookSubTitleTextView.setVisibility(View.INVISIBLE);
        mAuthorsTextView.setVisibility(View.INVISIBLE);
        mCategoriesTextView.setVisibility(View.INVISIBLE);
        mDelete.setVisibility(View.INVISIBLE);
        mDesc.setVisibility(View.INVISIBLE);
        mBookCover.setVisibility(View.INVISIBLE);
        if (mFragmentMenuItem != null)
            mFragmentMenuItem.setVisible(false);
    }

    private Intent createShareIntent(String bookTitle) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_text) + bookTitle);
        return shareIntent;
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {

    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(URI, mUri);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (Tools.isTablet(newConfig) && (Tools.isLandscape(newConfig)))
            getActivity().finish();
    }
}