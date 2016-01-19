package it.jaschke.alexandria.controller.extras;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;

import it.jaschke.alexandria.R;

/**
 * Created by Elorri on 07/01/2016.
 */
public class Tools {
    public static String fixIsbn(String isbnValue) {
        //catch isbn10 numbers
        if (isbnValue.length() >= 10 && !isbnValue.startsWith("978")) {
            return "978" + isbnValue;
        }
        return isbnValue;
    }

    /**
     * Returns true if the network is available or about to become available.
     *
     * @param c Context used to get the ConnectivityManager
     * @return true if the network is available
     */
    //Should be called on activity_main thread
    static public boolean isNetworkAvailable(Context c) {
        ConnectivityManager cm = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        Log.e("SuperDuo", Thread.currentThread().getStackTrace()[2] + "isConnected" + isConnected);
        return isConnected;
    }


    public static String getMainPagePreferences(Context context) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPrefs.getString(context.getString(R.string.pref_start_page_key),
                context.getString(R.string.pref_start_page_list));
    }




    // is device ready for features
    static public boolean isDeviceReadyForGooglePlayMobileVisionApi() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2;
    }




    /**
     * Compare the second Uri to the first and return true if equals, false if not
     * @param uri1 first uri
     * @param uri2 second uri to compare to the first
     * @return true if the 2 uris are equals, false otherwise
     */
    public static boolean compareUris(Uri uri1, Uri uri2) {
        return uri1.toString().equals(uri2.toString());
    }


}
