package barqsoft.footballscores;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import barqsoft.footballscores.about.AboutActivity;
import barqsoft.footballscores.games.GamesScoresFragment;
import barqsoft.footballscores.sync.FootballScoresSyncAdapter;
import barqsoft.footballscores.utils.Constants;

public class MainActivity extends AppCompatActivity {
    public static int selected_match_id;
    public static int current_fragment = 2;
    public static String LOG_TAG = "MainActivity";
    private final String save_tag = "Save Test";
    public static final int NUM_PAGES = 5;
    public ViewPager mPagerHandler;
    private GameScoreFragmentPagerAdapter mPagerAdapter;
    private GamesScoresFragment[] viewFragments = new GamesScoresFragment[5];

    List<updateContentListener> updateContentListenerList = new ArrayList<>();
    private BroadcastReceiver syncFinishedReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            //notify fragments to update
            for(updateContentListener listener : updateContentListenerList) {

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(LOG_TAG, "Reached MainActivity onCreate");
        FootballScoresSyncAdapter.initializeSyncAdapter(this);

        mPagerHandler = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new GameScoreFragmentPagerAdapter(getApplicationContext(), getSupportFragmentManager());
        mPagerHandler.setOffscreenPageLimit(2);
        mPagerHandler.setAdapter(mPagerAdapter);
        /**
         * Seems to be an issue with the PagerTabStrip ref: https://code.google.com/p/android/issues/detail?id=183127
         */
        mPagerHandler.setCurrentItem(1);
        mPagerHandler.setCurrentItem(0);
        //set to what we really want the item to be.
        mPagerHandler.setCurrentItem(MainActivity.current_fragment);
    }
    @Override
    public void onResume()
    {
        super.onResume();
        registerReceiver(syncFinishedReceiver, new IntentFilter(Constants.SYNC_FINISHED));
    }



    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if(syncFinishedReceiver != null) {
            unregisterReceiver(syncFinishedReceiver);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about) {
            Intent start_about = new Intent(this, AboutActivity.class);
            startActivity(start_about);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        Log.v(save_tag, "will save");
        Log.v(save_tag, "fragment: " + String.valueOf(mPagerHandler.getCurrentItem()));
        Log.v(save_tag, "selected id: " + selected_match_id);
        outState.putInt("Pager_Current", mPagerHandler.getCurrentItem());
        outState.putInt("Selected_match", selected_match_id);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        //todo not always called.
        Log.v(save_tag, "will retrive");
        Log.v(save_tag, "fragment: " + String.valueOf(savedInstanceState.getInt("Pager_Current")));
        Log.v(save_tag, "selected id: " + savedInstanceState.getInt("Selected_match"));
        current_fragment = savedInstanceState.getInt("Pager_Current");
        selected_match_id = savedInstanceState.getInt("Selected_match");
        super.onRestoreInstanceState(savedInstanceState);
    }



    public void addListener(updateContentListener listener) {
        updateContentListenerList.add(listener);
    }

    private interface updateContentListener {
        void updateContent();
    }
}
