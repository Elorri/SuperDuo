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

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import barqsoft.footballscores.data.ScoresContract;

/**
 * This fragment represent the main screen of the app and will display a list of matches with
 * data taken from database. It will also display appropriate message to te user when internet is
 * down or serveur is down.
 */
public class ScoresFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,
        SharedPreferences.OnSharedPreferenceChangeListener {

    public ScoresAdapter mAdapter;
    public static final int SCORES_LOADER = 0;
    private long mTimeDate;
    private ListView mScoreList;

    public static final String[] MATCHES_COLUMNS = {
            ScoresContract.ScoreEntry._ID,
            ScoresContract.ScoreEntry.DATE_TIME_COL,
            ScoresContract.ScoreEntry.HOME_COL,
            ScoresContract.ScoreEntry.AWAY_COL,
            ScoresContract.ScoreEntry.LEAGUE_COL,
            ScoresContract.ScoreEntry.HOME_GOALS_COL,
            ScoresContract.ScoreEntry.AWAY_GOALS_COL,
            ScoresContract.ScoreEntry.MATCH_ID,
            ScoresContract.ScoreEntry.MATCH_DAY,
    };

    // These indices are tied to MATCHES_COLUMNS.  If MATCHES_COLUMNS changes, these
    // must change.
    public static final int COL_MATCH_ID = 0;
    public static final int COL_DATE_TIME = 1;
    public static final int COL_HOME = 2;
    public static final int COL_AWAY = 3;
    public static final int COL_LEAGUE = 4;
    public static final int COL_HOME_GOALS = 5;
    public static final int COL_AWAY_GOALS = 6;
    public static final int COL_ID = 7;
    public static final int COL_MATCHDAY = 8;


    public ScoresFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scores, container, false);
        mScoreList = (ListView) view.findViewById(R.id.scores_list);
        mAdapter = new ScoresAdapter(getActivity(), null, 0);
        mScoreList.setAdapter(mAdapter);
        mAdapter.selectedMatchId = MainActivity.selectedMatchId;
        mScoreList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ScoresAdapter.ViewHolder selected = (ScoresAdapter.ViewHolder) view.getTag();
                mAdapter.selectedMatchId = selected.matchId;
                MainActivity.selectedMatchId = (int) selected.matchId;
                mAdapter.notifyDataSetChanged();
            }
        });
        return view;
    }



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(SCORES_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(),
                ScoresContract.ScoreEntry.buildMatchesByDateUri(String.valueOf(mTimeDate)),
                MATCHES_COLUMNS,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mAdapter.swapCursor(cursor);
        updateEmptyView();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mAdapter.swapCursor(null);
    }


    public void setDate(long timeDate) {
        this.mTimeDate = timeDate;
    }

    @Override
    public void onResume() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sp.registerOnSharedPreferenceChangeListener(this);
        super.onResume();
    }

    @Override
    public void onPause() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sp.unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_score_table_status_key))) {
            updateEmptyView();
        }
    }


    private void updateEmptyView() {
        if (mAdapter.getCount() != 0)
            return;
        @Status.NetworkStatus int networkStatus = Status.getNetworkStatus(getContext());
        TextView emptyTextView = (TextView) getView().findViewById(R.id.emptyListView);
        emptyTextView.setVisibility(View.VISIBLE);
        mScoreList.setEmptyView(emptyTextView);
        if (networkStatus == Status.INTERNET_OFF) {
            emptyTextView.setText(R.string.no_scores_internet_off);
            return;
        }
        @Status.FootballApiStatus int footballApiStatus = Status.getFootballApiStatus(getContext());
        if ((networkStatus == Status.INTERNET_ON) && (footballApiStatus == Status.SERVEUR_DOWN)) {
            emptyTextView.setText(R.string.no_scores_serveur_down);
            return;
        }
        if ((networkStatus == Status.INTERNET_ON) && (footballApiStatus == Status.SERVEUR_WRONG_URL_APP_INPUT)) {
            emptyTextView.setText(R.string.no_scores_wrong_url_app_input);
            return;
        }
        @Status.ScoreTableStatus int scoreTableStatus = Status.getScoreTableStatus(getContext());
        if ((networkStatus == Status.INTERNET_ON) && (footballApiStatus == Status.SERVEUR_OK) &&
                (scoreTableStatus == Status.TABLE_STATUS_UNKNOWN)) {
            emptyTextView.setText(R.string.no_scores_table_status_unknown);
            return;
        }
        emptyTextView.setText(R.string.no_scores);
    }
}
