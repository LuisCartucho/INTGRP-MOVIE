module easv.intgrpmovie {
    requires javafx.controls;
    requires javafx.fxml;


    opens easv.intgrpmovie to javafx.fxml;
    exports easv.intgrpmovie;
}