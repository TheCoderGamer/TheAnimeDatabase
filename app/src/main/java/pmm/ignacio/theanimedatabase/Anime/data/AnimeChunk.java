package pmm.ignacio.theanimedatabase.Anime.data;

import java.util.List;

public class AnimeChunk {
    public List<AnimeNode> data;
    public AnimePagin paging;
}

class AnimePagin {
    public String next;
    public String previous;
}

