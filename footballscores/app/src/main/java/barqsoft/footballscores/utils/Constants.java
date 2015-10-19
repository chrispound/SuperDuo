package barqsoft.footballscores.utils;

public class Constants {
    public static final String MATCH_LINK = "http://api.football-data.org/alpha/soccerseasons/";

    public  static final String SYNC_FINISHED = "finished";

    //Time Intervals
    public static final long SECONDS_PER_MINUTE = 60L;
    public static final long SYNC_INTERVAL_IN_MINUTES = 60L;
    public static final long SYNC_INTERVAL =
        SYNC_INTERVAL_IN_MINUTES *
            SECONDS_PER_MINUTE;

    public static final long SYNC_FLEXTIME = SYNC_INTERVAL / 3;

}
