package pmm.ignacio.theanimedatabase;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class AnimeDetailsActivity extends AppCompatActivity {
    private static final String TAG = AnimeDetailsActivity.class.getName();
    private static final String NAME_KEY = "NAME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anime_details);

        Intent intent = getIntent();
        String name = intent.getStringExtra(NAME_KEY);
        TextView textView = findViewById(R.id.anime_name);
        textView.setText(name);
    }
}
