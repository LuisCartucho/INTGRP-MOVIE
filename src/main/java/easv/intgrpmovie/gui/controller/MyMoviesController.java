package easv.intgrpmovie.gui.controller;

import easv.intgrpmovie.MyMoviesApplication;
import easv.intgrpmovie.be.Category;
import easv.intgrpmovie.be.Movie;
import easv.intgrpmovie.bll.CatMovieManager;
import easv.intgrpmovie.bll.MovieManager;
import easv.intgrpmovie.dal.MovieDAO;
import easv.intgrpmovie.gui.model.CategoryModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class MyMoviesController implements Initializable {

    @FXML
    private ListView<String> lstViewMain; // Left ListView (categories)
    @FXML
    private ListView<String> lstViewcategory; // Middle ListView (categories)
    //@FXML
    //private ListView<String> lstViewMovie; // Right ListView (media files)
    @FXML
    private ListView<Movie> lstViewMovie;
    @FXML
    private ComboBox<String> genreComboBox; // Not used for now

    private CategoryModel categoryModel = new CategoryModel();
    private CatMovieManager catMovieManager;
    private MovieManager movieManager= new MovieManager();
    private MovieDAO movieDAO = new MovieDAO();

    // Constructor no longer needed since FXML injection will handle it
    public MyMoviesController() {
        this.catMovieManager = new CatMovieManager(); // Initialize the manager here
    }


    // Button to open Add Movie dialog
    public void btnaddMovie(ActionEvent actionEvent) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MyMoviesApplication.class.getResource("add-edit-movies.fxml"));
            Stage stage = new Stage();
            Scene scene = new Scene(fxmlLoader.load());
            stage.setTitle("Add Movie");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Button to open Edit Movie dialog
    public void btnEditMovie(ActionEvent actionEvent) {

        Movie selectedMovie = lstViewMovie.getSelectionModel().getSelectedItem();

        if (selectedMovie == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Movie Selected");
            alert.setHeaderText(null);
            alert.setContentText("Please select a movie to edit.");
            alert.showAndWait();
            return;
        }

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MyMoviesApplication.class.getResource("add-edit-movies.fxml"));
            Parent root = fxmlLoader.load();

            AddEditMoviesController controller = fxmlLoader.getController();
            controller.setMovie(selectedMovie); // Pass the selected movie to the controller

            Stage stage = new Stage();
            stage.setTitle("Edit Movie");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Add movie categories to the middle ListView
        // Fetch categories from the model
        List<Category> categories = categoryModel.getCategory();

        // Populate the ListView with category names
        for (Category cat : categories) {
            lstViewcategory.getItems().add(cat.getName());
        }

        // Set up listener for category selection
        lstViewcategory.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                updateMediaList(newValue); // Update right ListView when a category is selected
            }
        });

        lstViewMain.getItems().addAll("Movie");

        lstViewMovie.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) { // Double-click detected
                Movie selectedMovie = lstViewMovie.getSelectionModel().getSelectedItem();
                if (selectedMovie != null) {
                    openMoviePlayer(selectedMovie);
                    updateLastView(selectedMovie.getID());
                }   else {
                System.out.println("No movie selected!");
                }
            }
        });
    }


    private void openMoviePlayer(Movie selectedMovie) {
        try {
            // Load the FXML file for the media player view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/easv/intgrpmovie/mediaDisplay.fxml"));

            if (loader.getLocation() == null) {
                System.out.println("FXML file location is not set.");
            } else {
                System.out.println("FXML file loaded successfully.");
            }

            Parent root = loader.load();

            // Get the controller for the new FXML file
            MediaDisplayController controller = loader.getController();
            controller.setMovie(selectedMovie); // Pass the selected movie to the controller

            // Create a new scene for the new window
            Scene scene = new Scene(root);

            // Create a new Stage (a new window)
            Stage newStage = new Stage();
            newStage.setTitle("Movie Player");
            newStage.setScene(scene);
            newStage.show(); // Show the new window

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateMediaList(String categoryName) {
        // Fetch the list of movies from the CatMovieManager
        List<Movie> movies = catMovieManager.getMoviesByCategory(categoryName); // Ensure this returns List<Movie>

        // Update the ListView with Movie objects
        ObservableList<Movie> observableList = FXCollections.observableArrayList(movies);
        lstViewMovie.setItems(observableList);

        // Handle the case where no movies are found
        if (movies.isEmpty()) {
            lstViewMovie.getItems().add(new Movie(0, "No media files found for " + categoryName, 0, "", ""));
        }

    }

    @FXML
    private void onBtnDelete() {
        // Get the selected movie from the ListView
        Movie selectedMovie = lstViewMovie.getSelectionModel().getSelectedItem();

        if (selectedMovie != null) {
            // Get the movie ID
            int movieId = selectedMovie.getID();

            // Show a confirmation dialog
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Movie");
            alert.setHeaderText("Are you sure you want to delete this movie?");
            alert.setContentText("Movie ID: " + movieId);

            if (alert.showAndWait().get() == ButtonType.OK) {
                // Call the service layer to delete the movie from the database
                movieManager.deleteMovie(movieId);

                // Refresh the movie list after deletion
                refreshMovieList();
            }
        } else {
            // If no movie is selected, show a warning
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Movie Selected");
            alert.setHeaderText("Please select a movie to delete.");
            alert.showAndWait();
        }
    }
    private void refreshMovieList() {
        // Code to refresh the movie list in the ListView
        lstViewMovie.getItems().clear();
        lstViewMovie.getItems().addAll(movieManager.getMoviesByCategory("Movie"));
    }

    private void updateLastView(int movieId) {
        try {
            // Get the current date
            Date lastViewDate = Date.valueOf(LocalDate.now());

            // Call the DAO method to update the last view date
            movieDAO.updateLastView(movieId, lastViewDate);

            System.out.println("Last view date updated successfully for movie ID: " + movieId);
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error updating last view date: " + e.getMessage());
        }
    }
}




