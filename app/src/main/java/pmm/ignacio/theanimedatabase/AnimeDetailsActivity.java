package pmm.ignacio.theanimedatabase;

import static pmm.ignacio.theanimedatabase.MainActivity.API_URL;
import static pmm.ignacio.theanimedatabase.MainActivity.authorization;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Collections;

import com.squareup.picasso.Picasso;

import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import pmm.ignacio.theanimedatabase.Anime.AnimeService;
import pmm.ignacio.theanimedatabase.Anime.data.AnimeChunk;
import pmm.ignacio.theanimedatabase.Anime.data.AnimeDetails;
import pmm.ignacio.theanimedatabase.Anime.data.AnimeNode;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AnimeDetailsActivity extends AppCompatActivity {

    private static final String TAG = AnimeDetailsActivity.class.getName();
    public static final String ID_ANIME = "ID_ANIME";
    private AnimeDetails _anime;
    private static final String FIELDS = "id,title,main_picture,alternative_titles,start_date,end_date,synopsis,mean,rank,popularity,num_list_users,num_scoring_users,nsfw,created_at,updated_at,media_type,status,genres,my_list_status,num_episodes,start_season,broadcast,source,rating,pictures";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anime_details);
        Intent intent = getIntent();
        int id = intent.getIntExtra(ID_ANIME, 0);

        // Retrofit API service
        OkHttpClient client = new OkHttpClient.Builder()
                .protocols(Collections.singletonList(Protocol.HTTP_1_1))
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        AnimeService _service = retrofit.create(AnimeService.class);

        // Call API: Get anime details
        Call<AnimeDetails> call = _service.animeDetails(authorization, id, FIELDS);
        call.enqueue(new Callback<AnimeDetails>() {
            @Override
            public void onResponse(@NonNull Call<AnimeDetails> call, @NonNull Response<AnimeDetails> response) {
                if (response.isSuccessful()) {
                    _anime = response.body();
                    Log.d(TAG, "Anime details loaded from API");
                    SetAnimeDetailsText();
                } else {
                    Log.e(TAG, "Error loading anime, server responds: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<AnimeDetails> call, @NonNull Throwable t) {
                Log.e(TAG, "Error calling API: ", t);
            }
        });

    }

    private void SetAnimeDetailsText() {
        // Set anime details textviews
        TextView anime_detail_name = findViewById(R.id.anime_detail_name);
        TextView anime_detail_alternative_en = findViewById(R.id.anime_detail_alternative_en);
        TextView anime_detail_alternative_ja = findViewById(R.id.anime_detail_alternative_ja);
        TextView anime_detail_start_date = findViewById(R.id.anime_detail_start_date);
        TextView anime_detail_end_date = findViewById(R.id.anime_detail_end_date);
        TextView anime_detail_status = findViewById(R.id.anime_detail_status);
        TextView anime_detail_rank = findViewById(R.id.anime_detail_rank);
        TextView anime_detail_mean = findViewById(R.id.anime_detail_mean);
        TextView anime_detail_popularity = findViewById(R.id.anime_detail_popularity);
        TextView anime_detail_num_episodes = findViewById(R.id.anime_detail_numEps);
        TextView anime_detail_synopsis = findViewById(R.id.anime_detail_synopsis);
        ImageView anime_detail_image = findViewById(R.id.anime_detail_image);

        anime_detail_name.setText(_anime.title);
        anime_detail_alternative_en.setText(_anime.alternativeTitles.en);
        anime_detail_alternative_ja.setText(_anime.alternativeTitles.ja);
        anime_detail_start_date.setText(_anime.startDate);
        anime_detail_end_date.setText(_anime.endDate);
        anime_detail_status.setText(_anime.status);
        anime_detail_rank.setText(String.valueOf(_anime.rank));
        anime_detail_mean.setText(String.valueOf(_anime.mean));
        anime_detail_popularity.setText(String.valueOf(_anime.popularity));
        anime_detail_num_episodes.setText(String.valueOf(_anime.numEpisodes));
        anime_detail_synopsis.setText(_anime.synopsis);
        anime_detail_synopsis.setMovementMethod(new ScrollingMovementMethod());
        Picasso.get().load(_anime.mainPicture.medium).into(anime_detail_image);
        anime_detail_image.setOnClickListener(v -> {
            Intent intent = new Intent(AnimeDetailsActivity.this, AnimeDetailsImageActivity.class);
            intent.putExtra(AnimeDetailsImageActivity.ANIME_PICTURE, _anime.mainPicture.large);
            startActivity(intent);
        });
    }
}
