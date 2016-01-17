package barqsoft.footballscores.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import barqsoft.footballscores.MainActivity;
import barqsoft.footballscores.R;

/**
 * Created by Elorri on 17/01/2016.
 */
public class FootballWidgetProvider extends AppWidgetProvider {


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        // Perform this loop procedure for each widget
        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_next_match);

            // Add the data to the RemoteViews
            views.setImageViewResource(R.id.home_crest, R.drawable.burney_fc_hd_logo);
            views.setImageViewResource(R.id.away_crest, R.drawable.everton_fc_logo1);
            views.setTextViewText(R.id.home_name, "Stoke City FC My");
            views.setTextViewText(R.id.away_name, "Norwish City FC My");
            views.setTextViewText(R.id.score_textview, "2 - 1");
            views.setTextViewText(R.id.time_textview, "20:50");

            // Create an Intent to launch MainActivity
            Intent launchIntent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, launchIntent, 0);
            views.setOnClickPendingIntent(R.id.widget, pendingIntent);

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}
