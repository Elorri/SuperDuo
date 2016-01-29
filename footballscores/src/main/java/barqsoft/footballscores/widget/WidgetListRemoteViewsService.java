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

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import barqsoft.footballscores.R;
import barqsoft.footballscores.ScoresFragment;
import barqsoft.footballscores.Utilities;
import barqsoft.footballscores.data.ScoresContract;

/**
 * Service which handles updating Football widgets 'next matches collection' with the latest data
 * Created by Elorri on 17/01/2016.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class WidgetListRemoteViewsService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {
            private Cursor data = null;

            @Override
            public void onCreate() {
                // Nothing to do
            }

            @Override
            public void onDataSetChanged() {
                if (data != null) {
                    data.close();
                }
                // This method is called by the app hosting the widget (e.g., the launcher)
                // However, our ContentProvider is not exported so it doesn't have access to the
                // data. Therefore we need to clear (and finally restore) the calling identity so
                // that calls use our process and permission
                final long identityToken = Binder.clearCallingIdentity();
                long now=System.currentTimeMillis();
                data = getContentResolver().query(
                        ScoresContract.ScoreEntry.buildNextMatchesByDateUri(String.valueOf(now)),
                        ScoresFragment.MATCHES_COLUMNS,
                        null,
                        null,
                        null);
                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                if (data != null) {
                    data.close();
                    data = null;
                }
            }

            @Override
            public int getCount() {
                return data == null ? 0 : data.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if (position == AdapterView.INVALID_POSITION ||
                        data == null || !data.moveToPosition(position)) {
                    return null;
                }
                RemoteViews views = new RemoteViews(getPackageName(),
                        R.layout.widget_item);

                // Extract the data from the Cursor
                Context context = getApplicationContext();
                String homeCrest = data.getString(ScoresFragment.COL_HOME);
                String awayCrest = data.getString(ScoresFragment.COL_AWAY);
                String scores = Utilities.getScores(context,
                        data.getInt(ScoresFragment.COL_HOME_GOALS),
                        data.getInt(ScoresFragment.COL_AWAY_GOALS));
                String dateTime=data.getString(ScoresFragment.COL_DATE_TIME);


                // Add the data to the RemoteViews
                Utilities.setWidgetImage(context, views, R.id.home_crest,homeCrest);
                Utilities.setWidgetImage(context, views, R.id.away_crest, awayCrest);
                views.setTextViewText(R.id.score_textview, scores);
                views.setTextViewText(R.id.time_textview, Utilities.convertDateTimeToTime(Long.valueOf(dateTime), context));


                //Add content description for images
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                    Utilities.setRemoteContentDescription(views, R.id.home_crest, homeCrest);
                    Utilities.setRemoteContentDescription(views, R.id.away_crest, awayCrest);
                }


                // Create an Intent to launch DetailActivity
                final Intent fillInIntent = new Intent();
                Uri detailUri = ScoresContract.ScoreEntry.buildMatchesByDateUri(String.valueOf(System
                        .currentTimeMillis()));
                fillInIntent.setData(detailUri);
                views.setOnClickFillInIntent(R.id.widget_item, fillInIntent);
                return views;
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.widget_item);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                if (data.moveToPosition(position))
                    return data.getLong(ScoresFragment.COL_MATCH_ID);
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}
