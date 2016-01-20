package it.jaschke.alexandria.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import it.jaschke.alexandria.R;
import it.jaschke.alexandria.controller.fragment.DetailFragment;
import it.jaschke.alexandria.fragment.ListFragment;

/**
 * Created by Elorri on 19/01/2016.
 */
public class BaseActivity extends AppCompatActivity implements ListFragment.Callback{

    @Override
    public void onItemSelected(Uri uri) {
        if (MainActivity.mTwoPane) {
            Bundle arguments = new Bundle();
            arguments.putParcelable(DetailFragment.URI, uri);

            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_fragment_container, fragment,
                            MainActivity.DETAILFRAGMENT_TAG)
                    .commit();
        } else {
            Intent intent = new Intent(this, DetailActivity.class);
            intent.setData(uri);
            startActivity(intent);
        }
    }
}
