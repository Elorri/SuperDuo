package barqsoft.footballscores;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by yehya khaled on 2/26/2015.
 */
public class ScoresAdapter extends CursorAdapter {


    public double selectedMatchId = 0;
    private String FOOTBALL_SCORES_HASHTAG = "#Football_Scores";



    public ScoresAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
    }


    public class ViewHolder {
        private FrameLayout detailViewConainer;
        private TextView homeName;
        private TextView awayName;
        private TextView score;
        private TextView date;
        private ImageView homeCrest;
        private ImageView awayCrest;
        public double matchId;

        public ViewHolder(View view) {
            detailViewConainer = (FrameLayout) view.findViewById(R.id.detailview_container);
            homeName = (TextView) view.findViewById(R.id.home_name);
            awayName = (TextView) view.findViewById(R.id.away_name);
            score = (TextView) view.findViewById(R.id.score_textview);
            date = (TextView) view.findViewById(R.id.data_textview);
            homeCrest = (ImageView) view.findViewById(R.id.home_crest);
            awayCrest = (ImageView) view.findViewById(R.id.away_crest);
        }
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View scoreItemView = LayoutInflater.from(context).inflate(R.layout.item_score, parent, false);
        ViewHolder viewHolder = new ViewHolder(scoreItemView);
        scoreItemView.setTag(viewHolder);
        return scoreItemView;
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        final ViewHolder viewHolder = (ViewHolder) view.getTag();
        viewHolder.homeName.setText(cursor.getString(ScoresFragment.COL_HOME));
        viewHolder.awayName.setText(cursor.getString(ScoresFragment.COL_AWAY));
        viewHolder.date.setText(cursor.getString(ScoresFragment.COL_MATCHTIME));
        viewHolder.score.setText(Utilities.getScores(context,cursor.getInt(ScoresFragment.COL_HOME_GOALS), cursor.getInt(ScoresFragment.COL_AWAY_GOALS)));
        viewHolder.matchId = cursor.getDouble(ScoresFragment.COL_ID);
        viewHolder.homeCrest.setImageResource(Utilities.getTeamCrestByTeamName(context,
                cursor.getString(ScoresFragment.COL_HOME)));
        viewHolder.awayCrest.setImageResource(Utilities.getTeamCrestByTeamName(context,
                cursor.getString(ScoresFragment.COL_AWAY)
        ));



        if (viewHolder.matchId == selectedMatchId) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getApplicationContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            LinearLayout detailView = (LinearLayout)layoutInflater.inflate(R.layout.item_score_detail, null);

            viewHolder.detailViewConainer.addView(detailView, 0,
                    new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT));
            TextView matchDay = (TextView) detailView.findViewById(R.id.matchday_textview);
            TextView league = (TextView) detailView.findViewById(R.id.league_textview);
            Button share_button = (Button) detailView.findViewById(R.id.share_button);


            matchDay.setText(Utilities.getMatchDay(context, cursor.getInt(ScoresFragment
                            .COL_MATCHDAY),
                    cursor.getInt(ScoresFragment.COL_LEAGUE)));
            league.setText(Utilities.getLeague(context, cursor.getInt(ScoresFragment.COL_LEAGUE)));
            share_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //add Share Action
                    context.startActivity(createShareForecastIntent(viewHolder.homeName.getText() + " "
                            + viewHolder.score.getText() + " " + viewHolder.awayName.getText() + " "));
                }
            });
        } else {
            viewHolder.detailViewConainer.removeAllViews();
        }

    }

    public Intent createShareForecastIntent(String ShareText) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, ShareText + FOOTBALL_SCORES_HASHTAG);
        return shareIntent;
    }

}
