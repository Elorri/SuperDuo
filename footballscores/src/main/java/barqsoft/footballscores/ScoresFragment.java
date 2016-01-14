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
    private String[] date = new String[1];
    private ListView mScoreList;


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


        mAdapter.detail_match_id = MainActivity.selectedMatchId;
        mScoreList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //TODO : 2.0 use Uri here
                ScoresAdapter.ViewHolder selected = (ScoresAdapter.ViewHolder) view.getTag();
                mAdapter.detail_match_id = selected.match_id;
                MainActivity.selectedMatchId = (int) selected.match_id;

                //TODO : 2.0 mAdapter.notifyDataSetChanged(); remove and see
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
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(getActivity(),
                ScoresContract.ScoreEntry.buildScoreWithDate(),
                null,
                null,
                date,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        //Log.v(FetchScoreTask.LOG_TAG,"loader finished");
        //cursor.moveToFirst();
        /*
        while (!cursor.isAfterLast())
        {
            Log.v(FetchScoreTask.LOG_TAG,cursor.getString(1));
            cursor.moveToNext();
        }
        */

        int i = 0;
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            i++;
            cursor.moveToNext();
        }
        //Log.v(FetchScoreTask.LOG_TAG,"Loader query: " + String.valueOf(i));
        mAdapter.swapCursor(cursor);
        //mAdapter.notifyDataSetChanged();
        updateEmptyView();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mAdapter.swapCursor(null);
    }




    public void setFragmentDate(String date) {
        this.date[0] = date;
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
