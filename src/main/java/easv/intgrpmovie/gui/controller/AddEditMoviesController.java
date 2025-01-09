package easv.intgrpmovie.gui.controller;

import easv.intgrpmovie.dal.CategoryDAO;
import easv.intgrpmovie.dal.MovieDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;

public class AddEditMoviesController {

    @FXML
    private Button chooseButton, btnSave;

    @FXML
    private ComboBox<String> genreComboBox;

    @FXML
    private TextField txtFieldTitle, txtFieldRating, txtFieldFile;

    private MovieDAO movieDAO = new MovieDAO();
    private CategoryDAO categoryDAO = new CategoryDAO();

    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("add-edit-movies.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Add/Edit Movie");
        stage.setScene(scene);
        stage.show();
    }

    private void saveMovie() {
        // Get input values from the UI
        String title = txtFieldTitle.getText();
        String ratingText = txtFieldRating.getText().trim();
        String fileLink = txtFieldFile.getText();

        if (title.isEmpty()) {
            showError("Title cannot be empty.");
            return;
        }

        if (ratingText.isEmpty()) {
            showError("Rating cannot be empty.");
            return;
        }

        int rating;
        try {
            rating = Integer.parseInt(ratingText); // Parse text to int
        } catch (NumberFormatException e) {
            showError("Rating must be an integer.");
            return;
        }

        if (fileLink.isEmpty()) {
            showError("File link cannot be empty.");
            return;
        }

        // Get the selected category from the ComboBox
        String selectedCategory = genreComboBox.getValue();
        if (selectedCategory == null) {
            showError("Please select a category.");
            return;
        }

        LocalDate lastView = LocalDate.now(); // Default to current date

        try {
            // Insert movie and get movieId
            int movieId = movieDAO.insertMovie(title, rating, fileLink, Date.valueOf(lastView));

            if (movieId != -1) {
                // Get categoryId
                int categoryId = categoryDAO.getCategoryId(selectedCategory);
                if (categoryId != -1) {
                    // Insert relationship between movie and category
                    categoryDAO.insertMovieCategory(movieId, categoryId);
                    System.out.println("Movie and category saved successfully!");
                } else {
                    showError("Invalid category selected.");
                }
            } else {
                showError("Error saving movie.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Database error: " + e.getMessage());
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("An error occurred");
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleChooseFile() {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("Movies", "*.mp4", "*.mpeg4");
        fileChooser.getExtensionFilters().add(extensionFilter);

        File selectedFile = fileChooser.showOpenDialog(chooseButton.getScene().getWindow());
        if (selectedFile != null) {
            txtFieldFile.setText(selectedFile.getAbsolutePath());
        } else {
            System.out.println("No file chosen.");
        }
    }

    @FXML
    public void initialize() {
        btnSave.setOnAction(event -> saveMovie());
        genreComboBox.getItems().addAll(
                "Action", "Animation", "Comedy", "Crime", "Drama", "Film-noir", "Horror", "Thriller", "War", "Western"
        );

        /*
        //Example of how you might handle movie selection
        movieListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) { // Double-click to view the movie
                Movie selectedMovie = movieListView.getSelectionModel().getSelectedItem();
                if (selectedMovie != null) {
                    // Call updateLastView with the movie ID
                    updateLastView(selectedMovie.getId());
                }
            }
        });
        */

    }
}
