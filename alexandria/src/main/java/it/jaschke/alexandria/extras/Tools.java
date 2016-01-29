/**
 * The MIT License (MIT)

 Copyright (c) 2015 ETCHEMENDY ELORRI

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
package it.jaschke.alexandria.extras;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.preference.PreferenceManager;
import android.widget.ImageView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.bumptech.glide.Glide;

import it.jaschke.alexandria.R;

/**
 * This a general class with different useful methods.
 * Created by Elorri on 07/01/2016.
 */
public class Tools {

    /**
     * This method add the start '978' to the isbn given as parameter.
     * @param isbnValue
     * @return isbnValue with a correct start
     */
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
    static public boolean isNetworkAvailable(Context c) {
        ConnectivityManager cm = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
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
     * Load an image from an url, and display a colorful rectangle with the first letter of the
     * book title inside if the image can't be load.
     * @param context
     * @param imgUrl
     * @param errorMsg
     * @param imageView
     */
    public static void loadImage(Context context, String imgUrl, String errorMsg, ImageView
            imageView) {
        ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
        int noImageColor = generator.getRandomColor();
        TextDrawable noImage = TextDrawable.builder()
                .beginConfig()
                .fontSize((int) context.getResources().getDimension(R.dimen.bookTextSizePx))
                .textColor(Color.BLACK)
                .endConfig().buildRect(errorMsg.substring(0, 1), //will display first letter title
                        noImageColor);
        Glide.with(context)
                .load(imgUrl)
                .error(noImage)
                .crossFade()
                .into(imageView);
    }


    // Those 2 functions will be useful to handle the case when user is in 2 pane mode and turn
    // device to landscape.
    public static boolean isTablet(Configuration configuration) {
        return (configuration.screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    public static boolean isLandscape(Configuration configuration) {
        return configuration.orientation == Configuration.ORIENTATION_LANDSCAPE;
    }



}
