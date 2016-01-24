package it.jaschke.alexandria.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import it.jaschke.alexandria.R;
import it.jaschke.alexandria.data.BookContract;
import it.jaschke.alexandria.extras.Tools;
import it.jaschke.alexandria.services.BookService;


public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String URI = "uri";
    private Uri mUri;


    private final int LOADER_ID = 10;


    private Toolbar mToolbarView;


    private ShareActionProvider mActivityShareProvider;
    private ShareActionProvider mFragmentShareProvider;

    public DetailFragment() {
        Log.e("SuperDuo", Thread.currentThread().getStackTrace()[2] + "");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.e("SuperDuo", Thread.currentThread().getStackTrace()[2] + "");
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null)
            mUri = savedInstanceState.getParcelable(URI);

    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.e("SuperDuo", Thread.currentThread().getStackTrace()[2] + "");
        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        mToolbarView = (Toolbar) view.findViewById(R.id.toolbar);

        if (mToolbarView != null)
            inflateFragmentMenuItem();
        else {
            setHasOptionsMenu(true);
        }

        Log.e("SuperDuo", Thread.currentThread().getStackTrace()[2] +
                "mActivityShareProvider " + mActivityShareProvider + " mFragmentShareProvider " +
                mFragmentShareProvider);

        view.findViewById(R.id.delete_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent bookIntent = new Intent(getActivity(), BookService.class);
                String isbn = BookContract.BookEntry.getIsbnFromFullBookUri(mUri);
                bookIntent.putExtra(BookService.ISBN, isbn);
                bookIntent.setAction(BookService.DELETE_BOOK);
                getActivity().startService(bookIntent);
                getActivity().finish();
            }
        });
        return view;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflateActivityMenuItem(menu, inflater);
        Log.e("SuperDuo", Thread.currentThread().getStackTrace()[2] +
                "mActivityShareProvider " + mActivityShareProvider + " mFragmentShareProvider " +
                mFragmentShareProvider);
    }

    private void inflateFragmentMenuItem() {
        Log.e("SuperDuo", Thread.currentThread().getStackTrace()[2] + "");
        Menu menu = mToolbarView.getMenu();
        if (null != menu) menu.clear();
        mToolbarView.inflateMenu(R.menu.fragment_detail);
        MenuItem menuItem = menu.findItem(R.id.action_share);
        mFragmentShareProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

    }

    private void inflateActivityMenuItem(Menu menu, MenuInflater inflater) {
        Log.e("SuperDuo", Thread.currentThread().getStackTrace()[2] + "");
        inflater.inflate(R.menu.fragment_detail, menu);
        MenuItem menuItem = menu.findItem(R.id.action_share);
        mActivityShareProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.e("SuperDuo", Thread.currentThread().getStackTrace()[2] + "");
        getLoaderManager().initLoader(LOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.e("SuperDuo", Thread.currentThread().getStackTrace()[2] + "");
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
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor
            data) {
        Log.e("SuperDuo", Thread.currentThread().getStackTrace()[2] + "");
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
        ((TextView) view.findViewById(R.id.bookTitle)).setText(bookTitle);
        ((TextView) view.findViewById(R.id.bookSubTitle)).setText(bookSubTitle);
        ((TextView) view.findViewById(R.id.bookDesc)).setText(desc);
        ((TextView) view.findViewById(R.id.categories)).setText(categories);
        TextView authorsTextView = ((TextView) view.findViewById(R.id.authors));
        //String[] authorsArr = authors.split(","); //could cause NullPointerException
        if (authors == null)
            authorsTextView.setText("");
        else {
            String[] authorsArr = authors.split(",");
            authorsTextView.setLines(authorsArr.length);
            authorsTextView.setText(authors.replace(",", "\n"));
        }

        ImageView bookCover = (ImageView) view.findViewById(R.id.bookCover);
        bookCover.setVisibility(View.VISIBLE);
        Tools.loadImage(getContext(), imgUrl, bookTitle, bookCover);


       Intent shareIntent = createShareIntent(bookTitle);
        if (mActivityShareProvider != null) {
            mActivityShareProvider.setShareIntent(shareIntent);
        }
        if (mFragmentShareProvider != null) {
            mFragmentShareProvider.setShareIntent(shareIntent);
        }
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
        Log.e("SuperDuo", Thread.currentThread().getStackTrace()[2] + "");
        outState.putParcelable(URI, mUri);
        super.onSaveInstanceState(outState);
    }


}