package easv.intgrpmovie.bll;

import easv.intgrpmovie.be.Movie;

import java.util.ArrayList;
import java.util.List;

public class CatMovieManager {
    private MovieManager movieManager;
    private CategoryManager categoryManager;

    public CatMovieManager() {
        movieManager = new MovieManager();
        categoryManager = new CategoryManager();
    }


    public List<String> getCategoryNames() {
        return categoryManager.getCategoryNames();
    }

    public List<Movie> getMoviesByCategory(String categoryName) {
        return movieManager.getMoviesByCategory(categoryName);
    }
}
