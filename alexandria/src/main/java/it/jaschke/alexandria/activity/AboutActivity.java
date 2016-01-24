package it.jaschke.alexandria.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import it.jaschke.alexandria.R;
import it.jaschke.alexandria.fragment.AboutFragment;

/**
 * Created by Elorri on 19/01/2016.
 */
public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

         if (savedInstanceState == null) {
        AboutFragment fragment = new AboutFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.main_container, fragment)
                .commit();
        }
    }


}
