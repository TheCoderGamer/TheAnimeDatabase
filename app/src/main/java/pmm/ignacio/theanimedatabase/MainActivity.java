package pmm.ignacio.theanimedatabase;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.gson.JsonObject;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import pmm.ignacio.theanimedatabase.Anime.Anime;
import pmm.ignacio.theanimedatabase.Anime.AnimeChunk;
import pmm.ignacio.theanimedatabase.Anime.AnimeService;
import pmm.ignacio.theanimedatabase.Anime.AnimeToken;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private static final String API_URL = "https://api.myanimelist.net/v2/";
    private static final String AUTH_URL = "https://myanimelist.net/v1/oauth2/";
    private static final String CLIENT_ID = "f0a1088b8a35d98ce6591e10fdc62232";
    private static final String REDIRECT_URL = "theanimedatabase://callback";
    private static final String TAG = MainActivity.class.getName();
    private AnimeService _service;
    private int _offset = 0;
    private static final int LIMIT = 20;
    private List<Anime> _anime = new ArrayList<Anime>();
    private RecyclerView.Adapter _adapter;

    private String codeVerifier;
    private String code;

    public String accessToken;
    public String tokenType;
    public String refreshToken;
    public int expiresIn;
    public String autorization;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (code == null){
            Log.i(TAG, "No code found, requesting new one : " + code);
            OAuthRequestPhase1();
        }
        //getAnime();


        Button loadMoreButton = findViewById(R.id.load_more_button);
        loadMoreButton.setOnClickListener(v -> loadChunk(_offset, LIMIT));

    }

    @Override
    protected void onResume() {
        super.onResume();

        // Get code from browser
        Uri uri = getIntent().getData();
        if (uri != null && uri.toString().startsWith(REDIRECT_URL)) {
            code = uri.getQueryParameter("code");
            Log.d(TAG, "code: " + code);
            OAuthRequestPhase2();
        }
    }


    private void OAuthRequestPhase1() {
        // ----- Request code -----
        // Gen Code challenge (PKCE protocol)
//        codeVerifier = PkceGenerator.INSTANCE.generateVerifier(128);
        codeVerifier = "1234567890123456789012345678901234567890123456789012345678901234"; //TODO: (cambiar) inseguro pero funciona
        String codeChallenge = codeVerifier;
        Log.d(TAG, "code verifier: " + codeVerifier);

        // Prepare request
        Uri uri = Uri.parse(AUTH_URL + "authorize")
                .buildUpon()
                .appendQueryParameter("response_type", "code")
                .appendQueryParameter("client_id", CLIENT_ID)
                .appendQueryParameter("code_challenge", codeChallenge)
                .appendQueryParameter("state", "theanimedatabase")
                .appendQueryParameter("redirect_uri", REDIRECT_URL)
                .appendQueryParameter("code_challenge_method", "plain")
                .build();

        // Launch browser
        Log.d(TAG, "launch uri: " + uri.toString());
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
        _service = retrofit.create(AnimeService.class);

        // Prepare request
        RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("client_id", CLIENT_ID)
                .addFormDataPart("code", code)
                .addFormDataPart("code_verifier", codeVerifier)
                .addFormDataPart("grant_type", "authorization_code")
                .addFormDataPart("redirect_uri", REDIRECT_URL)
                .build();

        // Send request
        Call<AnimeToken> call = _service.getAnimeToken(body);
        call.enqueue(new Callback<AnimeToken>() {
            @Override
            public void onResponse(Call<AnimeToken> call, Response<AnimeToken> response) {
                // Receive response
                if (response.isSuccessful()) {
                    accessToken = response.body().accessToken;
                    tokenType = response.body().tokenType;
                    refreshToken = response.body().refreshToken;
                    expiresIn = response.body().expiresIn;
                    autorization = tokenType + " " + accessToken;
                    Log.d(TAG, "token: " + response.body().accessToken);
                    Log.d(TAG, "token type: " + response.body().tokenType);
                    Log.d(TAG, "token expires in: " + response.body().expiresIn);
                    Log.d(TAG, "token refresh token: " + response.body().refreshToken);
                } else {
                    Log.e(TAG, "error: " + response.body().errorType);
                    Log.e(TAG, "message: " + response.body().message);
                    Log.e(TAG, "hint: " + response.body().hint);
                }
            }
            @Override
            public void onFailure(Call<AnimeToken> call, Throwable t) {
                Log.e(TAG, "error: " + t.getMessage());
            }
        });
    }


    private void getAnime() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        _service = retrofit.create(AnimeService.class);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        _adapter = new AnimeAdapter(_anime, anime -> {
//                 Intent intent = new Intent();
//                 intent.setClass(MainActivity.this, AnimeDetailsActivity.class);
//                 intent.putExtra(SpeciesDetailsActivity.NAME_KEY, species.name);
//                 startActivity(intent);
        });
        recyclerView.setAdapter(_adapter);


    }

    private void loadChunk(int offset, int limit) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        _service = retrofit.create(AnimeService.class);

        Call<AnimeChunk> call = _service.listAnime(autorization, offset, limit);
        call.enqueue(new Callback<AnimeChunk>() {
            @Override
            public void onResponse(Call<AnimeChunk> call, Response<AnimeChunk> response) {
                if (response.isSuccessful()) {
                    List<Anime> anime = response.body().data.node;
                    _anime.addAll(anime);
                    for (Anime a : anime) {
                        Log.d(TAG, a.title);
                    }
                    _adapter.notifyItemRangeInserted(offset, LIMIT);
                    _offset += LIMIT;
                } else {
                    Log.e(TAG, "Error loading chunk: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<AnimeChunk> call, Throwable t) {
                Log.e(TAG, "Error loading chunk", t);
            }
        });
    }


}
