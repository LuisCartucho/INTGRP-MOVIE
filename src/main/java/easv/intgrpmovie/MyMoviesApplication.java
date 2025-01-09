/**
 * =====================================================
 * Exam project - Private Movie Collection
 * EASV CSe2024 Alex, Davide, Luís, David and Phuoc Lam
 * =====================================================
 */

package easv.intgrpmovie;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MyMoviesApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("my-movies.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("MyMovies");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    } // Lansăm aplicația JavaFX
}
