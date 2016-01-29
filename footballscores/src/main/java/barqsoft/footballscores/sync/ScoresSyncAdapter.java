package barqsoft.footballscores.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Vector;

import barqsoft.footballscores.R;
import barqsoft.footballscores.Status;
import barqsoft.footballscores.Utilities;
import barqsoft.footballscores.data.ScoresContract;


/**
 * Created by Elorri on 01/12/2015.
 */
public class ScoresSyncAdapter extends AbstractThreadedSyncAdapter {


    // Interval at which to sync with the openbeelab server, in seconds.
    // 60 seconds (1 minute) * 180 = 3 hours
    // 60 seconds (1 minute) * 720 = 12 hours
    //public static final int SYNC_INTERVAL = 60 * 180;
    public static final int SYNC_INTERVAL = 60 * 720;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL / 3;
    private String LOG_TAG = ScoresSyncAdapter.class.getSimpleName();


    public static final String ACTION_DATA_UPDATED = "footballscores.barqsoft.ACTION_DATA_UPDATED";


    public ScoresSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
                              ContentProviderClient provider, SyncResult syncResult) {
        syncDb();
    }


    public void syncDb() {
        Context context = getContext();
        boolean isInternetOn = Utilities.isNetworkAvailable(context);
        if (!isInternetOn) {
            Status.setNetworkStatus(context, Status.INTERNET_OFF);
            return;
        }
        Status.setNetworkStatus(context, Status.INTERNET_ON);
        getData("n2");
        getData("p2");
        updateWidgets();
    }

    private void updateWidgets() {
        Context context = getContext();
        // Setting the package ensures that only components in our app will receive the broadcast
        Intent dataUpdatedIntent = new Intent(ACTION_DATA_UPDATED)
                .setPackage(context.getPackageName());
        context.sendBroadcast(dataUpdatedIntent);
    }


    /**
     * This function handle errors managed by the footballApi serveur
     * see http://api.football-data.org/docs/latest/index.html#_http_error_codes_returned
     *
     * @param context
     * @param jsonObject
     * @return
     * @throws JSONException
     */
    void setServeurStatus(Context context, JSONObject jsonObject) throws JSONException {
        if (jsonObject == null) {
            Status.setFootballApiStatus(context, Status.SERVEUR_WRONG_URL_APP_INPUT);
            return;
        }

        String ERROR_TAG = "error";
        final int TOO_MANY_REQUEST = 429;

        if (jsonObject.has(ERROR_TAG)) {
            int errorCode = jsonObject.getInt(ERROR_TAG);
            switch (errorCode) {
                case HttpURLConnection.HTTP_BAD_REQUEST:
                    Status.setFootballApiStatus(context, Status.SERVEUR_WRONG_URL_APP_INPUT);
                    return;
                case HttpURLConnection.HTTP_FORBIDDEN:
                    Status.setFootballApiStatus(context, Status.SERVEUR_WRONG_URL_APP_INPUT);
                    return;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    Status.setFootballApiStatus(context, Status.SERVEUR_WRONG_URL_APP_INPUT);
                    return;
                case TOO_MANY_REQUEST:
                    Status.setFootballApiStatus(context, Status.SERVEUR_DOWN);
                    return;
            }
        } else {
            Status.setFootballApiStatus(context, Status.SERVEUR_OK);
        }
    }

    private void getData(String timeFrame) {

        // correct  exception catch try JSONException and IException
        final String BASE_URL = "http://api.football-data.org/alpha/fixtures";
        final String QUERY_TIME_FRAME = "timeFrame";


        Uri uri = Uri.parse(BASE_URL).buildUpon().
                appendQueryParameter(QUERY_TIME_FRAME, timeFrame)
                .build();

        HttpURLConnection UrlConnection = null;
        BufferedReader reader = null;
        String jsonData = null;


        try {
            URL fetch = new URL(uri.toString());
            UrlConnection = (HttpURLConnection) fetch.openConnection();
            UrlConnection.setRequestMethod("GET");
            UrlConnection.addRequestProperty("X-Auth-Token",
                    getContext().getString(R.string.api_key));
            UrlConnection.connect();

            InputStream inputStream = UrlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }
            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return;
            }
            jsonData = buffer.toString();

            setServeurStatus(getContext(), new JSONObject(jsonData));
            if (!(Status.getFootballApiStatus(getContext()) == Status.SERVEUR_OK))
                // we won't be able to fetch data, no need to go further
                return;

            Status.setScoreTableStatus(getContext(), Status.TABLE_STATUS_UNKNOWN);

            JSONArray matches = new JSONObject(jsonData).getJSONArray("fixtures");
            if (matches.length() == 0) { //if there is no data, call the function on dummy data
                //this is expected behavior during the off season.
                processJSONdata(getContext().getString(R.string.dummy_data), getContext(), false);
                return;
            }
            processJSONdata(jsonData, getContext(), true);

            //If we reach this point app tables are up to date
            Status.setScoreTableStatus(getContext(), Status.TABLE_SYNC_DONE);

        } catch (IOException e) {
            //catch exceptions more precisely
            Log.e(LOG_TAG, "IOException" + e.getMessage());
            Status.setFootballApiStatus(getContext(), Status.SERVEUR_DOWN);
            e.printStackTrace();
        } catch (JSONException e) {
            Log.e(LOG_TAG, "JSONException" + e.getMessage());
            e.printStackTrace();
        } finally {
            if (UrlConnection != null) {
                UrlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Error Closing Stream");
                }
            }
        }
    }

    private void processJSONdata(String JSONdata, Context mContext, boolean isReal) {


        // This set of league codes is for the 2015/2016 season. In fall of 2016, they will need to
        // be updated. Feel free to use the codes
        final String DUMMY_LEAGUE = "000";

        final String BUNDESLIGA1 = "394";
        final String BUNDESLIGA2 = "395";
        final String LIGUE1 = "396";
        final String LIGUE2 = "397";
        final String PREMIER_LEAGUE = "398";
        final String PRIMERA_DIVISION = "399";
        final String SEGUNDA_DIVISION = "400";
        final String SERIE_A = "401";
        final String PRIMERA_LIGA = "402";
        final String BUNDESLIGA3 = "403";
        final String EREDIVISIE = "404";


        final String SEASON_LINK = "http://api.football-data.org/alpha/soccerseasons/";
        final String MATCH_LINK = "http://api.football-data.org/alpha/fixtures/";
        final String FIXTURES = "fixtures";
        final String LINKS = "_links";
        final String SOCCER_SEASON = "soccerseason";
        final String SELF = "self";
        final String MATCH_DATE = "date";
        final String HOME_TEAM = "homeTeamName";
        final String AWAY_TEAM = "awayTeamName";
        final String RESULT = "result";
        final String HOME_GOALS = "goalsHomeTeam";
        final String AWAY_GOALS = "goalsAwayTeam";
        final String MATCH_DAY = "matchday";

        //Match data
        String league;
        String timestamp;
        String home;
        String away;
        String homeGoals;
        String awayGoals;
        String matchId;
        String matchDay;


        try {
            JSONArray matches = new JSONObject(JSONdata).getJSONArray(FIXTURES);


            //ContentValues to be inserted
            Vector<ContentValues> values = new Vector<ContentValues>(matches.length());
            for (int i = 0; i < matches.length(); i++) {

                JSONObject matchData = matches.getJSONObject(i);
                league = matchData.getJSONObject(LINKS).getJSONObject(SOCCER_SEASON).getString("href");
                //From http://api.football-data.org/alpha/soccerseasons/398 we want 398
                league = league.replace(SEASON_LINK, "");
                //This if statement controls which leagues we're interested in the data from.
                //add leagues here in order to have them be added to the DB.
                // If you are finding no data in the app, check that this contains all the leagues.
                // If it doesn't, that can cause an empty DB, bypassing the dummy data routine.
                if (league.equals(DUMMY_LEAGUE) ||
                        league.equals(PREMIER_LEAGUE) ||
                        league.equals(LIGUE1) ||
                        league.equals(LIGUE2) ||
                        league.equals(SERIE_A) ||
                        league.equals(SEGUNDA_DIVISION) ||
                        league.equals(PRIMERA_LIGA) ||
                        league.equals(BUNDESLIGA3) ||
                        league.equals(BUNDESLIGA1) ||
                        league.equals(BUNDESLIGA2) ||
                        league.equals(EREDIVISIE) ||
                        league.equals(PRIMERA_DIVISION)) {
                    //Exple : http://api.football-data.org/alpha/fixtures/146892 -> 146892
                    matchId = matchData.getJSONObject(LINKS).getJSONObject(SELF).getString("href");
                    matchId = matchId.replace(MATCH_LINK, "");
                    if (!isReal) {
                        //This if statement changes the match ID of the dummy data so that it all goes into the database
                        //This is useful because the mock data has always the same matchId 000000,
                        // and matchId should be unique in db, concatenating i value
                        // will solve the pb
                        matchId = matchId + Integer.toString(i);
                    }

                    timestamp = matchData.getString(MATCH_DATE); //get timestamp ex:2016-01-13T19:45:00Z

                    long dateTime = Utilities.getLongDate(timestamp);

                    if (!isReal) {
                        //This if statement changes the dummy data's timestamp to match our current timestamp range.
                        dateTime = Utilities.addDay(-2, Calendar.getInstance().getTimeInMillis());
                    }

                    home = matchData.getString(HOME_TEAM);
                    away = matchData.getString(AWAY_TEAM);
                    homeGoals = matchData.getJSONObject(RESULT).getString(HOME_GOALS);
                    awayGoals = matchData.getJSONObject(RESULT).getString(AWAY_GOALS);
                    matchDay = matchData.getString(MATCH_DAY);


                    ContentValues match_values = new ContentValues();
                    match_values.put(ScoresContract.ScoreEntry.MATCH_ID, matchId);
                    match_values.put(ScoresContract.ScoreEntry.DATE_TIME_COL, dateTime);
                    match_values.put(ScoresContract.ScoreEntry.HOME_COL, home);
                    match_values.put(ScoresContract.ScoreEntry.AWAY_COL, away);
                    match_values.put(ScoresContract.ScoreEntry.HOME_GOALS_COL, homeGoals);
                    match_values.put(ScoresContract.ScoreEntry.AWAY_GOALS_COL, awayGoals);
                    match_values.put(ScoresContract.ScoreEntry.LEAGUE_COL, league);
                    match_values.put(ScoresContract.ScoreEntry.MATCH_DAY, matchDay);
                    values.add(match_values);
                }
            }
            int insertedData = 0;
            ContentValues[] insertData = new ContentValues[values.size()];
            values.toArray(insertData);
            // TODO delete all match > j-2 ou -3?
            long now = System.currentTimeMillis();
            mContext.getContentResolver().delete(ScoresContract.ScoreEntry
                    .buildMatchesByDateUri(String.valueOf(now)),null, null);

            insertedData = mContext.getContentResolver().bulkInsert(
                    ScoresContract.ScoreEntry.CONTENT_URI, insertData);
            Log.d(LOG_TAG, "Succesfully Inserted : " + insertedData);
        } catch (ParseException e) {
            Log.e(LOG_TAG, e.getMessage());
        }catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage());
        }

    }




    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if (null == accountManager.getPassword(newAccount)) {
        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
             /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        ScoresSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }

    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Utilities.isDeviceReadyForFlexTimeSync()) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    /**
     * Helper method to have the sync adapter sync immediately
     *
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);

        //Note if internet is on resquestSync will init ScoreSyncAdapter and call onPerformSync
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }
}
