package easv.intgrpmovie.gui.controller;

import easv.intgrpmovie.MyMoviesApplication;
import easv.intgrpmovie.be.Category;
import easv.intgrpmovie.be.Movie;
import easv.intgrpmovie.bll.CatMovieManager;
import easv.intgrpmovie.gui.model.CategoryModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
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
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MyMoviesApplication.class.getResource("add-edit-movies.fxml"));
            Stage stage = new Stage();
            Scene scene = new Scene(fxmlLoader.load());
            stage.setTitle("Edit Movie");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
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

            // Create a new scene and set it to the stage
            Scene scene = new Scene(root);
            Stage stage = (Stage) lstViewMovie.getScene().getWindow(); // Assuming you're using the same window
            stage.setScene(scene);
            stage.show();
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
}


