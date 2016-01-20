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
import java.util.Date;

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
            //TODO : 2.3 should be converted in julianday dig into the below
//            Time dayTime = new Time();
//            dayTime.setToNow();
//            // we start at the day returned by local time. Otherwise this is a mess.
//            int julianStartDay = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);  //gmtoff should be called utcoff it's the numbers of offset to reach //universal time = utc time
//            // now we work exclusively in UTC
//            dayTime = new Time();
//            // Cheating to convert this to UTC time, which is what we want anyhow
//            long dateTime = dayTime.setJulianDay(julianStartDay + i); // i is the number of days 0 for today, 1 for tomorrow
//            long todayInMillisUTC = dayTime.setJulianDay(julianStartDay + 0); // today in millis
//            long tomorrowInMillisUTC = dayTime.setJulianDay(julianStartDay + 1); // tomorrow in millis
//            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_DATE, dateTime);


//            //This works too
//            Time t = new Time();
//            t.setToNow();
//            int julianDay = Time.getJulianDay(dateInMillis, t.gmtoff);
//            int currentJulianDay = Time.getJulianDay(System.currentTimeMillis(), t.gmtoff);

            //TODO : 2.3 format with locale
            Date date = new Date(System.currentTimeMillis() + ((i - 2) * 86400000));
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String dateString=dateFormat.format(date);
            tabs[i] = new ScoresFragment();
            //TODO :2.1 check by rotation screen or use getArguments or ask on forums
            tabs[i].setDate(dateString);
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
            //TODO : 2.3 use your localisation function
            return getDayName(getActivity(), System.currentTimeMillis() + ((position - 2) * 86400000));
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
                // Otherwise, the format is just the day of the week (e.g "Wednesday".
                SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE");
                return dayFormat.format(dateInMillis);
            }
        }
    }
}
