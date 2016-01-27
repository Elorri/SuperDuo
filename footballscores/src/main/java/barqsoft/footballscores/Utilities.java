package barqsoft.footballscores;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.widget.ImageView;
import android.widget.RemoteViews;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.bumptech.glide.Glide;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by yehya khaled on 3/3/2015.
 */
public class Utilities {
    // League numbers
    public static final int BUNDESLIGA1 = 394;
    public static final int BUNDESLIGA2 = 395;
    public static final int LIGUE1 = 396;
    public static final int LIGUE2 = 397;
    public static final int PREMIER_LEAGUE = 398;
    public static final int PRIMERA_DIVISION = 399;
    public static final int SEGUNDA_DIVISION = 400;
    public static final int SERIE_A = 401;
    public static final int PRIMERA_LIGA = 402;
    public static final int BUNDESLIGA3 = 403;
    public static final int EREDIVISIE = 404;
    public static final int CHAMPIONS = 405;

    /**
     * Gets the name of the league corresponding to its number
     *
     * @param c          Context of the activity
     * @param league_num The number of the league
     * @return The name of the league
     */
    public static String getLeague(Context c, int league_num) {
        switch (league_num) {
            case BUNDESLIGA1:
                return c.getString(R.string.league_bundesliga1);
            case BUNDESLIGA2:
                return c.getString(R.string.league_bundesliga2);
            case BUNDESLIGA3:
                return c.getString(R.string.league_bundesliga3);
            case LIGUE1:
                return c.getString(R.string.league_ligue1);
            case LIGUE2:
                return c.getString(R.string.league_ligue2);
            case PREMIER_LEAGUE:
                return c.getString(R.string.league_premier_league);
            case PRIMERA_DIVISION:
                return c.getString(R.string.league_primera_division);
            case SEGUNDA_DIVISION:
                return c.getString(R.string.league_segunda_division);
            case SERIE_A:
                return c.getString(R.string.league_serie_a);
            case PRIMERA_LIGA:
                return c.getString(R.string.league_primeira_liga);
            case EREDIVISIE:
                return c.getString(R.string.league_eredivisie);
            case CHAMPIONS:
                return c.getString(R.string.league_champions);
            default:
                return c.getString(R.string.league_unknown);
        }
    }

    /**
     * Gets the number of the match day. If it is currently champions league, then it gets the
     * name of the stage of the league
     *
     * @param c          Context of the activity
     * @param match_day  The match day
     * @param league_num The league number
     * @return The name or number of the match day
     */
    public static String getMatchDay(Context c, int match_day, int league_num) {
        if (league_num == CHAMPIONS) {
            if (match_day <= 6) return c.getString(R.string.match_champions_gs);
            else if (match_day == 7 || match_day == 8)
                return c.getString(R.string.match_champions_fkr);
            else if (match_day == 9 || match_day == 10)
                return c.getString(R.string.match_champions_qf);
            else if (match_day == 11 || match_day == 12)
                return c.getString(R.string.match_champions_sf);
            else return c.getString(R.string.match_champiions_f);
        } else return c.getString(R.string.match_default, match_day);
    }


    public static String getScores(Context context, int homeGoals, int awayGoals) {
        if (homeGoals >= 0 && awayGoals >= 0) {
            return context.getString(R.string.scores, homeGoals, awayGoals);
        } else {
            return context.getString(R.string.scores, "", "");
        }
    }

    public static Integer getTeamCrestByTeamName(Context context, String teamName) {
        if (teamName == null) {
            return null;
        }

        if (teamName.equals(context.getString(R.string.team_arsenal_london)))
            return R.drawable.arsenal;
        else if (teamName.equals(context.getString(R.string.team_manshester)))
            return R.drawable.manchester_united;
        else if (teamName.equals(context.getString(R.string.team_swansea)))
            return R.drawable.swansea_city_afc;
        else if (teamName.equals(context.getString(R.string.team_leicester)))
            return R.drawable.leicester_city_fc_hd_logo;
        else if (teamName.equals(context.getString(R.string.team_everton)))
            return R.drawable.everton_fc_logo1;
        else if (teamName.equals(context.getString(R.string.team_west_ham)))
            return R.drawable.west_ham;
        else if (teamName.equals(context.getString(R.string.team_tottenham)))
            return R.drawable.tottenham_hotspur;
        else if (teamName.equals(context.getString(R.string.team_west_bromwich)))
            return R.drawable.west_bromwich_albion_hd_logo;
        else if (teamName.equals(context.getString(R.string.team_sunderland)))
            return R.drawable.sunderland;
        else if (teamName.equals(context.getString(R.string.team_stoke_city)))
            return R.drawable.stoke_city;
        else return null;
    }

    public static Drawable getNoCrestImage(Context context, String teamName) {
        ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
        int noImageColor = generator.getRandomColor();
        TextDrawable noImage = TextDrawable.builder()
                .beginConfig()
                .fontSize((int) context.getResources().getDimension(R.dimen.bookTextSizePx))
                .textColor(Color.BLACK)
                .endConfig().buildRound(teamName.substring(0, 1), //will display first letter title
                        noImageColor);
        return noImage;
    }


    /**
     * Returns true if the network is available or about to become available.
     *
     * @param c Context used to get the ConnectivityManager
     * @return true if the network is available
     */
    //Should be called on main thread
    static public boolean isNetworkAvailable(Context c) {
        ConnectivityManager cm = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public static boolean isDeviceReadyForFlexTimeSync() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }



    public static void setImage(Context context, ImageView crest, String teamName) {
        Integer crestImgRessource = getTeamCrestByTeamName(context, teamName);
        final String NO_ICON = "no_icon";

        Drawable noImage = Utilities.getNoCrestImage(context, teamName);
        Glide.with(context)
                .load(crestImgRessource == null ? NO_ICON : crestImgRessource)
                .error(noImage)
                .crossFade()
                .into(crest);
    }


    public static void setWidgetImage(Context context, RemoteViews views,
                                      int viewId, String teamName) {
        Integer crestImgRessource = getTeamCrestByTeamName(context, teamName);

        if (crestImgRessource == null) {
            Drawable drawable = Utilities.getNoWidgetCrestImage(context, teamName);
            Bitmap bitmap = drawableToBitmap(drawable);
            views.setImageViewBitmap(viewId, bitmap);
        } else {
            views.setImageViewResource(viewId, crestImgRessource);
        }
    }

    private static Drawable getNoWidgetCrestImage(Context context, String teamName) {
        ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
        int noImageColor = generator.getRandomColor();
        TextDrawable noImage = TextDrawable.builder()
                .beginConfig()
                .width(90)
                .height(90)
                .fontSize((int) context.getResources().getDimension(R.dimen.bookTextSizePx))
                .textColor(Color.BLACK)
                .endConfig().buildRound(teamName.substring(0, 1), //will display first letter title
                        noImageColor);
        return noImage;
    }


    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }


    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
    public static void setRemoteContentDescription(
            RemoteViews views, int ressource_image, String description) {
        views.setContentDescription(ressource_image, description);
    }


    public static Locale getMostSuitableLocale(Context context) {
        String usLocale = context.getResources().getString(R.string.us_locale);
        String frLocale = context.getResources().getString(R.string.fr_locale);
        String chLocale = context.getResources().getString(R.string.ch_locale);

        String usLocaleLang = usLocale.split("_")[0];
        String frLocaleLang = frLocale.split("_")[0];
        String chLocaleLang = chLocale.split("_")[0];

        String usLocaleCountry = usLocale.split("_")[1];
        String frLocaleCountry = frLocale.split("_")[1];
        String chLocaleCountry = chLocale.split("_")[1];

        if ((Locale.getDefault().getLanguage().equals(usLocaleLang)) && (Locale.getDefault()
                .getCountry().equals(usLocaleCountry)))
            return Locale.getDefault(); //The user Locale is the Locale we want, no further search

        if ((Locale.getDefault().getLanguage().equals(frLocaleLang)) && (Locale.getDefault().getCountry().equals(frLocaleCountry)))
            return Locale.getDefault(); //The user Locale is the Locale we want, no further search

        if (Locale.getDefault().getLanguage().equals(frLocaleLang)
                && isLocaleAvailable(frLocaleLang, frLocaleCountry))
            return new Locale(frLocaleLang, frLocaleCountry);

        if (Locale.getDefault().getLanguage().equals(chLocaleLang)
                && isLocaleAvailable(chLocaleLang, frLocaleCountry))
            return new Locale(chLocaleLang, chLocaleCountry);

        //us_US only locale Java guarantees is always available.
        return new Locale(usLocaleLang, usLocaleCountry);
    }

    public static boolean isLocaleAvailable(String language, String country) {
        Locale[] locales = Locale.getAvailableLocales();
        for (Locale locale : locales) {
            if ((locale.getLanguage().equals(language))
                    && (locale.getCountry().equals(country)))
                return true;
        }
        return false;
    }


    public static long getLongDate(String timestamp) throws ParseException {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        return df.parse(timestamp).getTime();
    }


    public static long addDay(int nbDays, long startDateInMillis) {
        Calendar endDate = Calendar.getInstance();
        endDate.setTime(new Date(startDateInMillis));
        endDate.add(Calendar.DATE, nbDays);
        return endDate.getTime().getTime();
    }

    /**
     * This method convert a long representing an instant ex 2016-01-25 19:00:00 to the beginning
     * of the day ex:2016-01-25 00:00:00
     * @param dateInMillis
     * @return long representing the start of the day
     */
    public static long setZero(long dateInMillis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(dateInMillis);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        return calendar.getTimeInMillis();
    }

    //This will be displayed to the user, that's why we need to format in its locale and timezone
    public static String convertDateTimeToTime(long dateTime, Context context) {
        SimpleDateFormat df = new SimpleDateFormat("HH:mm", getMostSuitableLocale(context));
       // df.setTimeZone(TimeZone.getDefault());
        return df.format(new Date(dateTime));
    }
}
