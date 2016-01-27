package barqsoft.footballscores;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by yehya khaled on 2/27/2015.
 */
public class MainFragment extends Fragment {
    public static final int NUM_PAGES = 5;
    private ScoresFragment[] tabs = new ScoresFragment[5];
    public ViewPager mViewPager;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pager, container, false);

        for (int i = 0; i < NUM_PAGES; i++) {
            long dateTime=Utilities.addDay(i-2, Calendar.getInstance().getTimeInMillis());
            tabs[i] = new ScoresFragment();
            tabs[i].setDate(dateTime);
        }

        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
        PageAdapter pageAdapter = new PageAdapter(getChildFragmentManager());
        mViewPager.setAdapter(pageAdapter);
        //TODO : 2.1 use preferences
        mViewPager.setCurrentItem(MainActivity.currentItem);



        PagerTabStrip pagerTabStrip = (PagerTabStrip) view.findViewById(R.id.pager_header);
        pagerTabStrip.setDrawFullUnderline(true);
        pagerTabStrip.setTabIndicatorColor(getResources().getColor(R.color.accent));


        return view;
    }

    private class PageAdapter extends FragmentStatePagerAdapter {

        public PageAdapter(FragmentManager fm) {
            super(fm);
        }


        @Override
        public Fragment getItem(int i) {
            return tabs[i];
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }



        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            return getDayName(getActivity(), Utilities.addDay(position-2, Calendar.getInstance()
                    .getTimeInMillis()));
        }

        public String getDayName(Context context, long dateInMillis) {
            // If the date is today, return the localized version of "Today" instead of the actual
            // day name.

            Time t = new Time();
            t.setToNow();
            int julianDay = Time.getJulianDay(dateInMillis, t.gmtoff);
            int currentJulianDay = Time.getJulianDay(System.currentTimeMillis(), t.gmtoff);
            if (julianDay == currentJulianDay) {
                return context.getString(R.string.today);
            } else if (julianDay == currentJulianDay + 1) {
                return context.getString(R.string.tomorrow);
            } else if (julianDay == currentJulianDay - 1) {
                return context.getString(R.string.yesterday);
            } else {
                Time time = new Time();
                time.setToNow();
                // Otherwise, the format is just the day of the week (e.g "Wednesday",
                // "Mercredi") in the user language
                SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Utilities
                        .getMostSuitableLocale(getContext()));
                return dayFormat.format(dateInMillis);
            }
        }
    }
}
