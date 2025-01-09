package easv.intgrpmovie.gui.controller;

import easv.intgrpmovie.dal.DBConnection;
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

    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("add-edit-movies.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Add/Edit Movie");
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private Button chooseButton, btnSave;

    @FXML
    private ComboBox<String> genreComboBox;

    @FXML
    private TextField txtFieldTitle, txtFieldRating, txtFieldFile;

    DBConnection C = new DBConnection();

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

        // Convert LocalDate for lastView (default date or current date)
        LocalDate lastView = LocalDate.now(); // Current date

        // SQL query to insert the movie into the Movie table
        String insertMovieQuery = "INSERT INTO Movie (name, rating, filelink, lastview) VALUES (?, ?, ?, ?)";
        try (Connection connection = C.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(insertMovieQuery, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, title);
            pstmt.setInt(2, rating);
            pstmt.setString(3, fileLink);
            pstmt.setDate(4, Date.valueOf(lastView));
            pstmt.executeUpdate(); // Execute the update to insert the movie

            // Get the generated movieId (the ID of the movie just inserted)
            ResultSet generatedKeys = pstmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                int movieId = generatedKeys.getInt(1);
                System.out.println("Movie saved with ID: " + movieId);
                insertMovieCategory(movieId, selectedCategory, connection); // Now, insert the relationship into the CatMovie table
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Database error: " + e.getMessage());
        }
    }

    private void insertMovieCategory(int movieId, String selectedCategory, Connection connection) {
        // SQL query to insert the relationship into the CatMovie table
        String insertCategoryQuery = "INSERT INTO CatMovie (movieId, categoryId) VALUES (?, ?)";

        // Get the categoryId based on the selected category
        int categoryId = getCategoryId(selectedCategory, connection);

        if (categoryId != -1) {
            try (PreparedStatement pstmt = connection.prepareStatement(insertCategoryQuery)) {
                // Set the parameters for the relationship
                pstmt.setInt(1, movieId);      // movieId
                pstmt.setInt(2, categoryId);   // categoryId

                // Execute the update to insert the relationship
                pstmt.executeUpdate();
                System.out.println("Category relationship added for movie ID: " + movieId);

            } catch (SQLException e) {
                e.printStackTrace();
                showError("Error inserting category relationship: " + e.getMessage());
            }
        } else {
            showError("Invalid category selected.");
        }
    }

    private int getCategoryId(String categoryName, Connection connection) {
        // SQL query to get the categoryId based on the category name
        String query = "SELECT id FROM Category WHERE name = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, categoryName);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("id"); // Return the category ID
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;  // Return -1 if the category doesn't exist
    }

    public void showError(String message) {
        // Create an alert of type ERROR
        Alert alert = new Alert(Alert.AlertType.ERROR);

        // Set the title and header for the alert
        alert.setTitle("Error");
        alert.setHeaderText("An error occurred");

        // Set the content of the alert with the error message
        alert.setContentText(message);

        // Show the alert and wait for the user to close it
        alert.showAndWait();
    }

    @FXML
    private void handleChooseFile() {
        // Create a FileChooser to open files
        FileChooser fileChooser = new FileChooser();

        // Optionally, you can filter file types (e.g., image files, text files)
        FileChooser.ExtensionFilter extensionFilter =
                new FileChooser.ExtensionFilter("Movies", "*.mp4", "*.mpeg4");
        fileChooser.getExtensionFilters().add(extensionFilter);

        // Open the file chooser and capture the file selected
        File selectedFile = fileChooser.showOpenDialog(chooseButton.getScene().getWindow());
        if (selectedFile != null) {
            txtFieldFile.setText(selectedFile.getAbsolutePath());
            System.out.println("File chosen: " + selectedFile.getAbsolutePath());

            // Assume movieId is available here
            //int movieId = getSelectedMovieId(); // Replace with your logic to get the movieId

            // Update the lastView date when the file is chosen
            //updateLastView(movieId);
        } else {
            System.out.println("No file chosen.");
        }
    }

    // Method to update the 'lastView' date when the user opens a file link
    public void updateLastView(int movieId) {
        
        // Get the current date when the user opens the file
        LocalDate currentDate = LocalDate.now();

        // SQL query to update the 'lastView' column in the database
        String query = "UPDATE Movie SET lastview = ? WHERE id = ?";

        try (Connection connection = C.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {

            // Set the parameters for the prepared statement
            pstmt.setDate(1, Date.valueOf(currentDate));  // Set the current date
            pstmt.setInt(2, movieId);  // Set the movie ID for the specific movie

            // Execute the update
            pstmt.executeUpdate();
            System.out.println("Last view updated successfully for movie ID " + movieId);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {

        btnSave.setOnAction(event -> saveMovie());
        // Populate the ComboBox
        genreComboBox.getItems().addAll(
                "Action",
                "Animation",
                "Comedy",
                "Crime",
                "Drama",
                "Film-noir",
                "Horror",
                "Thriller",
                "War",
                "Western"
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
