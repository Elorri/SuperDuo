package barqsoft.footballscores;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import barqsoft.footballscores.data.ScoresContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class ScoresFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,
        SharedPreferences.OnSharedPreferenceChangeListener {

    public ScoresAdapter mAdapter;
    public static final int SCORES_LOADER = 0;
    private String date;
    private ListView mScoreList;

    private static final String[] MATCHES_COLUMNS = {
                     ScoresContract.ScoreEntry._ID   ,
                     ScoresContract.ScoreEntry.DATE_COL  ,
                     ScoresContract.ScoreEntry.TIME_COL  ,
                     ScoresContract.ScoreEntry.HOME_COL  ,
                     ScoresContract.ScoreEntry.AWAY_COL  ,
                     ScoresContract.ScoreEntry.LEAGUE_COL  ,
                     ScoresContract.ScoreEntry.HOME_GOALS_COL  ,
                     ScoresContract.ScoreEntry.AWAY_GOALS_COL  ,
                     ScoresContract.ScoreEntry.MATCH_ID  ,
                     ScoresContract.ScoreEntry.MATCH_DAY  ,
    };

    // These indices are tied to MATCHES_COLUMNS.  If MATCHES_COLUMNS changes, these
    // must change.
    public static final int COL_MATCH_ID = 0;
    public static final int COL_DATE = 1;
    public static final int COL_MATCHTIME = 2;
    public static final int COL_HOME = 3;
    public static final int COL_AWAY = 4;
    public static final int COL_LEAGUE = 5;
    public static final int COL_HOME_GOALS = 6;
    public static final int COL_AWAY_GOALS = 7;
    public static final int COL_ID = 8;
    public static final int COL_MATCHDAY = 9;




    public ScoresFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        Log.e("SuperDuo", Thread.currentThread().getStackTrace()[2] + "");
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

                //TODO : 2.0 mAdapter.notifyDataSetChanged(); remove and see
                //mAdapter.notifyDataSetChanged();
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
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(getActivity(),
                ScoresContract.ScoreEntry.buildScoreByDate(date),
                MATCHES_COLUMNS,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {

        int i = 0;
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            i++;
            cursor.moveToNext();
        }
       mAdapter.swapCursor(cursor);
        updateEmptyView();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mAdapter.swapCursor(null);
    }




    public void setDate(String date) {
        this.date = date;
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
        @Status.NetworkStatus int networkStatus=Status.getNetworkStatus(getContext());
        TextView emptyTextView = (TextView) getView().findViewById(R.id.emptyListView);
        emptyTextView.setVisibility(View.VISIBLE);
        mScoreList.setEmptyView(emptyTextView);
        if(networkStatus==Status.INTERNET_OFF) {
            emptyTextView.setText(R.string.no_scores_internet_off);
            return;
        }
        @Status.FootballApiStatus int footballApiStatus=Status.getFootballApiStatus(getContext());
        if((networkStatus==Status.INTERNET_ON)&&(footballApiStatus==Status.SERVEUR_DOWN)) {
            emptyTextView.setText(R.string.no_scores_serveur_down);
            return;
        }
        if((networkStatus==Status.INTERNET_ON)&&(footballApiStatus==Status.SERVEUR_WRONG_URL_APP_INPUT)) {
            emptyTextView.setText(R.string.no_scores_wrong_url_app_input);
            return;
        }
        @Status.ScoreTableStatus int scoreTableStatus=Status.getScoreTableStatus(getContext());
        if((networkStatus==Status.INTERNET_ON)&&(footballApiStatus==Status.SERVEUR_OK)&&
                (scoreTableStatus==Status.TABLE_STATUS_UNKNOWN)) {
            emptyTextView.setText(R.string.no_scores_table_status_unknown);
            return;
        }
        emptyTextView.setText(R.string.no_scores);
    }
}
