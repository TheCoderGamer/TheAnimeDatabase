package pmm.ignacio.theanimedatabase.RecyclerView;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import pmm.ignacio.theanimedatabase.Anime.data.Anime;
import pmm.ignacio.theanimedatabase.R;

public class AnimeViewHolder extends RecyclerView.ViewHolder{

        private final TextView _animeNameTextView;
        private final AnimeAdapter.OnAnimeClickListener _onAnimeClickListener;
        private Anime _anime;

        public AnimeViewHolder(@NotNull View itemView, AnimeAdapter.OnAnimeClickListener onAnimeClickListener) {
            super(itemView);
            _animeNameTextView = itemView.findViewById(R.id.anime_name);
            this._onAnimeClickListener = onAnimeClickListener;
            _animeNameTextView.setOnClickListener(view -> {
                if (_anime != null) {
                    _onAnimeClickListener.onAnimeClick(_anime);
                }
            });
        }

        public void bind(Anime anime) {
            _anime = anime;
            _animeNameTextView.setText(anime.title);
        }

}
