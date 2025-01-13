package easv.intgrpmovie.gui.controller;
import easv.intgrpmovie.be.Movie;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
public class MediaDisplayController {
    @FXML
    private MediaView mediaView;
    @FXML
    private Slider seekSlider, volumeSlider;
    @FXML
    private Button playPauseButton;
    @FXML
    private Label durationLabel;
    private MediaPlayer mediaPlayer;
    private boolean isPlaying = false;
    private int currentMediaIndex = 0;
    private String[] mediaFiles;

    @FXML
    public void initialize() {
        // Set default size for the MediaView
        mediaView.setFitWidth(800);
        mediaView.setFitHeight(650);
        // Load media files when the controller is initialized
        String mediaFolderPath = "media/bee.mp4";
        mediaFiles = findMediaFiles(mediaFolderPath);
        if (mediaFiles.length > 0) {
            initializeMediaPlayer();
        } else {
            System.out.println("No media files found in: " + mediaFolderPath);
        }

        // Add resize listener to make the video fullscreen when the window is maximized
        mediaView.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.widthProperty().addListener((o, oldWidth, newWidth) -> {
                    mediaView.setFitWidth(newWidth.doubleValue());
                });
                newScene.heightProperty().addListener((o, oldHeight, newHeight) -> {
                    mediaView.setFitHeight(newHeight.doubleValue());
                });
            }
        });
    }

    private String[] findMediaFiles(String folderPath) {
        List<String> mediaFileList = new ArrayList<>();
        File mediaFolder = new File(folderPath);
        if (mediaFolder.exists() && mediaFolder.isDirectory()) {
            loadMediaFiles(mediaFolder, mediaFileList);
        } else {
            System.out.println("Media folder not found: " + folderPath);
        }
        return mediaFileList.toArray(new String[0]);
    }

    private void loadMediaFiles(File folder, List<String> mediaFileList) {
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    loadMediaFiles(file, mediaFileList);
                } else if (isValidMediaFile(file)) {
                    mediaFileList.add(file.toURI().toString()); // Ensure proper URI format
                }
            }
        }
    }

    private boolean isValidMediaFile(File file) {
        String fileName = file.getName().toLowerCase();
        return fileName.endsWith(".mp4") || fileName.endsWith(".avi") || fileName.endsWith(".mkv");
    }

    private void initializeMediaPlayer() {
        if (mediaFiles == null || mediaFiles.length == 0) {
            System.out.println("No media files to play.");
            return;
        }
        Media media = new Media(mediaFiles[currentMediaIndex]);
        mediaPlayer = new MediaPlayer(media);
        mediaView.setMediaPlayer(mediaPlayer);
        mediaPlayer.setOnReady(() -> {
            seekSlider.setMax(mediaPlayer.getTotalDuration().toSeconds());
            mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
                seekSlider.setValue(newValue.toSeconds());
                updateDuration();
            });
        });
        mediaPlayer.setOnEndOfMedia(() -> onNextButtonClicked());
        volumeSlider.setValue(0.5); // Default volume
        volumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (mediaPlayer != null) {
                mediaPlayer.setVolume(newValue.doubleValue());
            }
        });
        playPauseButton.setText("▶");
    }

    @FXML
    private void onPlayPauseButtonClicked() {
        if (mediaPlayer == null) return;
        if (isPlaying) {
            mediaPlayer.pause();
            playPauseButton.setText("▶");
        } else {
            mediaPlayer.play();
            playPauseButton.setText("⏸");
        }
        isPlaying = !isPlaying;
    }

    @FXML
    private void onSeekSliderReleased() {
        if (mediaPlayer != null) {
            mediaPlayer.seek(Duration.seconds(seekSlider.getValue()));
        }
    }

    @FXML
    private void onRewindButtonClicked() {
        if (mediaPlayer != null) {
            mediaPlayer.seek(mediaPlayer.getCurrentTime().subtract(Duration.seconds(10)));
        }
    }

    @FXML
    private void onFastForwardButtonClicked() {
        if (mediaPlayer != null) {
            mediaPlayer.seek(mediaPlayer.getCurrentTime().add(Duration.seconds(10)));
        }
    }

    @FXML
    private void onPreviousButtonClicked() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            currentMediaIndex = (currentMediaIndex - 1 + mediaFiles.length) % mediaFiles.length;
            initializeMediaPlayer();
            mediaPlayer.play();
            playPauseButton.setText("⏸");
            isPlaying = true;
        }
    }

    @FXML
    private void onNextButtonClicked() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            currentMediaIndex = (currentMediaIndex + 1) % mediaFiles.length;
            initializeMediaPlayer();
            mediaPlayer.play();
            playPauseButton.setText("⏸");
            isPlaying = true;
        }
    }

    private void updateDuration() {
        if (mediaPlayer != null) {
            Duration currentTime = mediaPlayer.getCurrentTime();
            Duration totalTime = mediaPlayer.getTotalDuration();
            durationLabel.setText(formatDuration(currentTime) + " / " + formatDuration(totalTime));
        }
    }

    private String formatDuration(Duration duration) {
        int minutes = (int) duration.toMinutes();
        int seconds = (int) (duration.toSeconds() % 60);
        return String.format("%02d:%02d", minutes, seconds);
    }

    private Movie movie;  // Class variable to hold the movie object

    // Define the setMovie method to set the movie
    public void setMovie(Movie movie) {
        this.movie = movie;
        // Add any additional logic here if necessary
        Media media = new Media(new File(movie.getFileLink()).toURI().toString());
        MediaPlayer mediaPlayer = new MediaPlayer(media);
        mediaView.setMediaPlayer(mediaPlayer);
        mediaPlayer.play();
}
}