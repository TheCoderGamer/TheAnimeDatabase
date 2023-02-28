package pmm.ignacio.theanimedatabase.Anime;

import pmm.ignacio.theanimedatabase.Anime.data.AnimeChunk;
import pmm.ignacio.theanimedatabase.Anime.data.AnimeDetails;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface AnimeService {
    @GET("anime")
    Call<AnimeChunk> listAnime(
            @Header("Authorization") String authorization,
            @Query("q") String query,
            @Query("offset") int offset,
            @Query("limit") int limit);


    @GET("ranking")
    Call<AnimeChunk> ranking(
            @Header("Authorization") String authorization,
            @Query("ranking_type") String rankingType,
            @Query("limit") int limit,
            @Query("offset") int offset);

    @GET("anime/{id}")
    Call<AnimeDetails> animeDetails(
            @Header("Authorization") String authorization,
            @Path("id") int id,
            @Query("fields") String fields);

}