package pmm.ignacio.theanimedatabase.Anime;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface AuthAnimeService {

    @Headers("Accept: application/json")
    @POST("token")
    Call<AnimeToken> getAnimeToken(@Body RequestBody body);
}