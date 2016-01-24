package it.jaschke.alexandria.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import it.jaschke.alexandria.R;
import it.jaschke.alexandria.extras.Tools;

public class MainActivity extends AppCompatActivity {

    boolean isAddFirstScreen;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isAddFirstScreen = (Tools.getMainPagePreferences(this))
                .equals(getResources().getString(R.string.pref_start_page_add));

        finish();
        if (isAddFirstScreen) {
            startActivity(new Intent(this, AddActivity.class));
        } else {
            startActivity(new Intent(this, ListActivity.class));
        }

    }
}