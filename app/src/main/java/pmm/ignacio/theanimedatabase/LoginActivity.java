package pmm.ignacio.theanimedatabase;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import pmm.ignacio.theanimedatabase.Anime.AnimeToken;
import pmm.ignacio.theanimedatabase.Anime.AuthAnimeService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {

    private String requestToken;
    private static final String TAG = LoginActivity.class.getSimpleName();
    private String codeVerifier;
    private static final String AUTH_URL = "https://myanimelist.net/v1/oauth2/";
    private static final String CLIENT_ID = BuildConfig.CLIENT_ID;
    private static final String REDIRECT_URL = "theanimedatabase://callback";
    public AnimeToken animeToken;
    public String authorization;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);

        // Check if user is already logged in
        SharedPreferences defPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String accessToken = defPref.getString("accessToken", null);
        if (accessToken != null){
            // Goto Main activity
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        }


        Button loginButton = findViewById(R.id.login_button);
        loginButton.setOnClickListener(v -> {
            // OAuth
            if (requestToken == null) {
                Log.i(TAG, "No code found, requesting new one : " + requestToken);
                OAuthRequestPhase1();
            }
        });


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

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("codeVerifier", codeVerifier);
        editor.apply();

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

        SharedPreferences defPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        codeVerifier  = defPref.getString("codeVerifier","");

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
        Log.d(TAG, "[OAUTH] requesting access token...");
        Call<AnimeToken> call = authService.getAnimeToken(body);
        call.enqueue(new Callback<AnimeToken>() {
            @Override
            public void onResponse(@NonNull Call<AnimeToken> call, @NonNull Response<AnimeToken> response) {
                // Receive response
                if (response.isSuccessful()) {
                    animeToken = response.body();
                    assert animeToken != null; // Avoid annoying warning
                    assert response.body() != null; // x2
                    authorization = animeToken.tokenType + " " + animeToken.accessToken;
                    Log.d(TAG, "[OAUTH] --- TOKEN OBTAINED ---");
                    Log.d(TAG, "[OAUTH] access token: " + animeToken.accessToken);
                    Log.d(TAG, "[OAUTH] token type: " + animeToken.tokenType);
                    Log.d(TAG, "[OAUTH] expires in (mls): " + animeToken.expiresIn);
                    Log.d(TAG, "[OAUTH] refresh token: " + animeToken.refreshToken);

                    SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("accessToken", animeToken.accessToken);
                    editor.putString("refreshToken", animeToken.refreshToken);
                    editor.putString("authorization", authorization);
                    editor.apply();

                    // Goto Main activity
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);

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

}
