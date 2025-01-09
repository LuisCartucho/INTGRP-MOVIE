package easv.intgrpmovie.dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CatMovieDAO {

    private DBConnection conn = new DBConnection();

    public List<String> getMoviesByCategory(String categoryName) {
        List<String> movieDetails = new ArrayList<>();
        String query = "SELECT m.name, m.rating " +
                "FROM Movie m " +
                "JOIN CatMovie cm ON m.id = cm.MovieId " +
                "JOIN Category c ON c.id = cm.CategoryId " +
                "WHERE c.name = ?";

        try (Connection connection = conn.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, categoryName);
            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                String name = resultSet.getString("name");
                double rating = resultSet.getDouble("rating");
                movieDetails.add(name + " (Rating: " + rating + ")");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return movieDetails;
    }
}




