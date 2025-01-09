package easv.intgrpmovie.bll;

import easv.intgrpmovie.be.Movie;
import easv.intgrpmovie.dal.MovieDAO;

import java.util.List;

public class MovieManager {
    private final MovieDAO movieDAO = new MovieDAO();

    public List<Movie> getMovies() {
        List<Movie> movies = movieDAO.getMovie();
        return movies;
    }
}
