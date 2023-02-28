package pmm.ignacio.theanimedatabase;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import pmm.ignacio.theanimedatabase.Anime.data.AnimePicture;

public class AnimeDetailsImageActivity extends AppCompatActivity {
    private static final String TAG = AnimeDetailsActivity.class.getName();
    private String _animePictureLarge;
    public static final String ANIME_PICTURE = "ANIME_PICTURE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anime_details_image);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            _animePictureLarge = extras.getString(ANIME_PICTURE);
        }
        ImageView imageView = findViewById(R.id.anime_details_image);
        Picasso.get().load(_animePictureLarge).into(imageView);
    }
}
