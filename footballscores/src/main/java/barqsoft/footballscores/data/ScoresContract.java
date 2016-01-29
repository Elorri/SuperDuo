package barqsoft.footballscores.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by yehya khaled on 2/25/2015.
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
