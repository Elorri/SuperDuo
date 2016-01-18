package barqsoft.footballscores.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import barqsoft.footballscores.sync.ScoresSyncAdapter;

/**
 * Created by Elorri on 17/01/2016.
 */
public class WidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.e("SuperDuo", Thread.currentThread().getStackTrace()[2] + "");
        context.startService(new Intent(context, WidgetIntentService.class));
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager,
                                          int appWidgetId, Bundle newOptions) {
        Log.e("SuperDuo", Thread.currentThread().getStackTrace()[2] + "");
        context.startService(new Intent(context, WidgetIntentService.class));
    }

    @Override
    public void onReceive(@NonNull Context context, @NonNull Intent intent) {
        Log.e("SuperDuo", Thread.currentThread().getStackTrace()[2] + "");
        super.onReceive(context, intent);
        if (ScoresSyncAdapter.ACTION_DATA_UPDATED.equals(intent.getAction())) {
            Log.e("SuperDuo", Thread.currentThread().getStackTrace()[2] + "");
            context.startService(new Intent(context, WidgetIntentService.class));
        }
    }
}
