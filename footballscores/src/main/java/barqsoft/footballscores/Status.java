package barqsoft.footballscores;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
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
    public static final int TABLE_SYNC_DONE = 1; /* fetch has happened recently, data up to date */




    /**
     *
     * @param c Context used to get the SharedPreferences
     * @return the serveur status integer type
     */
    @SuppressWarnings("ResourceType")
    @NetworkStatus
    public static int getNetworkStatus(Context c) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
        return sp.getInt(c.getString(R.string.pref_network_status_key), INTERNET_OFF);
    }


    /**
     *
     * @param c Context used to get the SharedPreferences
     * @return the football serveur status integer type
     */
    @SuppressWarnings("ResourceType")
    @FootballApiStatus
    public static int getFootballApiStatus(Context c) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
        return sp.getInt(c.getString(R.string.pref_football_api_status_key), SERVEUR_DOWN);
    }

    /**
     *
     * @param c Context used to get the SharedPreferences
     * @return the score table status integer type
     */
    @SuppressWarnings("ResourceType")
    @ScoreTableStatus
    public static int getScoreTableStatus(Context c) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
        return sp.getInt(c.getString(R.string.pref_score_table_status_key), TABLE_STATUS_UNKNOWN);
    }

    /**
     * Sets the network status into shared preference.  This function should not be called from
     * the UI thread because it uses commit to write to the shared preferences. Nb:if call from
     * UI thread use, apply instead.
     *
     * @param c      Context to get the PreferenceManager from.
     * @param networkStatus The IntDef value to set
     */
    public static  void setNetworkStatus(Context c, @NetworkStatus int networkStatus) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
        SharedPreferences.Editor spe = sp.edit();
        spe.putInt(c.getString(R.string.pref_network_status_key), networkStatus);
        spe.commit();
    }


    /**
     * Sets the footballApi status into shared preference.  This function should not be called from
     * the UI thread because it uses commit to write to the shared preferences. Nb:if call from
     * UI thread use, apply instead.
     *
     * @param c      Context to get the PreferenceManager from.
     * @param footballApiStatus The IntDef value to set
     */
    public static  void setFootballApiStatus(Context c, @FootballApiStatus int footballApiStatus) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
        SharedPreferences.Editor spe = sp.edit();
        spe.putInt(c.getString(R.string.pref_football_api_status_key), footballApiStatus);
        spe.commit();
    }

    /**
     * Sets the scoreTable status into shared preference.  This function should not be called from
     * the UI thread because it uses commit to write to the shared preferences. Nb:if call from
     * UI thread use, apply instead.
     *
     * @param c      Context to get the PreferenceManager from.
     * @param scoreTableStatus The IntDef value to set
     */
    public static  void setScoreTableStatus(Context c, @ScoreTableStatus int scoreTableStatus) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
        SharedPreferences.Editor spe = sp.edit();
        spe.putInt(c.getString(R.string.pref_score_table_status_key), scoreTableStatus);
        spe.commit();
    }
    

}