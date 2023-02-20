package pmm.ignacio.theanimedatabase.Anime;

import com.google.gson.annotations.SerializedName;

public class Anime {
    public String title;
    @SerializedName("main_picture")
    public AnimePicture mainPicture;

}


class AnimePicture {
    private String medium;
    private String large;
}
