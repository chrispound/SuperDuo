package barqsoft.footballscores.models;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FootballLeagueGames {

    @Expose
    public String timeFrameStart;
    @Expose
    public String timeFrameEnd;
    @Expose
    public Integer count;
    @SerializedName("fixtures")
    @Expose
    public List<Match> matches = new ArrayList<>();
}