package pmm.ignacio.theanimedatabase;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.RequestBody;
import pmm.ignacio.theanimedatabase.Anime.data.Anime;
import pmm.ignacio.theanimedatabase.Anime.data.AnimeChunk;
import pmm.ignacio.theanimedatabase.Anime.data.AnimeNode;
import pmm.ignacio.theanimedatabase.Anime.AnimeService;
import pmm.ignacio.theanimedatabase.Anime.AnimeToken;
import pmm.ignacio.theanimedatabase.Anime.AuthAnimeService;
import pmm.ignacio.theanimedatabase.RecyclerView.AnimeAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private static final String API_URL = "https://api.myanimelist.net/v2/";
    private static final String AUTH_URL = "https://myanimelist.net/v1/oauth2/";
    private static final String CLIENT_ID = BuildConfig.CLIENT_ID;
    private static final String REDIRECT_URL = "theanimedatabase://callback";
    private static final String TAG = MainActivity.class.getName();
    private AnimeService _service;
    private int _offset = 0;
    private static final int LIMIT = 20;
    private ArrayList<Anime> _anime = new ArrayList<Anime>();
    private RecyclerView.Adapter _adapter;

    // OAuth
    private String codeVerifier;
    private String requestToken;
    private Boolean OAuthPhase1 = false;

    public AnimeToken animeToken;
    public String autorization;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Restore state
        if (savedInstanceState != null) {
            requestToken = savedInstanceState.getString("code");
            OAuthPhase1 = savedInstanceState.getBoolean("OAuthPhase1");
            //animeToken = savedInstanceState.getSerializable("animeToken");
            autorization = savedInstanceState.getString("authorization");
        }

        // OAuth
        if (requestToken == null && !OAuthPhase1) {
            Log.i(TAG, "No code found, requesting new one : " + requestToken);
            OAuthRequestPhase1();
        }


        // Retrofit API service
        OkHttpClient client = new OkHttpClient.Builder()
                .protocols(Collections.singletonList(Protocol.HTTP_1_1))
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        _service = retrofit.create(AnimeService.class);


        Button loadMoreButton = findViewById(R.id.load_more_button);
        loadMoreButton.setOnClickListener(v -> AddAnime(_offset, LIMIT));

        getAnime();

    }

    @Override
    protected void onResume() {
        super.onResume();

        // OAuth: Get code from browser
        Uri uri = getIntent().getData();
        if (uri != null && uri.toString().startsWith(REDIRECT_URL) && animeToken == null) {
            requestToken = uri.getQueryParameter("code");
            Log.d(TAG, "[OAUTH] request token: " + requestToken);
            OAuthRequestPhase2();
        }


    }


    private void OAuthRequestPhase1() {
        // ----- Request code -----
        // Gen Code challenge (PKCE protocol)
        //codeVerifier = PkceGenerator.generateVerifier(64);
        // V  Por algun motivo si no lo establezco a mano, no autentifica. ¯\_(ツ)_/¯
        codeVerifier = "1234567890123456789012345678901234567890123456789012345678901234";
        Log.d(TAG, "[OAUTH] code verifier: " + codeVerifier);
        OAuthPhase1 = true;

        // Prepare request
        Uri uri = Uri.parse(AUTH_URL + "authorize")
                .buildUpon()
                .appendQueryParameter("response_type", "code")
                .appendQueryParameter("client_id", CLIENT_ID)
                .appendQueryParameter("code_challenge", codeVerifier)
                .appendQueryParameter("state", "theanimedatabase")
                .appendQueryParameter("redirect_uri", REDIRECT_URL)
                .appendQueryParameter("code_challenge_method", "plain")
                .build();

        // Launch browser
        Log.d(TAG, "[OAUTH] launch uri: " + uri.toString());
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    private void OAuthRequestPhase2() {
        // ----- Request token -----
        // Create retrofit service
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AUTH_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        AuthAnimeService authService = retrofit.create(AuthAnimeService.class);

        // Prepare request
        RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("client_id", CLIENT_ID)
                .addFormDataPart("code", requestToken)
                .addFormDataPart("code_verifier", codeVerifier)
                .addFormDataPart("grant_type", "authorization_code")
                .addFormDataPart("redirect_uri", REDIRECT_URL)
                .build();

        // Send request
        Log.d(TAG, "[OAUTH] requesting access token: " + body);
        Call<AnimeToken> call = authService.getAnimeToken(body);
        call.enqueue(new Callback<AnimeToken>() {
            @Override
            public void onResponse(@NonNull Call<AnimeToken> call, @NonNull Response<AnimeToken> response) {
                // Receive response
                if (response.isSuccessful()) {
                    animeToken = response.body();
                    assert animeToken != null; // Avoid annoying warning
                    assert response.body() != null; // x2
                    autorization = animeToken.tokenType + " " + animeToken.accessToken;
                    Log.d(TAG, "[OAUTH] --- TOKEN OBTAINED ---");
                    Log.d(TAG, "[OAUTH] access token: " + animeToken.accessToken);
                    Log.d(TAG, "[OAUTH] token type: " + animeToken.tokenType);
                    Log.d(TAG, "[OAUTH] expires in (mls): " + animeToken.expiresIn);
                    Log.d(TAG, "[OAUTH] refresh token: " + animeToken.refreshToken);

                } else {
                    Log.e(TAG, "[OAUTH] --- TOKEN ERROR ---");
                    Log.e(TAG, "[OAUTH] error: " + (response.body() != null ? response.body().errorType : null));
                    Log.e(TAG, "[OAUTH] message: " + response.body().message);
                    Log.e(TAG, "[OAUTH] hint: " + response.body().hint);
                }
            }

            @Override
            public void onFailure(@NonNull Call<AnimeToken> call, @NonNull Throwable t) {
                Log.e(TAG, "[OAUTH] Error calling API: " + t.getMessage());
            }
        });
    }


    private void getAnime() {

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        _adapter = new AnimeAdapter(_anime, anime -> {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, AnimeDetailsActivity.class);
            intent.putExtra(AnimeDetailsActivity.NAME_KEY, anime.title);
            startActivity(intent);
        });
        recyclerView.setAdapter(_adapter);

        //_adapter.notifyItemRangeInserted(offset, LIMIT);
        //_offset += LIMIT;


    }

    private void AddAnime(int offset, int limit) {
        Call<AnimeChunk> call = _service.listAnime(autorization,"one", offset, limit);
        call.enqueue(new Callback<AnimeChunk>() {
            @Override
            public void onResponse(Call<AnimeChunk> call, Response<AnimeChunk> response) {
                if (response.isSuccessful()) {
                    Log.i(TAG, "Anime received: " + response.body().data.size());
                    for (AnimeNode a : response.body().data) {
                        // Add anime to the arraylist
                        _anime.add(a.node);
                        Log.d(TAG, "Anime: " + a.node.title);
                    }
                } else {
                    Log.e(TAG, "Error loading anime, server responds: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<AnimeChunk> call, Throwable t) {
                Log.e(TAG, "Error calling API: ", t);
            }
        });
    }


    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        OAuthPhase1 = savedInstanceState.getBoolean("OAuthPhase1");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("authorization", autorization);
        outState.putBoolean("OAuthPhase1", OAuthPhase1);
        //outState.putSerializable("animeToken", animeToken);
        super.onSaveInstanceState(outState);
    }
}
