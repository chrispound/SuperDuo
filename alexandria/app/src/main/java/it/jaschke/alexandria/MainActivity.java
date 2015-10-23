package it.jaschke.alexandria;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.List;

import it.jaschke.alexandria.api.Callback;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, Callback {

    private CharSequence title;
    public static boolean IS_TABLET = false;
    private BroadcastReceiver messageReciever;

    public static final String MESSAGE_EVENT = "MESSAGE_EVENT";
    public static final String MESSAGE_KEY = "MESSAGE_EXTRA";
    private static final String BOOKS = "books";
    private static final String SCAN = "scan";
    private static final String ABOUT = "about";
    private Toolbar mToolbar;
    private NavigationView mNavigationView;
    private DrawerLayout mDrawer;
    public ActionBarDrawerToggle mToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        IS_TABLET = isTablet();
        if (IS_TABLET) {
            setContentView(R.layout.activity_main_tablet);
        } else {
            setContentView(R.layout.activity_main);
        }

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        messageReciever = new MessageReciever();
        IntentFilter filter = new IntentFilter(MESSAGE_EVENT);
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReciever, filter);

        title = getTitle();

        // Set up the drawer.
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
       mToggle = new ActionBarDrawerToggle(
            this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.setDrawerListener(mToggle);
        mToggle.syncState();

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, new ListOfBooks())
                .addToBackStack(BOOKS)
                .commit();
            mNavigationView.getMenu().getItem(0).setChecked(true);
            setTitle(R.string.books);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);
        mToggle.syncState();
    }

    public void setTitle(int titleId)
    {
        title = getString(titleId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        if (!mDrawer.isDrawerOpen(mNavigationView)) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy()
    {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReciever);
        super.onDestroy();
    }

    @Override
    public void onItemSelected(String ean)
    {
        Bundle args = new Bundle();
        args.putString(BookDetail.EAN_KEY, ean);

        BookDetail fragment = new BookDetail();
        fragment.setArguments(args);

        int id = R.id.container;
        if (findViewById(R.id.right_container) != null) {
            id = R.id.right_container;
        }
        getSupportFragmentManager().beginTransaction()
            .replace(id, fragment)
            .addToBackStack("Book Detail")
            .commit();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment nextFragment;
        int selected = item.getItemId();
        switch (selected) {
            case R.id.nav_books:
                nextFragment = new ListOfBooks();
                title = BOOKS;
                break;
            case R.id.nav_scan:
                nextFragment = new AddBook();
                title = SCAN;
                break;
            case R.id.nav_about:
                nextFragment = new About();
                title = ABOUT;
                break;
            default:
                nextFragment = new ListOfBooks();
                title = BOOKS;
                break;
        }

        fragmentManager.beginTransaction()
            .replace(R.id.container, nextFragment)
            .addToBackStack((String) title)
            .commit();
        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void addBook()
    {
        mNavigationView.getMenu().getItem(1).setChecked(true);
        FragmentManager fragmentManager = getSupportFragmentManager();

        fragmentManager.beginTransaction()
            .replace(R.id.container, new AddBook())
            .addToBackStack((String) title)
            .commit();
    }

    private class MessageReciever extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            if (intent.getStringExtra(MESSAGE_KEY) != null) {
                Toast.makeText(MainActivity.this, intent.getStringExtra(MESSAGE_KEY), Toast.LENGTH_LONG).show();
            }
        }
    }

    public void goBack(View view)
    {
        getSupportFragmentManager().popBackStack();
    }

    private boolean isTablet()
    {
        return (getApplicationContext().getResources().getConfiguration().screenLayout
            & Configuration.SCREENLAYOUT_SIZE_MASK)
            >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    @Override
    public void onBackPressed()
    {
        int count = getSupportFragmentManager().getBackStackEntryCount();
        if(count >1) {
            FragmentManager.BackStackEntry entry = getSupportFragmentManager()
                .getBackStackEntryAt(getSupportFragmentManager()
                    .getBackStackEntryCount() - 2);
            String name = entry.getName();
            switch (name) {
                case BOOKS:
                    mNavigationView.getMenu().getItem(0).setChecked(true);
                    break;
                case SCAN:
                    mNavigationView.getMenu().getItem(1).setChecked(true);
                    break;
                case ABOUT:
                    mNavigationView.getMenu().getItem(2).setChecked(true);
                    break;
            }

        }
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        }

        if (getSupportFragmentManager().getBackStackEntryCount() < 0) {
            finish();
        }
        super.onBackPressed();
    }


}