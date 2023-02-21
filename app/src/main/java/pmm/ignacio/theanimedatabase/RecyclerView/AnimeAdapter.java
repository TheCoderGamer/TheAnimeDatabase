package pmm.ignacio.theanimedatabase.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import pmm.ignacio.theanimedatabase.Anime.data.Anime;
import pmm.ignacio.theanimedatabase.R;

public class AnimeAdapter extends RecyclerView.Adapter<AnimeViewHolder> {

    public interface OnAnimeClickListener {
        void onAnimeClick(Anime anime);
    }

    public final List<Anime> _animeList;
    private final OnAnimeClickListener _onAnimeClickListener;


    public AnimeAdapter(List<Anime> animeList, OnAnimeClickListener onAnimeClickListener) {
        _animeList = animeList;
        _onAnimeClickListener = onAnimeClickListener;
    }

    @NotNull
    @Override
    public AnimeViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_anime_item, parent, false);
        return new AnimeViewHolder(view, _onAnimeClickListener);
    }

    @Override
    public void onBindViewHolder(AnimeViewHolder holder, int position) {
        holder.bind(_animeList.get(position));
    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
