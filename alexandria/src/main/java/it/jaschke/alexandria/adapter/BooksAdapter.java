package it.jaschke.alexandria.adapter;


import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import it.jaschke.alexandria.R;
import it.jaschke.alexandria.extras.Tools;
import it.jaschke.alexandria.model.data.BookContract;

/**
 * Created by saj on 11/01/15.
 */
public class BooksAdapter extends CursorAdapter {


    public static class ViewHolder {
        public final ImageView bookCover;
        public final TextView bookTitle;
        public final TextView bookSubTitle;

        public ViewHolder(View view) {
            bookCover = (ImageView) view.findViewById(R.id.fullBookCover);
            bookTitle = (TextView) view.findViewById(R.id.listBookTitle);
            bookSubTitle = (TextView) view.findViewById(R.id.listBookSubTitle);
        }
    }

    public BooksAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();

        String bookTitle = cursor.getString(cursor.getColumnIndex(BookContract.BookEntry.COLUMN_TITLE));
        viewHolder.bookTitle.setText(bookTitle);

        String bookSubTitle = cursor.getString(cursor.getColumnIndex(BookContract.BookEntry.COLUMN_SUBTITLE));
        viewHolder.bookSubTitle.setText(bookSubTitle);

        String imgUrl = cursor.getString(cursor.getColumnIndex(BookContract.BookEntry.COLUMN_IMAGE_URL));
        Tools.loadImage(context, imgUrl, bookTitle, viewHolder.bookCover);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_list_book, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }
}