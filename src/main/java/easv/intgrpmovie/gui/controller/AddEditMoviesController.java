package easv.intgrpmovie.gui.controller;

import easv.intgrpmovie.be.Category;
import easv.intgrpmovie.be.Movie;
import easv.intgrpmovie.dal.CategoryDAO;
import easv.intgrpmovie.dal.MovieDAO;
import easv.intgrpmovie.gui.model.CategoryModel;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class AddEditMoviesController {

    private final CategoryModel categoryModel = new CategoryModel();

    @FXML
    private ListView<String> lstViewCatMovie;

    @FXML
    private Button chooseButton, btnSave;

    @FXML
    private TextField txtFieldTitle, txtFieldRating, txtFieldFile;

    private final MovieDAO movieDAO = new MovieDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();
    private Movie movie;

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

        // Get all selected categories from the ListView
        ObservableList<String> selectedCategories = lstViewCatMovie.getSelectionModel().getSelectedItems();
        if (selectedCategories.isEmpty()) {
            showError("Please select at least one category.");
            return;
        }

        // Define the target directory (e.g., "media")
        Path mediaDir = Paths.get("media");

        // Ensure the media directory exists
        if (Files.notExists(mediaDir)) {
            try {
                Files.createDirectories(mediaDir); // Create the directory if it doesn't exist
            } catch (IOException e) {
                showError("Error creating media directory.");
                return;
            }
        }

        // Define the source file and target file path
        Path sourceFile = Paths.get(fileLink);

        // Use the title for the new file name (and ensure to keep the original file extension)
        String fileExtension = getFileExtension(sourceFile);  // Get the file extension
        Path targetFile = mediaDir.resolve(title + fileExtension); // Save with title as filename

        // Copy the file to the media directory
        try {
            Files.copy(sourceFile, targetFile, StandardCopyOption.REPLACE_EXISTING); // Replace if file exists
        } catch (IOException e) {
            showError("Error copying the file to the media directory.");
            return;
        }

        LocalDate lastView = LocalDate.now(); // Default to current date

        try {
            // Insert movie and get movieId
            int movieId = movieDAO.insertMovie(title, rating, targetFile.toString(), Date.valueOf(lastView));

            if (movieId != -1) {
                for (String category : selectedCategories) {
                    // Get categoryId for each selected category
                    int categoryId = categoryDAO.getCategoryId(category);
                    if (categoryId != -1) {
                        // Insert relationship between movie and each category
                        categoryDAO.insertMovieCategory(movieId, categoryId);
                    } else {
                        showError("Invalid category: " + category);
                    }
                }
                showSuccess("Movie and categories saved successfully!");
            } else {
                showError("Error saving movie.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Database error: " + e.getMessage());
        }
    }

    // Helper method to get the file extension (e.g., .mp3, .mp4, etc.)
    private String getFileExtension(Path file) {
        String fileName = file.getFileName().toString();
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0) {
            return fileName.substring(dotIndex);  // Returns the extension including the dot (e.g., .mp3, .mp4)
        } else {
            return "";  // If no extension, return empty string
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("An error occurred");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText("Operation Successful");
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

        List<Category> categories = categoryModel.getCategory();

        // Populate the ListView with category names
        for (Category cat : categories) {
            lstViewCatMovie.getItems().add(cat.getName());
        }
        lstViewCatMovie.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
        loadMovieDetails(); // Load movie details from the database
    }

    private void loadMovieDetails() {
        if (movie != null) {
            try {
                List<Movie> movies = movieDAO.getMovie();
                for (Movie m : movies) {
                    if (m.getID() == movie.getID()) {
                        txtFieldTitle.setText(m.getName());
                        txtFieldRating.setText(String.valueOf(m.getRating()));
                        txtFieldFile.setText(m.getFileLink());
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Error loading movie details: " + e.getMessage());
            }
        }
    }
}