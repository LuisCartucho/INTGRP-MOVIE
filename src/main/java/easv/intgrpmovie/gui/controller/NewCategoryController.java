package easv.intgrpmovie.gui.controller;

import easv.intgrpmovie.dal.CategoryDAO;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

    public class NewCategoryController {

        @FXML
        private TextField txtFieldNewCategory;

        private CategoryDAO categoryDAO = new CategoryDAO();

        @FXML
        public void onSaveCategory() {
            String categoryName = txtFieldNewCategory.getText().trim();

            if (categoryName.isEmpty()) {
                showAlert("Error", "Category name cannot be empty.", Alert.AlertType.ERROR);
                return;
            }

            try {
                boolean success = categoryDAO.insertCategory(categoryName);
                if (success) {
                    showAlert("Success", "Category added successfully!", Alert.AlertType.INFORMATION);
                    closeWindow();
                } else {
                    showAlert("Error", "Failed to add category. It may already exist.", Alert.AlertType.ERROR);
                }
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Error", "An unexpected error occurred.", Alert.AlertType.ERROR);
            }
        }

        private void showAlert(String title, String content, Alert.AlertType type) {
            Alert alert = new Alert(type);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(content);
            alert.showAndWait();
        }

        private void closeWindow() {
            Stage stage = (Stage) txtFieldNewCategory.getScene().getWindow();
            stage.close();
        }
    }
