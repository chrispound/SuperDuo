package barqsoft.footballscores.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import barqsoft.footballscores.utils.Constants;

public class Match implements Parcelable {

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



    public static class MatchResult implements Parcelable {
        @SerializedName("goalsHomeTeam")
        @Expose
        public Integer goalsHomeTeam;
        @SerializedName("goalsAwayTeam")
        @Expose
        public Integer goalsAwayTeam;


        @Override
        public int describeContents()
        {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags)
        {
            dest.writeValue(this.goalsHomeTeam);
            dest.writeValue(this.goalsAwayTeam);
        }

        public MatchResult()
        {
        }

        protected MatchResult(Parcel in)
        {
            this.goalsHomeTeam = (Integer) in.readValue(Integer.class.getClassLoader());
            this.goalsAwayTeam = (Integer) in.readValue(Integer.class.getClassLoader());
        }

        public static final Creator<MatchResult> CREATOR = new Creator<MatchResult>() {
            public MatchResult createFromParcel(Parcel source)
            {
                return new MatchResult(source);
            }

            public MatchResult[] newArray(int size)
            {
                return new MatchResult[size];
            }
        };
    }

    public static class Links implements Parcelable {

        @SerializedName("self")
        @Expose
        public Link self;
        @SerializedName("soccerseason")
        @Expose
        public Link soccerSeason;

        public static class Link implements Parcelable {
            @SerializedName("href")
            @Expose
            public String href;


            @Override
            public int describeContents()
            {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel dest, int flags)
            {
                dest.writeString(this.href);
            }

            public Link()
            {
            }

            protected Link(Parcel in)
            {
                this.href = in.readString();
            }

            public static final Creator<Link> CREATOR = new Creator<Link>() {
                public Link createFromParcel(Parcel source)
                {
                    return new Link(source);
                }

                public Link[] newArray(int size)
                {
                    return new Link[size];
                }
            };
        }


        @Override
        public int describeContents()
        {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags)
        {
            dest.writeParcelable(this.self, 0);
            dest.writeParcelable(this.soccerSeason, 0);
        }

        public Links()
        {
        }

        protected Links(Parcel in)
        {
            this.self = in.readParcelable(Link.class.getClassLoader());
            this.soccerSeason = in.readParcelable(Link.class.getClassLoader());
        }

        public static final Creator<Links> CREATOR = new Creator<Links>() {
            public Links createFromParcel(Parcel source)
            {
                return new Links(source);
            }

            public Links[] newArray(int size)
            {
                return new Links[size];
            }
        };
    }

    public String getMatchId() {
        return links.self.href.replace(Constants.MATCH_LINK, "");
    }

    public String getLeagueId() {
        return links.soccerSeason.href.replace(Constants.MATCH_LINK, "");
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(this.date);
        dest.writeString(this.status);
        dest.writeValue(this.matchday);
        dest.writeString(this.homeTeamName);
        dest.writeString(this.awayTeamName);
        dest.writeParcelable(this.result, 0);
        dest.writeParcelable(this.links, 0);
    }

    public Match()
    {
    }

    protected Match(Parcel in)
    {
        this.date = in.readString();
        this.status = in.readString();
        this.matchday = (Integer) in.readValue(Integer.class.getClassLoader());
        this.homeTeamName = in.readString();
        this.awayTeamName = in.readString();
        this.result = in.readParcelable(MatchResult.class.getClassLoader());
        this.links = in.readParcelable(Links.class.getClassLoader());
    }

    public static final Creator<Match> CREATOR = new Creator<Match>() {
        public Match createFromParcel(Parcel source)
        {
            return new Match(source);
        }

        public Match[] newArray(int size)
        {
            return new Match[size];
        }
    };
}