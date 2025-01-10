package easv.intgrpmovie.bll;

import easv.intgrpmovie.be.Movie;
import easv.intgrpmovie.dal.CatMovieDAO;
import easv.intgrpmovie.dal.MovieDAO;

import java.util.List;

public class MovieManager {
    private final MovieDAO movieDAO = new MovieDAO();  // Keep final, no reassignment

    public List<Movie> getMovies() {
        List<Movie> movies = movieDAO.getMovie();
        return movies;
    }

    public List<Movie> getMoviesByCategory(String categoryName) {
        return movieDAO.getMoviesByCategory(categoryName);
    }

    // Method to delete the movie from both the CatMovie and Movie tables
    public void deleteMovie(int movieId) {
        movieDAO.deleteMovieById(movieId);
    }
}




