package pmm.ignacio.theanimedatabase;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import pmm.ignacio.theanimedatabase.Anime.Anime;

public class AnimeViewHolder extends RecyclerView.ViewHolder{

        private final TextView _animeNameTextView;
        private final AnimeAdapter.OnAnimeClickListener _onAnimeClickListener;
        private Anime _anime;

        public AnimeViewHolder(@NotNull View itemView, AnimeAdapter.OnAnimeClickListener onAnimeClickListener) {
            super(itemView);
            _animeNameTextView = itemView.findViewById(R.id.anime_name);
            this._onAnimeClickListener = onAnimeClickListener;
            _animeNameTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (_anime != null) {
                        _onAnimeClickListener.onAnimeClick(_anime);
                    }
                }
            });
        }

        public void bind(Anime anime) {
            _anime = anime;
            _animeNameTextView.setText(anime.title);
        }

}
