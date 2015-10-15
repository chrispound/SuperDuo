package barqsoft.footballscores.endpoints;

import com.google.gson.JsonElement;

import org.json.JSONObject;

import java.util.List;

import barqsoft.footballscores.BuildConfig;
import barqsoft.footballscores.R;
import barqsoft.footballscores.models.FootballLeagueGames;
import barqsoft.footballscores.models.Match;
import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.Query;

public interface FootballApiEndpoints {

    @Headers("X-Auth-Token: " + BuildConfig.FOOTBALL_API_KEY)
    @GET("fixtures")
    Call<FootballLeagueGames> getMatches(@Query("timeFrame") String timeFrame);

}
