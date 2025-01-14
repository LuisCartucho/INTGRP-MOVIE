package easv.intgrpmovie.gui.controller;

import easv.intgrpmovie.MyMoviesApplication;
import easv.intgrpmovie.be.Category;
import easv.intgrpmovie.be.Movie;
import easv.intgrpmovie.bll.CatMovieManager;
import easv.intgrpmovie.bll.MovieManager;
import easv.intgrpmovie.dal.CategoryDAO;
import easv.intgrpmovie.dal.MovieDAO;
import easv.intgrpmovie.gui.model.CategoryModel;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class MyMoviesController implements Initializable {

    @FXML private ListView<String> lstMain;
    @FXML private ListView<String> lstCategory; // Category List View
    @FXML private ListView<Movie> lstMovie; // Movie List View
    @FXML private TextField txtMovieTitle; // Text Field for movie title search
    @FXML private Slider ratingSlider; // Minimum rating Slider
    @FXML private Label ratingLabel;

    private List<Movie> movies = new ArrayList<>();
    private CategoryModel categoryModel = new CategoryModel();
    private CatMovieManager catMovieManager;
    private MovieManager movieManager= new MovieManager();
    private MovieDAO movieDAO = new MovieDAO();
    private CategoryDAO categoryDAO = new CategoryDAO();


    // Constructor no longer needed since FXML injection will handle it
    public MyMoviesController() {
        this.catMovieManager = new CatMovieManager();
    }

    // Add Movie button handle
    public void btnAddMovie(ActionEvent actionEvent) {
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

    public void btnEditMovie(ActionEvent actionEvent) {
        Movie selectedMovie = lstMovie.getSelectionModel().getSelectedItem();

        if (selectedMovie == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Movie Selected");
            alert.setHeaderText(null);
            alert.setContentText("Please select a movie to edit.");
            alert.showAndWait();
            return;
        }

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MyMoviesApplication.class.getResource("EditMovies.fxml"));
            Parent root = fxmlLoader.load();

            EditController controller = fxmlLoader.getController();
            controller.setMovie(selectedMovie);

            Stage stage = new Stage();
            stage.setTitle("Edit Movie");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();



        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Could not open the Edit Movie window");
            alert.setContentText("An unexpected error occurred: " + e.getMessage());
            alert.showAndWait();
        }
    }




    @Override
    public void initialize(URL location, ResourceBundle resources) {
        List<Category> categories = categoryModel.getCategory();
        // Populate the Category List View
        for (Category cat : categories) {
            lstCategory.getItems().add(cat.getName());
        }

        // Set up listener for category selection
        lstCategory.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                updateMediaList(newValue); // Update right ListView when a category is selected
            }
        });

        lstMain.getItems().addAll("Movie");

        // Initialize Filter functionality
        txtMovieTitle.textProperty().addListener((observable, oldValue, newValue) -> {
            //System.out.println("Text changed to: " + newValue); // Debugging line
            filterMovies(newValue);
        });

        // Add listener for rating slider
        ratingSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            filterMoviesByRating(newValue.doubleValue()); // Filter based on slider value
        });

        ratingLabel.textProperty().bind(Bindings.format("%.1f", ratingSlider.valueProperty()));

        lstMovie.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) { // Double-click detected
                Movie selectedMovie = lstMovie.getSelectionModel().getSelectedItem();
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
        List<Movie> fetchedMovies = catMovieManager.getMoviesByCategory(categoryName); // Ensure this returns List<Movie>

        // Update the movies list
        movies = fetchedMovies; // Store the fetched movies

        // Update the ListView with Movie objects
        ObservableList<Movie> observableList = FXCollections.observableArrayList(fetchedMovies);
        lstMovie.setItems(observableList);

        // Apply filtering (by title and rating) after updating the movies list
        filterMovies(txtMovieTitle.getText()); // This will call filterMovies and apply both filters

        // Handle the case where no movies are found
        if (fetchedMovies.isEmpty()) {
            lstMovie.getItems().add(new Movie(0, "No media files found for " + categoryName, 0, "", ""));
        }
    }

    private void filterMovies(String filterText) {
        double minRating = ratingSlider.getValue(); // Get the minimum rating from the slider

        // If the filter text is empty, show all movies
        if (filterText == null || filterText.isEmpty()) {
            filterMoviesByRating(minRating); // Filter by rating only
        } else {
            // Filter movies by both title and rating
            List<Movie> filteredMovies = movies.stream()
                    .filter(movie -> movie.getName().toLowerCase().contains(filterText.toLowerCase())
                            && movie.getRating() >= minRating)
                    .collect(Collectors.toList());

            // Update the ListView with the filtered list
            lstMovie.setItems(FXCollections.observableList(filteredMovies));
        }
    }

    private void filterMoviesByRating(double minRating) {
        // Filter movies based on the rating (minRating) from the slider
        List<Movie> filteredMovies = movies.stream()
                .filter(movie -> movie.getRating() >= minRating)
                .collect(Collectors.toList());

        // Update the ListView with the filtered list
        lstMovie.setItems(FXCollections.observableList(filteredMovies));
    }

    @FXML
    private void btnDeleteMovie() {
        // Get the selected movie from the ListView
        Movie selectedMovie = lstMovie.getSelectionModel().getSelectedItem();

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
        lstMovie.getItems().clear();
        lstMovie.getItems().addAll(movieManager.getMoviesByCategory("Movie"));
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

    public void onBtnNewCategory(ActionEvent actionEvent) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/easv/intgrpmovie/NewCategory.fxml"));
            Parent root = fxmlLoader.load();

            Stage stage = new Stage();
            stage.setTitle("Add New Category");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL); // Makes it modal
            stage.showAndWait();

            // Optionally, refresh the categories list after the new category is added
            refreshCategoriesList();

        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Could not open the Add New Category window");
            alert.setContentText("An unexpected error occurred: " + e.getMessage());
            alert.showAndWait();
        }
    }
    private void refreshCategoriesList() {
        List<String> categories = categoryDAO.getCategoryNames();
        lstCategory.getItems().setAll(categories);
    }
    @FXML
    private void onDeleteCategory() {
        String selectedCategory = lstCategory.getSelectionModel().getSelectedItem();

        if (selectedCategory != null) {
            // Show confirmation dialog
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Category");
            alert.setHeaderText("Are you sure you want to delete this category?");
            alert.setContentText("This action cannot be undone.");
            Optional<ButtonType> result = alert.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {
                // Query the database to get the category ID based on the category name
                int categoryId = categoryDAO.getCategoryIdByName(selectedCategory);

                // Now pass the category ID to the delete method (which also deletes associated movies)
                categoryDAO.deleteCategory(categoryId);
                refreshCategoriesList();  // Refresh the categories list after deletion
            }
        } else {
            showError("No category selected.");
        }
    }

    // Helper method to show error messages
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(message);
        alert.showAndWait();
    }
}