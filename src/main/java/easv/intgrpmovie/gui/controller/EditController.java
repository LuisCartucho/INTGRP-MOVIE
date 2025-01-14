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

import java.sql.SQLException;
import java.util.List;

public class EditController {

    @FXML
    private TextField txtFieldTitleEdit, txtFieldRatingEdit, txtFieldFileEdit;
    @FXML
    private ListView<String> lstViewCatMovie; // ListView for categories

    private Movie movie; // Store the movie being edited
    private MovieDAO movieDAO = new MovieDAO();
    private CategoryDAO categoryDAO = new CategoryDAO(); // DAO for handling categories

    public void setMovie(Movie movie) {
        this.movie = movie;
        if (movie != null) {
            txtFieldTitleEdit.setText(movie.getName());
            txtFieldRatingEdit.setText(String.valueOf(movie.getRating()));
            txtFieldFileEdit.setText(movie.getFileLink());

            // Load categories and preselect the ones associated with the movie
            List<String> allCategories = categoryDAO.getCategoryNames(); // All categories
            lstViewCatMovie.getItems().setAll(allCategories);
            lstViewCatMovie.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

            // Preselect the categories associated with the movie
            List<String> movieCategories = categoryDAO.getCategoriesForMovie(movie.getID());
            for (String category : movieCategories) {
                lstViewCatMovie.getSelectionModel().select(category);
            }
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

            boolean movieUpdateSuccess = movieDAO.updateMovie(movie);
            if (!movieUpdateSuccess) {
                showError("Failed to update the movie.");
                return;
            }

            // Update categories
            List<String> selectedCategories = lstViewCatMovie.getSelectionModel().getSelectedItems();

            // Clear existing categories
            categoryDAO.deleteMovieCategories(movie.getID());

            // Insert new categories
            for (String categoryName : selectedCategories) {
                int categoryId = categoryDAO.getCategoryId(categoryName);
                if (categoryId != -1) {
                    categoryDAO.insertMovieCategory(movie.getID(), categoryId);
                }
            }

            showInfo("Movie and categories updated successfully!");

        } catch (NumberFormatException e) {
            showError("Rating must be a valid number.");
        } catch (SQLException e) {
            e.printStackTrace();
            showError("An error occurred while updating categories.");
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

