package easv.intgrpmovie.gui.controller;

import easv.intgrpmovie.be.Movie;
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


public class addeditMoviesController {

    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("add-removeMovies.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private Button chooseButton;

    @FXML
    private ComboBox<String> genreComboBox;

    @FXML
    private TextField txtFieldTitle;

    @FXML
    private TextField txtFieldRating;

    @FXML
    private TextField txtFieldFile;

    @FXML
    private Button btnSave;

    DBConnection C = new DBConnection();


    private void saveMovie() {
        // Get input values from the UI
        String title = txtFieldTitle.getText();
        int rating = Integer.parseInt(txtFieldRating.getText().trim()); // Parse text to int
        String fileLink = txtFieldFile.getText();

        // Use LocalDate for lastView (the default date)
        LocalDate lastView = LocalDate.of(2000, 12, 1);  // Default date: 2000-12-01

        // If you have a DatePicker for the user to choose a date:
        // LocalDate lastView = datePicker.getValue(); // If using a DatePicker component

        // SQL query to insert the data
        String query = "INSERT INTO Movie (name, rating, filelink, lastview) VALUES (?, ?, ?, ?)";

        try (Connection connection = C.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {

            // Set the parameters
            pstmt.setString(1, title);
            pstmt.setInt(2, rating);
            pstmt.setString(3, fileLink);

            // Convert LocalDate to java.sql.Date and set it
            pstmt.setDate(4, Date.valueOf(lastView));

            // Execute the update
            pstmt.executeUpdate();
            System.out.println("Movie saved successfully!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
        catch (NumberFormatException e) {
        // Handle invalid rating input
        System.err.println("Invalid rating: " + txtFieldRating.getText());
        showError("Rating must be an integer."); // Optionally show a dialog to the user
    }

}

        // Method to show an error message (optional, based on your implementation)
    private void showError(String message) {
        // Here you could show an error dialog, for now, it's just a print statement
    System.err.println("Error: " + message);
}


    @FXML
    private void handleChooseFile() {
        // Create a FileChooser to open files
        FileChooser fileChooser = new FileChooser();

        // Optionally, you can filter file types (e.g., image files, text files)
        FileChooser.ExtensionFilter extensionFilter =
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif");
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

       /** // Example of how you might handle movie selection:
        movieListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) { // Double-click to view the movie
                Movie selectedMovie = movieListView.getSelectionModel().getSelectedItem();
                if (selectedMovie != null) {
                    // Call updateLastView with the movie ID
                    updateLastView(selectedMovie.getId());
                }
            }
        });
    }*/
    }
}

