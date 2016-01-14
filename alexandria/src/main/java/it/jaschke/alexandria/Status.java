package it.jaschke.alexandria;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.IntDef;
import android.util.Log;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Elorri on 10/01/2016.
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
    public @interface GoogleBookApiStatus {
    }
    public static final int SERVEUR_DOWN = 0;
    public static final int SERVEUR_WRONG_URL_APP_INPUT = 1;
    public static final int SERVEUR_OK = 2;


    @Retention(RetentionPolicy.SOURCE)
    @IntDef({
            TABLE_STATUS_UNKNOWN,
            TABLE_SYNC_DONE })
    public @interface BookTableStatus { //There are several tables in our app, but we only care
    // of the book table status when it comes to display useful user infos
    }

    public static final int TABLE_STATUS_UNKNOWN = 0; /* we don't know if data store in the table is up to date */
    public static final int TABLE_SYNC_DONE = 1;






    /**
     *
     * @param c Context used to get the SharedPreferences
     * @return the network status integer type
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
     * @return the google book serveur status integer type
     */
    @SuppressWarnings("ResourceType")
    @GoogleBookApiStatus
    public static int getGoogleBookApiStatus(Context c) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
        return sp.getInt(c.getString(R.string.pref_google_book_api_status_key), SERVEUR_DOWN);
    }

    /**
     *
     * @param c Context used to get the SharedPreferences
     * @return the book table status integer type
     */
    @SuppressWarnings("ResourceType")
    @BookTableStatus
    public static int getBookTableStatus(Context c) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
        return sp.getInt(c.getString(R.string.pref_book_table_status_key), TABLE_STATUS_UNKNOWN);
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
        Log.e("SuperDuo", Thread.currentThread().getStackTrace()[2] + "");
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
     * @param googleBookApiStatus The IntDef value to set
     */
    public static  void setGoogleBookApiStatus(Context c, @GoogleBookApiStatus int
            googleBookApiStatus) {
        Log.e("SuperDuo", Thread.currentThread().getStackTrace()[2] + "");
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
        SharedPreferences.Editor spe = sp.edit();
        spe.putInt(c.getString(R.string.pref_google_book_api_status_key), googleBookApiStatus);
        spe.commit();
    }

    /**
     * Sets the scoreTable status into shared preference.  This function should not be called from
     * the UI thread because it uses commit to write to the shared preferences. Nb:if call from
     * UI thread use, apply instead.
     *
     * @param c      Context to get the PreferenceManager from.
     * @param bookTableStatus The IntDef value to set
     */
    public static  void setBookTableStatus(Context c, @BookTableStatus int bookTableStatus) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
        SharedPreferences.Editor spe = sp.edit();
        spe.putInt(c.getString(R.string.pref_book_table_status_key), bookTableStatus);
        spe.commit();
    }



}
