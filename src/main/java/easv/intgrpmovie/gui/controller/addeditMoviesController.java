package easv.intgrpmovie.gui.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
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
    private Button chooseButton;
    @FXML
    private TextField txtFilePath;

    @FXML
    private ComboBox<String> genreComboBox;

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
            if(selectedFile != null) {
                txtFilePath.setText(selectedFile.getAbsolutePath());
            }

        if (selectedFile != null) {
            System.out.println("File chosen: " + selectedFile.getAbsolutePath());
            // Do something with the chosen file, e.g., display or save it
        } else {
            System.out.println("No file chosen.");
        }
    }

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

