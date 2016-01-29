package barqsoft.footballscores.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import barqsoft.footballscores.Status;
import barqsoft.footballscores.data.ScoresContract.ScoreEntry;

/**
 * Created by yehya khaled on 2/25/2015.
 */
public class ScoresDBHelper extends SQLiteOpenHelper
{
    public static final String DATABASE_NAME = "Scores.db";
    private static final int DATABASE_VERSION = 4;
    private Context context;


    public ScoresDBHelper(Context context)    {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
        this.context=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        final String createScoresTable = "CREATE TABLE " + ScoreEntry.TABLE_NAME + " ("
                + ScoreEntry._ID + " INTEGER PRIMARY KEY,"
                + ScoreEntry.DATE_TIME_COL + " TEXT NOT NULL,"
                + ScoreEntry.HOME_COL + " TEXT NOT NULL,"
                + ScoreEntry.AWAY_COL + " TEXT NOT NULL,"
                + ScoreEntry.LEAGUE_COL + " INTEGER NOT NULL,"
                + ScoreEntry.HOME_GOALS_COL + " TEXT NOT NULL,"
                + ScoreEntry.AWAY_GOALS_COL + " TEXT NOT NULL,"
                + ScoreEntry.MATCH_ID + " INTEGER NOT NULL,"
                + ScoreEntry.MATCH_DAY + " INTEGER NOT NULL,"
                + " UNIQUE ("+ ScoreEntry.MATCH_ID+") ON CONFLICT REPLACE"
                + " );";
        db.execSQL(createScoresTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        //Remove old values when upgrading.
        db.execSQL("DROP TABLE IF EXISTS " + ScoreEntry.TABLE_NAME);
        onCreate(db);

        //I've noticed that some devices don't remove preferences files when the app is removed,
        // this why  need to reset the status somewhere, and here is a good place.
        Status.setNetworkStatus(context, Status.INTERNET_OFF);
        Status.setFootballApiStatus(context, Status.SERVEUR_DOWN);
        Status.setScoreTableStatus(context, Status.TABLE_STATUS_UNKNOWN);
    }
}
