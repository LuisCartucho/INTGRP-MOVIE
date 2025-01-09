package easv.intgrpmovie.be;

public class CatMovie {
    private int Id;
    private int CategoryId;
    private int MovieId;

    public CatMovie(int id, int categoryId, int movieId) {
        Id = id;
        CategoryId = categoryId;
        MovieId = movieId;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public int getCategoryId() {
        return CategoryId;
    }

    public void setCategoryId(int categoryId) {
        CategoryId = categoryId;
    }

    public int getMovieId() {
        return MovieId;
    }

    public void setMovieId(int movieId) {
        MovieId = movieId;
    }
}
