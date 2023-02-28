package pmm.ignacio.theanimedatabase.RecyclerView;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import pmm.ignacio.theanimedatabase.Anime.data.Anime;
import pmm.ignacio.theanimedatabase.R;

public class AnimeViewHolder extends RecyclerView.ViewHolder{

        private final TextView _animeNameTextView;
        private final ImageView _animeImageView;
        private final AnimeAdapter.OnAnimeClickListener _onAnimeClickListener;
        private Anime _anime;

        public AnimeViewHolder(@NotNull View itemView, AnimeAdapter.OnAnimeClickListener onAnimeClickListener) {
            super(itemView);
            this._onAnimeClickListener = onAnimeClickListener;

            // Text
            _animeNameTextView = itemView.findViewById(R.id.anime_name);
            _animeNameTextView.setOnClickListener(view -> {
                if (_anime != null) {
                    _onAnimeClickListener.onAnimeClick(_anime);
                }
            });

            // Image
            _animeImageView = itemView.findViewById(R.id.anime_image);
            _animeImageView.setOnClickListener(view -> {
                if (_anime != null) {
                    _onAnimeClickListener.onAnimeClick(_anime);
                }
            });
        }

        public void bind(Anime anime) {
            _anime = anime;
            _animeNameTextView.setText(anime.title);
            if (anime.mainPicture != null) {
                Picasso.get().load(_anime.mainPicture.medium).into(_animeImageView);
            }
            else {
                Picasso.get().load(R.drawable.ic_launcher_foreground).into(_animeImageView);
            }
        }

}
