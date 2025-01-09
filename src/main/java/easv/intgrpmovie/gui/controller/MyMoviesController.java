package easv.intgrpmovie.gui.controller;

import easv.intgrpmovie.MyMoviesApplication;
import easv.intgrpmovie.be.Category;
import easv.intgrpmovie.gui.model.CategoryModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class MyMoviesController implements Initializable {

    @FXML
    private ListView<String> lstViewMain; // Middle ListView (categories)

    @FXML
    private ListView<String> lstViewcategory;

    private CategoryModel categoryModel = new CategoryModel();
    @FXML
    private ListView<String> lstViewMovie; // Right ListView (media files)

    @FXML
    private ComboBox<String> genreComboBox; // Not used for now

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


    private void updateMediaList(String categoryName) {
        // Initialize a list to hold media files
        List<String> mediaFiles = new ArrayList<>();

        // Retrieve all files from the media directory
        File mediaDirectory = new File("src/main/resources/media");
        if (mediaDirectory.exists() && mediaDirectory.isDirectory()) {
            String[] allFiles = mediaDirectory.list((dir, name) -> name.endsWith(".mp4"));

            // Simulate filtering files based on the selected category (e.g., category prefix)
            if (allFiles != null) {
                for (String file : allFiles) {
                    if (file.toLowerCase().contains(categoryName.toLowerCase())) {
                        mediaFiles.add(file);
                    }
                }
            }
        }

        // Update the right ListView (lstViewMovie) with the filtered media files
        lstViewMovie.getItems().clear();
        if (!mediaFiles.isEmpty()) {
            lstViewMovie.getItems().addAll(mediaFiles);
        } else {
            lstViewMovie.getItems().add("No media files found for " + categoryName);
        }
    }
}


