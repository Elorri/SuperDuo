package it.jaschke.alexandria.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.SearchView;
import android.util.AttributeSet;

/**
 * Created by Elorri on 20/01/2016.
 */
public class IsbnSearchView extends SearchView {

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
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.IsbnSearchViewPreference, 0, 0);
        try {
            mMinLength = typedArray.getInteger(R.styleable.IsbnSearchViewPreference_minLength, DEFAULT_MINIMUM_LENGTH);
            mMaxLength = typedArray.getInteger(R.styleable.IsbnSearchViewPreference_maxLength, DEFAULT_MAXIMUM_LENGTH);
        } finally {
            typedArray.recycle();
        }

        setOnQueryTextListener(new OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.length()<=mMinLength){
                    System.out.println("Text character is more than 5");
                    return false;
                }
                if(newText.length()>mMaxLength){
                    System.out.println("Text character is more than 5");
                    setQuery(newText.substring(0, mMaxLength+1), false);
                    return false;
                }
                return true;
            }
        });
    }


}
