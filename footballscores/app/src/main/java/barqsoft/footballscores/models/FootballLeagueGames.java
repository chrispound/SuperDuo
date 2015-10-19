package barqsoft.footballscores.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FootballLeagueGames implements Parcelable {

    @Expose
    public String timeFrameStart;
    @Expose
    public String timeFrameEnd;
    @Expose
    public Integer count;
    @SerializedName("fixtures")
    @Expose
    public List<Match> matches = new ArrayList<>();


    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(this.timeFrameStart);
        dest.writeString(this.timeFrameEnd);
        dest.writeValue(this.count);
        dest.writeTypedList(matches);
    }

    public FootballLeagueGames()
    {
    }

    protected FootballLeagueGames(Parcel in)
    {
        this.timeFrameStart = in.readString();
        this.timeFrameEnd = in.readString();
        this.count = (Integer) in.readValue(Integer.class.getClassLoader());
        this.matches = in.createTypedArrayList(Match.CREATOR);
    }

    public static final Creator<FootballLeagueGames> CREATOR = new Creator<FootballLeagueGames>() {
        public FootballLeagueGames createFromParcel(Parcel source)
        {
            return new FootballLeagueGames(source);
        }

        public FootballLeagueGames[] newArray(int size)
        {
            return new FootballLeagueGames[size];
        }
    };

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FootballLeagueGames that = (FootballLeagueGames) o;

        if (!timeFrameStart.equals(that.timeFrameStart)) return false;
        if (!timeFrameEnd.equals(that.timeFrameEnd)) return false;
        if (!count.equals(that.count)) return false;
        return matches.equals(that.matches);

    }

    @Override
    public int hashCode()
    {
        int result = timeFrameStart.hashCode();
        result = 31 * result + timeFrameEnd.hashCode();
        result = 31 * result + count.hashCode();
        result = 31 * result + matches.hashCode();
        return result;
    }
}