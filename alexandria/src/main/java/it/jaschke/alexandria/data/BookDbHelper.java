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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
/**
 * This class create the differents tables used to store data.
 * of the app
 * Created by saj on 22/12/14.
 * @author saj
 * @author Elorri Etchemendy
 */
public class BookDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "alexandria.db";

    public BookDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_BOOK_TABLE = "CREATE TABLE " + BookContract.BookEntry.TABLE_NAME + " (" +
                BookContract.BookEntry._ID + " INTEGER PRIMARY KEY," +
                BookContract.BookEntry.COLUMN_TITLE + " TEXT NOT NULL," +
                BookContract.BookEntry.COLUMN_SUBTITLE + " TEXT ," +
                BookContract.BookEntry.COLUMN_DESC + " TEXT ," +
                BookContract.BookEntry.COLUMN_IMAGE_URL + " TEXT, " +
                BookContract.BookEntry.COLUMN_FAVORITE + " INTEGER NOT NULL,   " +
                "UNIQUE (" + BookContract.BookEntry._ID + ") ON CONFLICT IGNORE, " +
                "CONSTRAINT " + BookContract.BookEntry.FAVORITE_CONSTRAINT + " check  (" +
                BookContract.BookEntry.COLUMN_FAVORITE + " between " +
                BookContract.BookEntry.FAVORITE_OFF_VALUE + " AND " +
                BookContract.BookEntry.FAVORITE_ON_VALUE + ")); ";

        final String SQL_CREATE_AUTHOR_TABLE = "CREATE TABLE " + BookContract.AuthorEntry.TABLE_NAME + " (" +
                BookContract.AuthorEntry._ID + " INTEGER," +
                BookContract.AuthorEntry.COLUMN_AUTHOR + " TEXT, " +
                " FOREIGN KEY (" + BookContract.AuthorEntry._ID + ") REFERENCES " +
                BookContract.BookEntry.TABLE_NAME + " (" + BookContract.BookEntry._ID + "))";

        final String SQL_CREATE_CATEGORY_TABLE = "CREATE TABLE " + BookContract.CategoryEntry.TABLE_NAME + " (" +
                BookContract.CategoryEntry._ID + " INTEGER ," +
                BookContract.CategoryEntry.COLUMN_CATEGORY + " TEXT, " +
                " FOREIGN KEY (" + BookContract.CategoryEntry._ID + ") REFERENCES " +
                BookContract.BookEntry.TABLE_NAME + " (" + BookContract.BookEntry._ID + "))";

        db.execSQL(SQL_CREATE_BOOK_TABLE);
        db.execSQL(SQL_CREATE_AUTHOR_TABLE);
        db.execSQL(SQL_CREATE_CATEGORY_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
