package it.jaschke.alexandria.controller.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import it.jaschke.alexandria.R;
import it.jaschke.alexandria.controller.activity.MainActivity;
import it.jaschke.alexandria.model.data.BookContract;
import it.jaschke.alexandria.model.services.BookService;
import it.jaschke.alexandria.model.services.DownloadImage;


public class BookDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String ISBN_KEY = "ISBN";
    private final int LOADER_ID = 10;
    private View view;
    private String mIsbn;
    private ShareActionProvider mShareActionProvider;

    public BookDetailFragment(){
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments != null) {
            mIsbn = arguments.getString(BookDetailFragment.ISBN_KEY);

            //TODO : 2.4 remove this for performance
            getLoaderManager().restartLoader(LOADER_ID, null, this);
        }

        view = inflater.inflate(R.layout.fragment_full_book, container, false);


        view.findViewById(R.id.delete_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent bookIntent = new Intent(getActivity(), BookService.class);
                bookIntent.putExtra(BookService.ISBN, mIsbn);
                bookIntent.setAction(BookService.DELETE_BOOK);
                getActivity().startService(bookIntent);
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
        return view;
    }




    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.book_detail, menu);
        MenuItem menuItem = menu.findItem(R.id.action_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                getActivity(),
                BookContract.BookEntry.buildFullBookUri(Long.parseLong(mIsbn)),
                null,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
        if (!data.moveToFirst()) {
            return;
        }

        String bookTitle = data.getString(data.getColumnIndex(BookContract.BookEntry.TITLE));
        String bookSubTitle = data.getString(data.getColumnIndex(BookContract.BookEntry.SUBTITLE));
        String desc = data.getString(data.getColumnIndex(BookContract.BookEntry.DESC));
        String authors = data.getString(data.getColumnIndex(BookContract.AuthorEntry.AUTHOR));
        String imgUrl = data.getString(data.getColumnIndex(BookContract.BookEntry.IMAGE_URL));
        String categories = data.getString(data.getColumnIndex(BookContract.CategoryEntry.CATEGORY));


        ((TextView) view.findViewById(R.id.fullBookTitle)).setText(bookTitle);
        ((TextView) view.findViewById(R.id.fullBookSubTitle)).setText(bookSubTitle);
        ((TextView) view.findViewById(R.id.fullBookDesc)).setText(desc);
        ((TextView) view.findViewById(R.id.categories)).setText(categories);
        TextView authorsTextView=((TextView) view.findViewById(R.id.authors));
        //String[] authorsArr = authors.split(","); //could cause NullPointerException
        if (authors == null)
            authorsTextView.setText("");
        else {
            String[] authorsArr = authors.split(",");
            authorsTextView.setLines(authorsArr.length);
            authorsTextView.setText(authors.replace(",", "\n"));
        }
        if(Patterns.WEB_URL.matcher(imgUrl).matches()){
            ImageView bookCover=(ImageView) view.findViewById(R.id.fullBookCover);
            new DownloadImage(bookCover).execute(imgUrl);
            bookCover.setVisibility(View.VISIBLE);
        }


        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_text) + bookTitle);

        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(shareIntent);
        }

    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {

    }


    @Override
    public void onPause() {
        if(MainActivity.IS_TABLET && view.findViewById(R.id.right_container)==null){
            getActivity().getSupportFragmentManager().popBackStack();
        }
        super.onPause();
    }


}