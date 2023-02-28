package pmm.ignacio.theanimedatabase.Anime.data;

import com.google.gson.annotations.SerializedName;

public class AnimeDetails {
    public String title;
    @SerializedName("main_picture")
    public AnimePicture mainPicture;
    @SerializedName("alternative_titles")
    public AlternativeTitles alternativeTitles;
    @SerializedName("start_date")
    public String startDate;
    @SerializedName("end_date")
    public String endDate;
    public String synopsis;
    public String rank;
    public String popularity;
    public String mean;
    public String status;
    @SerializedName("num_episodes")
    public int numEpisodes;

}

