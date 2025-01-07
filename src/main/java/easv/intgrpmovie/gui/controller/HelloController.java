package easv.intgrpmovie.gui.controller;


import easv.intgrpmovie.HelloApplication;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class HelloController implements Initializable {

    @FXML
    private ListView<String> category;

    @FXML
    private ListView<String> movieCategoriesList;

    @FXML
    private ComboBox<String> genreComboBox;

    public void btnaddMovie(ActionEvent actionEvent) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("add-removeMovies.fxml"));

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
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("add-removeMovies.fxml"));

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
        // Adaugăm categoriile de filme în ListView
        movieCategoriesList.getItems().addAll(
                "Action", "Animation", "Comedy", "Crime", "Drama", "Film-noir", "Horror", "Thriller", "War", "Western"
        );
        category.getItems().addAll("Movie");
    }
}



