package it.jaschke.alexandria.controller.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import it.jaschke.alexandria.R;
import it.jaschke.alexandria.controller.fragment.AddFragment;

/**
 * Created by Elorri on 18/01/2016.
 */
public class AddActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            AddFragment fragment = new AddFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.add_fragment_container, fragment)
                    .commit();
        }
    }


}
