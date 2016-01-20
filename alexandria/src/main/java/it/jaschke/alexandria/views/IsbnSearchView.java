package it.jaschke.alexandria.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.SearchView;
import android.util.AttributeSet;
import android.util.Log;

import it.jaschke.alexandria.R;

/**
 * Created by Elorri on 20/01/2016.
 */
public class IsbnSearchView extends SearchView  implements SearchView.OnQueryTextListener{

    private static final int DEFAULT_MINIMUM_LENGTH = 10;
    private static final int DEFAULT_MAXIMUM_LENGTH = 13;

    public int getmMinLength() {
        return mMinLength;
    }

    public void setmMinLength(int mMinLength) {
        this.mMinLength = mMinLength;
        invalidate();
        requestLayout();
    }

    public void setmMaxLength(int mMaxLength) {
        this.mMaxLength = mMaxLength;
        invalidate();
        requestLayout();
    }

    public int getmMaxLength() {
        return mMaxLength;
    }



    private int mMinLength;
    private int mMaxLength;

    public IsbnSearchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.e("SuperDuo", Thread.currentThread().getStackTrace()[2] + "");
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.IsbnSearchViewPreference, 0, 0);
        try {
            mMinLength = typedArray.getInteger(R.styleable.IsbnSearchViewPreference_minLength, DEFAULT_MINIMUM_LENGTH);
            mMaxLength = typedArray.getInteger(R.styleable.IsbnSearchViewPreference_maxLength, DEFAULT_MAXIMUM_LENGTH);
        } finally {
            typedArray.recycle();
        }

        setOnQueryTextListener(this);
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if(newText.length()<=mMinLength){
            Log.e("SuperDuo", Thread.currentThread().getStackTrace()[2] + "Text character is less than 3");
            return false;
        }
        if(newText.length()>mMaxLength){
            Log.e("SuperDuo", Thread.currentThread().getStackTrace()[2] + "Text character is more than 13");
            setQuery(newText.substring(0, mMaxLength+1), false);
            return true;
        }
        return true;
    }
}
