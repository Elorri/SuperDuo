package barqsoft.footballscores;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import barqsoft.footballscores.data.ScoresContract;
import barqsoft.footballscores.service.FootballService;

/**
 * A placeholder fragment containing a simple view.
 */
public class ScoresFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public ScoresAdapter mAdapter;
    public static final int SCORES_LOADER = 0;
    private String[] date = new String[1];
    private int last_selected_item = -1;

    public ScoresFragment() {
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        updateScores();
        View view = inflater.inflate(R.layout.fragment_scores, container, false);
        final ListView scoreList = (ListView) view.findViewById(R.id.scores_list);
        mAdapter = new ScoresAdapter(getActivity(), null, 0);
        scoreList.setAdapter(mAdapter);



        mAdapter.detail_match_id = MainActivity.selectedMatchId;
        scoreList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mAdapter.swapCursor(null);
    }



    private void updateScores() {
        Intent serviceStart = new Intent(getActivity(), FootballService.class);
        getActivity().startService(serviceStart);
    }

    public void setFragmentDate(String date) {
        this.date[0] = date;
    }

}