package easv.intgrpmovie.gui.controller;

import easv.intgrpmovie.be.Category;
import easv.intgrpmovie.be.Movie;
import easv.intgrpmovie.dal.CategoryDAO;
import easv.intgrpmovie.dal.MovieDAO;
import easv.intgrpmovie.gui.model.CategoryModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

public class EditController {

    @FXML
    private TextField txtFieldTitleEdit, txtFieldRatingEdit, txtFieldFileEdit;

    private Movie movie; // Store the movie being edited
    private MovieDAO movieDAO = new MovieDAO();

    public void setMovie(Movie movie) {
        this.movie = movie;
        if (movie != null) {
            txtFieldTitleEdit.setText(movie.getName());
            txtFieldRatingEdit.setText(String.valueOf(movie.getRating()));
            txtFieldFileEdit.setText(movie.getFileLink());
        }
    }

    public void onBtnSave() {
        if (movie == null) {
            showError("No movie is being edited.");
            return;
        }

        String newTitle = txtFieldTitleEdit.getText();
        String newRating = txtFieldRatingEdit.getText();

        if (newTitle.isEmpty() || newRating.isEmpty()) {
            showError("All fields must be filled.");
            return;
        }

        try {
            int rating = Integer.parseInt(newRating);
            movie.setName(newTitle);
            movie.setRating(rating);

            boolean success = movieDAO.updateMovie(movie);
            if (success) {
                showInfo("Movie updated successfully!");
            } else {
                showError("Failed to update the movie.");
            }
        } catch (NumberFormatException e) {
            showError("Rating must be a valid number.");
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.showAndWait();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.showAndWait();
    }
}
