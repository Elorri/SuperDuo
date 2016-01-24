package it.jaschke.alexandria.activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import it.jaschke.alexandria.R;
import it.jaschke.alexandria.data.BookContract;
import it.jaschke.alexandria.extras.Tools;
import it.jaschke.alexandria.fragment.AddFragment;
import it.jaschke.alexandria.fragment.DetailFragment;
import it.jaschke.alexandria.fragment.ListFragment;
import it.jaschke.alexandria.fragment.MainFragment;


public class MainActivity extends BaseActivity {


    //TODO: 2.1 make sure all screens have a title

    public static boolean mTwoPane;
    private Uri mUri;
    static final String URI = "mUri";
    private MainFragment mMainFragment;
    public static final String DETAILFRAGMENT_TAG = "detail_fragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        if (savedInstanceState == null)
            mUri = buildMainPageUri();
        else
            mUri = savedInstanceState.getParcelable(URI);

        mMainFragment = getFragment(mUri);
        mMainFragment.setUri(mUri);
        getSupportFragmentManager().beginTransaction().replace(
                R.id.main_container,  mMainFragment).commit();

        if (findViewById(R.id.detail_fragment_container) != null) {
            mTwoPane = true;
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction().replace(
                        R.id.detail_fragment_container,
                        new DetailFragment(), DETAILFRAGMENT_TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
        }

    }

    private MainFragment getFragment(Uri uri) {
        if (getPageFromUri(uri).equals(getString(R.string.pref_start_page_list)))
            return new ListFragment();
        else
            return new AddFragment();
    }


    public Uri buildMainPageUri() {
        return BookContract.BASE_CONTENT_URI.buildUpon()
                .appendPath(Tools.getMainPagePreferences(this))
                .build();
    }

    public static String getPageFromUri(Uri uri) {
        return uri.getPathSegments().get(0);
    }



    @Override
    protected void onResume() {
        super.onResume();
        Uri currentMainUri = buildMainPageUri();
        if (!Tools.compareUris(mUri, currentMainUri)) {
            onMainUriChange(currentMainUri);
        }
    }

    private void onMainUriChange(Uri newUri) {
        mUri = newUri;
        mMainFragment = getFragment(mUri);
        mMainFragment.setUri(mUri);
        getSupportFragmentManager().beginTransaction().replace(
                R.id.main_container,  mMainFragment).commit();

        DetailFragment detailFragment = (DetailFragment) getSupportFragmentManager().findFragmentByTag(DETAILFRAGMENT_TAG);
        if (null != detailFragment) {
            //TODO : 2.1 see what to do here
            //detailFragment.onMainUriChange();
        }
    }





    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(URI, mUri);
    }

}