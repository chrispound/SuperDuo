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

        @Override
        public boolean equals(Object o)
        {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            MatchResult that = (MatchResult) o;

            if (goalsHomeTeam != null ? !goalsHomeTeam.equals(that.goalsHomeTeam) : that.goalsHomeTeam != null)
                return false;
            return !(goalsAwayTeam != null ? !goalsAwayTeam.equals(that.goalsAwayTeam) : that.goalsAwayTeam != null);

        }

        @Override
        public int hashCode()
        {
            int result = goalsHomeTeam != null ? goalsHomeTeam.hashCode() : 0;
            result = 31 * result + (goalsAwayTeam != null ? goalsAwayTeam.hashCode() : 0);
            return result;
        }
    }

    public static class Links implements Parcelable {

        @SerializedName("self")
        @Expose
        public Link self;
        @SerializedName("soccerseason")
        @Expose
        public Link soccerSeason;

        @Override
        public boolean equals(Object o)
        {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Links links = (Links) o;

            if (self != null ? !self.equals(links.self) : links.self != null) return false;
            return !(soccerSeason != null ? !soccerSeason.equals(links.soccerSeason) : links.soccerSeason != null);

        }

        @Override
        public int hashCode()
        {
            int result = self != null ? self.hashCode() : 0;
            result = 31 * result + (soccerSeason != null ? soccerSeason.hashCode() : 0);
            return result;
        }

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

            @Override
            public boolean equals(Object o)
            {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;

                Link link = (Link) o;

                return !(href != null ? !href.equals(link.href) : link.href != null);

            }

            @Override
            public int hashCode()
            {
                return href != null ? href.hashCode() : 0;
            }
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

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Match match = (Match) o;

        if (date != null ? !date.equals(match.date) : match.date != null) return false;
        if (status != null ? !status.equals(match.status) : match.status != null) return false;
        if (matchday != null ? !matchday.equals(match.matchday) : match.matchday != null)
            return false;
        if (homeTeamName != null ? !homeTeamName.equals(match.homeTeamName) : match.homeTeamName != null)
            return false;
        if (awayTeamName != null ? !awayTeamName.equals(match.awayTeamName) : match.awayTeamName != null)
            return false;
        if (result != null ? !result.equals(match.result) : match.result != null) return false;
        return !(links != null ? !links.equals(match.links) : match.links != null);

    }

    @Override
    public int hashCode()
    {
        int result1 = date != null ? date.hashCode() : 0;
        result1 = 31 * result1 + (status != null ? status.hashCode() : 0);
        result1 = 31 * result1 + (matchday != null ? matchday.hashCode() : 0);
        result1 = 31 * result1 + (homeTeamName != null ? homeTeamName.hashCode() : 0);
        result1 = 31 * result1 + (awayTeamName != null ? awayTeamName.hashCode() : 0);
        result1 = 31 * result1 + (result != null ? result.hashCode() : 0);
        result1 = 31 * result1 + (links != null ? links.hashCode() : 0);
        return result1;
    }
}