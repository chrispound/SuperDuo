package barqsoft.footballscores.data;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import barqsoft.footballscores.R;
import barqsoft.footballscores.games.GameScoreViewHolder;
import barqsoft.footballscores.utils.Utilities;

/**
 * Created by yehya khaled on 2/26/2015.
 */
public class GameScoreCursorAdapter extends CursorAdapter {

    public double detail_match_id = 0;
    private String FOOTBALL_SCORES_HASHTAG = "#Football_Scores";

    public GameScoreCursorAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View mItem = LayoutInflater.from(context).inflate(R.layout.scores_list_item, parent, false);
        GameScoreViewHolder mHolder = new GameScoreViewHolder(mItem);
        mItem.setTag(mHolder);
        //Log.v(FetchScoreTask.LOG_TAG,"new View inflated");
        return mItem;
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        final GameScoreViewHolder mHolder = (GameScoreViewHolder) view.getTag();
        mHolder.home_name.setText(cursor.getString(DatabaseContract.COL_HOME));
        mHolder.away_name.setText(cursor.getString(DatabaseContract.COL_AWAY));
        mHolder.date.setText(cursor.getString(DatabaseContract.COL_MATCHTIME));
        mHolder.score.setText(Utilities.getScores(cursor.getInt(DatabaseContract.COL_HOME_GOALS), cursor.getInt(DatabaseContract.COL_AWAY_GOALS)));
        mHolder.match_id = cursor.getDouble(DatabaseContract.COL_ID);
        mHolder.home_crest.setImageResource(Utilities.getTeamCrestByTeamName(
                cursor.getString(DatabaseContract.COL_HOME)));
        mHolder.away_crest.setImageResource(Utilities.getTeamCrestByTeamName(
                cursor.getString(DatabaseContract.COL_AWAY)
        ));
        //Log.v(FetchScoreTask.LOG_TAG,mHolder.home_name.getText() + " Vs. " + mHolder.away_name.getText() +" id " + String.valueOf(mHolder.match_id));
        //Log.v(FetchScoreTask.LOG_TAG,String.valueOf(detail_match_id));

        mHolder.matchDay.setText(Utilities.getMatchDay(cursor.getInt(DatabaseContract.COL_MATCHDAY),
            cursor.getInt(DatabaseContract.COL_LEAGUE)));
        mHolder.league.setText(Utilities.getLeague(context, cursor.getInt(DatabaseContract.COL_LEAGUE)));
        mHolder.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                //add Share Action
                context.startActivity(createShareForecastIntent(mHolder.home_name.getText() + " "
                    + mHolder.score.getText() + " " + mHolder.away_name.getText() + " "));
            }
        });

        //todo it would be nice to report if a game hasn't started or if there is no score to report.
        StringBuilder gameReport = new StringBuilder();
        gameReport.append(mHolder.home_name.getText().toString())
            .append(view.getContext().getString(R.string.playing))
            .append(mHolder.away_name.getText().toString())
            .append(view.getContext().getString(R.string.current_score))
            .append(mHolder.score.getText().toString());
        view.setContentDescription(gameReport.toString());

    }

    public Intent createShareForecastIntent(String ShareText) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, ShareText + FOOTBALL_SCORES_HASHTAG);
        return shareIntent;
    }

}
