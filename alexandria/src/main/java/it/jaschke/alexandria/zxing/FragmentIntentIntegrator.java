package it.jaschke.alexandria.zxing;

import android.content.Intent;
import android.support.v4.app.Fragment;

/**
 * Created by Elorri on 15/01/2016.
 */
public final class FragmentIntentIntegrator extends IntentIntegrator {

    private final Fragment fragment;

    public FragmentIntentIntegrator(Fragment fragment) {
        super(fragment.getActivity());
        this.fragment = fragment;
    }

    @Override
    protected void startActivityForResult(Intent intent, int code) {
        fragment.startActivityForResult(intent, code);
    }
}