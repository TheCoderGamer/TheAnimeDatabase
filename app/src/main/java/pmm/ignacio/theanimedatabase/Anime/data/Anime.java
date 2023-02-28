package pmm.ignacio.theanimedatabase.Anime.data;

import com.google.gson.annotations.SerializedName;

public class Anime {
    public int id;
    public String title;
    @SerializedName("main_picture")
    public AnimePicture mainPicture;

}


