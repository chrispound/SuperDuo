package barqsoft.footballscores.service;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.squareup.okhttp.OkHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.Vector;

import barqsoft.footballscores.BuildConfig;
import barqsoft.footballscores.R;
import barqsoft.footballscores.data.DatabaseContract;
import barqsoft.footballscores.endpoints.FootballApiEndpoints;
import barqsoft.footballscores.models.FootballLeagueGames;
import barqsoft.footballscores.models.Match;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by yehya khaled on 3/2/2015.
 */
public class GameDataService extends IntentService {
    public static final String LOG_TAG = GameDataService.class.getSimpleName();

    public GameDataService()
    {
        super("GameDataService");
    }
    //Creating fetch URL
    private static final String BASE_URL = "http://api.football-data.org/alpha/"; //Base URL
    private static final String QUERY_TIME_FRAME = "timeFrame"; //Time Frame parameter to determine days
    Gson mGson = new GsonBuilder()
        .create();

    //final String QUERY_MATCH_DAY = "matchday";

    @Override
    protected void onHandleIntent(Intent intent)
    {
        //todo check connection.
        getData("n2");
        getData("p2");
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
                int statusCode = response.code();
                FootballLeagueGames footballLeagueGame = response.body();
                if(footballLeagueGame == null)
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
            processJSONdata(getString(R.string.dummy_data), false);
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
                //todo don't need all this type conversion
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


                values.add(match_values);

            }

            ContentValues[] insert_data = new ContentValues[values.size()];
            values.toArray(insert_data);
            int inserted_data = getApplicationContext().getContentResolver().bulkInsert(
                DatabaseContract.BASE_CONTENT_URI, insert_data);

            Log.d(LOG_TAG, "Succesfully Inserted : " + String.valueOf(inserted_data));
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
        }
    }

    private void processJSONdata(String JSONdata,  boolean isReal)
    {
        //JSON data
        FootballLeagueGames games = mGson.fromJson(JSONdata, FootballLeagueGames.class);
        if (games.matches.size() == 0 && BuildConfig.DEBUG) {
            //if there is no data AND we're in debug mode, call the function on dummy data
            //this is expected behavior during the off season.
            processJSONdata(getString(R.string.dummy_data), false);
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
                //todo don't need all this type conversion
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
            int inserted_data = getApplicationContext().getContentResolver().bulkInsert(
                DatabaseContract.BASE_CONTENT_URI, insert_data);

            Log.d(LOG_TAG, "Succesfully Inserted : " + String.valueOf(inserted_data));
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
        }

    }
}

