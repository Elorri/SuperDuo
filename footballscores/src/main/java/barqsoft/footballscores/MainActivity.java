package barqsoft.footballscores;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import barqsoft.footballscores.service.FootballService;
import barqsoft.footballscores.sync.ScoresSyncAdapter;

public class MainActivity extends ActionBarActivity {
    private static final String CURRENT_ITEM = "current_item";
    private static final String SELECTED_MATCH_ID = "selectedMatchId";
    private static final String PAGER_FRAGMENT = "fragment_pager";
    private static final String SAVE_TAG = "save_tag";

    private MainFragment mMainFragment;

    //TODO : 2.1 put this private and store it in Preferences
    public static int selectedMatchId;
    public static int currentItem = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e("SuperDuo", Thread.currentThread().getStackTrace()[2] + "");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        syncDB();
        updateWidgets();
//        ScoresSyncAdapter.initializeSyncAdapter(this);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        if (savedInstanceState == null) {
            mMainFragment = new MainFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, mMainFragment)
                    .commit();
        }
    }

    private void updateWidgets() {
        Log.e("SuperDuo", Thread.currentThread().getStackTrace()[2] + "");
        // Setting the package ensures that only components in our app will receive the broadcast
        Intent dataUpdatedIntent =
                new Intent(ScoresSyncAdapter.ACTION_DATA_UPDATED).setPackage(getPackageName());
        sendBroadcast(dataUpdatedIntent);
    }

    //TODO : 2.2 delete old db data
    private void syncDB() {
        Intent serviceStart = new Intent(this, FootballService.class);
        startService(serviceStart);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about) {
            startActivity(new Intent(this, AboutActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //TODO :2.1 mViewPager should be private
        Log.v(SAVE_TAG, "will save");
        Log.v(SAVE_TAG, "fragment: " + String.valueOf(mMainFragment.mViewPager.getCurrentItem()));
        Log.v(SAVE_TAG, "selected id: " + selectedMatchId);
        outState.putInt(CURRENT_ITEM, mMainFragment.mViewPager.getCurrentItem());
        outState.putInt(SELECTED_MATCH_ID, selectedMatchId);
        getSupportFragmentManager().putFragment(outState, PAGER_FRAGMENT, mMainFragment);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.v(SAVE_TAG, "will retrieve");
        Log.v(SAVE_TAG, "fragment: " + String.valueOf(savedInstanceState.getInt(CURRENT_ITEM)));
        Log.v(SAVE_TAG, "selected id: " + savedInstanceState.getInt(SELECTED_MATCH_ID));
        currentItem = savedInstanceState.getInt(CURRENT_ITEM);
        selectedMatchId = savedInstanceState.getInt(SELECTED_MATCH_ID);
        mMainFragment = (MainFragment) getSupportFragmentManager().getFragment(savedInstanceState, PAGER_FRAGMENT);
        super.onRestoreInstanceState(savedInstanceState);
    }
}
