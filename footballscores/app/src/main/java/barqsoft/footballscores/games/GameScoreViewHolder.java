package barqsoft.footballscores.games;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import barqsoft.footballscores.R;

/**
 * Created by yehya khaled on 2/26/2015.
 */
public class GameScoreViewHolder {
    public TextView home_name;
    public TextView away_name;
    public TextView score;
    public TextView date;
    public ImageView home_crest;
    public ImageView away_crest;
    public double match_id;
    public TextView league;
    public TextView matchDay;
    public Button share;

    public GameScoreViewHolder(View view) {
        home_name = (TextView) view.findViewById(R.id.home_name);
        away_name = (TextView) view.findViewById(R.id.away_name);
        score = (TextView) view.findViewById(R.id.score_textview);
        date = (TextView) view.findViewById(R.id.data_textview);
        home_crest = (ImageView) view.findViewById(R.id.home_crest);
        away_crest = (ImageView) view.findViewById(R.id.away_crest);
        league = (TextView) view.findViewById(R.id.league_textview);
        matchDay = (TextView) view.findViewById(R.id.matchday_textview);
        share = (Button) view.findViewById(R.id.share_button);
    }
}
