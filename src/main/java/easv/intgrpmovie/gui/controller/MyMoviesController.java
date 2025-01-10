package easv.intgrpmovie.gui.controller;

import easv.intgrpmovie.MyMoviesApplication;
import easv.intgrpmovie.be.Category;
import easv.intgrpmovie.bll.CatMovieManager;
import easv.intgrpmovie.dal.DBConnection;
import easv.intgrpmovie.gui.model.CategoryModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.scene.Parent;


import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class MyMoviesController implements Initializable {

    @FXML
    private ListView<String> lstViewMain; // Left ListView (categories)

    @FXML
    private ListView<String> lstViewcategory; // Middle ListView (categories)

    @FXML
    private ListView<String> lstViewMovie; // Right ListView (media files)

    @FXML
    private ComboBox<String> genreComboBox; // Not used for now

    private CategoryModel categoryModel = new CategoryModel();
    private CatMovieManager catMovieManager;

    // Constructor no longer needed since FXML injection will handle it
    public MyMoviesController() {
        this.catMovieManager = new CatMovieManager(); // Initialize the manager here
    }

    // Path to the media folder (relative path from project root)
    private final String mediaPath = "media";

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
    }

    public void updateMediaList(String categoryName) {
        // Fetch the list of movies from the CatMovieManager
        List<String> movieDetails = catMovieManager.getMoviesByCategory(categoryName);

        // Update the ListView in the UI with the fetched movie details
        ObservableList<String> observableList = FXCollections.observableArrayList(movieDetails);
        lstViewMovie.setItems(observableList);

        // If no movies were found for the category, display a default message
        if (movieDetails.isEmpty()) {
            lstViewMovie.getItems().add("No media files found for " + categoryName);
        }
    }

    @FXML
    private void handleMovieDoubleClick(MouseEvent event) {
        if (event.getClickCount() == 2) { // Detect double-click
            String selectedMovie = lstViewMovie.getSelectionModel().getSelectedItem();
            if (selectedMovie != null) {
                // Open the mediaDisplay.fxml
                openMediaPlayer(selectedMovie);
            }
        }
    }

    private void openMediaPlayer(String movieName) {
        try {
            // Load the mediaDisplay FXML
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/easv/intgrpmovie/mediaDisplay.fxml"));
            Parent root = fxmlLoader.load();

            // Get the controller of the mediaDisplay FXML
            MoviePlayerController controller = fxmlLoader.getController();
            controller.initialize(); // Pass the movie name or path

            // Create a new scene with the loaded FXML
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Media Player");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

