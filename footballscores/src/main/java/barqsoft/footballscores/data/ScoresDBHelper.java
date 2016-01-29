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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import barqsoft.footballscores.Status;
import barqsoft.footballscores.data.ScoresContract.ScoreEntry;

/**
 * This class create the differents tables used to store data.
 * of the app
 * Created by yehya khaled on 22/12/14.
 * @author yehya khaled
 * @author Elorri Etchemendy
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
