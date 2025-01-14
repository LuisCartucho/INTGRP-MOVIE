package easv.intgrpmovie.dal;

import easv.intgrpmovie.be.Category;

import java.sql.*;
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


    public List<String> getCategoryNames() {
        List<String> categoryNames = new ArrayList<>();
        String query = "SELECT name FROM Category";

        try (Connection connection = conn.getConnection();
             Statement stmt = connection.createStatement()) {

            ResultSet resultSet = stmt.executeQuery(query);
            while (resultSet.next()) {
                categoryNames.add(resultSet.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categoryNames;
    }

    public List<String> getCategoriesForMovie(int movieId) {
        List<String> categories = new ArrayList<>();
        String query = "SELECT c.name FROM Category c " +
                "JOIN CatMovie cm ON c.id = cm.categoryId " +
                "WHERE cm.movieId = ?";
        try (Connection connection = conn.getConnection();
             PreparedStatement stmnt = connection.prepareStatement(query)) {
            stmnt.setInt(1, movieId);
            ResultSet rs = stmnt.executeQuery();
            while (rs.next()) {
                categories.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categories;
    }

    public void deleteMovieCategories(int movieId) throws SQLException {
        String deleteQuery = "DELETE FROM CatMovie WHERE movieId = ?";
        try (Connection connection = conn.getConnection();
             PreparedStatement stmnt = connection.prepareStatement(deleteQuery)) {
            stmnt.setInt(1, movieId);
            stmnt.executeUpdate();
        }
    }

    public boolean insertCategory(String categoryName) {
        String query = "INSERT INTO Category (name) VALUES (?)";
        try (Connection connection = conn.getConnection();
             PreparedStatement stmnt = connection.prepareStatement(query)) {
            stmnt.setString(1, categoryName);
            return stmnt.executeUpdate() > 0; // Returns true if a row was inserted
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void deleteCategory(int categoryId) {
        // First, delete all rows in the CatMovie table that reference this category
        String deleteMoviesQuery = "DELETE FROM CatMovie WHERE CategoryId = ?";
        try (Connection connection = conn.getConnection();
             PreparedStatement stmnt = connection.prepareStatement(deleteMoviesQuery)) {

            stmnt.setInt(1, categoryId);
            stmnt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Now, delete the category itself
        String deleteCategoryQuery = "DELETE FROM Category WHERE id = ?";
        try (Connection connection = conn.getConnection();
             PreparedStatement stmnt = connection.prepareStatement(deleteCategoryQuery)) {

            stmnt.setInt(1, categoryId);
            stmnt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getCategoryIdByName(String categoryName) {
        String query = "SELECT id FROM Category WHERE name = ?";
        int categoryId = -1;  // Default to -1 if not found

        try (Connection connection = conn.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)){

            stmt.setString(1, categoryName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    categoryId = rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categoryId;
    }

    public void deleteMoviesByCategoryId(int categoryId) {
        String query = "DELETE FROM Movie WHERE CategoryId = ?";

        try (Connection connection = conn.getConnection();
             PreparedStatement stmnt = connection.prepareStatement(query)) {

            stmnt.setInt(1, categoryId);
            stmnt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
