package barqsoft.footballscores;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Elorri on 12/01/2016.
 */
public class Status {


    @Retention(RetentionPolicy.SOURCE)
    @IntDef({
            INTERNET_OFF, /* page can't be found because of user has not turn on network connectivity on it's device*/
            INTERNET_ON})
    public @interface NetworkStatus {
    }

    public static final int INTERNET_OFF = 0;
    public static final int INTERNET_ON = 1;



    @Retention(RetentionPolicy.SOURCE)
    @IntDef({SERVEUR_DOWN, /* page can't be found because serveur is down */
            SERVEUR_WRONG_URL_APP_INPUT, /* page can't be found because app is too old and
            serveur url has change overtime*/
            SERVEUR_OK
    })
    public @interface FootballApiStatus {
    }
    public static final int SERVEUR_DOWN = 0;
    public static final int SERVEUR_WRONG_URL_APP_INPUT = 1;
    public static final int SERVEUR_OK = 2;


    @Retention(RetentionPolicy.SOURCE)
    @IntDef({
            TABLE_STATUS_UNKNOWN,
            TABLE_SYNC_DONE })
    public @interface ScoreTableStatus { 
    }

    public static final int TABLE_STATUS_UNKNOWN = 0; /* we don't know if data store in the table is up to date */
    public static final int TABLE_SYNC_DONE = 1;

}