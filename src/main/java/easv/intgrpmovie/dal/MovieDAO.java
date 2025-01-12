package easv.intgrpmovie.dal;

import easv.intgrpmovie.be.Movie;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MovieDAO {

    private DBConnection conn = new DBConnection();

    // Method to retrieve movies from the database
    public List<Movie> getMovie() {
        List<Movie> movies = new ArrayList<>();
        try {
            Connection c = conn.getConnection();
            String sql = "SELECT * FROM Movie";
            PreparedStatement stmnt = c.prepareStatement(sql);
            ResultSet rs = stmnt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("ID");
                String name = rs.getString("name");
                int rating = rs.getInt("rating");
                String fileLink = rs.getString("fileLink");
                String lastView = rs.getString("lastView");
                Movie movie = new Movie(id, name, rating, fileLink, lastView);
                movies.add(movie);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return movies;
    }

    public int insertMovie(String title, int rating, String fileLink, Date lastView) throws SQLException {
        String insertMovieQuery = "INSERT INTO Movie (name, rating, fileLink, lastView) VALUES (?, ?, ?, ?)";
        try (Connection c = conn.getConnection();
             PreparedStatement stmnt = c.prepareStatement(insertMovieQuery, Statement.RETURN_GENERATED_KEYS)) {
            stmnt.setString(1, title);
            stmnt.setInt(2, rating);
            stmnt.setString(3, fileLink);
            stmnt.setDate(4, lastView);
            stmnt.executeUpdate();
            ResultSet generatedKeys = stmnt.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);  // Return the generated movieId
            }
        }
        return -1;  // In case movie was not inserted
    }

    public void updateLastView(int movieId, Date lastView) throws SQLException {
        String updateLastViewQuery = "UPDATE Movie SET lastView = ? WHERE id = ?";
        try (Connection c = conn.getConnection();
             PreparedStatement stmnt = c.prepareStatement(updateLastViewQuery)) {
            stmnt.setDate(1, lastView);
            stmnt.setInt(2, movieId);
            stmnt.executeUpdate();
        }
    }
    public List<Movie> getMoviesByCategory(String categoryName) {
        List<Movie> movieDetails = new ArrayList<>();
        String query = "SELECT m.id, m.name, m.rating, m.fileLink, m.lastView " +
                "FROM Movie m " +
                "JOIN CatMovie cm ON m.id = cm.MovieId " +
                "JOIN Category c ON c.id = cm.CategoryId " +
                "WHERE c.name = ?";

        try (Connection connection = conn.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, categoryName);
            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                double rating = resultSet.getDouble("rating");
                String fileLink = resultSet.getString("fileLink");
                String lastView = resultSet.getString("lastView");

                // Create a Movie object and add it to the list
                Movie movie = new Movie(id, name, rating, fileLink, lastView);
                movieDetails.add(movie);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return movieDetails;
    }


    private static final String DELETE_CAT_MOVIE_SQL = "DELETE FROM CatMovie WHERE MovieId = ?";
    private static final String DELETE_MOVIE_SQL = "DELETE FROM Movie WHERE ID = ?";

    public void deleteMovieById(int movieId) {

        try (Connection connection = conn.getConnection()) {
            // Begin transaction to ensure both deletes happen together
            connection.setAutoCommit(false);

            // Delete from CatMovie table
            try (PreparedStatement stmt1 = connection.prepareStatement(DELETE_CAT_MOVIE_SQL)) {
                stmt1.setInt(1, movieId);
                stmt1.executeUpdate();
                }

                // Delete from Movie table
                try (PreparedStatement stmt2 = connection.prepareStatement(DELETE_MOVIE_SQL)) {
                    stmt2.setInt(1, movieId);
                    stmt2.executeUpdate();
                }

                // Commit the transaction
                connection.commit();
                System.out.println("Movie deleted successfully.");
            } catch (SQLException e) {
                // Handle any errors during the delete process
                System.err.println("Error deleting movie: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }



