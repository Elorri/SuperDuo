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
 * This class is responsible for filling the ScoresFragment with appropriate data from database
 * Created by yehya khaled on 2/26/2015.
 * @author yehya khaled
 * @author Elorri Etchemendy
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
            date = (TextView) view.findViewById(R.id.time_textview);
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
        String homeTeamName = cursor.getString(ScoresFragment.COL_HOME);
        String awayTeamName = cursor.getString(ScoresFragment.COL_AWAY);
        viewHolder.homeName.setText(homeTeamName);
        viewHolder.awayName.setText(awayTeamName);
        String dateTime=cursor.getString(ScoresFragment.COL_DATE_TIME);
        viewHolder.date.setText(Utilities.convertDateTimeToTime(Long.valueOf(dateTime), context));
        viewHolder.score.setText(Utilities.getScores(context, cursor.getInt(ScoresFragment.COL_HOME_GOALS), cursor.getInt(ScoresFragment.COL_AWAY_GOALS)));
        viewHolder.matchId = cursor.getDouble(ScoresFragment.COL_ID);
        Utilities.setImage(context, viewHolder.homeCrest,homeTeamName);
        Utilities.setImage(context, viewHolder.awayCrest,awayTeamName);



        if (viewHolder.matchId == selectedMatchId) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getApplicationContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            LinearLayout detailView = (LinearLayout) layoutInflater.inflate(R.layout.item_score_detail, null);

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
