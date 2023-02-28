package pmm.ignacio.theanimedatabase;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;

import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import pmm.ignacio.theanimedatabase.Anime.data.Anime;
import pmm.ignacio.theanimedatabase.Anime.data.AnimeChunk;
import pmm.ignacio.theanimedatabase.Anime.data.AnimeNode;
import pmm.ignacio.theanimedatabase.Anime.AnimeService;
import pmm.ignacio.theanimedatabase.RecyclerView.AnimeAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

//TODO: Gestionar el refresco de token
public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();
    static final String API_URL = "https://api.myanimelist.net/v2/";
    private AnimeService _service;
    private int _offset = 0;
    private static final int LIMIT = 50;
    private ArrayList<Anime> _anime = new ArrayList<Anime>();
    private RecyclerView.Adapter _adapter;
    static String authorization;
    RecyclerView recyclerView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Check if user is already logged in
        SharedPreferences defPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        authorization = defPref.getString("authorization", null);
        if (authorization == null){
            // Goto Login activity
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }

        // Retrofit API service
        OkHttpClient client = new OkHttpClient.Builder()
                .protocols(Collections.singletonList(Protocol.HTTP_1_1))
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        _service = retrofit.create(AnimeService.class);

        // RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        recyclerView.setItemAnimator(null); // Avoid bug doing fast scrolling
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!recyclerView.canScrollVertically(1)) {
                    recyclerView.postDelayed(() -> {
                        LoadChunk(_offset, LIMIT);
                    }, 1000);
                }
            }
        });
        _adapter = new AnimeAdapter(_anime, anime -> {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, AnimeDetailsActivity.class);
            intent.putExtra(AnimeDetailsActivity.ID_ANIME, anime.id);
            startActivity(intent);
        });
        recyclerView.setAdapter(_adapter);

        // Call API first time
        LoadChunk(_offset, LIMIT);

    }
    private void LoadChunk(int offset, int limit) {
        Log.i(TAG, "Loading anime from offset: " + offset + " limit: " + limit);
        Call<AnimeChunk> call = _service.listAnime(authorization,"piece", offset, limit);
        call.enqueue(new Callback<AnimeChunk>() {
            @Override
            public void onResponse(@NonNull Call<AnimeChunk> call, @NonNull Response<AnimeChunk> response) {
                if (response.isSuccessful()) {
                    int count = response.body() != null ? response.body().data.size() : 0;
                    Log.i(TAG, "Anime received: " + count);
                    for (AnimeNode a : response.body().data) {
                        // Add anime to the arraylist
                        _anime.add(a.node);
                        Log.d(TAG, "Anime: " + a.node.title);
                    }
                    // Notify adapter
                    recyclerView.post(() -> _adapter.notifyItemRangeInserted(_offset, count));
                    _offset += count;
                } else {
                    Log.e(TAG, "Error loading anime, server responds: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<AnimeChunk> call, @NonNull Throwable t) {
                Log.e(TAG, "Error calling API: ", t);
            }
        });
    }
}
