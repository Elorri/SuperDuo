package barqsoft.footballscores;

import android.content.UriMatcher;
import android.net.Uri;
import android.test.AndroidTestCase;
import android.util.Log;

import barqsoft.footballscores.data.ScoresContract;
import barqsoft.footballscores.data.ScoresProvider;

/**
 * Created by Elorri on 14/01/2016.
 */
public class TestUriMatcher extends AndroidTestCase {

    public void testUriMatcher() {
        UriMatcher testMatcher = ScoresProvider.buildUriMatcher();

        Uri uri=ScoresContract.ScoreEntry.CONTENT_URI;
        Log.e("SuperDuo", uri.toString());
        assertEquals("Error:", ScoresProvider.MATCHES,testMatcher.match(uri) );

        uri = ScoresContract.ScoreEntry.buildMatchesByDateUri("235788987l");
        Log.e("SuperDuo", uri.toString());
        assertEquals("Error:", ScoresProvider.MATCHES_BY_DATE,testMatcher.match(uri) );

        uri = ScoresContract.ScoreEntry.buildNextMatchesByDateUri("235788987l");
        Log.e("SuperDuo", uri.toString());
        assertEquals("Error:",ScoresProvider.NEXT_MATCHES_BY_DATE, testMatcher.match(uri));
    }
}
