    package barqsoft.footballscores.widget;

    import android.annotation.TargetApi;
    import android.content.Context;
    import android.content.Intent;
    import android.database.Cursor;
    import android.net.Uri;
    import android.os.Binder;
    import android.os.Build;
    import android.util.Log;
    import android.widget.AdapterView;
    import android.widget.RemoteViews;
    import android.widget.RemoteViewsService;

    import barqsoft.footballscores.R;
    import barqsoft.footballscores.ScoresFragment;
    import barqsoft.footballscores.Utilities;
    import barqsoft.footballscores.data.ScoresContract;

    /**
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
                    Log.e("SuperDuo", Thread.currentThread().getStackTrace()[2] + "");
                    if (data != null) {
                        data.close();
                    }
                    // This method is called by the app hosting the widget (e.g., the launcher)
                    // However, our ContentProvider is not exported so it doesn't have access to the
                    // data. Therefore we need to clear (and finally restore) the calling identity so
                    // that calls use our process and permission
                    final long identityToken = Binder.clearCallingIdentity();
                    String now = Utilities.getToday();
                    data = getContentResolver().query(
                            ScoresContract.ScoreEntry.buildScoreByDate(now),
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
                    Log.e("SuperDuo", Thread.currentThread().getStackTrace()[2] + "");
                    if (position == AdapterView.INVALID_POSITION ||
                            data == null || !data.moveToPosition(position)) {
                        return null;
                    }
                    RemoteViews views = new RemoteViews(getPackageName(),
                            R.layout.widget_item);

                    // Extract the data from the Cursor
                    Context context=getApplicationContext();
                    int homeCrest=Utilities.getTeamCrestByTeamName(context,
                            data.getString(ScoresFragment.COL_HOME));
                    int awayCrest=Utilities.getTeamCrestByTeamName(context,
                            data.getString(ScoresFragment.COL_AWAY));
                    String scores =Utilities.getScores(context,
                            data.getInt(ScoresFragment.COL_HOME_GOALS),
                            data.getInt(ScoresFragment.COL_AWAY_GOALS));
                    String time =data.getString(ScoresFragment.COL_MATCHTIME);

                    // Add the data to the RemoteViews
                    views.setImageViewResource(R.id.home_crest, homeCrest);
                    views.setImageViewResource(R.id.away_crest, awayCrest);
                    views.setTextViewText(R.id.score_textview, scores);
                    views.setTextViewText(R.id.time_textview, time);


                    // Create an Intent to launch DetailActivity
                    final Intent fillInIntent = new Intent();
                    Uri detailUri= ScoresContract.ScoreEntry.buildScoreByDate(Utilities.getToday());
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
