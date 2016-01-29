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
package barqsoft.footballscores.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import barqsoft.footballscores.Utilities;

/**
 * This class allow for query, update, delete the differents tables used by the app to persist
 * the data.
 * of the app
 * Created by yehya khaled on 2/25/2015.
 * @author yehya khaled.
 * @author Elorri Etchemendy
 */
public class ScoresProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private ScoresDBHelper mOpenHelper;

    public static final int MATCHES = 100;
    public static final int MATCHES_BY_DATE = 101;
    public static final int NEXT_MATCHES_BY_DATE = 102;

    private static final String SCORES_BY_DATE = ScoresContract.ScoreEntry.DATE_TIME_COL
            + " between ? and ?";
    private static final String PAST_MATCHES = ScoresContract.ScoreEntry.DATE_TIME_COL
            + " < ?";


    public static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = ScoresContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, ScoresContract.PATH_MATCHES, MATCHES);
        matcher.addURI(authority, ScoresContract.PATH_MATCHES + "/" + ScoresContract.PATH_DATE +
                "/*", MATCHES_BY_DATE);
        matcher.addURI(authority, ScoresContract.PATH_NEXT_MATCHES + "/" + ScoresContract.PATH_DATE +
                "/*", NEXT_MATCHES_BY_DATE);
        return matcher;
    }


    @Override
    public boolean onCreate() {
        mOpenHelper = new ScoresDBHelper(getContext());
        return true;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MATCHES:
                return ScoresContract.ScoreEntry.CONTENT_TYPE;
            case MATCHES_BY_DATE:
                return ScoresContract.ScoreEntry.CONTENT_TYPE;
            case NEXT_MATCHES_BY_DATE:
                return ScoresContract.ScoreEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri :" + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case MATCHES:
                Log.d("SuperDuo", Thread.currentThread().getStackTrace()[2] + "MATCHES " + uri
                        .toString());
                retCursor = mOpenHelper.getReadableDatabase().query(
                        ScoresContract.ScoreEntry.TABLE_NAME,
                        projection, null, null, null, null, sortOrder);
                break;
            case MATCHES_BY_DATE:
                Log.d("SuperDuo", Thread.currentThread().getStackTrace()[2] + "MATCHES_BY_DATE " + uri
                        .toString());
                String dateTime = ScoresContract.ScoreEntry.getDateFromMatchesByDateUri(uri);

                //Convert this instant into a range representing a day
                long dateStart = Utilities.setZero(Long.valueOf(dateTime));
                long dateEnd = Utilities.addDay(1, dateStart);

                retCursor = mOpenHelper.getReadableDatabase().query(
                        ScoresContract.ScoreEntry.TABLE_NAME,
                        projection, SCORES_BY_DATE, new String[]{String.valueOf(dateStart),
                                String.valueOf(dateEnd)},
                        null, null,
                        sortOrder);
                break;
            case NEXT_MATCHES_BY_DATE:
                Log.d("SuperDuo", Thread.currentThread().getStackTrace()[2] + "MATCHES_BY_DATE " + uri
                        .toString());
                String nextDateTime = ScoresContract.ScoreEntry.getDateFromNextMatchesByDateUri(uri);

                //Convert this instant into a range representing a day
                long nextDateStart = Utilities.setZero(Long.valueOf(nextDateTime));
                long nextDateEnd = Utilities.addDay(1, nextDateStart);

                retCursor = mOpenHelper.getReadableDatabase().query(
                        ScoresContract.ScoreEntry.TABLE_NAME,
                        projection, SCORES_BY_DATE, new String[]{String.valueOf(nextDateTime),
                                String.valueOf(nextDateEnd)},
                        null, null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        switch (sUriMatcher.match(uri)) {
            case MATCHES:
                Log.d("SuperDuo", Thread.currentThread().getStackTrace()[2] + "MATCHES " + uri
                        .toString());
                db.beginTransaction();
                int returncount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insertWithOnConflict(ScoresContract.ScoreEntry.TABLE_NAME, null, value,
                                SQLiteDatabase.CONFLICT_REPLACE);
                        if (_id != -1) {
                            returncount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returncount;
            default:
                return super.bulkInsert(uri, values);
        }
    }


    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted but there is no null where clause here
        if (null == selection) selection = "1";
        switch (match) {
            case MATCHES_BY_DATE:
                Log.d("SuperDuo", Thread.currentThread().getStackTrace()[2] + "MATCHES_BY_DATE " + uri);
                String now = ScoresContract.ScoreEntry.getDateFromMatchesByDateUri(uri);

                //Convert this instant into a range representing a day
                long todayMorning = Utilities.setZero(Long.valueOf(now));
                long twoDaysAgo = Utilities.addDay(-2, todayMorning);
                rowsDeleted = db.delete(ScoresContract.ScoreEntry.TABLE_NAME, PAST_MATCHES, new String[]{String.valueOf(twoDaysAgo)});
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
}
