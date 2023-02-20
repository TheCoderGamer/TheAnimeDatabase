package pmm.ignacio.theanimedatabase.Anime;

import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface AnimeService {

    @Headers("Accept: application/json")
    @POST("token")
//    @FormUrlEncoded
    Call<AnimeToken> getAnimeToken(@Body RequestBody body);



    @GET("anime")
    Call<AnimeChunk> listAnime(
            @Header("Authorization:") String authorization,
            @Query("offset") int offset,
            @Query("limit") int limit);


    @GET("anime/{id}")
    Call<AnimeDetails> animeDetails(@Path("id") int id);

}


// @Field("client_id") String clientId,
//            @Field("client_secret") String clientSecret,
//            @Field("code") String code,
//            @Field("code_verifier") String codeVerifier,
//            @Field("grant_type") String grantType,
//            @Field("redirect_uri") String redirectUri