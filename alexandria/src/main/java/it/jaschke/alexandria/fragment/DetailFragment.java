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
    private final int LOADER_ID = 10;
    private View view;


    private Toolbar mToolbarView;

    private Uri mUri;

    private ShareActionProvider mShareActionProvider;
    private boolean isUseActivityShareButtonOn;
    private Intent mShareIntent;

    public DetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null)
            mUri = savedInstanceState.getParcelable(URI);

        //TODO :2.1 does this change something ?
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_detail, container, false);
        mToolbarView = (Toolbar) view.findViewById(R.id.toolbar);

        if (mToolbarView != null)
            isUseActivityShareButtonOn = false;
        else
            isUseActivityShareButtonOn = true;


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
        if (isUseActivityShareButtonOn) {
            inflater.inflate(R.menu.fragment_detail, menu);
            MenuItem menuItem = menu.findItem(R.id.action_share);
            mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
        }
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        Log.e("SuperDuo", Thread.currentThread().getStackTrace()[2] + "");
//        int id = item.getItemId();
//        if (id == R.id.action_share) {
//            Log.e("SuperDuo", Thread.currentThread().getStackTrace()[2] + "");
//            if ((mShareActionProvider != null) || (mShareIntent != null)) {
//                mShareActionProvider.setShareIntent(mShareIntent);
//            }
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

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
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor
            data) {
        if (!data.moveToFirst()) {
            return;
        }

        String bookTitle = data.getString(data.getColumnIndex(BookContract.BookEntry.COLUMN_TITLE));
        String bookSubTitle = data.getString(data.getColumnIndex(BookContract.BookEntry.COLUMN_SUBTITLE));
        String desc = data.getString(data.getColumnIndex(BookContract.BookEntry.COLUMN_DESC));
        String authors = data.getString(data.getColumnIndex(BookContract.AuthorEntry.COLUMN_AUTHOR));
        String imgUrl = data.getString(data.getColumnIndex(BookContract.BookEntry.COLUMN_IMAGE_URL));
        String categories = data.getString(data.getColumnIndex(BookContract.CategoryEntry.COLUMN_CATEGORY));


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


        mShareIntent = createShareIntent(bookTitle);

        if ((mShareActionProvider != null) && (mShareIntent != null)) {
            mShareActionProvider.setShareIntent(mShareIntent);
        }


        if (!isUseActivityShareButtonOn) {
//            AppCompatActivity activity = ((AppCompatActivity) getActivity());
//            activity.setSupportActionBar(mToolbarView);
//            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
//            activity.getSupportActionBar().setDisplayShowTitleEnabled(false);

            Menu menu = mToolbarView.getMenu();
            if (null != menu) menu.clear();
            mToolbarView.inflateMenu(R.menu.fragment_detail);
            // Retrieve the share menu item
            MenuItem menuItem = menu.findItem(R.id.action_share);
            menuItem.setIntent(mShareIntent);
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

    public void onMainUriChange() {
        if (mUri != null) {
            getLoaderManager().restartLoader(LOADER_ID, null, this);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.e("SuperDuo", Thread.currentThread().getStackTrace()[2] + "");
        outState.putParcelable(MainFragment.URI, mUri);
        super.onSaveInstanceState(outState);
    }


}