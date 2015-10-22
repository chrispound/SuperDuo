package barqsoft.footballscores.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import barqsoft.footballscores.R;
import barqsoft.footballscores.data.DatabaseContract;
import barqsoft.footballscores.models.Match;
import barqsoft.footballscores.utils.Utilities;

public class FootballWidgetListService extends RemoteViewsService {


    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent)
    {
        return new FootballWidgetViewsFactory(this.getApplicationContext(), intent);
    }

    class FootballWidgetViewsFactory implements RemoteViewsService.RemoteViewsFactory {

        private final Context mContext;
        private final int mAppWidgetId;
        private ArrayList<Match> mMatches = new ArrayList<>();
        private boolean isEmptyView;

        public FootballWidgetViewsFactory(Context context, Intent intent) {
            mContext = context;
            mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);


        }

        @Override
        public void onCreate()
        {

            mMatches = new ArrayList<>();
            Date fragmentdate = new Date(System.currentTimeMillis());
            SimpleDateFormat mformat = new SimpleDateFormat("yyyy-MM-dd");
            String[] args = new String[1];
            args[0] = mformat.format(fragmentdate);
            Cursor cursor = mContext.getContentResolver().query(DatabaseContract.scores_table.buildScoreWithDate(),
                null, null, args, null);
            if(cursor.moveToFirst()) {
                do {
                    String homeName = cursor.getString(DatabaseContract.COL_HOME);
                    String awayName = cursor.getString(DatabaseContract.COL_AWAY);
                    String matchTime= cursor.getString(DatabaseContract.COL_MATCHTIME);
                    Match match = new Match();
                    match.homeTeamName = homeName;
                    match.awayTeamName = awayName;
                    match.date = matchTime;
                    match.result = new Match.MatchResult();
                    match.result.goalsAwayTeam = cursor.getInt(DatabaseContract.COL_AWAY_GOALS);
                    match.result.goalsHomeTeam = cursor.getInt(DatabaseContract.COL_HOME_GOALS);
                    mMatches.add(match);

                } while (cursor.moveToNext());
            }

            Log.d("Widget Service", "Added Items: " + mMatches.size());

        }

        @Override
        public void onDataSetChanged()
        {

        }

        @Override
        public void onDestroy()
        {
            mMatches.clear();
        }

        @Override
        public int getCount()
        {
            if (mMatches.size() == 0) {
                isEmptyView = true;
                return 1;
            }
            Log.d("widget service", "getCount: " + mMatches.size());
            return mMatches.size();
        }

        @Override
        public RemoteViews getViewAt(int position)
        {
            if (isEmptyView) {
                RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.empty_widget);
                return rv;
            }
            RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.widget_game_item);
            Match match = mMatches.get(position);
            rv.setTextViewText(R.id.home_name, match.homeTeamName);
            rv.setTextViewText(R.id.away_name, match.awayTeamName);
            rv.setTextViewText(R.id.score_textview, Utilities.getScores(match.result.goalsHomeTeam, match.result.goalsAwayTeam));
            rv.setImageViewResource(R.id.home_crest, Utilities.getTeamCrestByTeamName(match.homeTeamName));
            rv.setImageViewResource(R.id.away_crest, Utilities.getTeamCrestByTeamName(match.awayTeamName));
            return rv;
        }

        @Override
        public RemoteViews getLoadingView()
        {
            return null;
        }

        @Override
        public int getViewTypeCount()
        {
            return 1;
        }

        @Override
        public long getItemId(int position)
        {
            return position;
        }

        @Override
        public boolean hasStableIds()
        {
            return true;
        }
    }
}
