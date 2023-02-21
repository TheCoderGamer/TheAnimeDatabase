package pmm.ignacio.theanimedatabase.Anime;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class AnimeToken implements Serializable {

    @SerializedName("token_type")
    public String tokenType;
    @SerializedName("expires_in")
    public int expiresIn;
    @SerializedName("access_token")
    public String accessToken;
    @SerializedName("refresh_token")
    public String refreshToken;

    @SerializedName("error")
    public String errorType;
    @SerializedName("message")
    public String message;
    @SerializedName("hint")
    public String hint;

}
