package easv.intgrpmovie.bll;

import easv.intgrpmovie.be.Category;
import easv.intgrpmovie.dal.CategoryDAO;

import java.util.List;

public class CategoryManager {
    private final CategoryDAO categoryDAO = new CategoryDAO();

    public List<Category> getCategories() {
        List<Category> categories = categoryDAO.getCategory();
        return categories;
    }

    public List<String> getCategoryNames() {
        return categoryDAO.getCategoryNames();
    }
}

