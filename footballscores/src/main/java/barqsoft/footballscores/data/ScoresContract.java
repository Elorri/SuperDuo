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

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * This class list column names of the differents tables used to store data.
 * of the app
 * Created by yehya khaled on 2/25/2015.
 * @author yehya khaled
 * @author Elorri Etchemendy
 */
public class ScoresContract {

    //URI data
    public static final String CONTENT_AUTHORITY = "barqsoft.footballscores";

    public static Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_MATCHES = "matches";
    public static final String PATH_NEXT_MATCHES = "next_matches";
    public static final String PATH_DATE = "date";





    public static final class ScoreEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MATCHES).build();

        public static final String TABLE_NAME = "scores";
        public static final String LEAGUE_COL = "league";
        public static final String DATE_TIME_COL = "date_time";
        public static final String HOME_COL = "home";
        public static final String AWAY_COL = "away";
        public static final String HOME_GOALS_COL = "home_goals";
        public static final String AWAY_GOALS_COL = "away_goals";
        public static final String MATCH_ID = "matchId";
        public static final String MATCH_DAY = "match_day";

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MATCHES;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MATCHES;



        //will match MATCHES_BY_DATE
        public static Uri buildMatchesByDateUri(String dateTime) {
            return BASE_CONTENT_URI.buildUpon()
                    .appendPath(PATH_MATCHES)
                    .appendPath(PATH_DATE)
                    .appendPath(dateTime)
                    .build();
        }

        //will match NEXT_MATCHES_BY_DATE
        public static Uri buildNextMatchesByDateUri(String dateTime) {
            return BASE_CONTENT_URI.buildUpon()
                    .appendPath(PATH_NEXT_MATCHES)
                    .appendPath(PATH_DATE)
                    .appendPath(dateTime)
                    .build();
        }

        public static String getDateFromMatchesByDateUri(Uri uri) {
            return uri.getPathSegments().get(2);
        }
        public static String getDateFromNextMatchesByDateUri(Uri uri) {
            return uri.getPathSegments().get(2);
        }
    }

}
