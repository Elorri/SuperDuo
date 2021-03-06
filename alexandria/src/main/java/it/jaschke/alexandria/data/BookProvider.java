/**
 * The MIT License (MIT)

 Copyright (c) 2015 ETCHEMENDY ELORRI

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
package it.jaschke.alexandria.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import it.jaschke.alexandria.data.BookContract.AuthorEntry;
import it.jaschke.alexandria.data.BookContract.BookEntry;
import it.jaschke.alexandria.data.BookContract.CategoryEntry;

/**
 * This class allow for query, update, delete the differents tables used by the app to persist
 * the data.
 * of the app
 * Created by saj on 24/12/14.
 * @author saj
 * @author Elorri Etchemendy
 */
public class BookProvider extends ContentProvider {

    private static final int BOOK = 100;
    private static final int BOOK_ID = 101;
    private static final int AUTHOR = 200;
    private static final int AUTHOR_ID = 201;
    private static final int CATEGORY = 300;
    private static final int CATEGORY_ID = 301;



    private static final UriMatcher uriMatcher = buildUriMatcher();

    private BookDbHelper bookDbHelper;

    private static final SQLiteQueryBuilder bookFull;

    static {
        bookFull = new SQLiteQueryBuilder();
        bookFull.setTables(
                BookContract.BookEntry.TABLE_NAME + " LEFT OUTER JOIN " +
                        BookContract.AuthorEntry.TABLE_NAME + " USING (" + BookContract.BookEntry._ID + ")" +
                        " LEFT OUTER JOIN " + BookContract.CategoryEntry.TABLE_NAME + " USING (" + BookContract.BookEntry._ID + ")");
    }


    private static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = BookContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, BookContract.PATH_BOOKS + "/*", BOOK_ID);
        matcher.addURI(authority, BookContract.PATH_AUTHORS + "/#", AUTHOR_ID);
        matcher.addURI(authority, BookContract.PATH_CATEGORIES + "/#", CATEGORY_ID);
        matcher.addURI(authority, BookContract.PATH_BOOKS, BOOK);
        matcher.addURI(authority, BookContract.PATH_AUTHORS, AUTHOR);
        matcher.addURI(authority, BookContract.PATH_CATEGORIES, CATEGORY);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        bookDbHelper = new BookDbHelper(getContext());
        return true;

    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch (uriMatcher.match(uri)) {
            case BOOK:
                Log.d("SuperDuo", Thread.currentThread().getStackTrace()[2] + "BOOK uri: " + uri);
                final SQLiteDatabase db = bookDbHelper.getWritableDatabase();
                //we need first to delete unfavorite book to prevent db from growing
                delete(BookEntry.CONTENT_URI, null, null);
                retCursor = db.query(
                        BookContract.BookEntry.TABLE_NAME,
                        projection,
                        selection,
                        selection == null ? null : selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case AUTHOR:
                Log.d("SuperDuo", Thread.currentThread().getStackTrace()[2] + "AUTHOR uri: " + uri);
                retCursor = bookDbHelper.getReadableDatabase().query(
                        BookContract.AuthorEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case CATEGORY:
                Log.d("SuperDuo", Thread.currentThread().getStackTrace()[2] + "CATEGORY uri: " + uri);
                retCursor = bookDbHelper.getReadableDatabase().query(
                        BookContract.CategoryEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;

            case BOOK_ID:
                Log.d("SuperDuo", Thread.currentThread().getStackTrace()[2] + "BOOK_ID uri: " + uri);
                String[] bfd_projection = {
                        BookContract.BookEntry.TABLE_NAME + "." + BookContract.BookEntry.COLUMN_TITLE,
                        BookContract.BookEntry.TABLE_NAME + "." + BookContract.BookEntry.COLUMN_SUBTITLE,
                        BookContract.BookEntry.TABLE_NAME + "." + BookContract.BookEntry.COLUMN_IMAGE_URL,
                        BookContract.BookEntry.TABLE_NAME + "." + BookContract.BookEntry.COLUMN_DESC,
                        "group_concat(DISTINCT "
                                + BookContract.AuthorEntry.TABLE_NAME + "."
                                + BookContract.AuthorEntry.COLUMN_AUTHOR + ") as "
                                + BookContract.AuthorEntry.COLUMN_AUTHOR,
                        "group_concat(DISTINCT "
                        + BookContract.CategoryEntry.TABLE_NAME + "."
                        + BookContract.CategoryEntry.COLUMN_CATEGORY + ") as "
                        + BookContract.CategoryEntry.COLUMN_CATEGORY
                };
                retCursor = bookFull.query(bookDbHelper.getReadableDatabase(),
                        bfd_projection,
                        BookContract.BookEntry.TABLE_NAME + "."
                                + BookContract.BookEntry._ID + " = '"
                                + ContentUris.parseId(uri) + "'",
                        selectionArgs,
                        BookContract.BookEntry.TABLE_NAME + "."
                                + BookContract.BookEntry._ID,
                        null,
                        sortOrder);
                break;
            case AUTHOR_ID:
                Log.d("SuperDuo", Thread.currentThread().getStackTrace()[2] + "AUTHOR_ID uri: " + uri);
                retCursor = bookDbHelper.getReadableDatabase().query(AuthorEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case CATEGORY_ID:
                Log.d("SuperDuo", Thread.currentThread().getStackTrace()[2] + "CATEGORY_ID uri: " + uri);
                retCursor = bookDbHelper.getReadableDatabase().query(CategoryEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return retCursor;
    }


    @Override
    public String getType(Uri uri) {
        final int match = uriMatcher.match(uri);

        switch (match) {
            case BOOK_ID:
                return BookContract.BookEntry.CONTENT_ITEM_TYPE;
            case AUTHOR_ID:
                return BookContract.AuthorEntry.CONTENT_ITEM_TYPE;
            case CATEGORY_ID:
                return BookContract.CategoryEntry.CONTENT_ITEM_TYPE;
            case BOOK:
                return BookContract.BookEntry.CONTENT_TYPE;
            case AUTHOR:
                return BookContract.AuthorEntry.CONTENT_TYPE;
            case CATEGORY:
                return BookContract.CategoryEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = bookDbHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        Uri returnUri;
        switch (match) {
            case BOOK: {
                Log.d("SuperDuo", Thread.currentThread().getStackTrace()[2] + "BOOK uri: " + uri);
                long _id = db.insert(BookContract.BookEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = BookContract.BookEntry.buildBookUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            case AUTHOR: {
                Log.d("SuperDuo", Thread.currentThread().getStackTrace()[2] + "AUTHOR uri: " + uri);
                db.insert(BookContract.AuthorEntry.TABLE_NAME, null, values);
                long _id=values.getAsLong(AuthorEntry._ID);
                if (_id > 0) {
                    returnUri = BookContract.AuthorEntry.buildAuthorUri(_id);
                }
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case CATEGORY: {
                db.insert(BookContract.CategoryEntry.TABLE_NAME, null, values);
                long _id=values.getAsLong(CategoryEntry._ID);
                if (_id > 0)
                    returnUri = BookContract.CategoryEntry.buildCategoryUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = bookDbHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if (null == selection) selection = "1";
        switch (match) {
            case BOOK:
                //Need to delete Authors and Categories entry first to avoid foreign key conflict
                //Need to delete Authors and Categories, because 'on delete cascade does not seems 
                // to work'
                //delete from books where favorite=0;
                delete(AuthorEntry.CONTENT_URI, null, null);
                delete(CategoryEntry.CONTENT_URI, null, null);
                rowsDeleted = db.delete(BookEntry.TABLE_NAME, BookEntry.COLUMN_FAVORITE + "=?", new String[]{BookEntry.FAVORITE_OFF_VALUE});
                break;
            case AUTHOR:
                //delete from authors where _id in (select _id from books where favorite=0);
                rowsDeleted = db.delete(AuthorEntry.TABLE_NAME, AuthorEntry._ID + " in (select " + 
                        BookEntry._ID + " from " + BookEntry.TABLE_NAME + " where " + BookEntry.COLUMN_FAVORITE + "=" + BookEntry.FAVORITE_OFF_VALUE + ")", null);
                break;
            case CATEGORY:
                //delete from categories where _id in (select _id from books where favorite=0);
                rowsDeleted = db.delete(CategoryEntry.TABLE_NAME, CategoryEntry._ID + " in " +
                        "(select " + BookEntry._ID + " from " + BookEntry.TABLE_NAME + " where " + BookEntry.COLUMN_FAVORITE + "=" + BookEntry.FAVORITE_OFF_VALUE + ")", null);
                break;
            case BOOK_ID:
                rowsDeleted = db.delete(
                        BookContract.BookEntry.TABLE_NAME,
                        BookContract.BookEntry._ID + " = '" + ContentUris.parseId(uri) + "'",
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }


    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = bookDbHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        int rowsUpdated;
        switch (match) {
            case BOOK_ID:
                String isbn = BookEntry.getIsbnFromBookUri(uri);
                db.execSQL("update " + BookContract.BookEntry.TABLE_NAME + " set "
                                + BookContract.BookEntry.COLUMN_FAVORITE + "=? where "
                                + BookContract.BookEntry._ID + "=?",
                        new String[]{BookContract.BookEntry.FAVORITE_ON_VALUE, isbn});
                rowsUpdated = 1;
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }
}