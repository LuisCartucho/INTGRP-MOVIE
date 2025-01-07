package easv.intgrpmovie.gui.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;

import java.io.IOException;



public class addeditMoviesController {

    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("add-removeMovies.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private ComboBox<String> genreComboBox;

    @FXML
    public void initialize() {
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
    }
}

