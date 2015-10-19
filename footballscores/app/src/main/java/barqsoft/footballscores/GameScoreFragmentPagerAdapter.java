package barqsoft.footballscores;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.format.Time;

import java.text.SimpleDateFormat;
import java.util.Date;

import barqsoft.footballscores.games.GamesScoresFragment;

public class GameScoreFragmentPagerAdapter extends FragmentStatePagerAdapter
{
    public static final int NUM_PAGES = 5;
    private final Context mContext;
    private GamesScoresFragment[] viewFragments = new GamesScoresFragment[NUM_PAGES];

    public GameScoreFragmentPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
        for (int i = 0;i < NUM_PAGES;i++)
        {
            Date fragmentdate = new Date(System.currentTimeMillis()+((i-2)*86400000));
            SimpleDateFormat mformat = new SimpleDateFormat("yyyy-MM-dd");
            viewFragments[i] = new GamesScoresFragment();
            viewFragments[i].setFragmentDate(mformat.format(fragmentdate));
        }
    }

    @Override
    public Fragment getItem(int i)
    {
        return viewFragments[i];
    }

    @Override
    public int getCount()
    {
        return NUM_PAGES;
    }

    // Returns the page title for the top indicator
    @Override
    public CharSequence getPageTitle(int position)
    {
        return getDayName(mContext,System.currentTimeMillis()+((position-2)*86400000));
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
        } else if ( julianDay == currentJulianDay +1 ) {
            return context.getString(R.string.tomorrow);
        }
        else if ( julianDay == currentJulianDay -1)
        {
            return context.getString(R.string.yesterday);
        }
        else
        {
            Time time = new Time();
            time.setToNow();
            // Otherwise, the format is just the day of the week (e.g "Wednesday".
            SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE");
            return dayFormat.format(dateInMillis);
        }
    }
}