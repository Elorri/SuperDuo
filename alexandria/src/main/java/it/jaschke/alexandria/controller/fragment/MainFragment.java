package it.jaschke.alexandria.controller.fragment;

import android.net.Uri;
import android.support.v4.app.Fragment;

/**
 * Created by Elorri on 18/01/2016.
 */
public class MainFragment extends Fragment {

    protected static final String URI = "uri";
    protected Uri mUri;

    public void setUri(Uri uri) {
        this.mUri = uri;
    }
}
