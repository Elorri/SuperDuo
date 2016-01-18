package barqsoft.footballscores;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;

import java.text.SimpleDateFormat;
import java.util.Date;

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

    public static int getTeamCrestByTeamName(Context context, String teamName) {
        if (teamName == null) {
            return R.drawable.no_icon;
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
        else        return R.drawable.no_icon;
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

    public static String getNow() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(date);
    }

    public static String getToday() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(date);
    }
}
