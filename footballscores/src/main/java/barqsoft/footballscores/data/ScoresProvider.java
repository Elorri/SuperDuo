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
 * Created by yehya khaled on 2/25/2015.
 */
public class ScoresProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private ScoresDBHelper mOpenHelper;

    public static final int MATCHES = 100;
    public static final int MATCHES_BY_LEAGUE = 101;
    public static final int MATCHES_BY_ID = 102;
    public static final int MATCHES_BY_DATE = 103;

    private static final String SCORES_BY_LEAGUE = ScoresContract.ScoreEntry.LEAGUE_COL + " = ?";
    private static final String SCORES_BY_DATE = ScoresContract.ScoreEntry.DATE_TIME_COL
            +" between ? and ?";
    private static final String SCORES_BY_ID = ScoresContract.ScoreEntry.MATCH_ID + " = ?";


    public static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = ScoresContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, ScoresContract.PATH_MATCHES, MATCHES);
        matcher.addURI(authority, ScoresContract.PATH_MATCHES + "/" + ScoresContract.PATH_LEAGUE + "/*", MATCHES_BY_LEAGUE);
        matcher.addURI(authority, ScoresContract.PATH_MATCHES + "/" + ScoresContract.PATH_ID + "/#", MATCHES_BY_ID);
        //TODO : 2.2 change date * by date # if millis
        matcher.addURI(authority, ScoresContract.PATH_MATCHES + "/" + ScoresContract.PATH_DATE +
                "/#", MATCHES_BY_DATE);
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
            case MATCHES_BY_LEAGUE:
                return ScoresContract.ScoreEntry.CONTENT_TYPE;
            case MATCHES_BY_ID:
                return ScoresContract.ScoreEntry.CONTENT_ITEM_TYPE;
            case MATCHES_BY_DATE:
                return ScoresContract.ScoreEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri :" + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Log.e("SuperDuo", Thread.currentThread().getStackTrace()[2] + "" + uri.toString());
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case MATCHES:
                retCursor = mOpenHelper.getReadableDatabase().query(
                        ScoresContract.ScoreEntry.TABLE_NAME,
                        projection, null, null, null, null, sortOrder);
                break;
            case MATCHES_BY_DATE:
                String dateTime=ScoresContract.ScoreEntry.getDateFromScoresByDateUri(uri);

                //Convert this instant into a range representing a day
                long dateStart=Utilities.setZero(Long.valueOf(dateTime));
                long dateEnd=Utilities.addDay(1,dateStart);

                retCursor = mOpenHelper.getReadableDatabase().query(
                        ScoresContract.ScoreEntry.TABLE_NAME,
                        projection, SCORES_BY_DATE, new String[]{String.valueOf(dateStart),
                                String.valueOf(dateEnd)},
                        null, null,
                        sortOrder);
                break;
            case MATCHES_BY_ID:
                retCursor = mOpenHelper.getReadableDatabase().query(
                        ScoresContract.ScoreEntry.TABLE_NAME,
                        projection, SCORES_BY_ID, selectionArgs, null, null, sortOrder);
                break;
            case MATCHES_BY_LEAGUE:
                retCursor = mOpenHelper.getReadableDatabase().query(
                        ScoresContract.ScoreEntry.TABLE_NAME,
                        projection, SCORES_BY_LEAGUE, selectionArgs, null, null, sortOrder);
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
        return db.delete(ScoresContract.ScoreEntry.TABLE_NAME, selection, selectionArgs);
    }
}
