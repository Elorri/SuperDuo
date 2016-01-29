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

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import barqsoft.footballscores.sync.ScoresSyncAdapter;

/**
 * This activity is the one launched by the launcher it isresponsible for creating the tabs and
 * their corresponding fragments.
 */
public class MainActivity extends ActionBarActivity {
    private static final String CURRENT_ITEM = "current_item";
    private static final String SELECTED_MATCH_ID = "selectedMatchId";
    private static final String PAGER_FRAGMENT = "fragment_pager";

    public static int currentItem = 2;
    public static int selectedMatchId;
    private MainFragment mMainFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ScoresSyncAdapter.initializeSyncAdapter(this);


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
        outState.putInt(CURRENT_ITEM, mMainFragment.mViewPager.getCurrentItem());
        outState.putInt(SELECTED_MATCH_ID, selectedMatchId);
        getSupportFragmentManager().putFragment(outState, PAGER_FRAGMENT, mMainFragment);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        currentItem = savedInstanceState.getInt(CURRENT_ITEM);
        selectedMatchId = savedInstanceState.getInt(SELECTED_MATCH_ID);
        mMainFragment = (MainFragment) getSupportFragmentManager().getFragment(savedInstanceState, PAGER_FRAGMENT);
        super.onRestoreInstanceState(savedInstanceState);
    }
}
