package barqsoft.footballscores.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.Vector;

import barqsoft.footballscores.BuildConfig;
import barqsoft.footballscores.MainActivity;
import barqsoft.footballscores.R;
import barqsoft.footballscores.data.DatabaseContract;
import barqsoft.footballscores.endpoints.FootballApiEndpoints;
import barqsoft.footballscores.models.FootballLeagueGames;
import barqsoft.footballscores.models.Match;
import barqsoft.footballscores.utils.Constants;
import barqsoft.footballscores.utils.Utilities;
import barqsoft.footballscores.widget.FootballScoresWidgetProvider;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class FootballScoresSyncAdapter extends AbstractThreadedSyncAdapter {

    private static final String BASE_URL = "http://api.football-data.org/alpha/";
    public static final String LOG_TAG = GameDataService.class.getSimpleName();

    Gson mGson = new GsonBuilder()
        .create();

    private final ContentResolver mContentResolver;

    public FootballScoresSyncAdapter(Context context, boolean autoInitialize)
    {
        super(context, autoInitialize);
        mContentResolver = context.getContentResolver();
    }

    @Override
    public void onPerformSync(Account account, Bundle bundle, String s, ContentProviderClient contentProviderClient, SyncResult syncResult)
    {
        if(Utilities.isNetworkConnected(getContext())) {
            getData("n2");
            getData("p2");
        }
    }

    private void getData(String timeFrame)
    {

        Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(mGson))
            .build();
        FootballApiEndpoints apiService = retrofit.create(FootballApiEndpoints.class);
        Call<FootballLeagueGames> call = apiService.getMatches(timeFrame);
        call.enqueue(new Callback<FootballLeagueGames>() {

            @Override
            public void onResponse(Response<FootballLeagueGames> response, Retrofit retrofit)
            {
                //todo determine if data is new/different w/hash
                int statusCode = response.code();
                FootballLeagueGames footballLeagueGame = response.body();
                if (footballLeagueGame == null)
                    return;
                saveDataAndUpdateUi(footballLeagueGame, true);
            }

            @Override
            public void onFailure(Throwable t)
            {
                // Log error here since request failed\
                //todo
                Log.e(LOG_TAG, t.toString());
            }
        });
    }

    private void saveDataAndUpdateUi(FootballLeagueGames games, boolean isReal) {
        if (games.matches.size() == 0 && BuildConfig.DEBUG) {
            //if there is no data AND we're in debug mode, call the function on dummy data
            //this is expected behavior during the off season.
            processJSONdata(getContext().getString(R.string.dummy_data), false);
            return;
        }

        try {

            //ContentValues to be inserted
            Vector<ContentValues> values = new Vector<>(games.matches.size());
            for (Match match : games.matches) {

                String leagueId = match.getLeagueId();
                //This if statement controls which leagues we're interested in the data from.
                //add leagues here in order to have them be added to the DB.
                // If you are finding no data in the app, check that this contains all the leagues.
                // If it doesn't, that can cause an empty DB, bypassing the dummy data routine.
                String matchId = match.getMatchId();
                if (!isReal) {
                    //This if statement changes the match ID of the dummy data so that it all goes into the database
//                    match_id = match_id + Integer.toString(i);
                }

                String mDate = match.date;
                String mTime = mDate.substring(mDate.indexOf("T") + 1, mDate.indexOf("Z"));
                mDate = mDate.substring(0, mDate.indexOf("T"));
                SimpleDateFormat match_date = new SimpleDateFormat("yyyy-MM-ddHH:mm:ss");
                match_date.setTimeZone(TimeZone.getTimeZone("UTC"));
                try {
                    Date parseddate = match_date.parse(mDate + mTime);
                    SimpleDateFormat new_date = new SimpleDateFormat("yyyy-MM-dd:HH:mm");
                    new_date.setTimeZone(TimeZone.getDefault());
                    mDate = new_date.format(parseddate);
                    mTime = mDate.substring(mDate.indexOf(":") + 1);
                    mDate = mDate.substring(0, mDate.indexOf(":"));

                    if (!isReal) {
                        //This if statement changes the dummy data's date to match our current date range.
                        Date fragmentdate = new Date(System.currentTimeMillis() + ( 86400000));
                        SimpleDateFormat mformat = new SimpleDateFormat("yyyy-MM-dd");
                        mDate = mformat.format(fragmentdate);
                    }
                } catch (Exception e) {
                    Log.d(LOG_TAG, "error here!");
                    Log.e(LOG_TAG, e.getMessage());
                }
                String homeTeamName = match.homeTeamName;
                String awayTeamName = match.awayTeamName;
                String goalsHomeTeam = String.valueOf(match.result.goalsHomeTeam);
                String goalsAwayTeam = String.valueOf(match.result.goalsAwayTeam);
                String matchDay = String.valueOf(match.matchday);
                ContentValues match_values = new ContentValues();
                match_values.put(DatabaseContract.scores_table.MATCH_ID, matchId);
                match_values.put(DatabaseContract.scores_table.DATE_COL, mDate);
                match_values.put(DatabaseContract.scores_table.TIME_COL, mTime);
                match_values.put(DatabaseContract.scores_table.HOME_COL, homeTeamName);
                match_values.put(DatabaseContract.scores_table.AWAY_COL, awayTeamName);
                match_values.put(DatabaseContract.scores_table.HOME_GOALS_COL, goalsHomeTeam);
                match_values.put(DatabaseContract.scores_table.AWAY_GOALS_COL, goalsAwayTeam);
                match_values.put(DatabaseContract.scores_table.LEAGUE_COL, leagueId);
                match_values.put(DatabaseContract.scores_table.MATCH_DAY, matchDay);
                values.add(match_values);
            }

            ContentValues[] insert_data = new ContentValues[values.size()];
            values.toArray(insert_data);
            int inserted_data = getContext().getContentResolver().bulkInsert(
                DatabaseContract.BASE_CONTENT_URI, insert_data);

            //send notification
            //notify widget
            Intent i = new Intent(getContext(),FootballScoresWidgetProvider.class);
            i.setAction(Constants.SYNC_FINISHED);
            getContext().sendBroadcast(i);
            Intent activity = new Intent(Constants.SYNC_FINISHED);
            getContext().sendBroadcast(i);
            Log.d(LOG_TAG, "Succesfully Inserted : " + String.valueOf(inserted_data));
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
        }
    }

    /**
     * For testing purposes only.
     * @param JSONdata
     * @param isReal
     */
    private void processJSONdata(String JSONdata,  boolean isReal)
    {
        //JSON data
        FootballLeagueGames games = mGson.fromJson(JSONdata, FootballLeagueGames.class);
        if (games.matches.size() == 0 && BuildConfig.DEBUG) {
            //if there is no data AND we're in debug mode, call the function on dummy data
            //this is expected behavior during the off season.
            processJSONdata(getContext().getString(R.string.dummy_data), false);
            return;
        }

        try {

            //ContentValues to be inserted
            Vector<ContentValues> values = new Vector<>(games.matches.size());
            for (Match match : games.matches) {

                String leagueId = match.getLeagueId();
                //This if statement controls which leagues we
                //This if statement controls which leagues we're interested in the data from.
                //add leagues here in order to have them be added to the DB.
                // If you are finding no data in the app, check that this contains all the leagues.
                // If it doesn't, that can cause an empty DB, bypassing the dummy data routine.
                String matchId = match.getMatchId();
                if (!isReal) {
                    //This if statement changes the match ID of the dummy data so that it all goes into the database
//                    match_id = match_id + Integer.toString(i);
                }

                String mDate = match.date;
                String mTime = mDate.substring(mDate.indexOf("T") + 1, mDate.indexOf("Z"));
                mDate = mDate.substring(0, mDate.indexOf("T"));
                SimpleDateFormat match_date = new SimpleDateFormat("yyyy-MM-ddHH:mm:ss");
                match_date.setTimeZone(TimeZone.getTimeZone("UTC"));
                try {
                    Date parseddate = match_date.parse(mDate + mTime);
                    SimpleDateFormat new_date = new SimpleDateFormat("yyyy-MM-dd:HH:mm");
                    new_date.setTimeZone(TimeZone.getDefault());
                    mDate = new_date.format(parseddate);
                    mTime = mDate.substring(mDate.indexOf(":") + 1);
                    mDate = mDate.substring(0, mDate.indexOf(":"));

                    if (!isReal) {
                        //This if statement changes the dummy data's date to match our current date range.
//                        Date fragmentdate = new Date(System.currentTimeMillis() + ((i - 2) * 86400000));
//                        SimpleDateFormat mformat = new SimpleDateFormat("yyyy-MM-dd");
//                        mDate = mformat.format(fragmentdate);
                    }
                } catch (Exception e) {
                    Log.d(LOG_TAG, "error here!");
                    Log.e(LOG_TAG, e.getMessage());
                }
                String homeTeamName = match.homeTeamName;
                String awayTeamName = match.awayTeamName;
                String goalsHomeTeam = String.valueOf(match.result.goalsHomeTeam);
                String goalsAwayTeam = String.valueOf(match.result.goalsAwayTeam);
                String matchDay = String.valueOf(match.matchday);
                ContentValues match_values = new ContentValues();
                match_values.put(DatabaseContract.scores_table.MATCH_ID, matchId);
                match_values.put(DatabaseContract.scores_table.DATE_COL, mDate);
                match_values.put(DatabaseContract.scores_table.TIME_COL, mTime);
                match_values.put(DatabaseContract.scores_table.HOME_COL, homeTeamName);
                match_values.put(DatabaseContract.scores_table.AWAY_COL, awayTeamName);
                match_values.put(DatabaseContract.scores_table.HOME_GOALS_COL, goalsHomeTeam);
                match_values.put(DatabaseContract.scores_table.AWAY_GOALS_COL, goalsAwayTeam);
                match_values.put(DatabaseContract.scores_table.LEAGUE_COL, leagueId);
                match_values.put(DatabaseContract.scores_table.MATCH_DAY, matchDay);
                //log spam

                Log.d(LOG_TAG, matchId);
                Log.d(LOG_TAG, mDate);
                Log.d(LOG_TAG, mTime);
                Log.d(LOG_TAG, homeTeamName);
                Log.d(LOG_TAG, awayTeamName);
                Log.d(LOG_TAG, goalsHomeTeam);
                Log.d(LOG_TAG, goalsAwayTeam);

                values.add(match_values);

            }

            ContentValues[] insert_data = new ContentValues[values.size()];
            values.toArray(insert_data);
            int inserted_data = getContext().getContentResolver().bulkInsert(
                DatabaseContract.BASE_CONTENT_URI, insert_data);

            Log.d(LOG_TAG, "Succesfully Inserted : " + String.valueOf(inserted_data));
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
        }
    }

    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, long syncInterval, long flexTime)
    {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                syncPeriodic(syncInterval, flexTime).
                setSyncAdapter(account, authority).
                setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                authority, new Bundle(), syncInterval);
        }
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
            (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
            context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        FootballScoresSyncAdapter.configurePeriodicSync(context, Constants.SYNC_INTERVAL, Constants.SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }

    /**
     * Helper method to have the sync adapter sync immediately
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
            context.getString(R.string.content_authority), bundle);
    }

    public static void initializeSyncAdapter(Context context)
    {
        getSyncAccount(context);
    }
}
