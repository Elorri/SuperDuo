package it.jaschke.alexandria.controller.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import it.jaschke.alexandria.R;
import it.jaschke.alexandria.controller.fragment.ListFragment;
import it.jaschke.alexandria.model.data.BookContract;

/**
 * Created by Elorri on 19/01/2016.
 */
public class ListActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        if (savedInstanceState == null) {
            ListFragment fragment = new ListFragment();
            fragment.setUri(BookContract.BookEntry.CONTENT_URI);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.add_fragment_container, fragment)
                    .commit();
        }
    }


}