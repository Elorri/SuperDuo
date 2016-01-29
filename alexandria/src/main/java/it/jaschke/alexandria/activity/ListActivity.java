/**
 * The MIT License (MIT)

 Copyright (c) 2015 ETCHEMENDY ELORRI

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
package it.jaschke.alexandria.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import it.jaschke.alexandria.R;
import it.jaschke.alexandria.fragment.DetailFragment;
import it.jaschke.alexandria.fragment.ListFragment;

/**
 * This activity launch the screen with the 'Books list' section of the app
 * Created by Elorri on 19/01/2016.
 */
public class ListActivity extends AppCompatActivity implements ListFragment.Callback {

    public static final String DETAILFRAGMENT_TAG = "detail_fragment";
    private boolean mTwoPane = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (MainActivity.isAddFirstScreen)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            ListFragment listFragment = new ListFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.main_container, listFragment)
                    .commit();
        }

        if (findViewById(R.id.detail_fragment_container) != null) {
            mTwoPane = true;
            if (savedInstanceState != null) {
                Fragment detailFragment = getSupportFragmentManager().findFragmentByTag
                        (DETAILFRAGMENT_TAG);
                if (detailFragment == null) {
                    getSupportFragmentManager().beginTransaction().replace(
                            R.id.detail_fragment_container,
                            new DetailFragment(), DETAILFRAGMENT_TAG)
                            .commit();
                }
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (null != menu) menu.clear();
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        if (id == R.id.action_about) {
            startActivity(new Intent(this, AboutActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(Uri uri) {
        if (mTwoPane) {
            Bundle arguments = new Bundle();
            arguments.putParcelable(DetailFragment.URI, uri);

            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_fragment_container, fragment,
                            DETAILFRAGMENT_TAG)
                    .commit();
        } else {
            Intent intent = new Intent(this, DetailActivity.class);
            intent.setData(uri);
            startActivity(intent);
        }
    }
}