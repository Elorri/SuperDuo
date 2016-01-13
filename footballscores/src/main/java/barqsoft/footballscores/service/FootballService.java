package barqsoft.footballscores.service;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Looper;
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.Vector;

import barqsoft.footballscores.R;
import barqsoft.footballscores.Status;
import barqsoft.footballscores.Utilities;
import barqsoft.footballscores.data.ScoresContract;

/**
 * Created by yehya khaled on 3/2/2015.
 */
public class FootballService extends IntentService {

    public static final String LOG_TAG = FootballService.class.getSimpleName();
    public static final String SERVICE_NAME="FootballService";
    public FootballService() {
        super(SERVICE_NAME);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        syncDb();
    }

    private void syncDb() {
        //TODO : 2.0 check this method works when called from (B).Read http://stackoverflow.com/questions/9570237/android-check-internet-connection
        Log.d("SuperDuo", "current thread : "+  thread() );

        Context context = getApplicationContext();
        boolean isInternetOn = Utilities.isNetworkAvailable(context);
        if (!isInternetOn) {
            Log.e("SuperDuo", Thread.currentThread().getStackTrace()[2] + "isInternetOn"+isInternetOn);
            Status.setNetworkStatus(context, Status.INTERNET_OFF);
            return;
        }
        Status.setNetworkStatus(context, Status.INTERNET_ON);
        getData("n2");
        getData("p2");
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
        Log.e("SuperDuo", Thread.currentThread().getStackTrace()[2] + "");
        Log.e("SuperDuo","setServeurStatus");
        if (jsonObject == null) {
            Log.e("SuperDuo", Thread.currentThread().getStackTrace()[2] + "jsonObject is null");
            Status.setFootballApiStatus(context, Status.SERVEUR_WRONG_URL_APP_INPUT);
            return;
        }

        String ERROR_TAG = "error";
        final int TOO_MANY_REQUEST = 429;

        if (jsonObject.has(ERROR_TAG)) {
            int errorCode = jsonObject.getInt(ERROR_TAG);
            switch (errorCode) {
                case HttpURLConnection.HTTP_BAD_REQUEST:
                    Log.e("SuperDuo", Thread.currentThread().getStackTrace()[2] + "HTTP_BAD_REQUEST");
                    Status.setFootballApiStatus(context, Status.SERVEUR_WRONG_URL_APP_INPUT);
                    return;
                case HttpURLConnection.HTTP_FORBIDDEN:
                    Log.e("SuperDuo", Thread.currentThread().getStackTrace()[2] + "HTTP_FORBIDDEN");
                    Status.setFootballApiStatus(context, Status.SERVEUR_WRONG_URL_APP_INPUT);
                    return;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    Log.e("SuperDuo", Thread.currentThread().getStackTrace()[2] + "HTTP_NOT_FOUND");
                    Status.setFootballApiStatus(context, Status.SERVEUR_WRONG_URL_APP_INPUT);
                    return;
                case TOO_MANY_REQUEST:
                    Log.e("SuperDuo", Thread.currentThread().getStackTrace()[2] + "TOO_MANY_REQUEST");
                    Status.setFootballApiStatus(context, Status.SERVEUR_DOWN);
                    return;
            }
        }else{
            Log.e("SuperDuo", Thread.currentThread().getStackTrace()[2] + "SERVEUR_OK");
            Status.setFootballApiStatus(context, Status.SERVEUR_OK);
        }
    }

    private void getData(String timeFrame) {

        // correct  exception catch try JSONException and IException
        final String BASE_URL = "http://api.football-data.org/alpha/fixtures";
        final String QUERY_TIME_FRAME = "timeFrame";


        Uri fetch_build = Uri.parse(BASE_URL).buildUpon().
                appendQueryParameter(QUERY_TIME_FRAME, timeFrame)
                .build();
        //TODO : 4.0 replace Log.v par Log.d
        //Log.v(LOG_TAG, "The url we are looking at is: "+fetch_build.toString());
        Log.e("SuperDuo", "The url we are looking at is: "+fetch_build.toString());

        HttpURLConnection UrlConnection = null;
        BufferedReader reader = null;
        String jsonData = null;

        try {
            URL fetch = new URL(fetch_build.toString());
            UrlConnection = (HttpURLConnection) fetch.openConnection();
            UrlConnection.setRequestMethod("GET");
            UrlConnection.addRequestProperty("X-Auth-Token", getString(R.string.api_key));
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
            Log.e("SuperDuo", Thread.currentThread().getStackTrace()[2] + "jsonData: \n"+jsonData);

            setServeurStatus(getApplicationContext(), new JSONObject(jsonData));
            if (!(Status.getFootballApiStatus(getApplicationContext()) == Status.SERVEUR_OK))
                // we won't be able to fetch data, no need to go further
                return;

            Status.setScoreTableStatus(getApplicationContext(), Status.TABLE_STATUS_UNKNOWN);

            JSONArray matches = new JSONObject(jsonData).getJSONArray("fixtures");
            if (matches.length() == 0) { //if there is no data, call the function on dummy data
                //this is expected behavior during the off season.
                processJSONdata(getString(R.string.dummy_data), getApplicationContext(), false);
                return;
            }
            processJSONdata(jsonData, getApplicationContext(), true);

            //If we reach this point app tables are up to date
            Status.setScoreTableStatus(getApplicationContext(), Status.TABLE_SYNC_DONE);

        } catch (IOException e) {
            //catch exceptions more precisely
            Log.e(LOG_TAG, "IOException" + e.getMessage());
            //TODO : 2.0 add this line in alexanria
            Status.setFootballApiStatus(getApplicationContext(), Status.SERVEUR_DOWN);
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
        final String Bundesliga3 = "403";
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
        String date;
        String time;
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
                        league.equals(SERIE_A) ||
                        league.equals(BUNDESLIGA1) ||
                        league.equals(BUNDESLIGA2) ||
                        league.equals(PRIMERA_DIVISION)) {
                    //Exple : http://api.football-data.org/alpha/fixtures/146892 -> 146892
                    matchId = matchData.getJSONObject(LINKS).getJSONObject(SELF).getString("href");
                    matchId = matchId.replace(MATCH_LINK, "");
                    if (!isReal) {
                        //This if statement changes the match ID of the dummy data so that it all goes into the database
                        //This is useful because the mock data has always the same match_id 000000,
                        // and match_id should be unique in db, concatanating i value
                        // will solve the pb
                        matchId = matchId + Integer.toString(i);
                    }

                    //TODO 2.4 make sure you get time and datefrom timestamp
                    date = matchData.getString(MATCH_DATE);
                    time = date.substring(date.indexOf("T") + 1, date.indexOf("Z"));
                    date = date.substring(0, date.indexOf("T"));
                    SimpleDateFormat match_date = new SimpleDateFormat("yyyy-MM-ddHH:mm:ss");
                    match_date.setTimeZone(TimeZone.getTimeZone("UTC"));
                    try {
                        Date parseddate = match_date.parse(date + time);
                        SimpleDateFormat new_date = new SimpleDateFormat("yyyy-MM-dd:HH:mm");
                        new_date.setTimeZone(TimeZone.getDefault());
                        date = new_date.format(parseddate);
                        time = date.substring(date.indexOf(":") + 1);
                        date = date.substring(0, date.indexOf(":"));

                        if (!isReal) {
                            //This if statement changes the dummy data's date to match our current date range.
                            Date fragmentdate = new Date(System.currentTimeMillis() + ((i - 2) * 86400000));
                            SimpleDateFormat mformat = new SimpleDateFormat("yyyy-MM-dd");
                            date = mformat.format(fragmentdate);
                        }
                    } catch (Exception e) {
                        Log.d(LOG_TAG, "error here!");
                        Log.e(LOG_TAG, e.getMessage());
                    }
                    home = matchData.getString(HOME_TEAM);
                    away = matchData.getString(AWAY_TEAM);
                    homeGoals = matchData.getJSONObject(RESULT).getString(HOME_GOALS);
                    awayGoals = matchData.getJSONObject(RESULT).getString(AWAY_GOALS);
                    matchDay = matchData.getString(MATCH_DAY);
                    ContentValues match_values = new ContentValues();
                    match_values.put(ScoresContract.ScoreEntry.MATCH_ID, matchId);
                    match_values.put(ScoresContract.ScoreEntry.DATE_COL, date);
                    match_values.put(ScoresContract.ScoreEntry.TIME_COL, time);
                    match_values.put(ScoresContract.ScoreEntry.HOME_COL, home);
                    match_values.put(ScoresContract.ScoreEntry.AWAY_COL, away);
                    match_values.put(ScoresContract.ScoreEntry.HOME_GOALS_COL, homeGoals);
                    match_values.put(ScoresContract.ScoreEntry.AWAY_GOALS_COL, awayGoals);
                    match_values.put(ScoresContract.ScoreEntry.LEAGUE_COL, league);
                    match_values.put(ScoresContract.ScoreEntry.MATCH_DAY, matchDay);
                    //Log.v(LOG_TAG,matchId);
                    //Log.v(LOG_TAG,mDate);
                    //Log.v(LOG_TAG,time);
                    //Log.v(LOG_TAG,home);
                    //Log.v(LOG_TAG,away);
                    //Log.v(LOG_TAG,homeGoals);
                    //Log.v(LOG_TAG,awayGoals);

                    values.add(match_values);
                }
            }
            int insertedData = 0;
            ContentValues[] insert_data = new ContentValues[values.size()];
            values.toArray(insert_data);
            insertedData = mContext.getContentResolver().bulkInsert(
                    ScoresContract.BASE_CONTENT_URI, insert_data);
            //Log.v(LOG_TAG,"Succesfully Inserted : " + String.valueOf(insertedData));
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage());
        }

    }

    public static String thread() {
        if (Looper.getMainLooper().getThread() == Thread.currentThread())
            return "ThreadUI";
        else return "Background";
    }
}

