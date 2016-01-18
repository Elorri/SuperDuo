package barqsoft.footballscores.widget;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.widget.RemoteViews;

import barqsoft.footballscores.MainActivity;
import barqsoft.footballscores.R;
import barqsoft.footballscores.ScoresFragment;
import barqsoft.footballscores.Utilities;
import barqsoft.footballscores.data.ScoresContract;

/**
 * Created by Elorri on 17/01/2016.
 */
public class WidgetIntentService extends IntentService {
    /**
     * IntentService which handles updating all Football widgets with the latest data
     */
    public WidgetIntentService() {
        super("WidgetIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.e("SuperDuo", Thread.currentThread().getStackTrace()[2] + "");
        // Retrieve all of the Today widget ids: these are the widgets we need to update
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this,
                WidgetProvider.class));


        // Get now's data from the ContentProvider
        String now = Utilities.getNow();
        Cursor cursor = getContentResolver().query(
                ScoresContract.ScoreEntry.buildScoreByDate(now),
                ScoresFragment.MATCHES_COLUMNS,
                null,
                null,
                null);
        if (cursor == null) {
            return;
        }
        if (!cursor.moveToFirst()) {
            cursor.close();
            return;
        }

        //TODO :2.1 when no icons put the teams name instead
        // Extract the data from the Cursor
        Context context=getApplicationContext();
        int homeCrest=Utilities.getTeamCrestByTeamName(context,
                cursor.getString(ScoresFragment.COL_HOME));
        int awayCrest=Utilities.getTeamCrestByTeamName(context,
                cursor.getString(ScoresFragment.COL_AWAY));
        String scores =Utilities.getScores(context,
                cursor.getInt(ScoresFragment.COL_HOME_GOALS),
                cursor.getInt(ScoresFragment.COL_AWAY_GOALS));
        String time =cursor.getString(ScoresFragment.COL_MATCHTIME);
        cursor.close();


        // Perform this loop procedure for each widget
        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), 
                    R.layout.widget_one);

            //TODO: 2.3 set content description here
            // Add the data to the RemoteViews
            views.setImageViewResource(R.id.home_crest, homeCrest);
            views.setImageViewResource(R.id.away_crest, awayCrest);
            views.setTextViewText(R.id.score_textview, scores);
            views.setTextViewText(R.id.time_textview, time);

            // Create an Intent to launch MainActivity
            Intent launchIntent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, launchIntent, 0);
            views.setOnClickPendingIntent(R.id.widget_item, pendingIntent);

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}
