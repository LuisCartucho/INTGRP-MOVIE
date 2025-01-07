package easv.intgrpmovie;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloController {

    @FXML
    private ListView<String> category;

    @FXML
    private ListView<String> movieCategoriesList;

    public void initialize() {
        // Adaugăm categoriile de filme în ListView
        movieCategoriesList.getItems().addAll(
                "Action", "Animation", "Comedy", "Crime", "Drama", "Film-noir", "Horror", "Thriller", "War", "Western"
        );
        category.getItems().addAll("Movie");
    }

    public void btnaddMovie(ActionEvent actionEvent) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("add-removeMovies.fxml"));

            Stage stage = new Stage();
            Scene scene = new Scene(fxmlLoader.load());
            stage.setTitle("add-remove-movie");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

