/**
 * The MIT License (MIT)

 Copyright (c) 2016 ETCHEMENDY ELORRI

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
package barqsoft.footballscores.widget;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.widget.RemoteViews;

import barqsoft.footballscores.MainActivity;
import barqsoft.footballscores.R;
import barqsoft.footballscores.ScoresFragment;
import barqsoft.footballscores.Utilities;
import barqsoft.footballscores.data.ScoresContract;

/**
 * IntentService which handles updating Football widgets 'next match' with the latest data
 * Created by Elorri on 17/01/2016.
 */
public class WidgetIntentService extends IntentService {

    public WidgetIntentService() {
        super("WidgetIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // Retrieve all of the Today widget ids: these are the widgets we need to update
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this,
                WidgetProvider.class));


        // Get now's data from the ContentProvider
        long now = System.currentTimeMillis();
        Cursor cursor = getContentResolver().query(
                ScoresContract.ScoreEntry.buildNextMatchesByDateUri(String.valueOf(now)),
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

        // Extract the data from the Cursor
        Context context = getApplicationContext();
        String homeCrest = cursor.getString(ScoresFragment.COL_HOME);
        String awayCrest = cursor.getString(ScoresFragment.COL_AWAY);
        String scores = Utilities.getScores(context,
                cursor.getInt(ScoresFragment.COL_HOME_GOALS),
                cursor.getInt(ScoresFragment.COL_AWAY_GOALS));
        String dateTime=cursor.getString(ScoresFragment.COL_DATE_TIME);
        cursor.close();


        // Perform this loop procedure for each widget
        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(),
                    R.layout.widget_one);


            // Add the data to the RemoteViews
            Utilities.setWidgetImage(context, views, R.id.home_crest, homeCrest);
            Utilities.setWidgetImage(context, views, R.id.away_crest, awayCrest);
            views.setTextViewText(R.id.score_textview, scores);
            views.setTextViewText(R.id.time_textview, Utilities.convertDateTimeToTime(Long.valueOf(dateTime), context));
            views.setTextViewText(R.id.widget_empty, "");

            //Add content description for images
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                Utilities.setRemoteContentDescription(views, R.id.home_crest, homeCrest);
                Utilities.setRemoteContentDescription(views, R.id.away_crest, awayCrest);
            }


            // Create an Intent to launch MainActivity
            Intent launchIntent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, launchIntent, 0);
            views.setOnClickPendingIntent(R.id.widget_item, pendingIntent);

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}
