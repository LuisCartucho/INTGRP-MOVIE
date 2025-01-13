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
        String mediaFolderPath = "media/";
        mediaFiles = findMediaFiles(mediaFolderPath);
        if (mediaFiles.length > 0) {
            initializeMediaPlayer();
        } else {
            System.out.println("No media files found in: " + mediaFolderPath);
        }

        // Add resize listener to make the video fullscreen when the window is maximized
        mediaView.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.widthProperty().addListener((o, oldWidth, newWidth) -> mediaView.setFitWidth(newWidth.doubleValue()));
                newScene.heightProperty().addListener((o, oldHeight, newHeight) -> mediaView.setFitHeight(newHeight.doubleValue()));
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
        // Stop any existing MediaPlayer before creating a new one
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
        }

        Media media = new Media(mediaFiles[currentMediaIndex]);
        mediaPlayer = new MediaPlayer(media);
        mediaView.setMediaPlayer(mediaPlayer);

        // Set up the MediaPlayer listeners
        mediaPlayer.setOnReady(() -> {
            Duration totalDuration = mediaPlayer.getMedia().getDuration();
            seekSlider.setMax(totalDuration.toSeconds());
            updateDuration();
        });

        mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
            if (!seekSlider.isValueChanging()) {
                seekSlider.setValue(newValue.toSeconds());
            }
            updateDuration();
        });

        mediaPlayer.setOnEndOfMedia(this::onNextButtonClicked);

        // Configure the volume slider
        volumeSlider.setValue(mediaPlayer.getVolume() * 100); // Convert to percentage
        volumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            mediaPlayer.setVolume(newValue.doubleValue() / 100); // Convert back to 0-1 range
        });

        // Configure the seek slider
        seekSlider.setOnMousePressed(event -> {
            if (mediaPlayer != null) {
                mediaPlayer.pause();
            }
        });

        seekSlider.setOnMouseReleased(event -> {
            if (mediaPlayer != null) {
                mediaPlayer.seek(Duration.seconds(seekSlider.getValue()));
                mediaPlayer.play();
            }
        });

        playPauseButton.setText("▶");
        mediaPlayer.play();
        isPlaying = true;
        playPauseButton.setText("⏸");
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
        }
    }

    @FXML
    private void onNextButtonClicked() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            currentMediaIndex = (currentMediaIndex + 1) % mediaFiles.length;
            initializeMediaPlayer();
        }
    }

    private void updateDuration() {
        if (mediaPlayer != null) {
            Duration currentTime = mediaPlayer.getCurrentTime();
            Duration totalTime = mediaPlayer.getTotalDuration();
            if (totalTime != null && !totalTime.isUnknown()) {
                durationLabel.setText(formatDuration(currentTime) + " / " + formatDuration(totalTime));
            } else {
                durationLabel.setText(formatDuration(currentTime) + " / --:--");
            }
        }
    }

    private String formatDuration(Duration duration) {
        int minutes = (int) duration.toMinutes();
        int seconds = (int) (duration.toSeconds() % 60);
        return String.format("%02d:%02d", minutes, seconds);
    }

    private Movie movie;

    public void setMovie(Movie movie) {
        this.movie = movie;
        Media media = new Media(new File(movie.getFileLink()).toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        mediaView.setMediaPlayer(mediaPlayer);
        mediaPlayer.play();
    }
}
