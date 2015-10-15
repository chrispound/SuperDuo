package barqsoft.footballscores.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import barqsoft.footballscores.utils.Constants;

public class Match {

    @SerializedName("date")
    @Expose
    public String date;
    @SerializedName("status")
    @Expose
    public String status;
    @SerializedName("matchday")
    @Expose
    public Integer matchday;
    @SerializedName("homeTeamName")
    @Expose
    public String homeTeamName;
    @SerializedName("awayTeamName")
    @Expose
    public String awayTeamName;
    @SerializedName("result")
    @Expose
    public MatchResult result;
    @SerializedName("_links")
    @Expose
    public Links links;

    public class MatchResult {
        @SerializedName("goalsHomeTeam")
        @Expose
        public Integer goalsHomeTeam;
        @SerializedName("goalsAwayTeam")
        @Expose
        public Integer goalsAwayTeam;
    }

    public class Links {

        @SerializedName("self")
        @Expose
        public Self self;
        @SerializedName("soccerseason")
        @Expose
        public SoccerSeason soccerSeason;

        public class Self {
            @SerializedName("href")
            @Expose
            public String href;
        }

        public class SoccerSeason {
            @SerializedName("href")
            @Expose
            public String href;
        }
    }

    public String getMatchId() {
        return links.self.href.replace(Constants.MATCH_LINK, "");
    }

    public String getLeagueId() {
        return links.soccerSeason.href.replace(Constants.MATCH_LINK, "");
    }

}