package easv.intgrpmovie;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;

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
}
