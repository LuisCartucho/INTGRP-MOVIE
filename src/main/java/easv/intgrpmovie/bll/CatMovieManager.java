package easv.intgrpmovie.bll;

import java.util.List;

public class CatMovieManager {
    private MovieManager movieManager;
    private CategoryManager categoryManager;

    public CatMovieManager() {
        movieManager = new MovieManager();
        categoryManager = new CategoryManager();
    }

    public List<String> getMoviesByCategory(String categoryName) {
        return movieManager.getMoviesByCategory(categoryName);
    }

    public List<String> getCategoryNames() {
        return categoryManager.getCategoryNames();
    }
}
