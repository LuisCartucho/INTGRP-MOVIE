package easv.intgrpmovie.gui.model;

import easv.intgrpmovie.be.Category;
import easv.intgrpmovie.bll.CategoryManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class CategoryModel {

    private final CategoryManager categoryManager = new CategoryManager();

    private  ObservableList<Category> categoryObservableList = FXCollections.observableArrayList();

    public CategoryModel() {
        categoryObservableList.setAll(categoryManager.getCategory());
    }

    public  ObservableList<Category> getCategory() {
        return categoryObservableList;
}
}