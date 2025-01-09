package easv.intgrpmovie.gui.model;

import easv.intgrpmovie.be.Movie;
import easv.intgrpmovie.bll.MovieManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class MovieModel {
    private final MovieManager movieManager = new MovieManager();

    private ObservableList<Movie> movies = FXCollections.observableArrayList();

    public MovieModel() {
        movies.setAll(movieManager.getMovies());
    }

    public ObservableList<Movie> getMovies() {
        return movies;
    }
}
