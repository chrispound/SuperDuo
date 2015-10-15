package barqsoft.footballscores.models;

/**
 * Created by atlas on 10/14/15.
 */
public class Game {
    public static final String LEAGUE_COL = "league";
    public static final String DATE_COL = "date";
    public static final String TIME_COL = "time";
    public static final String HOME_COL = "home";
    public static final String AWAY_COL = "away";
    public static final String HOME_GOALS_COL = "home_goals";
    public static final String AWAY_GOALS_COL = "away_goals";
    public static final String MATCH_ID = "match_id";
    public static final String MATCH_DAY = "match_day";
    String home;
    String away;
    String homeScore;
    String awayScore;
    String gameDate;
    String gameTime;
    String matchId;
    String matchDay;
}
