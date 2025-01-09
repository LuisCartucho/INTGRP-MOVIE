package easv.intgrpmovie.dal;

import easv.intgrpmovie.be.Category;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO {

    private DBConnection conn = new DBConnection();

    // Method to collect movie categories from the database
    public List<Category> getCategory() {
        List<Category> categories = new ArrayList<>();
        try {
            Connection c = conn.getConnection();
            String sql = "SELECT * FROM Category";
            PreparedStatement stmnt = c.prepareStatement(sql);
            ResultSet rs = stmnt.executeQuery();
            while (rs.next()) { // While there are rows
                int id = rs.getInt("id");
                String name = rs.getString("name");
                Category category = new Category(id, name);
                categories.add(category);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return categories;
    }

    public int getCategoryId(String categoryName) throws SQLException {
        String query = "SELECT id FROM Category WHERE name = ?";
        try (Connection connection = conn.getConnection();
             PreparedStatement stmnt = connection.prepareStatement(query)) {
            stmnt.setString(1, categoryName);
            ResultSet rs = stmnt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");  // Return the category ID
            }
        }
        return -1;  // Return -1 if the category doesn't exist
    }

    public void insertMovieCategory(int movieId, int categoryId) throws SQLException {
        String insertCategoryQuery = "INSERT INTO CatMovie (movieId, categoryId) VALUES (?, ?)";
        try (Connection connection = conn.getConnection();
             PreparedStatement stmnt = connection.prepareStatement(insertCategoryQuery)) {
            stmnt.setInt(1, movieId);      // movieId
            stmnt.setInt(2, categoryId);   // categoryId
            stmnt.executeUpdate();
        }
    }
}
