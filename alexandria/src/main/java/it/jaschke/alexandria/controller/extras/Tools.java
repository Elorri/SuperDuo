package it.jaschke.alexandria.controller.extras;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * Created by Elorri on 07/01/2016.
 */
public class Tools {
    public static String fixIsbn(String isbnValue) {
        //catch isbn10 numbers
        if (isbnValue.length() == 10 && !isbnValue.startsWith("978")) {
            return  "978" + isbnValue;
        }
        return isbnValue;
    }

    /**
     * Returns true if the network is available or about to become available.
     *
     * @param c Context used to get the ConnectivityManager
     * @return true if the network is available
     */
    //Should be called on main thread
    static public boolean isNetworkAvailable(Context c) {
        ConnectivityManager cm =  (ConnectivityManager)c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected= activeNetwork != null &&  activeNetwork.isConnectedOrConnecting();
        Log.e("SuperDuo", Thread.currentThread().getStackTrace()[2] + "isConnected"+isConnected);
        return isConnected;
    }
}
